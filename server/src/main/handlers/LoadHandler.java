package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import request_result.request.LoadRequest;
import request_result.result.FillResult;
import request_result.result.LoadResult;
import services.LoadService;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Handles request to load data into the database
 */
public class LoadHandler extends PostRequestHandler {
    /**
     * Handle the given request and generate an appropriate response.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is <code>null</code>
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String handlerName = "Load";
        logEnter(handlerName);

        if (!isPostMethod(exchange)) {
            sendResponse(exchange,new FillResult(BAD_METHOD_ERROR,false));
            logger.severe(BAD_METHOD_ERROR);
            logExit(handlerName);
            return;
        }

        LoadRequest request;
        try {
            request = parseRequestBody(exchange,LoadRequest.class);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange,new LoadResult(JSON_READ_ERROR,false));
            logger.log(Level.SEVERE,JSON_READ_ERROR,e);
            logExit(handlerName);
            return;
        }

        LoadResult result = new LoadService().load(request);
        sendResponse(exchange,result);
        logExit(handlerName);
    }
}
