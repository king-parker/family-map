package request_result.result;

/**
 * Class that holds the results of a clear request
 */
public class ClearResult extends Result {
    /**
     * Creates a new results object contain the message of the result and if it was successful
     * @param message message service success or what error caused it to fail
     * @param success true if service was successful, false if it failed
     */
    public ClearResult(String message,boolean success){
        this.message = message;
        this.success =success;
    }
}
