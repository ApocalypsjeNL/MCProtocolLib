package nl.apocalypsje.protocollib.client;

import nl.apocalypsje.protocollib.event.EventType;
import nl.apocalypsje.protocollib.event.IEventHandler;
import nl.apocalypsje.protocollib.exceptions.*;
import nl.apocalypsje.protocollib.packet.IPacketHandler;
import nl.apocalypsje.protocollib.protocol.ProtocolState;
import nl.apocalypsje.protocollib.protocol.ProtocolVersion;
import nl.apocalypsje.protocollib.protocol.packet.Packet;
import nl.apocalypsje.protocollib.protocol.packet.PacketRegistery;
import nl.apocalypsje.protocollib.protocol.packet.handshake.Handshake;
import nl.apocalypsje.protocollib.protocol.packet.login.EncryptionRequest;
import nl.apocalypsje.protocollib.protocol.packet.login.EncryptionResponse;
import nl.apocalypsje.protocollib.protocol.packet.login.LoginStart;
import nl.apocalypsje.protocollib.stream.PacketCipher;
import nl.apocalypsje.protocollib.stream.PacketDecoder;
import nl.apocalypsje.protocollib.stream.PacketEncoder;
import nl.apocalypsje.protocollib.util.EncryptionUtils;
import nl.apocalypsje.protocollib.util.MojangAPI;
import nl.apocalypsje.protocollib.util.object.LoginResponse;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class Client implements IEventHandler, IPacketHandler {

    //Protocol stuff
    private ProtocolVersion protocolVersion;
    private ProtocolState protocolState;

    //Socket stuff
    private Socket socket;
    private PacketDecoder decoder;
    private PacketEncoder encoder;
    private PacketCipher encodeCipher;
    private PacketCipher decodeCipher;
    private Thread packetListener;

    //Other important stuff
    private String host;
    private int port;
    private boolean running;
    private Executor executor;
    private Map<String, String> parameters;
    private LoginResponse loginResponse;

    //Event stuff
    private List<IEventHandler> eventHandlers;

    //Packet stuff
    private List<IPacketHandler> packetHandlers;

    /**
     * Constructor for the client.
     *
     * @param protocolVersion Protocol version which indicates what version the client is using
     * @param threadPoolSize  Determines how large the executor pool will be
     */
    public Client(ProtocolVersion protocolVersion, int threadPoolSize) {
        this.protocolVersion = protocolVersion;
        this.protocolState = ProtocolState.HANDSHAKE;

        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.parameters = new HashMap<>();
        this.eventHandlers = new ArrayList<>();
        this.packetHandlers = new ArrayList<>();
        this.eventHandlers.add(this);
        this.packetHandlers.add(this);

        this.packetListener = new Thread(() -> {
            while (running) {
                try {
                    if (socket != null && !socket.isClosed() && decoder != null && decoder.available() > 0) {
                        synchronized (decoder) {
                            byte[] readByte = new byte[3];

                            for(int i = 0; i < readByte.length; i++) {
                                readByte[i] = decoder.readByte();

                                if(readByte[i] >= 0) {
                                    System.out.println(Arrays.toString(readByte));
                                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(readByte);
                                    PacketDecoder packetDecoder = new PacketDecoder(byteArrayInputStream);
                                    int varInt = packetDecoder.readVarInt();

                                    if(decoder.available() >= varInt) {
                                        byte[] packetBytes = new byte[varInt];
                                        decoder.readFully(packetBytes);

                                        System.out.println(Arrays.toString(packetBytes));

                                        handlePacket(packetBytes);

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    sleep(50);
                } catch (InterruptedException | IOException | PacketReadException e) {
                    e.printStackTrace();
                    running = false;
                }
            }
        });
    }

    private void handlePacket(byte[] data) throws PacketReadException {
        if(decodeCipher != null) {
            data = decodeCipher.decrypt(data);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        PacketDecoder packetDecoder = new PacketDecoder(inputStream);

        int packetId = packetDecoder.readVarInt();
        System.out.println("PacketID: " + packetId);

        Packet packet = PacketRegistery.getPacket(packetId, protocolState, false);
        if (packet != null) {
            packet.read(packetDecoder);
            //System.out.println(packet);
            for (IPacketHandler packetHandler : packetHandlers) {
                executor.execute(() -> packetHandler.handle(packet));
            }
        } else {
            System.out.println("Invalid packet for id '" + packetId + "' in state '" + protocolState.name() + "'");
        }
    }

    /**
     * Method tho get the version of the protocol that the client is using
     *
     * @return The used protocol version by the client
     */
    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Method to get the state of the protocol where the client is in
     *
     * @return The protocol state which the client is currently in
     */
    public ProtocolState getProtocolState() {
        return protocolState;
    }

    /**
     * Method to connect to a server by IP / Address only, uses 25565 as port
     *
     * @param host IP or Address to connect to, uses port 25565 as port
     * @throws ConnectException Thrown when an error occured while connecting
     */
    public void connect(String host) throws ConnectException {
        this.connect(host, 25565);
    }

    /**
     * Method to connect to a server by IP / Address and port
     *
     * @param host IP or Address to connect to
     * @param port Port to connect to
     * @throws ConnectException Thrown when an error occurred while connecting
     */
    public void connect(String host, int port) throws ConnectException {
        this.host = host;
        this.port = port;

        if (port <= 0) {
            throw new ConnectException("Can't connect to a port that's lower than 1");
        }

        try {
            socket = new Socket(host, port);
            encoder = new PacketEncoder(socket.getOutputStream());
            decoder = new PacketDecoder(socket.getInputStream());

            running = true;
            packetListener.start();

            fireEvent(EventType.CONNECT);
        } catch (IOException e) {
            throw new ConnectException("Failed to connect to '" + host + ":" + port + "'", e);
        }
    }

    /**
     * Method which allows to reconnect to the previously connected server
     *
     * @throws ConnectException Thrown when an error occurred while connecting or if you've never connected to a server before
     */
    public void reconnect() throws ConnectException {
        if (host != null && !this.host.isEmpty() && this.port > 0) {
            this.connect(host, port);
        } else {
            throw new ConnectException("You've never connected to a server before.");
        }
    }

    /**
     * Method to disconnect from the server
     *
     * @throws DisconnectException Thrown when an error occurred while disconnecting
     */
    public void disconnect() throws DisconnectException {
        try {
            if (socket != null && !socket.isClosed()) {
                //TODO Send disconnect packet to the server
                fireEvent(EventType.DISCONNECT);

                running = false;
                packetListener.join();
                socket.close();
            }
        } catch (IOException | InterruptedException e) {
            throw new DisconnectException("Failed to disconnect from the server", e);
        }
    }

    /**
     * Method which allows the client to send packets to the server
     *
     * @param packet The packet that's going to be send to the server
     */
    public void sendPacket(Packet packet) {
        try {
            if (!packet.protocolState().equals(protocolState)) {
                throw new PacketWriteException("Can't send a packet which requires protocolState '" + packet.protocolState().name() + "' while the client is in state '" + protocolState.name() + "'");
            }

            if (!packet.supportedVersions().contains(protocolVersion)) {
                throw new PacketWriteException("Can't send a packet which is made for protocolVersions '" + packet.supportedVersions().stream().map(Enum::name).collect(Collectors.toList()) + "' while the client is using protocolVersion '" + protocolVersion.name() + "'");
            }

            /*
            int readableBytes = in.readableBytes();
        byte[] heapIn = bufToByte( in );

        byte[] heapOut = heapOutLocal.get();
        int outputSize = cipher.getOutputSize( readableBytes );
        if ( heapOut.length < outputSize )
        {
            heapOut = new byte[ outputSize ];
            heapOutLocal.set( heapOut );
        }
        out.writeBytes( heapOut, 0, cipher.update( heapIn, 0, readableBytes, heapOut ) );
             */

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PacketEncoder packetEncoder = new PacketEncoder(outputStream);

            packetEncoder.writeVarInt(packet.packetId());
            packet.write(packetEncoder);

            synchronized (encoder) {
                if(encodeCipher != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    PacketEncoder packetEncoder1 = new PacketEncoder(byteArrayOutputStream);
                    packetEncoder1.writeVarInt(outputStream.size());
                    packetEncoder1.write(outputStream.toByteArray());
                    encodeCipher.encrypt(socket.getOutputStream(), byteArrayOutputStream);
                } else {
                    encoder.writeVarInt(outputStream.size());
                    encoder.write(outputStream.toByteArray());
                }
            }
        } catch (PacketWriteException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method which allows the client to trigger an event for all listeners
     *
     * @param type The event which will be called
     */
    private void fireEvent(EventType type) {
        for (IEventHandler eventHandler : eventHandlers) {
            executor.execute(() -> eventHandler.handle(type));
        }
    }

    /**
     * Method which registers an event handler
     *
     * @param eventHandler The event handler which is going to be added
     */
    public void addEventHandler(IEventHandler eventHandler) {
        this.eventHandlers.add(eventHandler);
    }

    /**
     * Method which is required before the client can login to a server
     *
     * @param loginResponse The login response which can be obtained from {@link nl.apocalypsje.protocollib.util.MojangAPI#login(String, String, String)} or {@link nl.apocalypsje.protocollib.util.MojangAPI#obtain(String, String, String, String)}
     */

    public void preLogin(LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
    }

    /**
     * Method which is going to try to join the server and send the encryption packets
     */
    public void login(boolean onlineMode) throws LoginException {
        if (onlineMode && loginResponse == null) {
            throw new LoginException("Client was set to online mode but no loginResponse was found. Did you call the preLogin method?");
        }

        if (!socket.isConnected()) {
            throw new LoginException("Can't login to a server if we're not connected to it!");
        }

        Handshake handshake = (Handshake) PacketRegistery.getPacket(0x00, protocolState, false);
        if (handshake == null) {
            throw new LoginException("Failed to get an instance of the Handshake packet!");
        }

        handshake.setProtocolVersion(protocolVersion.getProtocolId());
        handshake.setServerHost(host);
        handshake.setServerPort(port);
        handshake.setState(2);

        sendPacket(handshake);

        protocolState = ProtocolState.LOGIN;
        parameters.put("login", "true");

        LoginStart loginStart = (LoginStart) PacketRegistery.getPacket(0x00, protocolState, true);
        if (loginStart == null) {
            throw new LoginException("Failed to get an instance of the LoginStart packet!");
        }

        loginStart.setUsername(loginResponse.getSelectedProfile().getName());

        sendPacket(loginStart);

        fireEvent(EventType.LOGIN_START);
    }

    @Override
    public void handle(EventType type) {
        System.out.println(type.name());
    }

    @Override
    public void handle(Packet packet) {
        //System.out.println(packet);
        if (protocolState.equals(ProtocolState.LOGIN) && parameters.containsKey("login")) {
            if (packet.packetId() == 0x01) {
                EncryptionRequest encryptionRequest = (EncryptionRequest) packet;
                try {
                    SecretKey sharedSecret = EncryptionUtils.generateSharedKey();

                    String serverId = encryptionRequest.getServerId();
                    byte[] inputVerify = encryptionRequest.getVerifyToken();
                    PublicKey publicKey = EncryptionUtils.decodePublicKey( encryptionRequest.getPublicKey() );

                    String serverHash = ( new BigInteger( EncryptionUtils.getServerIdHash( serverId, publicKey, sharedSecret ) ) ).toString( 16 );

                    MojangAPI.joinServer(loginResponse, serverHash);

                    byte[] secret = EncryptionUtils.encrypt(publicKey, sharedSecret.getEncoded() );
                    byte[] verify = EncryptionUtils.encrypt(publicKey, inputVerify );
                    EncryptionResponse encryptionResponse = new EncryptionResponse();
                    encryptionResponse.setSharedSecret(secret);
                    encryptionResponse.setSharedSecretLength(secret.length);
                    encryptionResponse.setVerifyToken(verify);
                    encryptionResponse.setVerifyTokenLength(verify.length);

                    sendPacket(encryptionResponse);

                    encodeCipher = new PacketCipher();
                    encodeCipher.init(true, sharedSecret);

                    decodeCipher = new PacketCipher();
                    decodeCipher.init(false, sharedSecret);
                } catch (IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
