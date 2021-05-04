package services;

import data_access.*;
import model.Event;
import model.objData.Location;
import model.Person;
import model.User;
import request_result.request.FillRequest;
import request_result.result.FillResult;
import utility.DateTime;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import static server.Server.*;

/**
 * Class that preforms the service of filling in the database with person and event data for a specified user
 */
public class FillService extends Service {
    private Set<Person> people;
    private Set<Event> events;
    /**
     * Fills in specified number of generations of person and event data for a specified user
     * @param r Request object containing the user and number of generations
     * @return Result of the fill request
     */
    public FillResult fill(FillRequest r) {
        String service = "Fill";
        String method = "fill";
        logEnter(service,method);

        logger.finer(fieldMsg);
        FillResult result = checkFields(r);
        if (result != null) { logExit(service,method); return result; }

        Database database = new Database();
        try {
            logger.fine("Generating " + r.getNumGenerations() + " generations for " + r.getUsername());
            result = fill(r,database.getConnection());

            database.closeConnection(true);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            logExit(service,method);
            return new FillResult(DATABASE_ERROR,false);
        } finally {
            try {
                if (database.getConnection() != null) database.closeConnection(false);
            } catch (DataAccessException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
            }
        }
        return result;
    }

    /**
     * This method is not used by handlers. It provides functionality to allow the register service
     * to utilize this service as part of it's own service. It is also the base functionality this
     * service.
     * @param r fill request for the service
     * @param conn open connection to database
     * @return fill result of the service
     * @throws DataAccessException thrown when an error occurs when accessing user data, clearing
     * person and event user data or inserting person or event data
     */
    public FillResult fill(FillRequest r,Connection conn) throws DataAccessException {
        if ((LOCATION_DATA == null) || (FEMALE_NAME_DATA == null) ||
                (MALE_NAME_DATA == null) || (SURNAME_DATA == null)) {
            logger.severe("Location and/or Name data was not correctly loaded. " +
                               "Can't generate Person and Event data.");
            return new FillResult(DATABASE_ERROR,false);
        }

        logger.finer("Checking if user is in database");
        User user = checkUser(r.getUsername(),conn);
        if (user == null) return new FillResult(INV_INPUT_ERROR, false);

        logger.finer("Clearing user data");
        clearUserData(r.getUsername(),conn);

        people = new HashSet<>();
        events = new HashSet<>();

        logger.fine("Generating user's data");
        Person userPerson = generateUserPerson(user);
        Event userBirth = generateUserBirth(userPerson);

        int numGens = r.getNumGenerations();
        if (numGens > 0) {
            logger.fine("Generating ancestor data");
            Person father = generateFather(userPerson, userBirth.getYear(), numGens);
            Person mother = generateMother(userPerson, userBirth.getYear(), numGens);

            setRelationships(userPerson,father,mother);

            int userBirthYear = getBirthYear(userPerson);
            int fatherBirth = getBirthYear(father);
            int motherBirth = getBirthYear(mother);
            int marriageDate = generateMarriageEvents(father, fatherBirth, mother, motherBirth, userBirthYear);
            generateDeathEvent(father, fatherBirth, marriageDate, userBirthYear);
            generateDeathEvent(mother, motherBirth, marriageDate, userBirthYear);
        }

        logger.finer("Adding generated data to the database");
        PersonDao pDao = new PersonDao(conn);
        EventDao eDao = new EventDao(conn);
        for (Person person : people) pDao.insert(person);
        for (Event event : events) eDao.insert(event);

        return new FillResult("Successfully added " + people.size() +
                " persons and " + events.size() + " events to the database", true);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Fill Service Helper Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private FillResult checkFields(FillRequest r) {
        if (r.getUsername().isBlank()) return new FillResult(MISS_INPUT_ERROR,false);
        else if (r.getNumGenerations() < 0) return new FillResult(INV_INPUT_ERROR,false);
        else return null;
    }

    private User checkUser(String username,Connection conn) throws DataAccessException {
        UserDao uDao = new UserDao(conn);
        return uDao.find(username);
    }

    private void clearUserData(String username,Connection conn) throws DataAccessException {
        PersonDao pDao = new PersonDao(conn);
        pDao.clearUserData(username);
        EventDao eDao = new EventDao(conn);
        eDao.clearUserData(username);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Data Generation Helper Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //~~~~~~~ Generate Helper Methods ~~~~~~~

    private Person generateUserPerson(User user) {
        Person userPerson = new Person(user.getPersonID(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getGender());
        recordPerson(userPerson);
        return userPerson;
    }

    private Event generateUserBirth(Person user) {
        logger.finer("Generating user birth event");
        logger.finest("Getting user birth location");
        Location birthLocal = randLocation();

        logger.finest("Calculating user age");
        int youngestUser = 13;
        int avgYoungUser = 18;
        int avgOldUser = 25;
        int oldestUser = 30;
        int avgWeight = 3;
        int age = randNum(youngestUser,avgYoungUser,avgOldUser,oldestUser,avgWeight);
        int currentYear = DateTime.getCurrentYear();
        int birthDate = currentYear - age;

        logger.finest("Creating user birth event");
        Event userBirth = new Event(user.getUsername(),user.getPersonID(),birthLocal,Event.BIRTH,birthDate);
        recordEvent(userBirth);
        return userBirth;
    }

    private Person generateFather(Person child,int childBirth,int gensRemaining) throws DataAccessException {
        return generateParent(child,childBirth,randMaleName(),'m',gensRemaining);
    }

    private Person generateMother(Person child,int childBirth,int gensRemaining) throws DataAccessException {
        return generateParent(child,childBirth,randFemName(),'f',gensRemaining);
    }

    private Person generateParent(Person child,int childBirth,String firstName,char personGender,
                                  int gensRemaining) throws DataAccessException {
        if (gensRemaining < 1) return null;
        int gensLeft = gensRemaining - 1;

        logGenPar(personGender,child,gensLeft);
        Person person = new Person(child.getUsername(),firstName,randLastName(),personGender);
        recordPerson(person);

        logGenBirth(personGender,child,gensLeft);
        int personBirth = generateBirthEvent(person,childBirth);


        Person father = generateFather(person,personBirth,gensLeft);
        Person mother = generateMother(person,personBirth,gensLeft);

        if (father != null) {
            logSetRelationships(person,father,mother);
            setRelationships(person,father,mother);
        }

        // If more events are randomly generated, save death dates to bound life events
        if (father != null) generateParentDeaths(personBirth,father,mother);

        return person;
    }

    private int generateBirthEvent(Person person,int childBirth) {
        int earliestBirth;
        int avgEarlyBirth;
        if (person.getGender() == 'm') {
            earliestBirth = childBirth - 80;
            avgEarlyBirth = childBirth - 40;
        }
        else {
            earliestBirth = childBirth - 50;
            avgEarlyBirth = childBirth - 35;
        }
        int avgLateBirth = childBirth - 20;
        int latestBirth = childBirth - 14;
        int avgWeight = 5;

        int birthYear = randNum(earliestBirth,avgEarlyBirth,avgLateBirth,latestBirth,avgWeight);
        Event birth = new Event(person.getUsername(),person.getPersonID(),randLocation(),Event.BIRTH,birthYear);
        recordEvent(birth);

        return birthYear;
    }

    private int generateMarriageEvents(Person husband,int husbandBirth,Person wife,int wifeBirth,int childBirth) {
        int marriageYear = generateMarriageYear(husbandBirth,wifeBirth,childBirth);
        Location location = randLocation();

        logGenEvent(Event.MARRIAGE,husband);
        Event event = generateMarriageEvent(husband,location,marriageYear);
        recordEvent(event);
        logGenEvent(Event.MARRIAGE,wife);
        event = generateMarriageEvent(wife,location,marriageYear);
        recordEvent(event);

        return marriageYear;
    }

    private Event generateMarriageEvent(Person person, Location location, int marriageYear) {
        return new Event(person.getUsername(),person.getPersonID(),location,Event.MARRIAGE,marriageYear);
    }

    private int generateMarriageYear(int husbandBirth, int wifeBirth, int childBirth) {
        int youngAge = 16;
        int avgYoungAge = 20;
        int avgOldAge = 40;
        int oldAge = 100;
        int avgWeight = 4;

        int chanceWedlock = 15;
        if (isWedlock(chanceWedlock)) {
            return wedlockMarriageYear(husbandBirth,wifeBirth,childBirth,avgWeight,
                    youngAge,avgYoungAge,avgOldAge,oldAge);
        }
        else {
            return normalMarriageYear(husbandBirth,wifeBirth,childBirth,avgWeight,
                    youngAge,avgYoungAge,avgOldAge,oldAge);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private int[] generateParentDeaths(int childBirth,Person father,Person mother) {
        int fatherBirth = getBirthYear(father);
        int motherBirth = getBirthYear(mother);
        int marriageDate = generateMarriageEvents(father,fatherBirth,mother,motherBirth,childBirth);

        logGenEvent(Event.DEATH,father);
        int fatherDeath = generateDeathEvent(father,fatherBirth,marriageDate,childBirth);
        logGenEvent(Event.DEATH,father);
        int motherDeath = generateDeathEvent(mother,motherBirth,marriageDate,childBirth);

        return new int[]{fatherDeath,motherDeath};
    }

    private int generateDeathEvent(Person person, int personBirth, int personMarriage, int childBirth) {
        int youngAvgAge = 60;
        int oldAvgAge = 90;
        int maxAge = 120;

        int min = Math.max(personMarriage,childBirth);
        int avgMin = Math.max(personBirth + youngAvgAge,min);
        int avgMax = Math.max(personBirth + oldAvgAge,avgMin);
        int max = Math.max(personBirth + maxAge,avgMax);
        int avgWeight = 5;

        int deathDate = randNum(min,avgMin,avgMax,max,avgWeight);
        Event event = new Event(person.getUsername(),person.getPersonID(),randLocation(),Event.DEATH,deathDate);
        recordEvent(event);

        return deathDate;
    }

    //~~~~~~~ Marriage Helper Methods ~~~~~~~

    private boolean isWedlock(int percentChance) {
        int totalChance = 100;
        int birthEvent = new Random().nextInt(totalChance);
        return birthEvent < percentChance;
    }

    private int wedlockMarriageYear(int husbandBirth, int wifeBirth,int childBirth,int avgWeight,
                                    int youngAge,int avgYoungAge,int avgOldAge,int oldAge) {
        int coupleLatestBirth = Math.max(husbandBirth,wifeBirth);
        int coupleEarliestBirth = Math.min(husbandBirth,wifeBirth);
        int minYear = Math.max(coupleLatestBirth + youngAge,childBirth);
        int avgMin = Math.max(coupleLatestBirth + avgYoungAge,childBirth);
        int avgMax = Math.max(coupleEarliestBirth + avgOldAge,childBirth);
        int maxYear = Math.max(coupleEarliestBirth + oldAge,childBirth);

        return randNum(minYear,avgMin,avgMax,maxYear,avgWeight);
    }

    private int normalMarriageYear(int husbandBirth, int wifeBirth,int childBirth,int avgWeight,
                                   int youngAge,int avgYoungAge,int avgOldAge,int oldAge) {
        int coupleLatestBirth = Math.max(husbandBirth,wifeBirth);
        int coupleEarliestBirth = Math.min(husbandBirth,wifeBirth);
        int minYear = coupleLatestBirth + youngAge;
        int avgMin = Math.max(Math.min(coupleLatestBirth + avgYoungAge,childBirth),minYear);
        int avgMax = Math.max(Math.min(coupleLatestBirth + avgOldAge,childBirth),avgMin);
        int maxYear = Math.max(Math.min(coupleEarliestBirth + oldAge,childBirth),avgMax);

        return randNum(minYear,avgMin,avgMax,maxYear,avgWeight);
    }

    //~~~~~~~ Set Helper Methods ~~~~~~~

    private void setRelationships(Person child,Person father,Person mother) {
        child.setFatherID(father.getPersonID());
        child.setMotherID(mother.getPersonID());
        father.setSpouseID(mother.getPersonID());
        mother.setSpouseID(father.getPersonID());
    }

    //~~~~~~~ Record Helper Methods ~~~~~~~

    private void recordPerson(Person person) {
        getAvailablePersonID(person);
        people.add(person);
    }

    private void recordEvent(Event event) {
        getAvailableEventID(event);
        events.add(event);
    }

    //~~~~~~~ Get Helper Methods ~~~~~~~

    private void getAvailablePersonID(Person checkPerson) {
        boolean isAvailable = false;
        while (!isAvailable) {
            isAvailable = true;
            for (Person person : people) {
                if (checkPerson.getPersonID().equals(person.getPersonID())) {
                    isAvailable = false;
                    checkPerson.updateID();
                    break;
                }
            }
        }
    }

    private void getAvailableEventID(Event checkEvent) {
        boolean isAvailable = false;
        while (!isAvailable) {
            isAvailable = true;
            for (Event event : events) {
                if (checkEvent.getEventID().equals(event.getEventID())) {
                    isAvailable = false;
                    checkEvent.updateID();
                    break;
                }
            }
        }
    }

    private int getBirthYear(Person person) {
        for (Event event : events) {
            if (event.getPersonID().equals(person.getPersonID()) && event.getEventType().equals(Event.BIRTH)) {
                return event.getYear();
            }
        }
        return -1;
    }

    //~~~~~~~ Random Helper Methods ~~~~~~~

    private Location randLocation() {
        return LOCATION_DATA.getData()[new Random().nextInt(LOCATION_DATA.getData().length)];
    }

    private String randFemName() { return randName(FEMALE_NAME_DATA.getData()); }

    private String randMaleName() { return randName(MALE_NAME_DATA.getData()); }

    private String randLastName() { return randName(SURNAME_DATA.getData()); }

    private String randName(String[] names) { return names[new Random().nextInt(names.length)]; }

    private int randNum(int min,int avgLowBound,int avgHighBound,int max,int commonRangeWeight) {
        logger.logp(Level.FINEST,"service.FillService","randNum",
                "ENTRY. min = {0}, avgLowBound = {1}, avgHighBound = {2}, max = {3}",
                new Object[]{min, avgLowBound, avgHighBound, max});

        avgHighBound++;
        max++;
        int range = (max - min) + commonRangeWeight*(avgHighBound - avgLowBound) + 1;
        int bottomResultBound = avgLowBound - min;
        int topResultBound = range - 1 - (max - avgHighBound);
        logger.log(Level.FINEST,"Rand function range: {0}",Integer.toString(range));

        Random random = new Random();
        int randResult = random.nextInt(range);

        if (randResult < bottomResultBound) {
            return min + randResult;
        }
        else if (topResultBound < randResult) {
            return max - (randResult - topResultBound);
        }
        else {
            int inBound = randResult - bottomResultBound;
            int boundChunks = avgHighBound - avgLowBound;
            int rangeAdjuster = (inBound/boundChunks)*boundChunks;
            int ageAdd = inBound - rangeAdjuster;
            return avgLowBound + ageAdd;
        }
    }

    //~~~~~~~ Logging Methods ~~~~~~~

    private void logGenPar(char parentGender,Person child,int gensLeft) {
        logger.finest("Generating " + parentGender + " parent for " + child.getFirstName() + " " +
                child.getLastName() + " who is " + gensLeft + " from the last generation to be created");
    }

    private void logGenBirth(char parentGender, Person child, int gensLeft) {
        logger.finest("Generating " + Event.BIRTH + " event for " + parentGender +
                " parent for " + child.getFirstName() +" " + child.getLastName() +
                " who is " + gensLeft + " from the last generation to be created");
    }

    private void logGenEvent(String eventType,Person person) {
        logger.finest("Generating " + eventType + " event for " +
                  person.getFirstName() + " " + person.getLastName());
    }

    private void logSetRelationships(Person child,Person father,Person mother) {
        logger.finest("Setting the parent child relationships. Child: " +
                child.getFirstName() + " " + child.getLastName() + ", Father: " +
                father.getFirstName() + " " + father.getLastName() + ", Mother: " +
                mother.getFirstName() + " " + mother.getLastName());
    }
}
