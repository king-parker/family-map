package request_result.result;

/**
 * This is the base class of the result objects. It contains the message and success
 * variables required by each class and their getters.
 */
public abstract class Result {
    protected String message;
    protected boolean success;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
