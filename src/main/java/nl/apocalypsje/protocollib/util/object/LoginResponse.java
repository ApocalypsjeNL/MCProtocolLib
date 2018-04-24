package nl.apocalypsje.protocollib.util.object;

public class LoginResponse {

    private String accessToken;
    private String clientToken;
    private User selectedProfile;

    public LoginResponse(String accessToken, String clientToken, User selectedProfile) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.selectedProfile = selectedProfile;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public User getSelectedProfile() {
        return selectedProfile;
    }
}
