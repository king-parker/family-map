package request_result.result;

import model.Event;

/**
 * Class that holds the result of an event retrieval request
 */
public class EventResult extends Result {
    private String eventID;
    private String associatedUsername;
    private String personID;
    private String latitude;
    private String longitude;
    private String country;
    private String city;
    private String eventType;
    private String year;

    /**
     * Creates an object for a successful event retrieval
     * @param event event that was retrieved
     */
    public EventResult(Event event) {
        eventID = event.getEventID();
        associatedUsername = event.getUsername();
        personID = event.getPersonID();
        latitude = String.valueOf(event.getLatitude());
        longitude = String.valueOf(event.getLongitude());
        country = event.getCountry();
        city = event.getCity();
        eventType = event.getEventType();
        year = String.valueOf(event.getYear());
        success = true;
    }

    /**
     * Creates an object for an unsuccessful event retrieval
     * @param message Error message of what went wrong
     */
    public EventResult(String message) {
        this.message = message;
        success = false;
    }

    public String getEventID() {
        return eventID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public float getLatitude() {
        return Float.parseFloat(latitude);
    }

    public float getLongitude() {
        return Float.parseFloat(longitude);
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getEventType() {
        return eventType;
    }

    public int getYear() {
        return Integer.parseInt(year);
    }
}
