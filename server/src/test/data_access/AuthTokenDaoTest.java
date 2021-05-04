package data_access;
import model.AuthToken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenDaoTest {
    Database database;
    AuthTokenDao tokenDao;
    AuthToken token1;
    AuthToken token2;

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
        token1 = new AuthToken("1234-5678-90ab-cdef","kingpark",
                "1970-01-01 00:00:00");
        token2 = new AuthToken("cdef-5678-90ab-1234","phoenix",
                "2020-07-23 20:00:00");
        Connection conn = database.openConnection();
        tokenDao = new AuthTokenDao(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        database.closeConnection(false);
    }

    @Test
    void testInsertSuccess() throws DataAccessException {
        tokenDao.insert(token1);
        AuthToken compareToken = tokenDao.find(token1.getAuthToken());
        assertNotNull(compareToken,"No AuthToken object was returned from database");
        assertEquals(token1,compareToken,"AuthToken was not the same after it was retrieved");

        tokenDao.insert(token2);
        compareToken = tokenDao.find(token2.getAuthToken());
        assertNotNull(compareToken,"No AuthToken object was returned from database");
        assertEquals(token2,compareToken,"AuthToken was not the same after it was retrieved");

        compareToken = tokenDao.find(token1.getAuthToken());
        assertNotNull(compareToken,"No AuthToken object was returned from database");
        assertEquals(token1,compareToken,"AuthToken was not the same after it was retrieved");
    }

    @Test
    void testInsertFail() throws DataAccessException {
        AuthToken dupToken = new AuthToken(token1.getAuthToken(),token2.getUsername(),
                token2.getTimeStamp());

        tokenDao.insert(token1);
        assertThrows(DataAccessException.class,()-> tokenDao.insert(token1),"Double insert" +
                " succeeded when it shouldn't because unique authTokens are required");
        assertThrows(DataAccessException.class,()-> tokenDao.insert(dupToken),"Double insert" +
                " succeeded when it shouldn't because unique authTokens are required");
    }

    @Test
    void testFindSuccess() throws DataAccessException {
        tokenDao.insert(token1);
        tokenDao.insert(token2);
        AuthToken compareToken = tokenDao.find(token1.getAuthToken());
        assertNotNull(compareToken,"No AuthToken object was returned from database");
        assertEquals(token1,compareToken,"AuthToken was not the same after it was retrieved");
        compareToken = tokenDao.find(token2.getAuthToken());
        assertNotNull(compareToken,"No AuthToken object was returned from database");
        assertEquals(token2,compareToken,"AuthToken was not the same after it was retrieved");
    }

    @Test
    void testFindFail() throws DataAccessException {
        assertNull(tokenDao.find(token1.getAuthToken()), "A authToken was found when " +
                "no authTokens were in the database");
        tokenDao.insert(token1);
        assertNull(tokenDao.find(token2.getAuthToken()), "An authToken was found with a " +
                "authToken id that was not in the database");
    }

    @Test
    void testCreateOne() throws DataAccessException {
        tokenDao.insert(token1);
        tokenDao.insert(token2);
        AuthToken createdToken = tokenDao.createToken(token1.getUsername());
        assertNotEquals(createdToken,token1,"AuthToken objects found equal when only " +
                "usernames should match");
        assertNotEquals(createdToken,token2,"AuthToken objects found equal when they " +
                "should be completely different");
        assertNotNull(tokenDao.find(createdToken.getAuthToken()),"Created token was " +
                "not successfully inserted into database");
        assertEquals(createdToken,tokenDao.find(createdToken.getAuthToken()),
                "Inserted token was different than token returned by createToken method");
    }

    @Test
    void testCreateMany() throws DataAccessException {
        tokenDao.insert(token1);
        tokenDao.insert(token2);
        for (int i = 0; i < 10; i++) {
            AuthToken createdToken1 = null;
            try {
                createdToken1 = tokenDao.createToken(token1.getUsername());
            } catch (DataAccessException e) {
                if (e.getMessage().equals("Error encountered while inserting into the database")) {
                    fail("An error occurred trying to insert created token, most likely " +
                            "due to an non-unique token",e);
                }
                else {
                    fail("unexpected error",e);
                    e.getStackTrace();
                }
            }
            assertNotNull(createdToken1,"An AuthToken was not successfully returned by " +
                    "createToken method");
            assertNotEquals(createdToken1,token1,"AuthToken objects found equal when only " +
                    "usernames should match");
            assertNotEquals(createdToken1,token2,"AuthToken objects found equal when they " +
                    "should be completely different");
            assertNotNull(tokenDao.find(createdToken1.getAuthToken()),"Created token was " +
                    "not successfully inserted into database");
            assertEquals(createdToken1,tokenDao.find(createdToken1.getAuthToken()),
                    "Inserted token was different than token returned by createToken method");

            AuthToken createdToken2 = null;
            try {
                createdToken2 = tokenDao.createToken(token1.getUsername());
            } catch (DataAccessException e) {
                if (e.getMessage().equals("Error encountered while inserting into the database")) {
                    fail("An error occurred trying to insert created token, most likely " +
                            "due to an non-unique token",e);
                }
                else {
                    fail("unexpected error",e);
                    e.getStackTrace();
                }
            }
            assertNotNull(createdToken2,"An AuthToken was not successfully returned by " +
                    "createToken method");
            assertNotEquals(createdToken2,token2,"AuthToken objects found equal when only " +
                    "usernames should match");
            assertNotEquals(createdToken2,token1,"AuthToken objects found equal when they " +
                    "should be completely different");
            assertNotNull(tokenDao.find(createdToken2.getAuthToken()),"Created token was " +
                    "not successfully inserted into database");
            assertEquals(createdToken2,tokenDao.find(createdToken2.getAuthToken()),
                    "Inserted token was different than token returned by createToken method");
        }
    }

    @Test
    void testClearTable() throws DataAccessException {
        tokenDao.insert(token1);
        AuthToken compareToken = tokenDao.find(token1.getAuthToken());
        assertEquals(token1,compareToken,"AuthToken was not found, clear function can't be confirmed");
        tokenDao.clearTable();
        assertNull(tokenDao.find(token1.getAuthToken()),"AuthToken data was not properly cleared");
    }

    @Test
    void testClearTableMany() throws DataAccessException {
        tokenDao.insert(token1);
        for (int i = 0; i < 20; i++) {
            tokenDao.createToken(token1.getUsername());
            tokenDao.createToken(token2.getUsername());
        }
        AuthToken compareToken = tokenDao.find(token1.getAuthToken());
        assertEquals(token1,compareToken,"AuthToken was not found, clear function can't be confirmed");
        tokenDao.clearTable();
        assertNull(tokenDao.find(token1.getAuthToken()),"AuthToken data was not properly cleared");
    }
}