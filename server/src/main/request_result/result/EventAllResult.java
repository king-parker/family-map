package request_result.result;

import com.google.gson.annotations.SerializedName;
import model.Event;

import java.util.List;

/**
 * Class that holds the result of a request to receive all events associated with a user
 */
public class EventAllResult extends Result {
    @SerializedName("data")
    private Event[] events;

    /**
     * Creates an object for a successful event retrieval
     * @param events array of event objects associated with the current user
     */
    public EventAllResult(List<Event> events) {
        this.events = events.toArray(new Event[events.size()]);
        success = true;
    }

    /**
     * Creates an object for an unsuccessful event retrieval
     * @param message Error message of what went wrong
     */
    public EventAllResult(String message) {
        this.message = message;
        success = false;
    }

    public Event[] getEvents() {
        return events;
    }
}
