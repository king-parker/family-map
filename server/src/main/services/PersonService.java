package services;

import data_access.DataAccessException;
import request_result.request.PersonRequest;
import request_result.result.PersonAllResult;
import request_result.result.PersonResult;

import java.util.logging.Level;

/**
 * Preforms the service of retrieving person(s) data from the database
 */
public class PersonService extends DataRetrieveService {
    /**
     * Searches for a person specified by the request
     * @param r Person request that has the person ID and auth token
     * @return Result of the person service
     */
    public PersonResult person(PersonRequest r) {
        try {
            return (PersonResult) data(r.getAuthToken(),r.getPersonID(),PersonResult.class);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            return new PersonResult(DATABASE_ERROR);
        }
    }

    /**
     * Searches for all people associated with the user the auth token is for
     * @param authToken token from the logged in user
     * @return Result of the person service
     */
    public PersonAllResult person(String authToken) {
        try {
            return (PersonAllResult) data(authToken,PersonAllResult.class);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            return new PersonAllResult(DATABASE_ERROR);
        }
    }
}
