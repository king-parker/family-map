package data_access;

import model.Event;
import model.ModelObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventDaoTest {
    Database database;
    EventDao eventDao;
    Event event1;
    Event event2;

    String user1ID2;
    String user1ID3;
    String user1ID4;
    String user1ID5;
    String user2ID2;
    String user2ID3;
    String user2ID4;
    String user2ID5;
    Event event1U2;
    Event event1U3;
    Event event1U4;
    Event event1U5;
    Event event2U2;
    Event event2U3;
    Event event2U4;
    Event event2U5;

    @BeforeAll
    static void clearDB() throws DataAccessException {
        Database database = new Database();
        database.openConnection();
        database.clearTables();
        database.closeConnection(true);
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        database = new Database();
        event1 = new Event("1234-5678-90ab-cdef","kingpark",
                "fake_id",40.24f,111.653f,
                "United States","Provo",Event.SCHOOL_GRAD,2021);
        event2 = new Event("cdef-5678-90ab-1234","phoenix",
                "FAKE_ID",40.24f,111.653f,
                "United States","Provo",Event.MARRIAGE,2017);
        Connection conn = database.openConnection();
        eventDao = new EventDao(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        database.closeConnection(false);
    }

    @Test
    void testInsertSuccess() throws DataAccessException {
        eventDao.insert(event1);
        Event compareEvent = eventDao.find(event1.getEventID());
        assertNotNull(compareEvent,"No Event object was returned from database");
        assertEquals(event1,compareEvent,"Event was not the same after it was retrieved");

        eventDao.insert(event2);
        compareEvent = eventDao.find(event2.getEventID());
        assertNotNull(compareEvent,"No Event object was returned from database");
        assertEquals(event2,compareEvent,"Event was not the same after it was retrieved");

        compareEvent = eventDao.find(event1.getEventID());
        assertNotNull(compareEvent,"No Event object was returned from database");
        assertEquals(event1,compareEvent,"Event was not the same after it was retrieved");
    }

    @Test
    void testInsertFail() throws DataAccessException {
        Event dupID = new Event(event1.getEventID(),event2.getUsername(),
                event2.getPersonID(),event2.getLatitude(),event2.getLongitude(),
                event2.getCountry(),event2.getCity(),event2.getEventType(),event2.getYear());

        eventDao.insert(event1);
        assertThrows(DataAccessException.class,()-> eventDao.insert(event1),"Double insert" +
                " succeeded when it shouldn't because unique event IDs are required");
        assertThrows(DataAccessException.class,()-> eventDao.insert(dupID),"Double insert" +
                " succeeded when it shouldn't because unique event IDs are required");
    }

    @Test
    void testFindSuccess() throws DataAccessException {
        eventDao.insert(event1);
        eventDao.insert(event2);
        Event compareEvent = eventDao.find(event1.getEventID());
        assertNotNull(compareEvent,"No Event object was returned from database");
        assertEquals(event1,compareEvent,"Event was not the same after it was retrieved");
        compareEvent = eventDao.find(event2.getEventID());
        assertNotNull(compareEvent,"No Event object was returned from database");
        assertEquals(event2,compareEvent,"Event was not the same after it was retrieved");
    }

    @Test
    void testFindFail() throws DataAccessException {
        assertNull(eventDao.find(event1.getEventID()), "An event was found when " +
                "no events were in the database");
        eventDao.insert(event1);
        assertNull(eventDao.find(event2.getEventID()), "An event was found with an " +
                "event ID that was not in the database");
    }

    @Test
    void testGetAllOneUser() throws DataAccessException {
        prepExtraEvents();
        List<ModelObject> getResults = eventDao.getAll(event1.getUsername());
        assertEquals(0,getResults.size(),
                "No Event objects should be returned");

        eventDao.insert(event1);
        getResults = eventDao.getAll(event1.getUsername());
        assertEquals(1,getResults.size(), "Only one Event " +
                "object should be returned");
        checkGetAllResults(getResults,event1);

        eventDao.insert(event1U2);
        getResults = eventDao.getAll(event1.getUsername());
        assertEquals(2,getResults.size(), "Two Event " +
                "objects should be returned");
        checkGetAllResults(getResults,event1);
        checkGetAllResults(getResults,event1U2);

        eventDao.insert(event1U3);
        eventDao.insert(event1U4);
        eventDao.insert(event1U5);
        getResults = eventDao.getAll(event1.getUsername());
        assertEquals(5,getResults.size(), "Five Event " +
                "objects should be returned");
        checkGetAllResults(getResults,event1);
        checkGetAllResults(getResults,event1U2);
        checkGetAllResults(getResults,event1U3);
        checkGetAllResults(getResults,event1U4);
        checkGetAllResults(getResults,event1U5);
    }

    @Test
    void testGetAllTwoUsers() throws DataAccessException {
        eventDao.insert(event1);
        eventDao.insert(event2);
        List<ModelObject> getResults = eventDao.getAll(event1.getUsername());
        assertEquals(1,getResults.size(), "Only one Event " +
                "object should be returned");
        checkGetAllResults(getResults,event1);
        getResults = eventDao.getAll(event2.getUsername());
        assertEquals(1,getResults.size(), "Only one Event " +
                "object should be returned");
        checkGetAllResults(getResults,event2);

        eventDao.clearTable();
        prepExtraEvents();
        insertAll();

        getResults = eventDao.getAll(event1.getUsername());
        assertEquals(5,getResults.size(), "Only five Event " +
                "objects should be returned");
        checkGetAllResults(getResults,event1);
        checkGetAllResults(getResults,event1U2);
        checkGetAllResults(getResults,event1U3);
        checkGetAllResults(getResults,event1U4);
        checkGetAllResults(getResults,event1U5);

        getResults = eventDao.getAll(event2.getUsername());
        assertEquals(5,getResults.size(), "Only five Event " +
                "objects should be returned");
        checkGetAllResults(getResults,event2);
        checkGetAllResults(getResults,event2U2);
        checkGetAllResults(getResults,event2U3);
        checkGetAllResults(getResults,event2U4);
        checkGetAllResults(getResults,event2U5);
    }

    @Test
    void testAvailableIDPass() throws DataAccessException {
        assertTrue(eventDao.availableID(event1.getEventID()),
                "Stated ID was not available when database was empty");
        assertTrue(eventDao.availableID(event2.getEventID()),
                "Stated ID was not available when database was empty");

        eventDao.insert(event1);
        assertTrue(eventDao.availableID(event2.getEventID()),
                "ID was incorrectly labeled as not available");
        eventDao.insert(event2);
        assertTrue(eventDao.availableID("newName"),"ID " +
                "was incorrectly labeled as not available");
    }

    @Test
    void testAvailableIDFail() throws DataAccessException {
        eventDao.insert(event1);
        assertFalse(eventDao.availableID(event1.getEventID()),
                "ID was incorrectly labeled as available");
        assertTrue(eventDao.availableID(event2.getEventID()),
                "ID was incorrectly labeled as not available");

        eventDao.insert(event2);
        assertFalse(eventDao.availableID(event2.getEventID()),
                "ID was incorrectly labeled as available " +
                        "now that it has been used");
        assertFalse(eventDao.availableID(event1.getEventID()),
                "ID was incorrectly labeled as available " +
                        "after declared not available earlier");
    }

    @Test
    void TestClearUserDataOnePerson() throws DataAccessException {
        eventDao.insert(event1);
        assertEquals(event1,eventDao.find(event1.getEventID()),
                "Event was not found, clear function can't be confirmed");
        try {
            eventDao.clearUserData(event1.getUsername());
        }
        catch (Exception e){
            e.printStackTrace();
            fail("An error occurred while clearing user data");
        }
        assertNull(eventDao.find(event1.getEventID()),
                "An Event object was found when none should be in database");

        eventDao.insert(event1);
        eventDao.insert(event2);
        try {
            eventDao.clearUserData(event2.getUsername());
        }
        catch (Exception e){
            e.printStackTrace();
            fail("An error occurred while clearing user data");
        }
        assertEquals(event1,eventDao.find(event1.getEventID()),
                "Event object unrelated to username used to clear was missing or changed");
        assertNull(eventDao.find(event2.getEventID()),
                "An Event object was found when it should have been cleared from database");
    }

    @Test
    void TestClearUserDataManyPeople() throws DataAccessException {
        prepExtraEvents();
        insertAll();
        assertEquals(event1U5,eventDao.find(event1U5.getEventID()),
                "Data was not inserted correctly. Can't validate test");
        assertEquals(event2U3,eventDao.find(event2U3.getEventID()),
                "Data was not inserted correctly. Can't validate test");

        eventDao.clearUserData(event1.getUsername());
        assertNull(eventDao.find(event1.getEventID()),
                "User data was found when it should have been cleared");
        assertEquals(0,eventDao.getAll(event1.getUsername()).size(),
                "Event data for username is still found in database");

        eventDao.clearUserData(event2.getUsername());
        assertNull(eventDao.find(event2.getEventID()),
                "User data was found when it should have been cleared");
        assertEquals(0,eventDao.getAll(event2.getUsername()).size(),
                "Event data for username is still found in database");
    }

    @Test
    void testClearTable() throws DataAccessException {
        eventDao.insert(event1);
        Event compareEvent = eventDao.find(event1.getEventID());
        assertEquals(event1,compareEvent,"Event was not found, clear function can't be confirmed");
        eventDao.clearTable();
        assertNull(eventDao.find(event1.getEventID()),"Event data was not properly cleared");
    }

    @Test
    void testClearNoData() {
        assertDoesNotThrow(()->eventDao.clearTable(),"Error occurred when clearing data when non exists");
    }

    void prepExtraEvents() {
        user1ID2 = "abcd-1234-ef56-7890";
        user1ID3 = "1234-abcd-ef56-7890";
        user1ID4 = "ef56-1234-abcd-7890";
        user1ID5 = "7890-1234-ef56-abcd";
        user2ID2 = "ab12-c345-d678-ef90";
        user2ID3 = "c345-ab12-d678-ef90";
        user2ID4 = "d678-c345-ab12-ef90";
        user2ID5 = "ef90-c345-d678-ab12";

        event1U2 = new Event(user1ID2,event1.getUsername(),
                event1.getPersonID(),event1.getLatitude(),event1.getLongitude(),
                event1.getCountry(),event1.getCity(),Event.BIRTH,1994);
        event1U3 = new Event(user1ID3,event1.getUsername(),
                event1.getPersonID(),event1.getLatitude(),event1.getLongitude(),
                event1.getCountry(),event1.getCity(),Event.BIRTH,1994);
        event1U4 = new Event(user1ID4,event1.getUsername(),
                event1.getPersonID(),event1.getLatitude(),event1.getLongitude(),
                event1.getCountry(),event1.getCity(),Event.BIRTH,1994);
        event1U5 = new Event(user1ID5,event1.getUsername(),
                event1.getPersonID(),event1.getLatitude(),event1.getLongitude(),
                event1.getCountry(),event1.getCity(),Event.BIRTH,1994);
        event2U2 = new Event(user2ID2,event2.getUsername(),
                event2.getPersonID(),event2.getLatitude(),event2.getLongitude(),
                event2.getCountry(),event2.getCity(),Event.BIRTH,1994);
        event2U3 = new Event(user2ID3,event2.getUsername(),
                event2.getPersonID(),event2.getLatitude(),event2.getLongitude(),
                event2.getCountry(),event2.getCity(),Event.BIRTH,1994);
        event2U4 = new Event(user2ID4,event2.getUsername(),
                event2.getPersonID(),event2.getLatitude(),event2.getLongitude(),
                event2.getCountry(),event2.getCity(),Event.BIRTH,1994);
        event2U5 = new Event(user2ID5,event2.getUsername(),
                event2.getPersonID(),event2.getLatitude(),event2.getLongitude(),
                event2.getCountry(),event2.getCity(),Event.BIRTH,1994);
    }

    private void insertAll() throws DataAccessException {
        eventDao.insert(event1);
        eventDao.insert(event1U2);
        eventDao.insert(event1U3);
        eventDao.insert(event1U4);
        eventDao.insert(event1U5);
        eventDao.insert(event2);
        eventDao.insert(event2U2);
        eventDao.insert(event2U3);
        eventDao.insert(event2U4);
        eventDao.insert(event2U5);
    }

    void checkGetAllResults(List<ModelObject> getResults, Event expectedEvent) {
        Event compareEvent = null;
        for (ModelObject object : getResults) {
            Event event = (Event) object;
            if(event.getEventID().equals(expectedEvent.getEventID())) {
                compareEvent = event;
                break;
            }
        }
        assertNotNull(compareEvent,"Event was not found in " +
                "results returned by getAll method");
        assertEquals(compareEvent,expectedEvent,"Correct event object " +
                "was not found in returned data");
    }
}