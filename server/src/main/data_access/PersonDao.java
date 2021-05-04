package data_access;

import model.ModelObject;
import model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Class that allows access to person data in the database
 */
public class PersonDao extends DaoObject {
    private final String TABLE_PERSON = "person";
    private final String PERSON_ID = "person_id";
    private final String USER_NAME = "user_name";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";
    private final String GENDER = "gender";
    private final String FATHER_ID = "father_id";
    private final String MOTHER_ID = "mother_id";
    private final String SPOUSE_ID = "spouse_id";

    /**
     * Creates a new data access object to access person data in the database
     * @param conn the current connection to the database
     */
    public PersonDao(Connection conn) {
        super(conn);
    }

    /**
     * Inserts person object into database
     * @param insertPerson person object to add
     * @throws DataAccessException Thrown when an error occurs from inserting
     */
    public void insert(ModelObject insertPerson) throws DataAccessException {
        if (!(insertPerson instanceof  Person)) throw new DataAccessException("Invalid object type");
        Person person = (Person) insertPerson;

        String sql = "INSERT INTO " + TABLE_PERSON + " (" + PERSON_ID + "," +
                USER_NAME + "," + FIRST_NAME + "," + LAST_NAME + "," + GENDER +
                "," + FATHER_ID + "," + MOTHER_ID + "," + SPOUSE_ID + ") VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,person.getPersonID());
            stmt.setString(2,person.getUsername());
            stmt.setString(3,person.getFirstName());
            stmt.setString(4,person.getLastName());
            stmt.setString(5,String.valueOf(person.getGender()));
            stmt.setString(6,person.getFatherID());
            stmt.setString(7,person.getMotherID());
            stmt.setString(8,person.getSpouseID());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Finds a person in the database using a person ID
     * @param personID ID of the person to get from the database
     * @return Person object belonging to the person ID
     * @throws DataAccessException Thrown when an error occurs from retrieving person
     */
    public Person find(String personID) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_PERSON + " WHERE " + PERSON_ID + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString(PERSON_ID),rs.getString(USER_NAME),
                        rs.getString(FIRST_NAME),rs.getString(LAST_NAME),
                        rs.getString(GENDER).charAt(0),rs.getString(FATHER_ID),
                        rs.getString(MOTHER_ID),rs.getString(SPOUSE_ID));

                return person;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        }
        finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Gets an array of every person who is associated with the given username
     * @param userName associated username to search for each person with
     * @return array of person objects associated with the username
     * @throws DataAccessException Thrown when an error occurs from retrieving person
     */
    public List<ModelObject> getAll(String userName) throws DataAccessException {
        List<ModelObject> people = new ArrayList<>();
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_PERSON + " WHERE " + USER_NAME + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,userName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                person = new Person(rs.getString(PERSON_ID),rs.getString(USER_NAME),
                        rs.getString(FIRST_NAME),rs.getString(LAST_NAME),
                        rs.getString(GENDER).charAt(0),rs.getString(FATHER_ID),
                        rs.getString(MOTHER_ID),rs.getString(SPOUSE_ID));
                people.add(person);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting person data");
        }
        finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return people;
    }

    /**
     * Checks the database to see if a person ID is used yet
     * @param personID ID to check for in database
     * @return True if ID is not in database already, false if it is in it
     * @throws DataAccessException Thrown when an error occurs from retrieving data
     */
    public boolean availableID(String personID) throws DataAccessException {
        HashSet<String> allID = new HashSet<>();
        String sql = "SELECT " + PERSON_ID +  " FROM " + TABLE_PERSON;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allID.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving person IDs");
        }
        return available_Helper(personID,allID);
    }

    /**
     * Clears all person data relating to a specific user in the database
     * @param userName username for the user to delete the data for
     * @throws DataAccessException Thrown when an error occurs from deleting data
     */
    public void clearUserData(String userName) throws DataAccessException {
        String sql = "DELETE FROM " + TABLE_PERSON + " WHERE " + USER_NAME + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,userName);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL Error encountered while clearing user data from Person");
        }
    }

    /**
     * Clears the data from the table relating to the DAO
     * @throws DataAccessException Thrown when an error occurs from deleting object data
     */
    @Override
    public void clearTable() throws DataAccessException {
        clearTable(TABLE_PERSON);
    }
}
