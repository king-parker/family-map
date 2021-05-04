package request_result.result;

/**
 * This class represents the login result message of the login service.
 */
public class LoginResult extends Result {
    protected String authToken;
    protected String userName;
    protected String personID;

    /**
     * This constructs a login result object that contains the information for
     * a successful service result
     * @param authToken authToken for the login
     * @param userName username of the user
     * @param personID Person ID of the user that logged in
     */
    public LoginResult(String authToken,String userName,String personID) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.success = true;
    }

    /**
     * This constructs a login result object that contains the information for
     * an unsuccessful service result
     * @param message Error message explaining the service error
     */
    public LoginResult(String message) {
        this.message = message;
        this.success = false;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return userName;
    }

    public String getPersonID() {
        return personID;
    }
}
