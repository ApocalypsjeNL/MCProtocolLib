package nl.apocalypsje.protocollib.util;

import com.google.gson.Gson;
import nl.apocalypsje.protocollib.util.object.LoginResponse;
import nl.apocalypsje.protocollib.util.object.User;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class MojangAPI {

    private static Gson GSON = new Gson();

    public static LoginResponse login(String username, String password, String clientToken) throws IOException {
        String data = "{\"agent\":\"MINECRAFT\",\"username\":\"" + username + "\",\"password\":\"" + password +
                "\",\"clientToken\":\"" + clientToken + "\",\"requestUser\":false}";

        return GSON.fromJson(makePostRequest("https://authserver.mojang.com/authenticate", data), LoginResponse.class);
    }

    public static LoginResponse obtain(String accessToken, String clientToken, String id, String name) {
        return new LoginResponse(accessToken, clientToken, new User(id, name));
    }

    public static void joinServer(LoginResponse loginResponse, String serverHash) throws IOException {
        String data = "{\"accessToken\":\"" + loginResponse.getAccessToken() + "\",\"selectedProfile\":\"" +
                loginResponse.getSelectedProfile().getId() + "\",\"serverId\":\"" + serverHash + "\"}";

        makePostRequest("https://sessionserver.mojang.com/session/minecraft/join", data);
    }

    private static String makePostRequest(String url, String data) throws IOException {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "ApocalypsjeNL | ProtocolLib");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(data);
        }

        StringBuilder response = new StringBuilder();

        InputStream inputStream = (con.getResponseCode() >= 200 && con.getResponseCode() < 300) ? con.getInputStream() : con.getErrorStream();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return response.toString();
    }
}
