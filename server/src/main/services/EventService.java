package services;

import data_access.*;
import request_result.request.EventRequest;
import request_result.result.*;

import java.util.logging.Level;

/**
 * Class that preforms the service of searching for an event(s) in the database
 */
public class EventService extends DataRetrieveService {
    /**
     * Searches for an event specified by the request
     * @param r Event request that has the event ID and auth token
     * @return Result of the event service
     */
    public EventResult event(EventRequest r) {
        try {
            return (EventResult) data(r.getAuthToken(),r.getEventID(),EventResult.class);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            return new EventResult(DATABASE_ERROR);
        }
    }

    /**
     * Searches for all events associated with the user the auth token is for
     * @param authToken token from the logged in user
     * @return Result of the event service
     */
    public EventAllResult event(String authToken) {
        try {
            return (EventAllResult) data(authToken,EventAllResult.class);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            return new EventAllResult(DATABASE_ERROR);
        }
    }
}
