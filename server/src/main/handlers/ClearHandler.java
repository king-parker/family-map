package handlers;

import com.sun.net.httpserver.HttpExchange;
import request_result.result.ClearResult;
import request_result.result.RegisterResult;
import services.ClearService;

import java.io.IOException;

/**
 * Handlers requests to clear all data from the database
 */
public class ClearHandler extends PostRequestHandler {
    /**
     * Handle the given request and generate an appropriate response.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is <code>null</code>
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String handlerName = "Clear";
        logEnter(handlerName);

        if (!isPostMethod(exchange)) {
            sendResponse(exchange,new RegisterResult(BAD_METHOD_ERROR));
            logger.severe(BAD_METHOD_ERROR);
            logExit(handlerName);
            return;
        }

        ClearResult result = new ClearService().clear();

        sendResponse(exchange,result);
        logExit(handlerName);
    }
}
