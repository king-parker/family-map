package data_access;

import model.AuthToken;
import model.ModelObject;
import utility.DateTime;

import java.sql.*;
import java.util.HashSet;

/**
 * This class provides an interface to interact with Authentication Tokens in the database
 */
public class AuthTokenDao extends DaoObject {
    private final String TABLE_AUTH_TOKEN = "authToken";
    private final String AUTH_TOKEN = "auth_token";
    private final String USER_NAME = "user_name";
    private final String CREATED_TIME = "created_time";

    /**
     * Creates a new data access object to access auth token data in the database
     * @param conn the current connection to the database
     */
    public AuthTokenDao(Connection conn) {
        super(conn);
    }

    /**
     * Inserts the given AuthToken object into the database
     * @param insertAuthToken AuthToken object to be inserted
     * @throws DataAccessException Thrown when an error occurs from inserting
     */
    public void insert(ModelObject insertAuthToken) throws DataAccessException {
        if (!(insertAuthToken instanceof AuthToken)) throw new DataAccessException("Invalid object type");
        AuthToken authToken = (AuthToken) insertAuthToken;

        String sql = "INSERT INTO " + TABLE_AUTH_TOKEN + " (" + AUTH_TOKEN +
                "," + USER_NAME + "," + CREATED_TIME + ") VALUES (?,?,?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Timestamp insertTime = new Timestamp(DateTime.stringToLong(authToken.getTimeStamp()));
            stmt.setString(1,authToken.getAuthToken());
            stmt.setString(2,authToken.getUsername());
            stmt.setTimestamp(3,insertTime);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Searches the database for an AuthToken object with the given authToken string
     * @param authToken authToken string to search with
     * @return Associated AuthToken object in database, null if none found
     * @throws DataAccessException Thrown when an error occurs from searching database
     */
    public AuthToken find(String authToken) throws DataAccessException {
        AuthToken foundToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_AUTH_TOKEN + " WHERE " + AUTH_TOKEN + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,authToken);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String dateTime = DateTime.longToDateTime(rs.getTimestamp(CREATED_TIME).getTime());
                foundToken = new AuthToken(rs.getString(AUTH_TOKEN),rs.getString(USER_NAME),
                        dateTime);
                return foundToken;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding auth token");
        }
        finally {
            if (rs != null) {
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
     * Checks the database to see if a token ID is used yet
     * @param authToken token ID to check for in database
     * @return True if ID is not in database already, false if it is in it
     * @throws DataAccessException Thrown when an error occurs from retrieving data
     */
    private boolean availableID(String authToken) throws DataAccessException {
        HashSet<String> allTokens = new HashSet<>();
        String sql = "SELECT " + AUTH_TOKEN + " FROM " + TABLE_AUTH_TOKEN;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allTokens.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving auth tokens");
        }
        return available_Helper(authToken,allTokens);
    }

    /**
     * Creates a new authentication token for the given user
     * @param userName user logging in
     * @return a new authentication token for the login
     * @throws DataAccessException Thrown when an error occurs from inserting
     */
    public AuthToken createToken(String userName) throws DataAccessException {
        AuthToken authToken = new AuthToken(userName);
        while (!availableID(authToken.getAuthToken())) {
            authToken.updateID();
        }
        insert(authToken);
        return authToken;
    }

    /**
     * Clears the data from the table relating to the DAO
     * @throws DataAccessException Thrown when an error occurs from deleting object data
     */
    @Override
    public void clearTable() throws DataAccessException {
        clearTable("authToken");
    }
}
