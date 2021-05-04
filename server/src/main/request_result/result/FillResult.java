package request_result.result;

/**
 * Class that holds the results of a Fill request
 */
public class FillResult extends Result {
    /**
     * Creates an object for both a successful and unsuccessful request
     * @param message message of what the result was
     * @param success True if successful, false if not
     */
    public FillResult(String message,boolean success){
        this.message = message;
        this.success =success;
    }
}
