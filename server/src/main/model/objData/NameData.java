package model.objData;

/**
 * Class to hold names to be used when generating new person objects
 */
public class NameData {
    private String[] data;

    /**
     * Creates a new data storage object for female, male or last names
     * @param data name data to store
     */
    public NameData(String[] data) {
        this.data = data;
    }

    public String[] getData() {
        return data;
    }
}
