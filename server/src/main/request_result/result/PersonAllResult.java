package request_result.result;

import com.google.gson.annotations.SerializedName;
import model.Person;

import java.util.List;

/**
 * Class that holds the result of a request to receive all persons associated with a user
 */
public class PersonAllResult extends Result {
    @SerializedName("data")
    private Person[] people;

    /**
     * Creates an object for a successful person retrieval
     * @param people array of person objects associated with the current user
     */
    public PersonAllResult(List<Person> people) {
        this.people = people.toArray(new Person[people.size()]);
        success = true;
    }

    /**
     * Creates an object for an unsuccessful event retrieval
     * @param message Error message of what went wrong
     */
    public PersonAllResult(String message) {
        this.message = message;
        success = false;
    }

    public Person[] getPeople() {
        return people;
    }
}
