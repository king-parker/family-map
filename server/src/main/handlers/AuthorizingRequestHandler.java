package handlers;

import com.sun.net.httpserver.HttpExchange;
import request_result.request.EventRequest;
import request_result.request.PersonRequest;
import request_result.result.EventResult;
import request_result.result.PersonResult;
import request_result.result.Result;
import services.EventService;
import services.PersonService;
import services.Service;

import java.io.IOException;

/**
 * Base class for requests that require an auth token to perform the request. Provides functionality to
 * perform data retrieval requests for GET methods.
 */
public abstract class AuthorizingRequestHandler extends RequestHandler {
    private static final String AUTH_TOKEN_HEADER = "Authorization";

    protected <Req> void dataRetrievalRequest(HttpExchange exchange,Class<Req> resultClass,
                                                   String handlerName) throws IOException {
        logEnter(handlerName);

        if (!isGetMethod(exchange) || !hasAuthToken(exchange)) {
            sendResponse(exchange,failedResult(resultClass,BAD_METHOD_ERROR));
            logger.severe(BAD_METHOD_ERROR);
            logEnter(handlerName);
            return;
        }

        String authToken = getAuthToken(exchange);
        if (authToken.isBlank()) {
            sendResponse(exchange,failedResult(resultClass,Service.MISS_TOKEN_ERROR));
            logExit(handlerName);
            return;
        }
        logger.info("Auth Token: " + authToken);

        int apiCall = 1;
        int pathValues = 1;
        int maxPathStr = apiCall + pathValues;
        String[] path = parsePath(exchange,maxPathStr);

        if (path.length > maxPathStr) {
            logger.warning("Bad path");
            sendResponse(exchange,failedResult(resultClass,BAD_REQUEST_ERROR));
            logExit(handlerName);
            return;
        }

        Result result;
        if (path.length == apiCall) result = runAllService(resultClass,authToken);
        else result = runService(resultClass,path[1],authToken);
        sendResponse(exchange,result);
        logExit(handlerName);
    }

    private <T> Result runAllService(Class<T> resultClass, String authToken) {
        if (resultClass == PersonResult.class) return new PersonService().person(authToken);
        if (resultClass == EventResult.class) return new EventService().event(authToken);
        return null;
    }

    private <T> Result runService(Class<T> resultClass,String id,String authToken) {
        if (resultClass == PersonResult.class) return new PersonService().person(new PersonRequest(id,authToken));
        if (resultClass == EventResult.class) return new EventService().event(new EventRequest(id,authToken));
        return null;
    }

    private <T> Result failedResult(Class<T> resultClass, String error) {
        if (resultClass == PersonResult.class) return new PersonResult(error);
        if (resultClass == EventResult.class) return new EventResult(error);
        return null;
    }

    private boolean hasAuthToken(HttpExchange exchange) {
        return exchange.getRequestHeaders().containsKey(AUTH_TOKEN_HEADER);
    }

    private String getAuthToken(HttpExchange exchange) {
        return exchange.getRequestHeaders().getFirst(AUTH_TOKEN_HEADER);
    }
}
