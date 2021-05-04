package model.objData;

/**
 * Class to hold locations to be used in event generation
 */
public class LocationData {
    private Location[] data;

    /**
     * Creates a new data storage object for locations
     * @param data location data to store
     */
    public LocationData(Location[] data) {
        this.data = data;
    }

    public Location[] getData() {
        return data;
    }
}
