package data_access;

import model.Event;
import model.ModelObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class that provides access to the events in the database
 */
public class EventDao extends DaoObject {
    private final String TABLE_EVENT = "event";
    private final String EVENT_ID = "event_id";
    private final String ASSOC_USER = "associated_username";
    private final String PERSON_ID = "person_id";
    private final String LAT = "latitude";
    private final String LONG = "longitude";
    private final String COUNTRY = "country";
    private final String CITY = "city";
    private final String EVENT_TYPE = "event_type";
    private final String YEAR = "year";
    /**
     * Creates a new data access object to access event data in the database
     * @param conn the current connection to the database
     */
    public EventDao(Connection conn) {
        super(conn);
    }

    /**
     * @param insertEvent Object to be inserted into database
     * @throws DataAccessException Thrown when an error occurs from inserting an event
     */
    public void insert(ModelObject insertEvent) throws DataAccessException {
        if (!(insertEvent instanceof Event)) throw new DataAccessException("Invalid object type");
        Event event = (Event) insertEvent;

        String sql = "INSERT INTO " + TABLE_EVENT + " (" + EVENT_ID + "," + ASSOC_USER +
                "," + PERSON_ID + "," + LAT + "," + LONG + "," + COUNTRY + "," + CITY +
                "," + EVENT_TYPE + "," + YEAR + ") VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * @param eventID ID of the event to be found in the database
     * @return Event object that matches the event ID
     * @throws DataAccessException Thrown when an error occurs from searching for an event
     */
    public Event find(String eventID) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_EVENT + " WHERE " + EVENT_ID + " = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString(EVENT_ID), rs.getString(ASSOC_USER),
                        rs.getString(PERSON_ID), rs.getFloat(LAT), rs.getFloat(LONG),
                        rs.getString(COUNTRY), rs.getString(CITY), rs.getString(EVENT_TYPE),
                        rs.getInt(YEAR));

                return event;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        }
        finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param userName Username associated with the events to be retrieved
     * @return Array of Events with every event associated with the username
     * @throws DataAccessException Thrown when an error occurs from searching for events
     */
    public List<ModelObject> getAll(String userName) throws DataAccessException {
        List<ModelObject> events = new ArrayList<>();
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_EVENT + " WHERE " + ASSOC_USER + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,userName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                event = new Event(rs.getString(EVENT_ID), rs.getString(ASSOC_USER),
                        rs.getString(PERSON_ID), rs.getFloat(LAT), rs.getFloat(LONG),
                        rs.getString(COUNTRY), rs.getString(CITY), rs.getString(EVENT_TYPE),
                        rs.getInt(YEAR));
                events.add(event);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting event data");
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return events;
    }

    /**
     * Checks the database to see if an event ID is used yet
     * @param eventID ID to check for in database
     * @return True if ID is not in database already, false if it is in it
     * @throws DataAccessException Thrown when an error occurs from retrieving data
     */
    public boolean availableID(String eventID) throws DataAccessException {
        HashSet<String> allID = new HashSet<>();
        String sql = "SELECT " + EVENT_ID + " FROM " + TABLE_EVENT;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allID.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving event IDs");
        }
        return available_Helper(eventID,allID);
    }

    /**
     * Clears all event data relating to a specific user in the database
     * @param userName username for the user to delete the data for
     * @throws DataAccessException Thrown when an error occurs from deleting data
     */
    public void clearUserData(String userName) throws DataAccessException {
        String sql = "DELETE FROM " + TABLE_EVENT + " WHERE " + ASSOC_USER + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,userName);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL Error encountered while clearing user data from Event");
        }
    }

    /**
     * Clears the data from the table relating to the DAO
     * @throws DataAccessException Thrown when an error occurs from deleting object data
     */
    @Override
    public void clearTable() throws DataAccessException {
        clearTable("event");
    }
}
