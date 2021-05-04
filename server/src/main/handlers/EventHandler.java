package handlers;

import com.sun.net.httpserver.HttpExchange;
import request_result.result.EventResult;

import java.io.IOException;

/**
 * Handles requests to get event data from the database
 */
public class EventHandler extends AuthorizingRequestHandler {
    /**
     * Handle the given request and generate an appropriate response.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is <code>null</code>
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        dataRetrievalRequest(exchange,EventResult.class,"Event");
    }
}
