package services;

import data_access.DataAccessException;
import data_access.Database;
import request_result.result.ClearResult;

import java.util.logging.Level;

/**
 * Class that preforms the service of clearing the database
 */
public class ClearService extends Service {
    /**
     * Preforms the necessary functions for clearing the database
     * @return result of the clear service
     */
    public ClearResult clear() {
        String service = "Clear";
        String method = "clear";

        Database database = new Database();
        try {
            database.getConnection();
            database.clearTables();
            database.closeConnection(true);

            logExit(service,method);
            return new ClearResult("Clear succeeded",true);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            logExit(service,method);
            return new ClearResult(DATABASE_ERROR,false);
        } finally {
            try {
                if (database.getConnection() != null) database.closeConnection(false);
            } catch (DataAccessException e) {
                logExit(service,method);
            }
        }
    }
}
