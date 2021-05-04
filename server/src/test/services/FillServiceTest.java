package services;

import data_access.*;
import model.*;
import model.objData.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_result.request.FillRequest;
import request_result.result.FillResult;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class FillServiceTest {
    String userName1;
    String userID1;
    String userName2;
    String userID2;

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

        User user1 = new User(userName1,"pass1","email1",
                "Parker","King",'m',userID1);
        User user2 = new User(userName2,"pass2","email2",
                "Hannah","King",'f',userID2);

        Database database = new Database();
        UserDao uDao = new UserDao(database.openConnection());
        uDao.insert(user1);
        uDao.insert(user2);
        database.closeConnection(true);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        dbSetUp();
    }

    @Test
    void testFillZeroGens() throws DataAccessException {
        String fillUser = userName1;
        String fillUserID = userID1;
        int numGens = 0;
        FillRequest zeroGensReq = new FillRequest(fillUser,numGens);
        FillResult zeroGensRes = new FillService().fill(zeroGensReq);

        assertTrue(zeroGensRes.isSuccess(),"Service reported it failed " +
                "when it should have succeeded");
        List<ModelObject> createdPeople = getPeopleList(fillUser);
        List<ModelObject> createdEvents = getEventList(fillUser);

        baseServiceChecks(createdPeople,createdEvents,fillUser,fillUserID,numGens);
        noDataCheck(userName2);
    }

    @Test
    void testFillOneGens() throws DataAccessException {
        String fillUser = userName1;
        String fillUserID = userID1;
        int numGens = 1;
        FillRequest oneGensReq = new FillRequest(fillUser,numGens);
        FillResult oneGensRes = new FillService().fill(oneGensReq);

        assertTrue(oneGensRes.isSuccess(),"Service reported it failed " +
                "when it should have succeeded");
        List<ModelObject> createdPeople = getPeopleList(fillUser);
        List<ModelObject> createdEvents = getEventList(fillUser);

        baseServiceChecks(createdPeople,createdEvents,fillUser,fillUserID,numGens);
        beyondZeroGensChecks(createdPeople,createdEvents,fillUserID);
        noDataCheck(userName2);
    }

    @Test
    void testFillDefault() throws DataAccessException {
        String fillUser = userName1;
        String fillUserID = userID1;
        FillRequest request = new FillRequest(fillUser);
        FillResult result = new FillService().fill(request);

        assertTrue(result.isSuccess(),"Service reported it failed " +
                "when it should have succeeded");
        List<ModelObject> createdPeople = getPeopleList(fillUser);
        List<ModelObject> createdEvents = getEventList(fillUser);

        baseServiceChecks(createdPeople,createdEvents,fillUser,fillUserID,request.getNumGenerations());
        beyondZeroGensChecks(createdPeople,createdEvents,fillUserID);
        noDataCheck(userName2);
    }

    @Test
    void testFillWrongUser() throws DataAccessException {
        String fillUser = "not found";
        FillRequest request = new FillRequest(fillUser);
        FillResult result = new FillService().fill(request);

        assertFalse(result.isSuccess(),"Service reported successful even " +
                "though the user does not exist");
        assertTrue(result.getMessage().contains("Error: Bad Request"),
                "Service returned an error message for something other than bad input");

        noDataCheck(fillUser);
        noDataCheck(userName1);
        noDataCheck(userName2);
    }

    @Test
    void testFillDouble() throws DataAccessException {
        String fillUser = userName1;
        String fillUserID = userID1;
        FillRequest request = new FillRequest(fillUser);
        FillResult result = new FillService().fill(request);

        assertTrue(result.isSuccess(),"Service reported it failed " +
                "when it should have succeeded");

        result = new FillService().fill(request);

        assertTrue(result.isSuccess(),"Service reported it failed " +
                "when it should have succeeded");
        List<ModelObject> createdPeople = getPeopleList(fillUser);
        List<ModelObject> createdEvents = getEventList(fillUser);

        baseServiceChecks(createdPeople,createdEvents,fillUser,fillUserID,request.getNumGenerations());
        beyondZeroGensChecks(createdPeople,createdEvents,fillUserID);
        noDataCheck(userName2);
    }

    @Test
    void testFillMultiUsers() throws DataAccessException {
        int numGens1 = 4;
        FillRequest request1 = new FillRequest(userName1,numGens1);
        FillResult result1 = new FillService().fill(request1);

        int numGens2 = 3;
        FillRequest request2 = new FillRequest(userName2,numGens2);
        FillResult result2 = new FillService().fill(request2);

        assertTrue(result2.isSuccess(),"Service reported it failed " +
                "when it should have succeeded");
        List<ModelObject> createdPeople1 = getPeopleList(userName1);
        List<ModelObject> createdEvents1 = getEventList(userName1);

        baseServiceChecks(createdPeople1,createdEvents1,userName1,userID1,numGens1);
        beyondZeroGensChecks(createdPeople1,createdEvents1,userID1);

        assertTrue(result1.isSuccess(),"Service reported it failed " +
                "when it should have succeeded");
        List<ModelObject> createdPeople2 = getPeopleList(userName2);
        List<ModelObject> createdEvents2 = getEventList(userName2);

        baseServiceChecks(createdPeople2,createdEvents2,userName2,userID2,numGens2);
        beyondZeroGensChecks(createdPeople2,createdEvents2,userID2);
    }

    @Test
    void testFillManyGens() throws DataAccessException {
        String fillUser = userName1;
        String fillUserID = userID1;
        int numGens = 8;
        FillRequest request = new FillRequest(fillUser,numGens);
        FillResult result = new FillService().fill(request);

        assertTrue(result.isSuccess(),"Service reported it failed " +
                "when it should have succeeded");
        List<ModelObject> createdPeople = getPeopleList(fillUser);
        List<ModelObject> createdEvents = getEventList(fillUser);

        baseServiceChecks(createdPeople,createdEvents,fillUser,fillUserID,numGens);
        beyondZeroGensChecks(createdPeople,createdEvents,fillUserID);
        noDataCheck(userName2);
    }

    void baseServiceChecks(List<ModelObject> createdPeople,List<ModelObject> createdEvents, String userName,
                           String userID,int numGens) throws DataAccessException {
        int expectedNumPeople = (int) (Math.pow(2,numGens+1)-1);
        assertEquals(expectedNumPeople,createdPeople.size(),"Only " +
                expectedNumPeople + " Person objects expected for generating " +
                numGens + " generations");
        checkContainsUser(userName,userID,createdPeople);
        checkUserBirth(userID,createdEvents);
    }

    void beyondZeroGensChecks(List<ModelObject> createdPeople,List<ModelObject> createdEvents, String userID) {
        checkRequiredEvents(createdPeople,createdEvents,userID);
        checkParentChildDates(createdPeople,createdEvents);
        checkCoupleMarriageEvents(createdPeople,createdEvents);
    }

    void noDataCheck(String checkUser) throws DataAccessException {
        List<ModelObject> createdPeople = getPeopleList(checkUser);
        List<ModelObject> createdEvents = getEventList(checkUser);
        assertEquals(0,createdPeople.size(),"Found data. No data " +
                "should have been created for given user");
        assertEquals(0,createdEvents.size(),"Found data. No data " +
                "should have been created for given user");
    }

    List<ModelObject> getPeopleList(String username) throws DataAccessException {
        Database database = new Database();
        PersonDao pDao = new PersonDao(database.openConnection());
        List<ModelObject> createdPeople = pDao.getAll(username);
        database.closeConnection(false);
        return createdPeople;
    }

    List<ModelObject> getEventList(String username) throws DataAccessException {
        Database database = new Database();
        EventDao eDao = new EventDao(database.getConnection());
        List<ModelObject> createdEvents = eDao.getAll(username);
        database.closeConnection(false);
        return createdEvents;
    }

    void checkContainsUser(String username,String userID,List<ModelObject> createdPeople)
            throws DataAccessException {
        boolean containsUser = false;
        for (ModelObject object : createdPeople) {
            Person user = (Person) object;
            if (user.getPersonID().equals(userID)) {
                if (containsUser) {
                    fail("Found two Person objects containing user's personID");
                }

                Database database = new Database();
                UserDao uDao = new UserDao(database.openConnection());
                User fillUser = uDao.find(username);
                database.closeConnection(false);
                boolean dataMatch = user.getUsername().equals(username) &&
                        user.getFirstName().equals(fillUser.getFirstName())
                        && user.getLastName().equals(fillUser.getLastName())
                        && user.getGender()==fillUser.getGender();
                assertTrue(dataMatch,"Person with same personID as " +
                        "user was found, but info did not match");

                containsUser = true;
            }
        }
        assertTrue(containsUser,"Person object for the user was not found");
    }

    void checkUserBirth(String userID,List<ModelObject> createdEvents) {
        boolean hasBirth = false;
        for (ModelObject object : createdEvents) {
            Event birth = (Event) object;
            if (birth.getEventType().equals(Event.BIRTH) && birth.getPersonID().equals(userID)) {
                if (hasBirth) {
                    fail("User cannot have two birth events");
                }
                hasBirth = true;
            }
        }
        assertTrue(hasBirth,"No birth event was generated for user");
    }

    void checkRequiredEvents(List<ModelObject> createdPeople,List<ModelObject> createdEvents,String userID) {
        boolean hasBirth;
        int birthYear;
        boolean hasMarriage;
        int marriageYear;
        boolean hasDeath;
        int deathYear;
        for (ModelObject personObj : createdPeople) {
            Person person = (Person) personObj;
            if (person.getPersonID().equals(userID)) continue;
            hasBirth = false;
            birthYear = 0;
            hasMarriage = false;
            marriageYear = 0;
            hasDeath = false;
            deathYear = 0;
            for (ModelObject eventObj : createdEvents) {
                Event event = (Event) eventObj;
                if (event.getPersonID().equals(person.getPersonID())) {
                    switch (event.getEventType()) {
                        case Event.BIRTH:
                            if (hasBirth) {
                                fail("Person cannot have two birth events");
                            }
                            hasBirth = true;
                            birthYear = event.getYear();
                            break;
                        case Event.MARRIAGE:
                            if (hasMarriage) {
                                fail("Person cannot have two marriage events");
                            }
                            hasMarriage = true;
                            marriageYear = event.getYear();
                            break;
                        case Event.DEATH:
                            if (hasDeath) {
                                fail("Person cannot have two death events");
                            }
                            hasDeath = true;
                            deathYear = event.getYear();
                            break;
                    }
                }
            }
            assertTrue(hasBirth,"Person was not given a birth event");
            assertTrue(hasMarriage,"Person was not given a marriage event");
            assertTrue(hasDeath,"Person was not given a death event");
            assertTrue((birthYear+15)<=marriageYear,"Marriage event did not " +
                    "occur far enough away from birth");
            assertTrue(marriageYear<=deathYear,"Marriage event occurred " +
                    "after death event");
            assertTrue((deathYear-birthYear)<=120,"Birth and death years " +
                    "indicate person was too old");
            assertTrue(birthYear<deathYear,"Birth cannot occur after death");
        }
    }

    void checkCoupleMarriageEvents(List<ModelObject> createdPeople,List<ModelObject> createdEvents) {
        int husbandDate;
        Location husbandLocation;
        int wifeDate;
        Location wifeLocation;
        // Search for males who are not the user
        for (ModelObject husbObj : createdPeople) {
            Person husband = (Person) husbObj;
            husbandDate = 0;
            husbandLocation = null;
            wifeDate = 0;
            wifeLocation = null;
            // Check if person is male and married
            if (husband.getGender() == 'm' && !husband.getSpouseID().isBlank()) {
                String wifeID = husband.getSpouseID();
                // Search for wife of the male
                for (ModelObject wifeObj : createdPeople) {
                    Person wife = (Person) wifeObj;
                    // Checks if person is wife of found male
                    if (wife.getPersonID().equals(wifeID)) {
                        // Search for marriage info
                        for (ModelObject marryObj : createdEvents) {
                            Event marriage = (Event) marryObj;
                            // Check if the event is a marriage event
                            if (marriage.getEventType().equals(Event.MARRIAGE)) {
                                // Check if marriage is for found male or female
                                // Assign marriage info if it is
                                if (marriage.getPersonID().equals(husband.getPersonID())) {
                                    husbandDate = marriage.getYear();
                                    husbandLocation = new Location(marriage.getCountry(),
                                            marriage.getCity(),marriage.getLatitude(),
                                            marriage.getLongitude());
                                }
                                else if (marriage.getPersonID().equals(wife.getPersonID())) {
                                    wifeDate = marriage.getYear();
                                    wifeLocation = new Location(marriage.getCountry(),
                                            marriage.getCity(),marriage.getLatitude(),
                                            marriage.getLongitude());
                                }
                            }
                        }
                        // Now that marriage info is found, check, and move on to next male
                        //noinspection SimplifiableJUnitAssertion
                        assertTrue(husbandDate==wifeDate,"Marriage dates don't match");
                        assertNotNull(husbandLocation,"No location data was generated for the husband");
                        //noinspection SimplifiableJUnitAssertion
                        assertTrue(husbandLocation.equals(wifeLocation),"Marriage " +
                                "locations don't match");
                        break;
                    }
                }
            }
            // Found a wife with matching marriage year, continue searching People
        }
    }

    void checkParentChildDates(List<ModelObject> createdPeople, List<ModelObject> createdEvents) {
        int childBirth;
        int motherBirth;
        int motherDeath;
        int fatherBirth;
        int fatherDeath;
        // Check births of each person who has parents
        for (ModelObject childObj : createdPeople) {
            Person child = (Person) childObj;
            childBirth = 0;
            motherBirth = 0;
            motherDeath = 0;
            fatherBirth = 0;
            fatherDeath = 0;
            // Check if person has parents
            if (!child.getFatherID().isBlank() || !child.getMotherID().isBlank()) {
                String fatherID = child.getFatherID();
                String motherID = child.getMotherID();
                assertFalse(fatherID.isBlank(),"Child has a mother but no father");
                assertFalse(motherID.isBlank(),"Child has a father but no mother");
                // Search events for needed dates
                for (ModelObject eventObj : createdEvents) {
                    Event event = (Event) eventObj;
                    if (event.getPersonID().equals(child.getPersonID())) {
                        if (event.getEventType().equals(Event.BIRTH)) {
                            childBirth = event.getYear();
                        }
                    }
                    else if (event.getPersonID().equals(fatherID)) {
                        if (event.getEventType().equals(Event.BIRTH)) {
                            fatherBirth = event.getYear();
                        }
                        else if (event.getEventType().equals(Event.DEATH)) {
                            fatherDeath = event.getYear();
                        }
                    }
                    else if (event.getPersonID().equals(motherID)) {
                        if (event.getEventType().equals(Event.BIRTH)) {
                            motherBirth = event.getYear();
                        }
                        else if (event.getEventType().equals(Event.DEATH)) {
                            motherDeath = event.getYear();
                        }
                    }
                }
                // Check date relationships
                assertTrue((childBirth-fatherBirth)>13,"Father and child " +
                        "birth dates are too close");
                assertTrue((childBirth-motherBirth)>13,"Mother and child " +
                        "birth dates are too close");
                assertTrue(childBirth<=fatherDeath,"Father died before " +
                        "child was born");
                assertTrue(childBirth<=motherDeath,"Mother died before " +
                        "child was born");
                assertTrue(childBirth<=(motherBirth+50),"Mother was too " +
                        "old to give birth");
            }
        }
    }
}