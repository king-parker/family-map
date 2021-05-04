package services;

import data_access.*;
import model.AuthToken;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_result.request.EventRequest;
import request_result.result.*;
import utility.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {
    User[] users;
    Person[] people;
    Event[] events;
    Event[] user1Events;
    String userName1;
    String userID1;
    String token1;
    String userName2;
    String userID2;
    String token2;
    String userName3;
    String userID3;
    String token3;
    String placeHolder;
    int year;

    @BeforeAll
    static void dbSetUp() throws DataAccessException {
        Database database = new Database();
        database.openConnection();
        database.clearTables();
        database.closeConnection(true);
    }

    @BeforeEach
    void testSetUp() throws DataAccessException {
        userName1 = "kingpark";
        userID1 = "1234-5678-90ab-cdef";
        token1 = "1111-2222-3333-4444";
        userName2 = "phoenix";
        userID2 = "cdef-5678-90ab-1234";
        token2 = "2222-3333-4444-5555";
        userName3 = "pokey";
        userID3 = "159c-260d-37ae-48bf";
        token3 = "3333-4444-5555-6666";
        placeHolder = "test";
        year = 2020;

        users = new User[]{new User(userName1, placeHolder, "email1",
                placeHolder, placeHolder, 'm',"1111-1111-1111-1111"),
                new User(userName2, placeHolder, "email2",
                        placeHolder, placeHolder, 'm'),
                new User(userName3, placeHolder, "email3",
                        placeHolder, placeHolder, 'm')};
        people = new Person[]{new Person("1111-1111-1111-1111",userName1,placeHolder,placeHolder,'m'),
                new Person("1111-1111-1111-1112",userName1,placeHolder,placeHolder,'m'),
                new Person("1111-1111-1111-1113",userName1,placeHolder,placeHolder,'f'),
                new Person("1111-1111-1111-1114",userName1,placeHolder,placeHolder,'f'),
                new Person("1111-1111-1111-1115",userName2,placeHolder,placeHolder,'m'),
                new Person("1111-1111-1111-1116",userName2,placeHolder,placeHolder,'m'),
                new Person("1111-1111-1111-1117",userName2,placeHolder,placeHolder,'f'),
                new Person("1111-1111-1111-1118",userName2,placeHolder,placeHolder,'f'),
                new Person("1111-1111-1111-1119",userName3,placeHolder,placeHolder,'m'),
                new Person("1111-1111-1111-1110",userName3,placeHolder,placeHolder,'m'),
                new Person("1111-1111-1111-1121",userName3,placeHolder,placeHolder,'f'),
                new Person("1111-1111-1111-1122",userName3,placeHolder,placeHolder,'f')};
        events = new Event[]{new Event("1111-1111-1111-1111",userName1,placeHolder,0,
                0,placeHolder,placeHolder,Event.BIRTH,year),
                new Event("1111-1111-1111-1112",userName1,placeHolder,0,
                        0,placeHolder,placeHolder,Event.MARRIAGE,year),
                new Event("1111-1111-1111-1113",userName1,placeHolder,0,
                        0,placeHolder,placeHolder,Event.DEATH,year),
                new Event("1111-1111-1111-1114",userName1,placeHolder,0,
                        0,placeHolder,placeHolder,Event.MILIT_DEP,year),
                new Event("1111-1111-1111-1115",userName1,placeHolder,0,
                        0,placeHolder,placeHolder,Event.SCHOOL_GRAD,year),
                new Event("1111-1111-1111-1116",userName1,placeHolder,0,
                        0,placeHolder,placeHolder,Event.CHRISTENING,year),
                new Event("1111-1111-1111-1117",userName1,placeHolder,0,
                        0,placeHolder,placeHolder,Event.BAPTISM,year),
                new Event("1111-1111-1111-1118",userName2,placeHolder,0,
                        0,placeHolder,placeHolder,Event.BIRTH,year),
                new Event("1111-1111-1111-1119",userName2,placeHolder,0,
                        0,placeHolder,placeHolder,Event.MARRIAGE,year),
                new Event("1111-1111-1111-1110",userName2,placeHolder,0,
                        0,placeHolder,placeHolder,Event.DEATH,year),
                new Event("1111-1111-1111-1121",userName2,placeHolder,0,
                        0,placeHolder,placeHolder,Event.MILIT_DEP,year),
                new Event("1111-1111-1111-1122",userName2,placeHolder,0,
                        0,placeHolder,placeHolder,Event.SCHOOL_GRAD,year),
                new Event("1111-1111-1111-1123",userName2,placeHolder,0,
                        0,placeHolder,placeHolder,Event.CHRISTENING,year),
                new Event("1111-1111-1111-1124",userName2,placeHolder,0,
                        0,placeHolder,placeHolder,Event.BAPTISM,year),
                new Event("1111-1111-1111-1125",userName3,placeHolder,0,
                        0,placeHolder,placeHolder,Event.BIRTH,year),
                new Event("1111-1111-1111-1126",userName3,placeHolder,0,
                        0,placeHolder,placeHolder,Event.MARRIAGE,year),
                new Event("1111-1111-1111-1127",userName3,placeHolder,0,
                        0,placeHolder,placeHolder,Event.DEATH,year),
                new Event("1111-1111-1111-1128",userName3,placeHolder,0,
                        0,placeHolder,placeHolder,Event.MILIT_DEP,year),
                new Event("1111-1111-1111-1129",userName3,placeHolder,0,
                        0,placeHolder,placeHolder,Event.SCHOOL_GRAD,year),
                new Event("1111-1111-1111-1120",userName3,placeHolder,0,
                        0,placeHolder,placeHolder,Event.CHRISTENING,year),
                new Event("1111-1111-1111-1131",userName3,placeHolder,0,
                        0,placeHolder,placeHolder,Event.BAPTISM,year)};

        List<Event> getUser1 = new ArrayList<>();
        for (Event checkEvent : events) {
            if (checkEvent.getUsername().equals(userName1)) getUser1.add(checkEvent);
        }
        user1Events = getUser1.toArray(new Event[getUser1.size()]);

        Database database = new Database();
        UserDao uDao = new UserDao(database.openConnection());
        PersonDao pDao = new PersonDao(database.getConnection());
        EventDao eDao = new EventDao(database.getConnection());
        AuthTokenDao tDao = new AuthTokenDao(database.getConnection());

        for (User user : users) uDao.insert(user);
        for (Person person : people) pDao.insert(person);
        for (Event event : events) eDao.insert(event);
        tDao.insert(new AuthToken(token1,userName1, DateTime.getDateTime()));
        tDao.insert(new AuthToken(token2,userName2, DateTime.getDateTime()));
        tDao.insert(new AuthToken(token3,userName3, DateTime.getDateTime()));

        database.closeConnection(true);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        dbSetUp();
    }

    @Test
    void eventSuccess() throws DataAccessException {
        EventRequest request = new EventRequest(events[1].getEventID(),token1);
        EventResult result = new EventService().event(request);
        checkSuccessResult(result);
    }

    @Test
    void eventWrongUser() {
        EventRequest request = new EventRequest(events[3].getEventID(),token2);
        EventResult result = new EventService().event(request);
        checkFailResult(result,Service.INV_INPUT_ERROR);

        request = new EventRequest(events[9].getEventID(),token1);
        result = new EventService().event(request);
        checkFailResult(result,Service.INV_INPUT_ERROR);
    }

    @Test
    void eventBadPersonID() {
        EventRequest request = new EventRequest("1111-2222-3333-4444",token1);
        EventResult result = new EventService().event(request);
        checkFailResult(result,Service.INV_INPUT_ERROR);
    }

    @Test
    void eventBadToken() {
        EventRequest request = new EventRequest(events[1].getEventID(),"4444-3333-2222-1111");
        EventResult result = new EventService().event(request);
        checkFailResult(result,Service.INV_TOKEN_ERROR);
    }

    @Test
    void eventNullToken() {
        EventRequest request = new EventRequest(events[1].getEventID(),"");
        EventResult result = new EventService().event(request);
        checkFailResult(result,Service.MISS_TOKEN_ERROR);
    }

    @Test
    void eventAllSuccess() throws DataAccessException {
        EventRequest request = new EventRequest("",token1);
        EventAllResult result = new EventService().event(request.getAuthToken());
        checkSuccessResult(result);
    }

    @Test
    void eventAllNoToken() {
        EventRequest request = new EventRequest("","4444-3333-2222-1111");
        EventAllResult result = new EventService().event(request.getAuthToken());
        checkFailResult(result,Service.INV_TOKEN_ERROR);
    }

    @Test
    void eventAllNullToken() {
        EventRequest request = new EventRequest("","");
        EventAllResult result = new EventService().event(request.getAuthToken());
        checkFailResult(result,Service.MISS_TOKEN_ERROR);
    }

    void checkSuccessResult(Result r) throws DataAccessException {
        assertNotNull(r,"Result was returned null");
        assertTrue(r.isSuccess(),"Service failed when it should have succeeded");

        Database database = new Database();
        if (r.getClass() == EventResult.class) {
            database.openConnection();
            Event event = new EventDao(database.getConnection()).find(((EventResult) r).getEventID());
            database.closeConnection(false);
            assertNotNull(event,"Result had an ID for a event not in the database");
            assertEquals(event.getUsername(),((EventResult) r).getAssociatedUsername(),
                    "Username of found event object did not match result's username");
            assertEquals(event.getLatitude(),((EventResult) r).getLatitude(),
                    "Latitude of found event object did not match result's latitude");
            assertEquals(event.getLongitude(),((EventResult) r).getLongitude(),
                    "Longitude of found event object did not match result's longitude");
            assertEquals(event.getCountry(),((EventResult) r).getCountry(),
                    "Country of found event object did not match result's country");
            assertEquals(event.getCity(),((EventResult) r).getCity(),
                    "City of found event object did not match result's city");
            assertEquals(event.getEventType(),((EventResult) r).getEventType(),
                    "Event type of found event object did not match result's event type");
            assertEquals(event.getYear(),((EventResult) r).getYear(),
                    "Year of found event object did not match result's year");

            boolean isFound = false;
            for (Event checkEvent : events) if (checkEvent.equals(event)) { isFound = true; break; }
            assertTrue(isFound,"Event relating to the result event was not in the database");
        }
        else if (r.getClass() == EventAllResult.class) {
            for (Event resultEvent : ((EventAllResult) r).getEvents()) {
                boolean isFound = false;
                for (Event checkEvent : user1Events) if (resultEvent.equals(checkEvent)) { isFound = true; break; }
                assertTrue(isFound,"Returned event was not found in database");
            }
            for (Event checkEvent : user1Events) {
                boolean isFound = false;
                for (Event resultEvent : ((EventAllResult) r).getEvents()) {
                    if (resultEvent.equals(checkEvent)) { isFound = true; break; }
                }
                assertTrue(isFound,"Event in database was not found in list of returned events");
            }
        }
        else {
            fail("Result object was of the wrong class");
        }
    }

    void checkFailResult(Result r,String expectedMessage) {
        assertNotNull(r,"Result came back null");
        assertFalse(r.isSuccess(),"Result reported success when it should have failed");

        if (r.getClass() == EventResult.class || r.getClass() == EventAllResult.class) {
            assertEquals(expectedMessage,r.getMessage(),"Error occurred for the wrong reason");
        }
        else {
            fail("Result object was of the wrong class");
        }
    }
}