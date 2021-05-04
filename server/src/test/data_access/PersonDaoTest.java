package data_access;

import model.ModelObject;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonDaoTest {
    Database database;
    PersonDao personDao;
    Person person1;
    Person person2;

    String user1ID2;
    String user1ID3;
    String user1ID4;
    String user1ID5;
    String user2ID2;
    String user2ID3;
    String user2ID4;
    String user2ID5;
    Person person1U2;
    Person person1U3;
    Person person1U4;
    Person person1U5;
    Person person2U2;
    Person person2U3;
    Person person2U4;
    Person person2U5;

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
        person1 = new Person("fake_ID","kingpark",
                "Parker","King",'m');
        person2 = new Person("FAKE_id","phoenix",
                "Parker","King",'m');
        Connection conn = database.openConnection();
        personDao = new PersonDao(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        database.closeConnection(false);
    }

    @Test
    void testInsertSuccess() throws DataAccessException {
        personDao.insert(person1);
        Person comparePerson = personDao.find(person1.getPersonID());
        assertNotNull(comparePerson,"No Person object was returned from database");
        assertEquals(person1,comparePerson,"Person was not the same after it was retrieved");

        personDao.insert(person2);
        comparePerson = personDao.find(person2.getPersonID());
        assertNotNull(comparePerson,"No Person object was returned from database");
        assertEquals(person2,comparePerson,"Person was not the same after it was retrieved");

        comparePerson = personDao.find(person1.getPersonID());
        assertNotNull(comparePerson,"No Person object was returned from database");
        assertEquals(person1,comparePerson,"Person was not the same after it was retrieved");
    }

    @Test
    void testInsertFail() throws DataAccessException {
        Person dupID = new Person(person1.getPersonID(),person2.getUsername(),
                person2.getFirstName(),person2.getLastName(),person2.getGender());

        personDao.insert(person1);
        assertThrows(DataAccessException.class,()-> personDao.insert(person1),"Double insert" +
                " succeeded when it shouldn't because unique person IDs are required");
        assertThrows(DataAccessException.class,()-> personDao.insert(dupID),"Double insert" +
                " succeeded when it shouldn't because unique person IDs are required");
    }

    @Test
    void testFindSuccess() throws DataAccessException {
        personDao.insert(person1);
        personDao.insert(person2);
        Person comparePerson = personDao.find(person1.getPersonID());
        assertNotNull(comparePerson,"No Person object was returned from database");
        assertEquals(person1,comparePerson,"Person was not the same after it was retrieved");
        comparePerson = personDao.find(person2.getPersonID());
        assertNotNull(comparePerson,"No Person object was returned from database");
        assertEquals(person2,comparePerson,"Person was not the same after it was retrieved");
    }

    @Test
    void testFindFail() throws DataAccessException {
        assertNull(personDao.find(person1.getPersonID()), "A person was found when " +
                "no people were in the database");
        personDao.insert(person1);
        assertNull(personDao.find(person2.getPersonID()), "A person was found with a " +
                "person ID that was not in the database");
    }

    @Test
    void testGetAllOneUser() throws DataAccessException {
        prepExtraPeople();
        List<ModelObject> getResults = personDao.getAll(person1.getUsername());
        assertEquals(0,getResults.size(),
                "No Person objects should be returned");

        personDao.insert(person1);
        getResults = personDao.getAll(person1.getUsername());
        assertEquals(1,getResults.size(), "Only one Person " +
                "object should be returned");
        checkGetAllResults(getResults,person1);

        personDao.insert(person1U2);
        getResults = personDao.getAll(person1.getUsername());
        assertEquals(2,getResults.size(), "Two Person " +
                "objects should be returned");
        checkGetAllResults(getResults,person1);
        checkGetAllResults(getResults,person1U2);

        personDao.insert(person1U3);
        personDao.insert(person1U4);
        personDao.insert(person1U5);
        getResults = personDao.getAll(person1.getUsername());
        assertEquals(5,getResults.size(), "Five Person " +
                "objects should be returned");
        checkGetAllResults(getResults,person1);
        checkGetAllResults(getResults,person1U2);
        checkGetAllResults(getResults,person1U3);
        checkGetAllResults(getResults,person1U4);
        checkGetAllResults(getResults,person1U5);
    }

    @Test
    void testGetAllTwoUsers() throws DataAccessException {
        personDao.insert(person1);
        personDao.insert(person2);
        List<ModelObject> getResults = personDao.getAll(person1.getUsername());
        assertEquals(1,getResults.size(), "Only one Person " +
                "object should be returned");
        checkGetAllResults(getResults,person1);
        getResults = personDao.getAll(person2.getUsername());
        assertEquals(1,getResults.size(), "Only one Person " +
                "object should be returned");
        checkGetAllResults(getResults,person2);

        personDao.clearTable();
        prepExtraPeople();
        insertAll();

        getResults = personDao.getAll(person1.getUsername());
        assertEquals(5,getResults.size(), "Only five Person " +
                "objects should be returned");
        checkGetAllResults(getResults,person1);
        checkGetAllResults(getResults,person1U2);
        checkGetAllResults(getResults,person1U3);
        checkGetAllResults(getResults,person1U4);
        checkGetAllResults(getResults,person1U5);

        getResults = personDao.getAll(person2.getUsername());
        assertEquals(5,getResults.size(), "Only five Person " +
                "objects should be returned");
        checkGetAllResults(getResults,person2);
        checkGetAllResults(getResults,person2U2);
        checkGetAllResults(getResults,person2U3);
        checkGetAllResults(getResults,person2U4);
        checkGetAllResults(getResults,person2U5);
    }

    @Test
    void testAvailableIDPass() throws DataAccessException {
        assertTrue(personDao.availableID(person1.getPersonID()),
                "Stated ID was not available when database was empty");
        assertTrue(personDao.availableID(person2.getPersonID()),
                "Stated ID was not available when database was empty");

        personDao.insert(person1);
        assertTrue(personDao.availableID(person2.getPersonID()),
                "ID was incorrectly labeled as not available");
        personDao.insert(person2);
        assertTrue(personDao.availableID("newName"),"ID " +
                "was incorrectly labeled as not available");
    }

    @Test
    void testAvailableIDFail() throws DataAccessException {
        personDao.insert(person1);
        assertFalse(personDao.availableID(person1.getPersonID()),
                "ID was incorrectly labeled as available");
        assertTrue(personDao.availableID(person2.getPersonID()),
                "ID was incorrectly labeled as not available");

        personDao.insert(person2);
        assertFalse(personDao.availableID(person2.getPersonID()),
                "ID was incorrectly labeled as available " +
                        "now that it has been used");
        assertFalse(personDao.availableID(person1.getPersonID()),
                "ID was incorrectly labeled as available " +
                        "after declared not available earlier");
    }

    @Test
    void TestClearUserDataOnePerson() throws DataAccessException {
        personDao.insert(person1);
        assertEquals(person1,personDao.find(person1.getPersonID()),
                "Person was not found, clear function can't be confirmed");
        try {
            personDao.clearUserData(person1.getUsername());
        }
        catch (Exception e){
            e.printStackTrace();
            fail("An error occurred while clearing user data");
        }
        assertNull(personDao.find(person1.getPersonID()),
                "A person object was found when none should be in database");

        personDao.insert(person1);
        personDao.insert(person2);
        try {
            personDao.clearUserData(person2.getUsername());
        }
        catch (Exception e){
            e.printStackTrace();
            fail("An error occurred while clearing user data");
        }
        assertEquals(person1,personDao.find(person1.getPersonID()),
                "Person object unrelated to username used to clear was missing or changed");
        assertNull(personDao.find(person2.getPersonID()),
                "A person object was found when it should have been cleared from database");
    }

    @Test
    void TestClearUserDataManyPeople() throws DataAccessException {
        prepExtraPeople();
        insertAll();
        assertEquals(person1U5,personDao.find(person1U5.getPersonID()),
                "Data was not inserted correctly. Can't validate test");
        assertEquals(person2U3,personDao.find(person2U3.getPersonID()),
                "Data was not inserted correctly. Can't validate test");

        personDao.clearUserData(person1.getUsername());
        assertNull(personDao.find(person1.getPersonID()),
                "User data was found when it should have been cleared");
        assertEquals(0,personDao.getAll(person1.getUsername()).size(),
                "Person data for username is still found in database");

        personDao.clearUserData(person2.getUsername());
        assertNull(personDao.find(person2.getPersonID()),
                "User data was found when it should have been cleared");
        assertEquals(0,personDao.getAll(person2.getUsername()).size(),
                "Person data for username is still found in database");
    }

    @Test
    void testClearTable() throws DataAccessException {
        personDao.insert(person1);
        Person comparePerson = personDao.find(person1.getPersonID());
        assertEquals(person1,comparePerson,"Person was not found, clear function can't be confirmed");
        personDao.clearTable();
        assertNull(personDao.find(person1.getPersonID()),"Person data was not properly cleared");
    }

    @Test
    void testClearNoData() {
        assertDoesNotThrow(()->personDao.clearTable(),"Error occurred when clearing data when non exists");
    }

    void prepExtraPeople() {
        user1ID2 = "abcd-1234-ef56-7890";
        user1ID3 = "1234-abcd-ef56-7890";
        user1ID4 = "ef56-1234-abcd-7890";
        user1ID5 = "7890-1234-ef56-abcd";
        user2ID2 = "ab12-c345-d678-ef90";
        user2ID3 = "c345-ab12-d678-ef90";
        user2ID4 = "d678-c345-ab12-ef90";
        user2ID5 = "ef90-c345-d678-ab12";

        person1U2 = new Person(user1ID2,person1.getUsername(),
                "Billy","Joel", 'm');
        person1U3 = new Person(user1ID3,person1.getUsername(),
                "Martha","Stewart", 'f');
        person1U4 = new Person(user1ID4,person1.getUsername(),
                "Freddy","Mercury", 'm');
        person1U5 = new Person(user1ID5,person1.getUsername(),
                "Betty","White", 'f');
        person2U2 = new Person(user2ID2,person2.getUsername(),
                "Abraham","Lincoln", 'm');
        person2U3 = new Person(user2ID3,person2.getUsername(),
                "Rosa","Parks", 'f');
        person2U4 = new Person(user2ID4,person2.getUsername(),
                "Arnold","Schwarzenegger", 'm');
        person2U5 = new Person(user2ID5,person2.getUsername(),
                "Jennifer","Lopez", 'f');
    }

    private void insertAll() throws DataAccessException {
        personDao.insert(person1);
        personDao.insert(person1U2);
        personDao.insert(person1U3);
        personDao.insert(person1U4);
        personDao.insert(person1U5);
        personDao.insert(person2);
        personDao.insert(person2U2);
        personDao.insert(person2U3);
        personDao.insert(person2U4);
        personDao.insert(person2U5);
    }

    void checkGetAllResults(List<ModelObject> getResults,Person expectedPerson) {
        Person comparePerson = null;
        for (ModelObject object : getResults) {
            Person person = (Person) object;
            if(person.getPersonID().equals(expectedPerson.getPersonID())) {
                comparePerson = person;
                break;
            }
        }
        assertNotNull(comparePerson,"Person was not found in " +
                "results returned by getAll method");
        assertEquals(comparePerson,expectedPerson,"Correct person object " +
                "was not found in returned data");
    }
}