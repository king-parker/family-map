package request_result.request;

/**
 * Class that holds the data necessary to process a fill service request
 */
public class FillRequest {
    private String username;
    private int numGenerations;

    /**
     * Creates fill request object for the given user and uses the default number for
     * amount of generations to fill
     * @param username Username to fill in data for
     */
    public FillRequest(String username) {
        this.username = username;
        numGenerations = 4;
    }

    /**
     * Creates fill request object for the given user to have the given number of generations
     * to fill
     * @param username Username to fill in data for
     * @param numGenerations Number of generations to fill
     */
    public FillRequest(String username, int numGenerations) {
        this.username = username;
        this.numGenerations = numGenerations;
    }

    public String getUsername() {
        return username;
    }

    public int getNumGenerations() {
        return numGenerations;
    }
}
