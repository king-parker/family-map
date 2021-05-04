package model;

/**
 * Class that represents person data in the database
 */
public class Person extends ModelObject {
    private String personID;
    private String associatedUsername;
    private String firstName;
    private String lastName;
    private char gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    /**
     * Constructor used when retrieving person data from the database that has no linked person
     * data
     * @param personID ID for the person
     * @param associatedUsername associated username for the person
     * @param firstName first name of the person
     * @param lastName last name of the person
     * @param gender gender of the person
     */
    public Person(String personID,String associatedUsername,String firstName,String lastName,char gender) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        fatherID = "";
        motherID = "";
        spouseID = "";
    }

    /**
     * Constructs a new person object. Typically this constructor is used for constructing a
     * person object to represent a new person. ID is randomly generated and should be checked
     * for availability. Father, mother and spouse ID's need to be set after those person objects
     * are constructed.
     * @param associatedUsername associated username for the person
     * @param firstName first name of the person
     * @param lastName last name of the person
     * @param gender gender of the person
     */
    public Person(String associatedUsername,String firstName,String lastName,char gender) {
        this("",associatedUsername,firstName,lastName,gender);
        personID = generateID();
    }

    /**
     * Constructor used when retrieving person data from the database or for LoadService
     * @param personID ID for the person
     * @param associatedUsername associated username for the person
     * @param firstName first name of the person
     * @param lastName last name of the person
     * @param gender gender of the person
     * @param fatherID ID of the person's father
     * @param motherID ID of the person's mother
     * @param spouseID ID of the person's mother
     */
    public Person(String personID,String associatedUsername,String firstName,String lastName,
                  char gender,String fatherID,String motherID,String spouseID) {
        this(personID,associatedUsername,firstName,lastName,gender);
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    public String getPersonID() {
        return personID;
    }

    public String getUsername() {
        return associatedUsername;
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

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    /**
     * Replaces current ID with a new randomly generated one
     */
    @Override
    public void updateID() {
        personID = generateID();
    }

    /**
     * Returns a hash code for this person. It is computed by summing the hash code of all field variables
     * that are strings with the int value of the gender. Father, mother and spouse ID hash codes are only
     * summed when they contain a value
     * @return hash code value for this object
     */
    @Override
    public int hashCode() {
        int code = personID.hashCode() + associatedUsername.hashCode() + firstName.hashCode() +
                lastName.hashCode() + (int) gender;
        if (!fatherID.isBlank()) code += fatherID.hashCode();
        if (!motherID.isBlank()) code += motherID.hashCode();
        if (!spouseID.isBlank()) code += spouseID.hashCode();

        return code;
    }

    /**
     * Compares this person to the specified object. The result is true if and only if the the argument
     * is not null and is a Person object that has the same values for each data member as this object.
     * @param o the object to compare this Person against
     * @return true if the given object represents a Person equivalent to this person, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Person) {
            Person oPerson = (Person) o;
            return oPerson.getPersonID().equals(getPersonID()) &&
                    oPerson.getUsername().equals(getUsername()) &&
                    oPerson.getFirstName().equals(getFirstName()) &&
                    oPerson.getLastName().equals(getLastName()) &&
                    oPerson.getGender() == getGender() &&
                    oPerson.getFatherID().equals(getFatherID()) &&
                    oPerson.getMotherID().equals(getMotherID()) &&
                    oPerson.getSpouseID().equals(getSpouseID());
        }
        else {
            return false;
        }
    }
}
