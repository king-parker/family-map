package request_result.request;

/**
 * Class that stores data to process a login service request
 */
public class LoginRequest {
    private String userName;
    private String password;

    /**
     * Creates a login request object with the username and password to be used to login
     * @param username username of user logging in
     * @param password password of user logging in
     */
    public LoginRequest(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
