package data_access;

import model.ModelObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

/**
 * This abstract object forms the base of all the DAO objects. It contains the connection
 * each class will use to connect to the database in order to retrieve data and update the
 * database.
 * As the base class it also contains methods to clear data from the associated DAO object
 * table, to generate an ID for newly created objects and to check if data is already
 * contained in the database.
 */
public abstract class DaoObject {
    protected final Connection conn;

    /**
     * Creates a new Data Access Object using the given connection
     * @param conn connection to the database
     */
    public DaoObject(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts the given model object into the database
     * @param object model object to be inserted
     * @throws DataAccessException Thrown when an error occurs from inserting object
     */
    public abstract void insert(ModelObject object) throws DataAccessException;

    /**
     * Retrieve a model object from the database using it's primary key
     * @param search string used to identify the model object
     * @return model object found with the given search string
     * @throws DataAccessException Thrown when an error occurs from searching for a user
     */
    public abstract ModelObject find(String search) throws DataAccessException;

    /**
     * Clears the data from the table relating to the DAO
     * @throws DataAccessException Thrown when an error occurs from deleting object data
     */
    public abstract void clearTable() throws DataAccessException;

    /**
     * @param table Specified table in the database to clear data from
     * @throws DataAccessException Thrown when an error occurs from deleting object data
     */
    protected void clearTable(String table) throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM " + table;
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL Error encountered while clearing "
                    + table + " table");
        }
    }

    /**
     * @param check Value to check if already contained in database
     * @param dbList List of all values in desired database table that are the same attribute
     *               type as check
     * @return True if the value of check is not found in the database, False if the value of
     * check is found in the database
     */
    protected boolean available_Helper(String check, HashSet<String> dbList) {
        return !dbList.contains(check);
    }
}
