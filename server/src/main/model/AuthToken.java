package model;

import utility.DateTime;

/**
 * Class that represents authentication tokens
 */
public class AuthToken extends ModelObject {
    private String authToken;
    private String username;
    private String timeStamp;

    /**
     * Creates a new authentication token object for the given user. Generates a random
     * string for the token and generates the timestamp for token's creation. Availability
     * for the token should be checked before inserting into the database.
     * @param username username of the user the token is being created for
     */
    public AuthToken(String username) {
        authToken = generateID();
        this.username = username;
        timeStamp = DateTime.getDateTime();
    }

    /**
     * Creates an auth token from the given data.
     * @param authToken ID of the token
     * @param username username of the associated user
     * @param timeStamp timestamp of the token's creation
     */
    public AuthToken(String authToken, String username, String timeStamp) {
        this.authToken = authToken;
        this.username = username;
        this.timeStamp = timeStamp;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Replaces current ID with a new randomly generated one
     */
    @Override
    public void updateID() {
        authToken = generateID();
    }

    /**
     * Returns a hash code for this auth token. It is computed by summing the hash code of all field
     * variables that are strings
     * @return hash code value for this object
     */
    @Override
    public int hashCode() {
        return authToken.hashCode() + username.hashCode() + (int) DateTime.stringToLong(timeStamp);
    }

    /**
     * Compares this authToken to the specified object. The result is true if and only if the the argument
     * is not null and is an AuthToken object that has the same values for each data member as this object.
     * @param o the object to compare this AuthToken against
     * @return true if the given object represents an AuthToken equivalent to this authToken, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof AuthToken) {
            AuthToken oAuthToken = (AuthToken) o;
            return oAuthToken.getAuthToken().equals(getAuthToken()) &&
                    oAuthToken.getUsername().equals(getUsername()) &&
                    oAuthToken.getTimeStamp().equals(getTimeStamp());
        }
        else {
            return false;
        }
    }
}
