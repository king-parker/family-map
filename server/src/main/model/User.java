package model;

/**
 * User object to hold data to be put into the database, or hold data retrieved from the database.
 */
public class User extends ModelObject {
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private char gender;
    private String personID;

    /**
     * Constructs a new user object with the given values and generates a random person ID. The
     * availability of this ID should be checked before inserting into the database.
     * @param username username of the user
     * @param password password of the user
     * @param email email of the user
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @param gender gender of the user
     */
    public User(String username,String password,String email,String firstName,
                String lastName,char gender){
        this.userName = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        personID = generateID();
    }

    /**
     * Constructs a new user object with the given values
     * @param username username of the user
     * @param password password of the user
     * @param email email of the user
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @param gender gender of the user
     * @param personID personID of the user
     */
    public User(String username,String password,String email,String firstName,
                String lastName,char gender,String personID) {
        this.userName = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() { return password; }

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

    public String getPersonID() {
        return personID;
    }

    /**
     * Replaces current person ID for the user with a new randomly generated one
     */
    @Override
    public void updateID() {
        personID = generateID();
    }

    /**
     * Returns a hash code for this user. It is computed by summing the hash code of all field variables
     * that are strings with the int value of the gender.
     * @return hash code value for this object
     */
    @Override
    public int hashCode() {
        return userName.hashCode() + password.hashCode() + email.hashCode() + firstName.hashCode() +
                lastName.hashCode() + (int) gender + personID.hashCode();
    }

    /**
     * Compares this user to the specified object. The result is true if and only if the the argument
     * is not null and is a User object that has the same values for each data member as this object.
     * @param o the object to compare this User against
     * @return true if the given object represents a User equivalent to this user, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof User) {
            User oUser = (User) o;
            return oUser.getUsername().equals(getUsername()) &&
                    oUser.getPassword().equals(getPassword()) &&
                    oUser.getEmail().equals(getEmail()) &&
                    oUser.getFirstName().equals(getFirstName()) &&
                    oUser.getLastName().equals(getLastName()) &&
                    oUser.getGender() == getGender() &&
                    oUser.getPersonID().equals(getPersonID());
        }
        else {
            return false;
        }
    }
}
