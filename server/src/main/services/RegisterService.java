package services;

import data_access.*;
import model.User;
import request_result.request.FillRequest;
import request_result.request.LoginRequest;
import request_result.request.RegisterRequest;
import request_result.result.FillResult;
import request_result.result.LoginResult;
import request_result.result.RegisterResult;

import java.sql.Connection;
import java.util.logging.Level;

/**
 * This class carries out the services required to register a new user to the database. In
 * order to do so it receives a register request from the server. The service will return a
 * result whether the service is successful or not. If successful it will login the new user
 * and return a new authorization token for the login. If unsuccessful it will return a message
 * containing the error. The service will fail if a request property is missing or has an
 * invalid value, the username is already taken by another user, or an internal server error
 * occurs.
 */
public class RegisterService extends Service {
    /**
     * Takes information for a new user, creates an account for that user, a person object
     * containing the user's information, generates 4 generations of ancestor data for the
     * user, logs the user in and returns an auth token if the user is successfully registered
     * @param r request object containing all the new user's information
     * @return result object containing the login auth token if successful, a result object
     * containing an error message if unsuccessful
     */
    public RegisterResult register(RegisterRequest r) {
        String service = "Register";
        String method = "register";
        logEnter(service,method);

        logger.finer(fieldMsg);
        RegisterResult result = checkFields(r);
        if (result != null) {logExit(service,method); return result;}

        Database database = new Database();
        try {
            logger.finer("Checking availability of username and password");
            result = checkAvailability(r,database.openConnection());
            if (result != null) {logExit(service,method); return result;}

            logger.finer("Adding user to database");
            String personID = createUser(r,database.getConnection());

            logger.finer("Generating default generations of family data");
            boolean generateSuccess = generateFamily(r.getUsername(),database.getConnection());
            if (!generateSuccess) {logExit(service,method); return new RegisterResult(DATABASE_ERROR); }

            logger.finer("Generating Auth Token for new user by logging in");
            String authToken = loginUser(r.getUsername(),r.getPassword(),database.getConnection());
            if (authToken == null) {logExit(service,method); return new RegisterResult(DATABASE_ERROR); }

            logger.finer("Register success");
            database.closeConnection(true);
            logExit(service,method);
            return new RegisterResult(authToken,r.getUsername(),personID);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            logExit(service,method);
            return new RegisterResult(DATABASE_ERROR);
        } finally {
            try {
                if (database.getConnection() != null) database.closeConnection(false);
            } catch (DataAccessException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
            }
        }
    }

    private RegisterResult checkFields(RegisterRequest r) {
        if (r.getUsername().isBlank() || r.getPassword().isBlank() ||
              r.getEmail().isBlank() || r.getFirstName().isBlank() ||
                                         r.getLastName().isBlank()) {
            return new RegisterResult(MISS_INPUT_ERROR);
        }
        else if (!(r.getGender() == 'm' || r.getGender() == 'f')) return new RegisterResult(INV_INPUT_ERROR);
        else return null;
    }

    private RegisterResult checkAvailability(RegisterRequest r,Connection conn) throws DataAccessException {
        UserDao uDao = new UserDao(conn);
        if (!uDao.availableUserName(r.getUsername())) return new RegisterResult(Service.USER_TAKEN_ERROR);
        else if (!uDao.availableEmail(r.getEmail())) return new RegisterResult(Service.EMAIL_TAKEN_ERROR);
        else return null;
    }

    private String createUser(RegisterRequest r,Connection conn) throws DataAccessException {
        User user = new User(r.getUsername(),r.getPassword(),r.getEmail(),r.getFirstName(),
                r.getLastName(),r.getGender());

        UserDao uDao = new UserDao(conn);
        PersonDao pDao = new PersonDao(conn);
        while (!pDao.availableID(user.getPersonID())) user.updateID();
        uDao.insert(user);

        return user.getPersonID();
    }

    private boolean generateFamily(String username,Connection conn) throws DataAccessException {
        FillRequest request = new FillRequest(username,4);
        FillResult result = new FillService().fill(request,conn);
        return result.isSuccess();
    }

    private String loginUser(String username,String password,Connection conn) throws DataAccessException {
        LoginRequest request = new LoginRequest(username,password);
        LoginResult result = new LoginService().login(request,conn);
        if (result.isSuccess()) return result.getAuthToken();
        else return null;
    }
}
