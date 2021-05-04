package services;

import data_access.AuthTokenDao;
import data_access.DataAccessException;
import data_access.Database;
import data_access.UserDao;
import model.AuthToken;
import request_result.request.LoginRequest;
import request_result.result.LoginResult;
import request_result.result.RegisterResult;

import java.sql.Connection;
import java.util.logging.Level;

/**
 * Preforms the service of logging a user back into the program
 */
public class LoginService extends Service {
    /**
     * Logs the user back in and generates a new token for the session
     * @param r Request object containing the username and password being used to login
     * @return Result of the login request
     */
     public LoginResult login(LoginRequest r) {
         String service = "Login";
         String method = "login";
         logEnter(service,method);

         LoginResult result;

         logger.finer(fieldMsg);
         result = checkFields(r);
         if (result != null) {logExit(service,method); return result; }

         Database database = new Database();
         try {
             logger.finer("Generating Auth Token for new user by logging in");
             result = login(r,database.getConnection());

             database.closeConnection(true);
             logExit(service,method);
             return result;
         } catch (DataAccessException e) {
             logger.log(Level.SEVERE,e.getMessage(),e);
             logExit(service,method);
             return new LoginResult(DATABASE_ERROR);
         } finally {
             try {
                 if (database.getConnection() != null) database.closeConnection(false);
             } catch (DataAccessException e) {
                 logger.log(Level.SEVERE,e.getMessage(),e);
             }
         }
     }

    /**
     * This method is not used by handlers. It provides functionality to allow the register service
     * to utilize this service as part of it's own service. It is also the base functionality this
     * service.
     * @param r login request for the service
     * @param conn open connection to database
     * @return login result of the service
     * @throws DataAccessException thrown when an error occurs when accessing user data or inserting authToken
     * data
     */
    public LoginResult login(LoginRequest r,Connection conn) throws DataAccessException {
        UserDao uDao = new UserDao(conn);
        if (!uDao.loginValidate(r.getUsername(),r.getPassword())) return new LoginResult(INV_INPUT_ERROR);

        String personID = uDao.find(r.getUsername()).getPersonID();
        AuthTokenDao tDao = new AuthTokenDao(conn);
        AuthToken createdToken = tDao.createToken(r.getUsername());

        return new LoginResult(createdToken.getAuthToken(),r.getUsername(),personID);
    }

    private LoginResult checkFields(LoginRequest r) {
        if (r.getUsername().isBlank() || r.getPassword().isBlank()) return new RegisterResult(MISS_INPUT_ERROR);
        else return null;
    }
}
