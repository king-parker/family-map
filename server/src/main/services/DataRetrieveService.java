package services;

import data_access.*;
import model.AuthToken;
import model.Event;
import model.ModelObject;
import model.Person;
import request_result.result.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Base class for all services that retrieve data from the database (GET methods). Provides functionality
 * to retrieve a specific data object or retrieve all objects associated with a user.
 */
public abstract class DataRetrieveService extends Service {
    protected <ResultT> Result data(String authToken,String searchID,Class<ResultT> resultTClass)
            throws DataAccessException {
        if (authToken.isBlank()) { return failResult(MISS_TOKEN_ERROR,resultTClass); }
        String username = checkToken(authToken);
        if (username == null) return failResult(INV_TOKEN_ERROR,resultTClass);

        ModelObject foundData = null;
        Database database = new Database();
        if (resultTClass == PersonResult.class) {
            foundData = getData(searchID,new PersonDao(database.openConnection()));
        }
        else if (resultTClass == EventResult.class) {
            foundData = getData(searchID,new EventDao(database.openConnection()));
        }
        database.closeConnection(false);

        if (foundData == null) return failResult(INV_INPUT_ERROR,resultTClass);
        if (!foundData.getUsername().equals(username)) return failResult(INV_INPUT_ERROR,resultTClass);

        return successResult(foundData,resultTClass);
    }

    protected <ResultT> Result data(String authToken,Class<ResultT> resultTClass)
            throws DataAccessException {
        if (authToken.isBlank()) { return failResult(MISS_TOKEN_ERROR,resultTClass); }
        String username = checkToken(authToken);
        if (username == null) return failResult(INV_TOKEN_ERROR,resultTClass);

        Database database = new Database();
        List<ModelObject> foundData = null;
        if (resultTClass == PersonAllResult.class) {
            foundData = getUserData(username,new PersonDao(database.openConnection()));
        }
        else if (resultTClass == EventAllResult.class) {
            foundData = getUserData(username,new EventDao(database.openConnection()));
        }
        database.closeConnection(false);

        return successResult(foundData,resultTClass);
    }

    private String checkToken(String authToken) throws DataAccessException {
        Database database = new Database();
        AuthTokenDao tDao = new AuthTokenDao(database.openConnection());
        AuthToken token = tDao.find(authToken);
        database.closeConnection(false);
        if (token != null) {
            return token.getUsername();
        }
        else {
            return null;
        }
    }

    private ModelObject getData(String searchID,DaoObject dao) {
        try {
            return dao.find(searchID);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            return null;
        }
    }

    private List<ModelObject> getUserData(String username,DaoObject dao) {
        try {
            if (dao.getClass() == PersonDao.class) return ((PersonDao) dao).getAll(username);
            else if (dao.getClass() == EventDao.class) return ((EventDao) dao).getAll(username);
            else return null;
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            return null;
        }
    }

    private <ResultT> Result successResult(ModelObject foundData,Class<ResultT> resultTClass) {
        if (resultTClass == PersonResult.class) {
            Person foundPerson = (Person) foundData;
            return new PersonResult(foundPerson);
        }
        else if (resultTClass == EventResult.class) {
            Event foundEvent = (Event) foundData;
            return new EventResult(foundEvent);
        }
        else return null;
    }

    private <ResultT> Result successResult(List<ModelObject> foundData,Class<ResultT> resultTClass) {
        if (resultTClass == PersonAllResult.class) {
            List<Person> foundPeople = new ArrayList<>();
            for (ModelObject data : foundData) foundPeople.add((Person) data);
            return new PersonAllResult(foundPeople);
        }
        else if (resultTClass == EventAllResult.class) {
            List<Event> foundEvents = new ArrayList<>();
            for (ModelObject data : foundData) foundEvents.add((Event) data);
            return new EventAllResult(foundEvents);
        }
        else return null;
    }

    private <ResultT> Result failResult(String errorMsg,Class<ResultT> resultTClass) {
        if (resultTClass == PersonResult.class) return new PersonResult(errorMsg);
        else if (resultTClass == EventResult.class) return new EventResult(errorMsg);
        else if (resultTClass == PersonAllResult.class) return new PersonAllResult(errorMsg);
        else if (resultTClass == EventAllResult.class) return new EventAllResult(errorMsg);
        else return null;
    }
}
