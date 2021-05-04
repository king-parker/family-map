package data_access;

/**
 * Exception to be thrown when an error occurs accessing the database
 */
public class DataAccessException extends Exception {
    /**
     * Creates a new exception caused by an error in communicating with the database with the given
     * message for the error source
     * @param message error message for why the exception occurred
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Creates a new exception caused by an error in communicating with the database
     */
    public DataAccessException() {
        super();
    }
}
