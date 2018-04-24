package nl.apocalypsje.protocollib.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class EncryptionUtils {

    public static SecretKey generateSharedKey() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            return gen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Failed to generate shared key.", e);
        }
    }

    public static PublicKey decodePublicKey(byte bytes[]) throws IOException {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
        } catch (GeneralSecurityException e) {
            throw new IOException("Could not decrypt public key.", e);
        }
    }

    public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey) {
        try {
            return encrypt(serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (UnsupportedEncodingException e) {
            throw new Error("Failed to generate server id hash.", e);
        }
    }

    private static byte[] encrypt(byte[]... data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            for (byte array[] : data) {
                digest.update(array);
            }

            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Failed to encrypt data.", e);
        }
    }

    public static byte[] encrypt(Key key, byte[] b) throws GeneralSecurityException {
        Cipher hasher = Cipher.getInstance("RSA");
        hasher.init(Cipher.ENCRYPT_MODE, key);
        return hasher.doFinal(b);
    }
}
