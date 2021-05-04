package handlers;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import request_result.result.Result;
import server.Server;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the base functionality of each of the server's handlers. It contains strings
 * for each of the error message types to help simplify choosing and status code to return in the response.
 * The class also contains methods for processing a HTTP request as well as methods for generating and
 * sending the HTTP response. Minor logger functionality also implemented.
 */
public abstract class RequestHandler implements HttpHandler {
    /**
     * Error message when an error occurs from an incorrect Http Method in the request
     */
    public static final String BAD_METHOD_ERROR = "Error: Bad Http Method";
    /**
     * Error message when an error occurs from a request containing bad information resulting in a failed
     * service or even preventing a service from starting
     */
    public static final String BAD_REQUEST_ERROR = "Error: Bad Request";
    /**
     * Error message when an error occurs from an incorrectly formatted request body
     */
    public static final String JSON_READ_ERROR = "Error: Unsupported Media Type";

    protected static Logger logger;

    static { logger = Logger.getLogger(Server.logName); }

    protected void logEnter(String handlerName) {
        logger.entering( "handlers." + handlerName + "Handler","handle");
    }

    protected boolean isPostMethod(HttpExchange exchange) {
        return exchange.getRequestMethod().toLowerCase().equals("post");
    }

    protected boolean isGetMethod(HttpExchange exchange) {
        return exchange.getRequestMethod().toLowerCase().equals("get");
    }

    protected String[] parsePath(HttpExchange exchange,int expectStrNum) {
        String path = exchange.getRequestURI().getPath();
        logger.info("Request path: " + path);

        return new PathParser(path).parsePath(expectStrNum);
    }

    protected void sendResponse(HttpExchange exchange,Result result) throws IOException {
        sendResponseHeaders(exchange,result);
        sendResponseBody(exchange,result);
        exchange.getResponseBody().close();
    }

    protected void sendResponse(HttpExchange exchange,int responseCode) throws IOException {
        sendResponseHeaders(exchange,responseCode);
        exchange.getResponseBody().close();
    }

    protected void sendResponse(HttpExchange exchange,int responseCode,File sendFile) throws IOException {
        sendResponseHeaders(exchange,responseCode);
        sendResponseBody(exchange,sendFile);
        exchange.getResponseBody().close();
    }

    private void sendResponseHeaders(HttpExchange exchange,Result result) throws IOException {
        if (result.isSuccess()) sendResponseHeaders(exchange,HttpURLConnection.HTTP_OK);
        else {
            if (result.getMessage().contains(BAD_REQUEST_ERROR))
                sendResponseHeaders(exchange,HttpURLConnection.HTTP_BAD_REQUEST);
            else if (result.getMessage().equals(BAD_METHOD_ERROR))
                sendResponseHeaders(exchange,HttpURLConnection.HTTP_BAD_METHOD);
            else if (result.getMessage().equals(JSON_READ_ERROR))
                sendResponseHeaders(exchange,HttpURLConnection.HTTP_UNSUPPORTED_TYPE);
            else exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR,0);
        }
        if (result.getMessage() == null) logger.info("Service was successful");
        else logger.info(result.getMessage());
    }

    private void sendResponseHeaders(HttpExchange exchange,int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode,0);
    }

    protected void sendResponseBody(HttpExchange exchange,Result result) throws IOException {
        final int MAX_LENGTH = 350;
        String respBody = createResponseBody(result);
        writeResponseBody(exchange,respBody);
        logger.info("Response body sent");
        if (respBody.length() > MAX_LENGTH) {
            logger.finer("Body too big to display, displayed in FINEST");
            logger.finest("\n" + respBody);
        }
        else logger.finer("\n" + respBody);
    }

    protected void sendResponseBody(HttpExchange exchange,File sendFile) throws IOException {
        OutputStream respBody = exchange.getResponseBody();
        Files.copy(sendFile.toPath(),respBody);
    }

    private String createResponseBody(java.lang.Object response) {
        return new GsonBuilder().create().toJson(response);
    }

    private void writeResponseBody(HttpExchange exchange, String respBody) throws IOException {
        try (Writer writer = new OutputStreamWriter(exchange.getResponseBody())) {
            writer.write(respBody);
        }
    }

    protected void logExit(String handlerName) {
        logger.exiting("handlers." + handlerName + "Handler","handle");
    }

    /**
     * This class parses a path into different parts to retrieve values needed to perform services
     */
    private static class PathParser {
        private final List<String> PATH_STRINGS;
        private final String PATH;
        private int beginIndex;

        /**
         * Creates a new parser contained the path string to be parsed
         * @param path String of the path to be parsed
         */
        public PathParser(String path) {
            PATH_STRINGS = new ArrayList<>();
            this.PATH = path;
            beginIndex = path.indexOf("/");
        }

        /**
         * Executes the parse algorithm to extract the parts of the path
         * @param expectStrNum The number of max expected parts from the path
         * @return A string array containing each part extracted from the path. If the path contains
         * more parts than expected, this method will include whatever was left of the path at the
         * end of the array. If the number of parts is less than the expected number, it will only
         * return an array with string of each part that was found in the path. The size of the array
         * is always the number of returned parts.
         */
        public String[] parsePath(int expectStrNum) {
            if (expectStrNum == 0) return new String[]{PATH};

            for (int i = 0; i < expectStrNum; i++) {
                PATH_STRINGS.add(getParseResult());

                if (beginIndex < 0) break;
                if (PATH_STRINGS.size() == expectStrNum) {
                    String parseResult = getPathRemainder();
                    if (!parseResult.isBlank()) PATH_STRINGS.add(parseResult);
                }
            }

            return PATH_STRINGS.toArray(new String[0]);
        }

        private String getParseResult() {
            String parseResult;
            int endIndex = PATH.indexOf("/",++beginIndex);
            if (endIndex < 0) parseResult = PATH.substring(beginIndex);
            else parseResult = PATH.substring(beginIndex,endIndex);
            beginIndex = endIndex;
            return parseResult;
        }

        private String getPathRemainder(){
            return PATH.substring(beginIndex);
        }
    }
}