package services;

import data_access.*;
import model.AuthToken;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_result.request.RegisterRequest;
import request_result.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {
    User priorUser;
    User registerUser;

    @BeforeAll
    static void dbSetUp() throws DataAccessException {
        Database database = new Database();
        database.openConnection();
        database.clearTables();
        database.closeConnection(true);
    }

    @BeforeEach
    void testSetUp() throws DataAccessException {
        priorUser = new User("kingpark","fakePass",
                "kingpark@test.com","Parker",
                "King",'m',"1234-5678-90ab-cdef");
        Person person = new Person(priorUser.getPersonID(), priorUser.getUsername(),
                priorUser.getFirstName(),priorUser.getLastName(), priorUser.getGender());

        Database database = new Database();
        UserDao uDao = new UserDao(database.openConnection());
        PersonDao pDao = new PersonDao(database.getConnection());
        uDao.insert(priorUser);
        pDao.insert(person);
        database.closeConnection(true);

        registerUser = new User("phoenix","anotherPass",
                "second@test.com","Parker",
                "King",'m',"notNeeded");
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        dbSetUp();
    }

    @Test
    void emptyDatabase() throws DataAccessException {
        dbSetUp();
        RegisterRequest request = new RegisterRequest(registerUser.getUsername(),registerUser.getPassword(),
                registerUser.getEmail(),registerUser.getFirstName(),registerUser.getLastName(),
                registerUser.getGender());
        RegisterResult result = new RegisterService().register(request);

        assertNotNull(result,"The result of the service was null");
        assertTrue(result.isSuccess(),"Service failed when it should have succeeded.");
        checkSuccessResult(result);
    }

    @Test
    void badInput() {
        RegisterRequest nullUserName = new RegisterRequest("",registerUser.getPassword(),
                registerUser.getEmail(),registerUser.getFirstName(),registerUser.getLastName(),
                registerUser.getGender());
        RegisterRequest nullPassword = new RegisterRequest(registerUser.getUsername(),"",
                registerUser.getEmail(),registerUser.getFirstName(),registerUser.getLastName(),
                registerUser.getGender());
        RegisterRequest nullEmail = new RegisterRequest(registerUser.getUsername(),
                registerUser.getPassword(), "",registerUser.getFirstName(),
                registerUser.getLastName(),registerUser.getGender());
        RegisterRequest nullFirstName = new RegisterRequest(registerUser.getUsername(),
                registerUser.getPassword(), registerUser.getEmail(),"",
                registerUser.getLastName(),registerUser.getGender());
        RegisterRequest nullLastName = new RegisterRequest(registerUser.getUsername(),
                registerUser.getPassword(), registerUser.getEmail(),registerUser.getFirstName(),
                "",registerUser.getGender());
        RegisterRequest wrongGenderChar = new RegisterRequest(registerUser.getUsername(),
                registerUser.getPassword(), registerUser.getEmail(),registerUser.getFirstName(),
                registerUser.getLastName(),'b');

        RegisterResult result = new RegisterService().register(nullUserName);
        assertNotNull(result,"The result of the service was null");
        assertFalse(result.isSuccess(),"Service succeeded when it should have failed");
        assertEquals(Service.MISS_INPUT_ERROR,result.getMessage(),
                "Service failed for the wrong reason");

        result = new RegisterService().register(nullPassword);
        assertNotNull(result,"The result of the service was null");
        assertFalse(result.isSuccess(),"Service succeeded when it should have failed");
        assertEquals(Service.MISS_INPUT_ERROR,result.getMessage(),
                "Service failed for the wrong reason");

        result = new RegisterService().register(nullEmail);
        assertNotNull(result,"The result of the service was null");
        assertFalse(result.isSuccess(),"Service succeeded when it should have failed");
        assertEquals(Service.MISS_INPUT_ERROR,result.getMessage(),
                "Service failed for the wrong reason");

        result = new RegisterService().register(nullFirstName);
        assertNotNull(result,"The result of the service was null");
        assertFalse(result.isSuccess(),"Service succeeded when it should have failed");
        assertEquals(Service.MISS_INPUT_ERROR,result.getMessage(),
                "Service failed for the wrong reason");

        result = new RegisterService().register(nullLastName);
        assertNotNull(result,"The result of the service was null");
        assertFalse(result.isSuccess(),"Service succeeded when it should have failed");
        assertEquals(Service.MISS_INPUT_ERROR,result.getMessage(),
                "Service failed for the wrong reason");

        result = new RegisterService().register(wrongGenderChar);
        assertNotNull(result,"The result of the service was null");
        assertFalse(result.isSuccess(),"Service succeeded when it should have failed");
        assertEquals(Service.INV_INPUT_ERROR,result.getMessage(),
                "Service failed for the wrong reason");
    }

    @Test
    void unavailableValues() {
        RegisterRequest dupUsername = new RegisterRequest(priorUser.getUsername(),
                registerUser.getPassword(), registerUser.getEmail(),registerUser.getFirstName(),
                registerUser.getLastName(), registerUser.getGender());
        RegisterRequest dupEmail = new RegisterRequest(registerUser.getUsername(),
                registerUser.getPassword(), priorUser.getEmail(),registerUser.getFirstName(),
                registerUser.getLastName(), registerUser.getGender());

        RegisterResult result = new RegisterService().register(dupUsername);
        assertNotNull(result,"The result of the service was null");
        assertFalse(result.isSuccess(),"Service succeeded when it should have failed");
        assertEquals(Service.USER_TAKEN_ERROR,result.getMessage(),
                "Service failed for the wrong reason");

        result = new RegisterService().register(dupEmail);
        assertNotNull(result,"The result of the service was null");
        assertFalse(result.isSuccess(),"Service succeeded when it should have failed");
        assertEquals(Service.EMAIL_TAKEN_ERROR,result.getMessage(),
                "Service failed for the wrong reason");
    }

    @Test
    void multipleUsers() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(registerUser.getUsername(),registerUser.getPassword(),
                registerUser.getEmail(),registerUser.getFirstName(),registerUser.getLastName(),
                registerUser.getGender());
        RegisterResult result = new RegisterService().register(request);

        assertNotNull(result,"The result of the service was null");
        assertTrue(result.isSuccess(),"Service failed when it should have succeeded.");
        checkSuccessResult(result);

        request = new RegisterRequest("Another User","blablabla",
                "oneMoreEmail@test.com","Whatzit","Tooya", 'f');
        result = new RegisterService().register(request);

        assertNotNull(result,"The result of the service was null");
        assertTrue(result.isSuccess(),"Service failed when it should have succeeded.");
        checkSuccessResult(result);
    }

    void checkSuccessResult(RegisterResult result) throws DataAccessException {
        Database database = new Database();
        AuthTokenDao tDao = new AuthTokenDao(database.openConnection());
        UserDao uDao = new UserDao(database.getConnection());
        PersonDao pDao = new PersonDao(database.getConnection());

        AuthToken token = tDao.find(result.getAuthToken());
        User user = uDao.find(result.getUsername());
        Person person = pDao.find(result.getPersonID());
        database.closeConnection(false);

        assertNotNull(token,"Auth Token was not created");
        assertEquals(result.getUsername(),token.getUsername(),
                "Username in retrieved token does not match the user that was registered");
        assertNotNull(user,"User was not created");
        assertEquals(result.getPersonID(),user.getPersonID(),
                "Person ID in retrieved user does not match the user that was registered");
        assertNotNull(person,"Person object for user was not created");
        assertEquals(result.getUsername(),person.getUsername(),
                "Username in retrieved person does not match the user that was registered");
        assertNotNull(person.getFatherID(),"Family tree data was not created properly");
        assertNotNull(person.getMotherID(),"Family tree data was not created properly");
    }
}