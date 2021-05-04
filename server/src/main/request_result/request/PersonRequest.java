package request_result.request;

/**
 * Class to hold the data necessary to process a person retrieval request
 */
public class PersonRequest extends DataRetrievalRequest {
    private String personID;

    /**
     * Constructs a new request to retrieve a specific person from the database
     * @param personID ID of the person
     * @param authToken token for the logged in user
     */
    public PersonRequest(String personID, String authToken) {
        this.personID = personID;
        this.authToken = authToken;
    }

    public String getPersonID() {
        return personID;
    }
}
