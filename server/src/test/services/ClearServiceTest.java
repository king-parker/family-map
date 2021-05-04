package services;

import data_access.DataAccessException;
import data_access.Database;
import data_access.EventDao;
import data_access.PersonDao;
import model.Event;
import model.ModelObject;
import model.Person;
import org.junit.jupiter.api.Test;
import request_result.request.RegisterRequest;
import request_result.result.ClearResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    @Test
    void fillAndClear() throws DataAccessException {
        String placeHolder = "test";
        new RegisterService().register(new RegisterRequest(placeHolder,placeHolder,placeHolder,
                placeHolder,placeHolder,'m'));
        Database database = new Database();
        List<ModelObject> people = new PersonDao(database.openConnection()).getAll(placeHolder);
        List<ModelObject> events = new EventDao(database.getConnection()).getAll(placeHolder);
        database.closeConnection(false);
        assertFalse(people.size() == 0 && events.size() == 0,"Could not confirm a successful clear");

        ClearResult result = new ClearService().clear();
        assertTrue(result.isSuccess(),"Service was reported as failed when it should have succeeded.");
        people = new PersonDao(database.openConnection()).getAll(placeHolder);
        events = new EventDao(database.getConnection()).getAll(placeHolder);
        database.closeConnection(false);
        assertTrue(people.size() == 0 && events.size() == 0,"Clear was not successful");
    }

    @Test
    void doubleClear() throws DataAccessException {
        ClearService service = new ClearService();

        assertDoesNotThrow(()->service.clear(),"Error occurred in clearing");
        assertDoesNotThrow(()->service.clear(),"Error occurred in clearing a second time in a row");
    }
}