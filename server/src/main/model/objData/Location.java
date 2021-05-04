package model.objData;

/**
 * Class used to hold location information to be used in event generation.
 */
public class Location {
    private String country;
    private String city;
    private float latitude;
    private float longitude;

    /**
     * Create a location object based of the given data
     * @param country country of the location
     * @param city city of the location
     * @param latitude latitude of the location
     * @param longitude longitude of the location
     */
    public Location(String country,String city,float latitude,float longitude) {
        this.country = country;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    /**
     * Returns a hash code for this location. It is computed by summing the hash code of all field variables
     * that are strings with the int value of the latitude and longitude after being cast from float to int.
     * @return hash code value for this object
     */
    @Override
    public int hashCode() {
        return country.hashCode() + city.hashCode() + (int) latitude + (int) longitude;
    }

    /**
     * Compares this location to the specified object. The result is true if and only if the the argument
     * is not null and is a Location object that has the same values for each data member as this object.
     * @param o the object to compare this Location against
     * @return true if the given object represents a Location equivalent to this location, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Location) {
            Location oEvent = (Location) o;
            return oEvent.getCountry().equals(getCountry()) &&
                    oEvent.getCity().equals(getCity()) &&
                    oEvent.getLatitude() == (getLatitude()) &&
                    oEvent.getLongitude() == (getLongitude());
        } else {
            return false;
        }
    }
}
