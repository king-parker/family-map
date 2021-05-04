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
import request_result.request.PersonRequest;
import request_result.result.PersonAllResult;
import request_result.result.PersonResult;
import request_result.result.Result;
import utility.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonServiceTest {
    User[] users;
    Person[] people;
    Person[] user1People;
    Event[] events;
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
        people = new Person[]{new Person("1111-1111-1111-1111",userName1,placeHolder,placeHolder,'m',"1111-1111-1111-1112","1111-1111-1111-1113","1111-1111-1111-1114"),
                new Person("1111-1111-1111-1112",userName1,placeHolder,placeHolder,'m',"","1111-1111-1111-1111","1111-1111-1111-1113"),
                new Person("1111-1111-1111-1113",userName1,placeHolder,placeHolder,'f',"1111-1111-1111-1111","","1111-1111-1111-1112"),
                new Person("1111-1111-1111-1114",userName1,placeHolder,placeHolder,'f',"1111-1111-1111-1111","1111-1111-1111-1112",""),
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

        List<Person> getUser1 = new ArrayList<>();
        for (Person checkPerson : people) {
            if (checkPerson.getUsername().equals(userName1)) getUser1.add(checkPerson);
        }
        user1People = getUser1.toArray(new Person[getUser1.size()]);

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
    void personSuccessAllRelationships() throws DataAccessException {
        PersonRequest request = new PersonRequest(people[0].getPersonID(),token1);
        PersonResult result = new PersonService().person(request);
        checkSuccessResult(result);
    }

    @Test
    void personSuccessNoFather() throws DataAccessException {
        PersonRequest request = new PersonRequest(people[1].getPersonID(),token1);
        PersonResult result = new PersonService().person(request);
        checkSuccessResult(result);
    }

    @Test
    void personSuccessNoMother() throws DataAccessException {
        PersonRequest request = new PersonRequest(people[2].getPersonID(),token1);
        PersonResult result = new PersonService().person(request);
        checkSuccessResult(result);
    }

    @Test
    void personSuccessNoSpouse() throws DataAccessException {
        PersonRequest request = new PersonRequest(people[3].getPersonID(),token1);
        PersonResult result = new PersonService().person(request);
        checkSuccessResult(result);
    }

    @Test
    void personWrongUser() {
        PersonRequest request = new PersonRequest(people[3].getPersonID(),token2);
        PersonResult result = new PersonService().person(request);
        checkFailResult(result,Service.INV_INPUT_ERROR);

        request = new PersonRequest(people[6].getPersonID(),token1);
        result = new PersonService().person(request);
        checkFailResult(result,Service.INV_INPUT_ERROR);
    }

    @Test
    void personBadPersonID() {
        PersonRequest request = new PersonRequest("1111-2222-3333-4444",token1);
        PersonResult result = new PersonService().person(request);
        checkFailResult(result,Service.INV_INPUT_ERROR);
    }

    @Test
    void personBadToken() {
        PersonRequest request = new PersonRequest(people[1].getPersonID(),"4444-3333-2222-1111");
        PersonResult result = new PersonService().person(request);
        checkFailResult(result,Service.INV_TOKEN_ERROR);
    }

    @Test
    void personNullToken() {
        PersonRequest request = new PersonRequest(people[1].getPersonID(),"");
        PersonResult result = new PersonService().person(request);
        checkFailResult(result,Service.MISS_TOKEN_ERROR);
    }

    @Test
    void allPersonSuccess() throws DataAccessException {
        PersonRequest request = new PersonRequest("",token1);
        PersonAllResult result = new PersonService().person(request.getAuthToken());
        checkSuccessResult(result);
    }

    @Test
    void allPersonNoToken() {
        PersonRequest request = new PersonRequest("","4444-3333-2222-1111");
        PersonAllResult result = new PersonService().person(request.getAuthToken());
        checkFailResult(result,Service.INV_TOKEN_ERROR);
    }

    @Test
    void allPersonNullToken() {
        PersonRequest request = new PersonRequest("","");
        PersonAllResult result = new PersonService().person(request.getAuthToken());
        checkFailResult(result,Service.MISS_TOKEN_ERROR);
    }

    void checkSuccessResult(Result r) throws DataAccessException {
        assertNotNull(r,"Result was returned null");
        assertTrue(r.isSuccess(),"Service failed when it should have succeeded");

        Database database = new Database();
        if (r.getClass() == PersonResult.class) {
            database.openConnection();
            Person person = new PersonDao(database.getConnection()).find(((PersonResult) r).getPersonID());
            database.closeConnection(false);
            assertNotNull(person,"Result had an ID for a person not in the database");
            assertEquals(person.getUsername(),((PersonResult) r).getAssociatedUsername(),
                    "Username of found person object did not match result's username");
            assertEquals(person.getFirstName(),((PersonResult) r).getFirstName(),
                    "First name of found person object did not match result's first name");
            assertEquals(person.getLastName(),((PersonResult) r).getLastName(),
                    "Last name of found person object did not match result's last name");
            assertEquals(person.getGender(),((PersonResult) r).getGender(),
                    "Gender of found person object did not match result's gender");
            if (((PersonResult) r).getFatherID() != null) {
                assertEquals(person.getFatherID(),((PersonResult) r).getFatherID(),
                        "Father ID of found person object did not match result's father ID");
            } else {
                assertTrue(person.getFatherID().isBlank(),"Result listed no father ID when " +
                        "Person object has one");
            }
            if (((PersonResult) r).getMotherID() != null) {
                assertEquals(person.getMotherID(),((PersonResult) r).getMotherID(),
                        "Mother ID of found person object did not match result's mother ID");
            } else {
                assertTrue(person.getMotherID().isBlank(),"Result listed no mother ID when " +
                        "Person object has one");
            }
            if (((PersonResult) r).getSpouseID() != null) {
                assertEquals(person.getSpouseID(),((PersonResult) r).getSpouseID(),
                        "Spouse ID of found person object did not match result's spouse ID");
            } else {
                assertTrue(person.getSpouseID().isBlank(),"Result listed no spouse ID when " +
                        "Person object has one");
            }

            boolean isFound = false;
            for (Person checkPerson : people) if (checkPerson.equals(person)) { isFound = true; break; }
            assertTrue(isFound,"Person relating to the result person was not in the database");
        }
        else if (r.getClass() == PersonAllResult.class) {
            for (Person resultPerson : ((PersonAllResult) r).getPeople()) {
                boolean isFound = false;
                for (Person checkPerson : user1People) if (resultPerson.equals(checkPerson)) { isFound = true; break; }
                assertTrue(isFound,"Returned person was not found in database");
            }
            for (Person checkPerson : user1People) {
                boolean isFound = false;
                for (Person resultPerson : ((PersonAllResult) r).getPeople()) {
                    if (resultPerson.equals(checkPerson)) { isFound = true; break; }
                }
                assertTrue(isFound,"Person in database was not found in list of returned people");
            }
        }
        else {
            fail("Result object was of the wrong class");
        }
    }

    void checkFailResult(Result r,String expectedMessage) {
        assertNotNull(r,"Result came back null");
        assertFalse(r.isSuccess(),"Result reported success when it should have failed");

        if (r.getClass() == PersonResult.class || r.getClass() == PersonAllResult.class) {
            assertEquals(expectedMessage,r.getMessage(),"Error occurred for the wrong reason");
        }
        else {
            fail("Result object was of the wrong class");
        }
    }
}