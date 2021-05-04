package services;

import data_access.AuthTokenDao;
import data_access.DataAccessException;
import data_access.Database;
import data_access.UserDao;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_result.request.LoginRequest;
import request_result.result.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    User user1;
    User user2;
    AuthToken token1;
    AuthToken token2;
    LoginRequest request1;
    LoginRequest request2;
    LoginResult result1;
    LoginResult result2;

    void popDB() throws DataAccessException {
        Database database = new Database();
        database.openConnection();
        UserDao uDao = new UserDao(database.getConnection());
        uDao.insert(user1);
        uDao.insert(user2);
        AuthTokenDao tDao = new AuthTokenDao(database.getConnection());
        tDao.insert(token1);
        tDao.insert(token2);
        database.closeConnection(true);
    }

    void successService(LoginRequest request,LoginResult result) throws DataAccessException {
        compareSuccessfulResults(result,new LoginService().login(request));
    }

    void failedService(String username,String password,String errorMessage) {
        LoginRequest badRequest = new LoginRequest(username,password);
        LoginResult badResult = new LoginResult(errorMessage);
        LoginResult compareResult = new LoginService().login(badRequest);
        compareFailedResults(badResult,compareResult);
    }

    void compareSuccessfulResults(LoginResult expectedResult,LoginResult actualResult) throws DataAccessException {
        assertTrue(actualResult.isSuccess(),"Request was not successful " +
                "when it should have succeeded");
        Database database = new Database();
        AuthTokenDao tDao = new AuthTokenDao(database.openConnection());
        AuthToken tokenDB = tDao.find(actualResult.getAuthToken());
        database.closeConnection(true);
        assertNotNull(tokenDB,"Auth token was not inserted into the database");
        assertEquals(expectedResult.getUsername(),tokenDB.getUsername(),
                "Result of service did not return proper auth token");
        assertEquals(expectedResult.getUsername(),actualResult.getUsername(),
                "Result of service did not return proper username");
        assertEquals(expectedResult.getPersonID(),actualResult.getPersonID(),
                "Result of service did not return proper person ID");
    }

    void compareFailedResults(LoginResult expectedResult,LoginResult actualResult) {
        assertFalse(actualResult.isSuccess(),"Request was successful " +
                "when it should have failed");
        assertEquals(expectedResult.getMessage(),actualResult.getMessage(),
                "Request message did not match proper error message");
    }

    @BeforeAll
    static void dbSetUp() throws DataAccessException {
        Database database = new Database();
        database.openConnection();
        database.clearTables();
        database.closeConnection(true);
    }

    @BeforeEach
    void testSetUp() throws DataAccessException {
        user1 = new User("kingpark","fakePass",
                "kingpark@test.com","Parker",
                "King",'m',"fake_ID");
        user2 = new User("phoenix","anotherPass",
                "second@test.com","Parker",
                "King",'m',"FAKE_id");
        token1 = new AuthToken("1234-5678-90ab-cdef","kingpark",
                "1970-01-01 00:00:00");
        token2 = new AuthToken("cdef-5678-90ab-1234","phoenix",
                "2020-07-23 20:00:00");

        request1 = new LoginRequest(user1.getUsername(),user1.getPassword());
        request2 = new LoginRequest(user2.getUsername(),user2.getPassword());
        result1 = new LoginResult(token1.getAuthToken(),user1.getUsername(),user1.getPersonID());
        result2 = new LoginResult(token2.getAuthToken(),user2.getUsername(),user2.getPersonID());
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        Database database = new Database();
        database.openConnection();
        database.clearTables();
        database.closeConnection(true);
    }

    @Test
    void testLoginPriorData() throws DataAccessException {
        popDB();
        successService(request1,result1);
        successService(request2,result2);

        failedService(user1.getUsername(),user2.getPassword(),LoginService.INV_INPUT_ERROR);
        failedService("",user1.getPassword(),LoginService.MISS_INPUT_ERROR);
        failedService(user1.getUsername(),"",LoginService.MISS_INPUT_ERROR);
    }

    @Test
    void testLoginNoData() throws DataAccessException {
        failedService(user1.getUsername(),user1.getPassword(),LoginService.INV_INPUT_ERROR);
        failedService(user2.getUsername(),user2.getPassword(),LoginService.INV_INPUT_ERROR);

        popDB();
        successService(request1,result1);
    }
}