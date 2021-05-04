package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import request_result.request.LoginRequest;
import request_result.result.LoginResult;
import services.LoginService;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Handles requests to login a user that has been registered in the database and returns a new auth token.
 */
public class LoginHandler extends PostRequestHandler {
    /**
     * Handle the given request and generate an appropriate response.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is <code>null</code>
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String handlerName = "Login";
        logEnter(handlerName);

        if (!isPostMethod(exchange)) {
            sendResponse(exchange,new LoginResult(BAD_METHOD_ERROR));
            logger.severe(BAD_METHOD_ERROR);
            logExit(handlerName);
            return;
        }

        LoginRequest request;
        try {
            request = parseRequestBody(exchange,LoginRequest.class);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange,new LoginResult(JSON_READ_ERROR));
            logger.log(Level.SEVERE,JSON_READ_ERROR,e);
            logExit(handlerName);
            return;
        }

        LoginResult result = new LoginService().login(request);
        sendResponse(exchange,result);
    }
}
