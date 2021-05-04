package services;

import data_access.*;
import model.ModelObject;
import request_result.request.LoadRequest;
import request_result.result.LoadResult;

import java.util.logging.Level;

/**
 * Class that preforms the service of loading given data into the server
 */
public class LoadService extends Service {
    /**
     * Loads given user, person and event data into the server
     * @param r Request object containing user, person, and event data
     * @return The result of the load request
     */
    public LoadResult load(LoadRequest r) {
        String service = "Load";
        String method = "load";

        logger.finer(fieldMsg);
        LoadResult result = checkFields(r);
        if (result != null) {logExit(service,method); return result; }
        logger.finer("Checking number of objects:" +
                "\n\tNum of users: " + r.getUsers().length +
                "\n\tNum of people: " + r.getPeople().length +
                "\n\tNum of events: " + r.getEvents().length);

        Database database = new Database();
        try {
            database.getConnection();
            database.clearTables();

            insert(r.getUsers(),new UserDao(database.getConnection()));
            insert(r.getPeople(),new PersonDao(database.getConnection()));
            insert(r.getEvents(),new EventDao(database.getConnection()));

            database.closeConnection(true);

            String successMessage = "Successfully added " + r.getUsers().length + " users, " +
                    r.getPeople().length + " persons, and " + r.getEvents().length + " events to the database.";
            logExit(service,method);
            return new LoadResult(successMessage,true);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            logExit(service,method);
            if (e.getMessage().equals("Error encountered while inserting into the database")) {
                return  new LoadResult(INV_INPUT_ERROR,false);
            }
            else return new LoadResult(DATABASE_ERROR,false);
        } finally {
            try {
                if (database.getConnection() != null) database.closeConnection(false);
            } catch (DataAccessException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
            }
        }
    }

    private LoadResult checkFields(LoadRequest r) {
        if (r.getUsers() == null || r.getPeople() == null || r.getEvents() == null) {
            return new LoadResult(MISS_INPUT_ERROR,false);
        }
        return null;
    }

    private void insert(ModelObject[] objects,DaoObject dao) throws DataAccessException {
        for (ModelObject object : objects) dao.insert(object);
    }
}
