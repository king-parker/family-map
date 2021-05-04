package data_access;

import server.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that contains the connection to the database to be used by the data access objects
 */
public class Database {
    private static int numConn = 0;
    private static final Logger logger;

    static { logger = Logger.getLogger(Server.logName); }

    private Connection conn;

    /**
     * Opens a connection to the database in order to begin a transaction
     * @return The current connection that is now connected to the database
     * @throws DataAccessException Thrown when an error occurs from opening the connection
     */
    public Connection openConnection() throws DataAccessException {
        try {
            final String CONNECTION_URL = "jdbc:sqlite:familymap.sqlite";
            conn = DriverManager.getConnection(CONNECTION_URL);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            throw new DataAccessException("Unable to open connection to database");
        }
        logger.fine("Database connection opened. Num open connections: " + ++numConn);
        return conn;
    }

    /**
     * Gets the current connection to the database. If no connection is already open, a new
     * connection will be opened
     * @return the current database connection
     * @throws DataAccessException Thrown when an error occurs from opening the connection
     */
    public Connection getConnection() throws DataAccessException {
        if(conn == null) {
            return openConnection();
        } else {
            return conn;
        }
    }

    /**
     * Closes the current connection and either commits or does a rollback on the current
     * transaction.
     * @param commit True if transaction is to be committed to the database, False
     *               if an error occurs in the transaction and a rollback is needed
     * @throws DataAccessException Thrown when an error occurs from closing the connection
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        String logCommit;
        try {
            if (commit) {
                logCommit = "commit";
                conn.commit();
            }
            else {
                logCommit = "rollback";
                conn.rollback();
            }
            conn.close();
            conn = null;
        }
        catch (SQLException e) {
            String databaseError = "Unable to close database connection";
            logger.log(Level.SEVERE,databaseError + ": " + e.getMessage(),e);
            throw new DataAccessException(databaseError);
        }
        logger.fine("Database connection closed. Transaction " + logCommit +
                ". Num open connections: " + --numConn);
    }

    /**
     * Clears data from each of the data types in the database
     * @throws DataAccessException Thrown when an error occurs from deleting data
     */
    public void clearTables() throws DataAccessException {
        logger.finer("Clearing all tables");
        new UserDao(conn).clearTable();
        new AuthTokenDao(conn).clearTable();
        new PersonDao(conn).clearTable();
        new EventDao(conn).clearTable();
        logger.finer("Clear successful");
    }
}
