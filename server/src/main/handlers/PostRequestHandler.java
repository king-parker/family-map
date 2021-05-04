package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.util.Scanner;

/**
 * Base class for Handlers that handle a post request. This class contains methods used for parsing a
 * request body to get data needed for the requested service.
 */
public abstract class PostRequestHandler extends RequestHandler {

    protected <T> T parseRequestBody(HttpExchange exchange,Class<T> classOfT) {
        String json = getRequestBody(exchange);
        Gson gson = new Gson();
        return gson.fromJson(json,classOfT);
    }

    private String getRequestBody(HttpExchange exchange) {
        Scanner s = new Scanner(exchange.getRequestBody()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";

        logger.info("Request body received");
        logger.fine(result);
        return result;
    }
}
