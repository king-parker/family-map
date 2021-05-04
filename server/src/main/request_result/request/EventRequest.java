package request_result.request;

/**
 * Class to hold the data necessary to process an event retrieval request
 */
public class EventRequest extends DataRetrievalRequest {
    private String eventID;

    /**
     * Constructs a new request to retrieve a specific event from the database
     * @param eventID ID of the event
     * @param authToken token for the logged in user
     */
    public EventRequest(String eventID, String authToken) {
        this.eventID = eventID;
        this.authToken = authToken;
    }

    public String getEventID() {
        return eventID;
    }
}
