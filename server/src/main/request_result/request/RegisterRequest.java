package request_result.request;

/**
 * This class receives the data from the message body of a request to register a new user. This
 * data is used by the register service class to attempt to register the user to the database.
 */
public class RegisterRequest {
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private char gender;

    /**
     * Constructs a request object for the register service and assigns the variables contained
     * in the request body
     * @param userName username for new user (needs to be unique)
     * @param password password for new user
     * @param email email of new user (needs to be unique)
     * @param firstName first name of the new user
     * @param lastName last name of the new user
     * @param gender gender of the new user
     */
    public RegisterRequest(String userName,String password,String email,String firstName,
                           String lastName,char gender) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public char getGender() {
        return gender;
    }
}
