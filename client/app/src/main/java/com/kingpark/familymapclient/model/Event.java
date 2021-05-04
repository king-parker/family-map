package com.kingpark.familymapclient.model;

import java.util.Comparator;

/**
 * Class that represents event data in the database
 */
public class Event implements ModelObject, Comparable<Event> {
    /**
     * String used events of the type "Birth"
     */
    public final static String BIRTH = "Birth";
    /**
     * String used events of the type "Marriage"
     */
    public final static String MARRIAGE = "Marriage";
    /**
     * String used events of the type  "Death"
     */
    public final static String DEATH = "Death";
    /**
     * String used events of the type "Baptism"
     */
    public final static String BAPTISM = "Baptism";
    /**
     * String used events of the type "Christening"
     */
    public final static String CHRISTENING = "Christening";
    /**
     * String used events of the type "School Graduation"
     */
    public final static String SCHOOL_GRAD = "School Graduation";
    /**
     * String used events of the type "Military Deployment"
     */
    public final static String MILIT_DEP = "Military Deployment";
    
    private String eventID;
    private String associatedUsername;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;
    
    /**
     * Creates an event object from the given data
     * @param eventID ID of the event
     * @param username username associated with the event
     * @param personID ID of the person the event is for
     * @param latitude latitude the event occurred at
     * @param longitude longitude the event occurred at
     * @param country country the event occurred in
     * @param city city the event occurred in
     * @param eventType the type of event
     * @param year year the event occurred
     */
    public Event(String eventID, String username, String personID, float latitude, float longitude,
                 String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = username;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }
    
    public String getId() {
        return eventID;
    }
    
    public String getUsername() {
        return associatedUsername;
    }
    
    public String getPersonID() {
        return personID;
    }
    
    public float getLatitude() {
        return latitude;
    }
    
    public float getLongitude() {
        return longitude;
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
        return year;
    }
    
    public String getDescriptionStr() {
        return eventType.toUpperCase() + ": " + year;
    }
    
    public String getLocationStr() {
        return city + ", " + country;
    }
    
    /**
     * Returns a hash code for this event. It is computed by summing the hash code of all field variables
     * that are strings with the int value of latitude, longitude and year. Latitude and longitude being
     * cast from float to int.
     * @return hash code value for this object
     */
    @Override
    public int hashCode() {
        return eventID.hashCode() + associatedUsername.hashCode() + personID.hashCode() + (int) latitude +
                (int) longitude + country.hashCode() + city.hashCode() + year;
    }
    
    /**
     * Compares the two events for order
     * @param compEvent event to be compared
     * @return negative integer if event < compEvent, zero if event = compEvent, positive integer if event > compEvent
     */
    @Override
    public int compareTo(Event compEvent) {
        if (this.equals(compEvent)) return 0;
        
        int year = this.getYear();
        int compYear = compEvent.getYear();
        int compare = year - compYear;
        
        if (compare != 0) return compare;
        
        String type = this.getEventType();
        String compType = compEvent.getEventType();
        if (type.toUpperCase().equals("BIRTH")) return 1;
        if (type.toUpperCase().equals("DEATH")) return -1;
        if (!type.toUpperCase().equals(compType.toUpperCase())) return type.compareTo(compType);
        else return type.toUpperCase().compareTo(compType.toUpperCase());
    }
    
    /**
     * Compares this event to the specified object. The result is true if and only if the the argument
     * is not null and is an Event object that has the same values for each data member as this object.
     * @param o the object to compare this Event against
     * @return true if the given object represents an Event equivalent to this event, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Event) {
            Event oEvent = (Event) o;
            return oEvent.getId().equals(getId()) &&
                    oEvent.getUsername().equals(getUsername()) &&
                    oEvent.getPersonID().equals(getPersonID()) &&
                    oEvent.getLatitude() == (getLatitude()) &&
                    oEvent.getLongitude() == (getLongitude()) &&
                    oEvent.getCountry().equals(getCountry()) &&
                    oEvent.getCity().equals(getCity()) &&
                    oEvent.getEventType().equals(getEventType()) &&
                    oEvent.getYear() == (getYear());
        } else {
            return false;
        }
    }
    
}
