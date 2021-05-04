package services;

import server.Server;
import java.util.logging.Logger;

/**
 * Base class of all service classes. This class contains strings for each of the possible errors that can
 * occur while a service is executing. It also implements the server's logger with entry and exit functions.
 */
public abstract class Service {
    /**
     * Error string for when a database error occurs
     */
    public static final String DATABASE_ERROR = "Error: Internal Server Error";
    /**
     * Error string for when an error occurs from a missing auth token
     */
    public static final String MISS_TOKEN_ERROR = "Error: Bad Request occurred from missing auth token";
    /**
     * Error string for when an error occurs from an invalid auth token
     */
    public static final String INV_TOKEN_ERROR = "Error: Bad Request occurred from invalid auth token";
    /**
     * Error string for when an error occurs from a missing request value
     */
    public static final String MISS_INPUT_ERROR = "Error: Bad Request occurred from missing value";
    /**
     * Error string for when an error occurs from an invalid request value
     */
    public static final String INV_INPUT_ERROR = "Error: Bad Request occurred from invalid value";
    /**
     * Error string for when a requested username is not available
     */
    public static final String USER_TAKEN_ERROR = "Error: Bad Request occurred because Username already used";
    /**
     * Error string for when a requested email is not available
     */
    public static final String EMAIL_TAKEN_ERROR = "Error: Bad Request occurred because email already used";

    protected static String fieldMsg = "Checking if any request fields are null or empty";
    protected static Logger logger;

    static { logger = Logger.getLogger(Server.logName); }

    protected void logEnter(String service,String method) {
        logger.entering( "services." + service + "Service",method);
    }

    protected void logExit(String service,String method) {
        logger.exiting("services." + service + "Service",method);
    }
}
