package services;

import data_access.*;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_result.request.LoadRequest;
import request_result.result.LoadResult;

import static org.junit.jupiter.api.Assertions.*;

class LoadServiceTest {
    User[] users;
    Person[] people;
    Event[] events;
    String userName1;
    String userID1;
    String userName2;
    String userID2;
    String userName3;
    String userID3;
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
        userName2 = "phoenix";
        userID2 = "cdef-5678-90ab-1234";
        userName3 = "pokey";
        userID3 = "159c-260d-37ae-48bf";
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
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        dbSetUp();
    }

    @Test
    void emptyRequest() throws DataAccessException {
        users = new User[]{};
        people = new Person[]{};
        events = new  Event[]{};

        checkResultStatus(runService(),true,"Success");
    }

    @Test
    void regularLoad() throws DataAccessException {
        checkResultStatus(runService(),true,"Success");
        checkDatabase();
    }

    @Test
    void oneArrayEmpty() throws DataAccessException {
        people = new Person[]{};

        checkResultStatus(runService(),true,"Success");
        checkDatabase();
    }

    @Test
    void oneArrayNull() throws DataAccessException {
        events = null;

        checkResultStatus(runService(),false,Service.MISS_INPUT_ERROR);
    }

    @Test
    void dupUserData() throws DataAccessException {
        users[2] = users[0];
        checkDupTest();
    }

    @Test
    void dupPersonData() throws DataAccessException {
        people[4] = people[1];
        checkDupTest();
    }

    @Test
    void dupEventData() throws DataAccessException {
        events[4] = events[1];
        checkDupTest();
    }

    LoadResult runService(){
        return new LoadService().load(new LoadRequest(users,people,events));
    }

    void checkResultStatus(LoadResult result,boolean isSuccess,String resultMessage) {
        assertNotNull(result,"Result was returned null");
        if (resultMessage.equals("Success")) {
            LoadRequest r = new LoadRequest(users,people,events);
            resultMessage = "Successfully added " + r.getUsers().length + " users, " +
                    r.getPeople().length + " persons, and " + r.getEvents().length + " events to the database.";
        }
        if (isSuccess) {
            assertTrue(result.isSuccess(),"Result reported failure when it should have succeeded");
            assertEquals(resultMessage, result.getMessage(),
                    "Incorrect success message");
        } else {
            assertFalse(result.isSuccess(),"Result reported success when it should have failed");
            assertEquals(resultMessage, result.getMessage(),
                    "Incorrect Error caused service to fail");
        }
    }

    void checkDatabase() throws DataAccessException {
        Database database = new Database();
        database.openConnection();

        for (User user : users) {
            User foundUser = new UserDao(database.getConnection()).find(user.getUsername());
            assertNotNull(foundUser,"User data was not inserted correctly. A user is missing");
            assertEquals(user,foundUser,"User data was not inserted correctly. Wrong user data found");
        }
        for (Person person : people) {
            Person foundPerson = new PersonDao(database.getConnection()).find(person.getPersonID());
            assertNotNull(foundPerson,"Person data was not inserted correctly. A person is missing");
            assertEquals(person,foundPerson,"Person data was not inserted correctly. Wrong person data found");
        }
        for (Event event : events) {
            Event foundEvent = new EventDao(database.getConnection()).find(event.getEventID());
            assertNotNull(foundEvent,"Event data was not inserted correctly. An event is missing");
            assertEquals(event,foundEvent,"Event data was not inserted correctly. Wrong event data found");
        }

        database.closeConnection(false);
    }

    void checkDupTest() {
        LoadResult result = runService();
        checkResultStatus(result,false,Service.INV_INPUT_ERROR);
        assertEquals(Service.INV_INPUT_ERROR,result.getMessage(),"Incorrect Error caused service to fail");
    }
}