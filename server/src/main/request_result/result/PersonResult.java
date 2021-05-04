package request_result.result;

import model.Person;

/**
 * Class that holds the result of a request to receive a person associated with a user
 */
public class PersonResult extends Result {
    private String associatedUsername;
    private String personID;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    /**
     * Creates an object for a successful person retrieval
     * @param person found person object from service
     */
    public PersonResult(Person person) {
        personID = person.getPersonID();
        associatedUsername = person.getUsername();
        firstName = person.getFirstName();
        lastName = person.getLastName();
        gender = String.valueOf(person.getGender());
        if (!(person.getFatherID() == null || person.getFatherID().isBlank())) fatherID = person.getFatherID();
        if (!(person.getMotherID() == null || person.getMotherID().isBlank())) motherID = person.getMotherID();
        if (!(person.getSpouseID() == null || person.getSpouseID().isBlank())) spouseID = person.getSpouseID();
        success = true;
    }

    /**
     * Creates an object for an unsuccessful event retrieval
     * @param message Error message of what went wrong
     */
    public PersonResult(String message) {
        this.message = message;
        success = false;
    }

    public String getPersonID() {
        return personID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public char getGender() {
        return gender.charAt(0);
    }

    public String getFatherID() {
        return fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }
}
