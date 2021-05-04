package request_result.result;

/**
 * Class that holds the result data of a load request
 */
public class LoadResult extends Result {
    /**
     * Creates an object for both a successful and unsuccessful request
     * @param message message of what the result was
     * @param success True if successful, false if not
     */
    public LoadResult(String message,boolean success) {
        this.message = message;
        this.success = success;
    }
}
