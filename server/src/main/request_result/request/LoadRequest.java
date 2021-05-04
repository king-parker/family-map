package request_result.request;

import model.User;
import model.Person;
import model.Event;

/**
 * Class that holds the data necessary to process a load service request
 */
public class LoadRequest {
    private User[] users;
    private Person[] persons;
    private Event[] events;

    /**
     * Constructs an object that contains all the data to be inserted into the database
     * @param users array of user objects to insert into the database
     * @param people array of person objects to insert into the database
     * @param events array of event objects to insert into the database
     */
    public LoadRequest(User[] users,Person[] people,Event[] events) {
        this.users = users;
        this.persons = people;
        this.events = events;
    }

    public User[] getUsers() {
        return users;
    }

    public Person[] getPeople() {
        return persons;
    }

    public Event[] getEvents() {
        return events;
    }
}
