package request_result.request;

/**
 * Base class for the Request objects that hold request data for data retrieval services
 */
public abstract class DataRetrievalRequest {
    protected String authToken;

    public String getAuthToken() {
        return authToken;
    }
}
