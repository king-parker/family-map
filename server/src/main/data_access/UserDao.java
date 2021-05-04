package data_access;

import model.ModelObject;
import model.User;

import java.sql.*;
import java.util.HashSet;

/**
 * This class provides a way to read and write user data in the database.
 */
public class UserDao extends DaoObject {
    private final String TABLE_USER = "users";
    private final String USER_NAME = "user_name";
    private final String PASSWORD = "password";
    private final String EMAIL = "email";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";
    private final String GENDER = "gender";
    private final String PERSON_ID = "person_id";

    /**
     * Creates a new data access object to access user data in the database
     * @param conn the current connection to the database
     */
    public UserDao(Connection conn) {
        super(conn);
    }

    /**
     * Inserts a user object into the database
     * @param insertUser user object to insert into database
     * @throws DataAccessException Thrown when an error occurs from inserting a user
     */
    public void insert(ModelObject insertUser) throws DataAccessException {
        if (!(insertUser instanceof User)) throw new DataAccessException("Invalid object type");
        User user = (User) insertUser;

        String sql = "INSERT INTO " + TABLE_USER + " (" + USER_NAME +  "," + PASSWORD + "," +
            EMAIL + "," + FIRST_NAME + "," + LAST_NAME + "," + GENDER +
            "," + PERSON_ID + ") VALUES (?,?,?,?,?,?,?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,user.getUsername());
            stmt.setString(2,user.getPassword());
            stmt.setString(3,user.getEmail());
            stmt.setString(4,user.getFirstName());
            stmt.setString(5,user.getLastName());
            stmt.setString(6, String.valueOf(user.getGender()));
            stmt.setString(7,user.getPersonID());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Finds a user based off of the username
     * @param userName username of the user being searched for
     * @return User object with the given username
     * @throws DataAccessException Thrown when an error occurs from searching for a user
     */
    public User find(String userName) throws DataAccessException {
        User user;
        ResultSet rs = null;
        String sql = "Select * FROM " + TABLE_USER + " WHERE " + USER_NAME + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString(USER_NAME),rs.getString(PASSWORD),
                        rs.getString(EMAIL),rs.getString(FIRST_NAME),rs.getString(LAST_NAME),
                        rs.getString(GENDER).charAt(0),rs.getString(PERSON_ID));
                return user;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new  DataAccessException("Error encountered while finding user");
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
     * Validates the username and password for a login attempt
     * @param userName username to check for in database
     * @param password entered password for the user
     * @return True if the user is contained in the database and the password matches for the
     * user. Otherwise false.
     * @throws DataAccessException Thrown when an error occurs from searching for a user
     */
    public boolean loginValidate(String userName,String password) throws DataAccessException {
        User user = find(userName);
        if (user != null) {
            return password.equals(user.getPassword());
        }
        else {
            return false;
        }
    }

    /**
     * Checks database to see if given username is available for a new user to use
     * @param userName username to check if it is available
     * @return True if username is available, False if it is not
     * @throws DataAccessException Thrown when an error occurs from searching for usernames
     */
    public boolean availableUserName(String userName) throws DataAccessException {
        HashSet<String> allUserNames = new HashSet<>();
        String sql = "SELECT " + USER_NAME +  " FROM " + TABLE_USER;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allUserNames.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving usernames");
        }
        return available_Helper(userName,allUserNames);
    }

    /**
     * Checks database to see if given email is available for a new user to use
     * @param email email to check if it is available
     * @return True if email is available, False if it is not
     * @throws DataAccessException Thrown when an error occurs from searching for emails
     */
    public boolean availableEmail(String email) throws DataAccessException {
        HashSet<String> allEmails = new HashSet<>();
        String sql = "SELECT " + EMAIL + " FROM " + TABLE_USER;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allEmails.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving usernames");
        }
        return available_Helper(email,allEmails);
    }

    /**
     * Clears the data from the table relating to the DAO
     * @throws DataAccessException Thrown when an error occurs from deleting object data
     */
    @Override
    public void clearTable() throws DataAccessException {
        clearTable(TABLE_USER);
    }
}
