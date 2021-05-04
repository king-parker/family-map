package handlers;

import com.sun.net.httpserver.HttpExchange;
import request_result.request.FillRequest;
import request_result.result.FillResult;
import services.FillService;

import java.io.IOException;

/**
 * Handles requests to generate a specified number of generations of person and event data for a given user
 */
public class FillHandler extends PostRequestHandler {
    /**
     * Handle the given request and generate an appropriate response.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is <code>null</code>
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String handlerName = "Fill";
        logEnter(handlerName);

        if (!isPostMethod(exchange)) {
            sendResponse(exchange,new FillResult(BAD_METHOD_ERROR,false));
            logger.severe(BAD_METHOD_ERROR);
            logExit(handlerName);
            return;
        }

        int apiCall = 1;
        int pathValues = 2;
        int maxPathStr = apiCall + pathValues;
        String[] path = parsePath(exchange,maxPathStr);

        if (path.length > maxPathStr) {
            logger.severe("Bad path");
            sendResponse(exchange,new FillResult(BAD_REQUEST_ERROR,false));
            logExit(handlerName);
            return;
        }

        FillRequest request;
        if (path.length == (apiCall + 1)) request = new FillRequest(path[1],4);
        else request = new FillRequest(path[1],Integer.parseInt(path[2]));
        sendResponse(exchange,new FillService().fill(request));
        logExit(handlerName);
    }
}
