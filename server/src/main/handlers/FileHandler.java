package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * This class handles any request that is not implemented by another handler and interprets it as a request
 * for a file to be displayed. If blank, index.html is sent in response
 */
public class FileHandler extends RequestHandler {
    /**
     * Handle the given request and generate an appropriate response.
     * See {@link HttpExchange} for a description of the steps
     * involved in handling an exchange.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is <code>null</code>
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String handlerName = "File";
        logEnter(handlerName);

        if (!isGetMethod(exchange)) {
            sendResponse(exchange,HttpURLConnection.HTTP_BAD_METHOD);
            logger.severe(BAD_METHOD_ERROR);
            logExit(handlerName);
            return;
        }

        String filePath = "web" + getUrlPath(exchange);

        logger.fine("Attempting to get file: " + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            sendResponse(exchange,HttpURLConnection.HTTP_OK,file);
        } else {
            send404(exchange);
        }
        logExit(handlerName);
    }

    private String getUrlPath(HttpExchange exchange) {
        int expectedParts = 0;
        String[] pathParts = parsePath(exchange,expectedParts);
        String urlPath = pathParts[0];
        if (urlPath.isBlank() || urlPath.equals("/")) urlPath = "/index.html";
        return  urlPath;
    }

    private void send404(HttpExchange exchange) throws IOException {
        String filePath = "web/HTML/404.html";
        logger.fine("File Not Found, attempting to get file: " + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            sendResponse(exchange,HttpURLConnection.HTTP_NOT_FOUND,file);
            logger.fine("Error File successfully retrieved");
        }
        else {
            sendResponse(exchange,HttpURLConnection.HTTP_INTERNAL_ERROR);
            logger.severe("Could not return 404 error file");
        }
    }
}
