package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import request_result.request.RegisterRequest;
import request_result.result.LoginResult;
import request_result.result.RegisterResult;
import services.RegisterService;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Handles requests to register a new user to the database and returns a new auth token.
 */
public class RegisterHandler extends PostRequestHandler {
    /**
     * Handle the given request and generate an appropriate response.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is <code>null</code>
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String handlerName = "Register";
        logEnter(handlerName);

        if (!isPostMethod(exchange)) {
            sendResponse(exchange,new RegisterResult(BAD_METHOD_ERROR));
            logger.severe(BAD_METHOD_ERROR);
            logExit(handlerName);
            return;
        }

        RegisterRequest request;
        try {
            request = parseRequestBody(exchange,RegisterRequest.class);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange,new LoginResult(JSON_READ_ERROR));
            logger.log(Level.SEVERE,JSON_READ_ERROR,e);
            logExit(handlerName);
            return;
        }

        RegisterResult result = new RegisterService().register(request);
        sendResponse(exchange,result);
        logExit(handlerName);
    }
}
