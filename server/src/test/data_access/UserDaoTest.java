package data_access;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {
    Database database;
    UserDao userDao;
    User user1;
    User user2;

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
        user1 = new User("kingpark","fakePass",
                "kingpark@test.com","Parker",
                "King",'m',"fake_ID");
        user2 = new User("phoenix","anotherPass",
                "second@test.com","Parker",
                "King",'m',"FAKE_id");
        Connection conn = database.openConnection();
        userDao = new UserDao(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        database.closeConnection(false);
    }

    @Test
    void testInsertSuccess() throws DataAccessException {
        userDao.insert(user1);
        User compareUser = userDao.find(user1.getUsername());
        assertNotNull(compareUser,"No User object was returned from database");
        assertEquals(user1,compareUser,"User was not the same after it was retrieved");

        userDao.insert(user2);
        compareUser = userDao.find(user2.getUsername());
        assertNotNull(compareUser,"No User object was returned from database");
        assertEquals(user2,compareUser,"User was not the same after it was retrieved");

        compareUser = userDao.find(user1.getUsername());
        assertNotNull(compareUser,"No User object was returned from database");
        assertEquals(user1,compareUser,"User was not the same after it was retrieved");
    }

    @Test
    void testInsertFail() throws DataAccessException {
        User dupUserName = new User(user1.getUsername(),user2.getPassword(),user2.getEmail(),
                user2.getFirstName(),user2.getLastName(),user2.getGender(),user2.getPersonID());
        User dupEmail = new User(user2.getUsername(),user2.getPassword(),user1.getEmail(),
                user2.getFirstName(),user2.getLastName(),user2.getGender(),user2.getPersonID());
        User dupID = new User(user2.getUsername(),user2.getPassword(),user2.getEmail(),
                user2.getFirstName(),user2.getLastName(),user2.getGender(),user1.getPersonID());

        userDao.insert(user1);
        assertThrows(DataAccessException.class,()->userDao.insert(user1),"Double insert" +
                " of same user succeeded when it shouldn't");
        assertThrows(DataAccessException.class,()->userDao.insert(dupUserName),"Double insert" +
                " succeeded when it shouldn't because unique usernames are required");
        assertThrows(DataAccessException.class,()->userDao.insert(dupEmail),"Double insert" +
                " succeeded when it shouldn't because unique emails are required");
        assertThrows(DataAccessException.class,()->userDao.insert(dupID),"Double insert" +
                " succeeded when it shouldn't because unique personIDS are required");
    }

    @Test
    void testFindSuccess() throws DataAccessException {
        userDao.insert(user1);
        userDao.insert(user2);
        User compareUser = userDao.find(user1.getUsername());
        assertNotNull(compareUser,"No User object was returned from database");
        assertEquals(user1,compareUser,"User was not the same after it was retrieved");
        compareUser = userDao.find(user2.getUsername());
        assertNotNull(compareUser,"No User object was returned from database");
        assertEquals(user2,compareUser,"User was not the same after it was retrieved");
    }

    @Test
    void testFindFail() throws DataAccessException {
        assertNull(userDao.find(user1.getUsername()), "A user was found when no" +
                " users were in the database");
        userDao.insert(user1);
        assertNull(userDao.find(user2.getUsername()), "A user was found with a " +
                "username that was not in the database");
    }

    @Test
    void testLoginValidatePass() throws DataAccessException{
        userDao.insert(user1);
        assertFalse(userDao.loginValidate(user2.getUsername(),user2.getPassword()),
                "Accepted credentials for a user that doesn't exist");
        assertTrue(userDao.loginValidate(user1.getUsername(),user1.getPassword()),
                "Failed validate the user correctly");

        userDao.insert(user2);
        assertTrue(userDao.loginValidate(user2.getUsername(),user2.getPassword()),
                "Failed validate the user correctly");
        assertTrue(userDao.loginValidate(user1.getUsername(),user1.getPassword()),
                "Failed validate the user correctly after another user was added");

        User samePass = new User("dupPass",user1.getPassword(),
                "dupPass@test.com",user1.getFirstName(),user1.getLastName(),
                'm',"dupPass_id");
        assertFalse(userDao.loginValidate(samePass.getUsername(),samePass.getPassword()),
                "Validated new user that had not been put in database");
        userDao.insert(samePass);
        assertEquals(user1.getPassword(),userDao.find(samePass.getUsername()).getPassword(),
                "Passwords were not the same, could not execute test with duplicate passwords");
        assertTrue(userDao.loginValidate(samePass.getUsername(),samePass.getPassword()),
                "Failed to validate user with same password as another user");
    }

    @Test
    void testLoginValidateFail() throws DataAccessException{
        assertFalse(userDao.loginValidate(user1.getUsername(),user1.getPassword()),
                "Validated the user info when no info was present in the database");
        userDao.insert(user1);
        userDao.insert(user2);
        assertFalse(userDao.loginValidate(user1.getUsername(),user2.getPassword()),
                "Validated user info when info from two users was presented");
        assertFalse(userDao.loginValidate(user2.getUsername(),user1.getPassword()),
                "Validated user info when info from two users was presented");
    }

    @Test
    void testAvailableUserNamePass() throws DataAccessException{
        assertTrue(userDao.availableUserName(user1.getUsername()),
                "Stated username was not available when database was empty");
        assertTrue(userDao.availableUserName(user2.getUsername()),
                "Stated username was not available when database was empty");

        userDao.insert(user1);
        assertTrue(userDao.availableUserName(user2.getUsername()),
                "Username was incorrectly labeled as not available");
        userDao.insert(user2);
        assertTrue(userDao.availableUserName("newName"),"Username " +
                "was incorrectly labeled as not available");
    }

    @Test
    void testAvailableUserNameFail() throws DataAccessException{
        userDao.insert(user1);
        assertFalse(userDao.availableUserName(user1.getUsername()),
                "Username was incorrectly labeled as available");
        assertTrue(userDao.availableUserName(user2.getUsername()),
                "Username was incorrectly labeled as not available");

        userDao.insert(user2);
        assertFalse(userDao.availableUserName(user2.getUsername()),
                "Username was incorrectly labeled as available " +
                        "now that it has been used");
        assertFalse(userDao.availableUserName(user1.getUsername()),
                "Username was incorrectly labeled as available " +
                        "after declared not available earlier");
    }

    @Test
    void testAvailableEmailPass() throws DataAccessException{
        assertTrue(userDao.availableEmail(user1.getEmail()),
                "Stated username was not available when database was empty");
        assertTrue(userDao.availableEmail(user2.getEmail()),
                "Stated username was not available when database was empty");

        userDao.insert(user1);
        assertTrue(userDao.availableEmail(user2.getEmail()),
                "Username was incorrectly labeled as not available");
        userDao.insert(user2);
        assertTrue(userDao.availableEmail("new@test.com"),"Username " +
                "was incorrectly labeled as not available");
    }

    @Test
    void testAvailableEmailFail() throws DataAccessException{
        userDao.insert(user1);
        assertFalse(userDao.availableEmail(user1.getEmail()),
                "Username was incorrectly labeled as available");
        assertTrue(userDao.availableEmail(user2.getEmail()),
                "Username was incorrectly labeled as not available");

        userDao.insert(user2);
        assertFalse(userDao.availableEmail(user2.getEmail()),
                "Username was incorrectly labeled as available " +
                        "now that it has been used");
        assertFalse(userDao.availableEmail(user1.getEmail()),
                "Username was incorrectly labeled as available " +
                        "after declared not available earlier");
    }

    @Test
    void testClearTable() throws DataAccessException {
        userDao.insert(user1);
        User compareUser = userDao.find(user1.getUsername());
        assertEquals(user1,compareUser,"User was not found, clear function can't be confirmed");
        userDao.clearTable();
        assertNull(userDao.find(user1.getUsername()),"User data was not properly cleared");
    }

    @Test
    void testClearNoData() throws DataAccessException {
        assertDoesNotThrow(()->userDao.clearTable(),"Error occurred when clearing data when non exists");
    }
}