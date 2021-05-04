package request_result.result;

/**
 * This class represents the register result message of the register service. It
 * utilizes the functionality of the login result class since the response bodies
 * contain the same data.
 */
public class RegisterResult extends LoginResult {
    /**
     * This constructs a register result object that contains the information for
     * a successful service result
     * @param authToken authToken for the login
     * @param userName username of the new user
     * @param personID Person ID of the user that logged in
     */
    public RegisterResult(String authToken,String userName,String personID) {
        super(authToken,userName,personID);
    }

    /**
     * This constructs a register result object that contains the information for
     * an unsuccessful service result
     * @param message Error message explaining the service error
     */
    public RegisterResult(String message) {
        super(message);
    }
}
