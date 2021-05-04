package com.kingpark.familymapclient.model;

import com.kingpark.familymapclient.network.result.DataResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class DataCacheTest {
    private DataCache mDataCache;
    
    @Before
    public void setUp() {
        mDataCache = DataCache.getInstance();
        
        mDataCache.setPeople(mPeople);
        mDataCache.setEvents(mEvents);
        DataResult result = mDataCache.configureData(mUserPersonId);
        
        assertTrue(result.getMessage(),result.isSuccess());
    }
    
    private void setSpecialData(Person[] people,Event[] events) {
        DataCache.getInstance().clearCache();
        DataCache.getInstance().setPeople(people);
        DataCache.getInstance().setEvents(events);
        DataCache.getInstance().configureData(mUserPersonId);
    }
    
    @After
    public void tearDown() {
        DataCache.getInstance().clearCache();
    }
    
    @Test
    public void PeopleStorage() {
        List<Person> people = mDataCache.getPeople();
        assertEquals("DataCache returned a different number of Person objects",mPeople.length,people.size());
        getPeopleTest(people);
    }
    
    @Test
    public void NoPeopleStorage() {
        setSpecialData(new Person[0],new Event[0]);
        List<Person> people = mDataCache.getPeople();
        assertEquals("DataCache returned Person objects when there should be none",0,people.size());
    }
    
    private void getPeopleTest(List<Person> people) {
        for (Person personExpected : mPeople) {
            boolean isFound = false;
            for (Person personActual : people) {
                if (personActual.equals(personExpected)) {
                    if (isFound) {
                        fail("Two Person objects found in DataCache for " + personExpected.getFirstName() +
                                " " + personExpected.getLastName() + ". Person ID: " + personExpected.getId());
                    } else {
                        isFound = true;
                    }
                }
            }
            assertTrue("Person object was not found for " + personExpected.getFirstName() +
                            " " + personExpected.getLastName() + ". Person ID: " + personExpected.getId(),
                    isFound);
        }
    }
    
    @Test
    public void EventStorage() {
        List<Event> events = mDataCache.getEvents();
        assertEquals("DataCache returned a different number of Event objects",mEvents.length,events.size());
        getEventTest(events);
    }
    
    @Test
    public void NoEventsStorage() {
        setSpecialData(new Person[0],new Event[0]);
        List<Event> events = mDataCache.getEvents();
        assertEquals("DataCache returned Event objects when there should be none",0,events.size());
    }
    
    private void getEventTest(List<Event> events) {
        for (Event eventExpected : mEvents) {
            boolean isFound = false;
            for (Event eventActual : events) {
                if (eventActual.equals(eventExpected)) {
                    if (isFound) {
                        fail("Duplicate event found for ID " + eventExpected.getId() + " and of event type " +
                                eventExpected.getEventType() + " in the year " + eventExpected.getYear());
                    } else {
                        isFound = true;
                    }
                }
            }
            assertTrue("Event object not found for ID " + eventExpected.getId() + " and of event type " +
                    eventExpected.getEventType() + " in the year " + eventExpected.getYear(),isFound);
        }
    }
    
    @Test
    public void getUserPerson() {
        Person user = DataCache.getInstance().getUserPerson();
        assertNotNull("No user Person object return",user);
        assertEquals("Returned user Person object didn't match",mPersonUser,user);
    }
    
    @Test
    public void noUserPerson() {
        Person[] people = new Person[] {mPersonBrother1,mPersonBrother2,mPersonBrother3,mPersonSister,mPersonFather,mPersonMother};
        setSpecialData(people,mEvents);
        Person user = DataCache.getInstance().getUserPerson();
        
        assertNull("A user Person object was returned",user);
    }
    
    @Test
    public void getMapStartEvent() {
        Event startEvent = DataCache.getInstance().getMapStartEvent();
        assertNotNull("No Event object returned",startEvent);
        assertEquals("DataCache returned the wrong event to start the map with",mEventUserBirth,startEvent);
    }
    
    @Test
    public void nonUserBirthStartUserEvent() {
        Event[] events = new Event[] {mEventUserMisc2,mEventUserMisc1,mEventFatherBirth,mEventFatherMisc5,mEventFatherMisc1};
        setSpecialData(mPeople,events);
        Event startEvent = DataCache.getInstance().getMapStartEvent();
    
        assertNotNull("No Event object returned",startEvent);
        assertEquals("Non User Event selected for start",mUserPersonId,startEvent.getPersonID());
        assertEquals("Event Year -- Expected:" + mEventUserMisc1.getYear() + " Actual:" + startEvent.getYear(),
                mEventUserMisc1,startEvent);
    }
    
    @Test
    public void nonUserBirthStartOtherBirth() {
        Event[] events = new Event[] {mEventFatherBirth,mEventFatherMisc5,mEventFatherMisc1};
        setSpecialData(mPeople,events);
        Event startEvent = DataCache.getInstance().getMapStartEvent();
    
        assertNotNull("No Event object returned",startEvent);
        assertEquals("Birth event should be selected","BIRTH",startEvent.getEventType().toUpperCase());
        assertEquals("Wrong event was selected",mEventFatherBirth,startEvent);
    }
    
    @Test
    public void nonUserBirthStartOtherEvent() {
        Event[] events = new Event[] {mEventFatherMisc5,mEventFatherMisc1};
        setSpecialData(mPeople,events);
        Event expEvent = mEventFatherMisc1;
        Event startEvent = DataCache.getInstance().getMapStartEvent();
    
        assertNotNull("No Event object returned",startEvent);
        assertEquals("Event Year -- Expected:" + expEvent.getYear() + " Actual:" + startEvent.getYear(),
                expEvent,startEvent);
    }
    
    @Test
    public void nonUserBirthStartTwoBirths() {
        Event[] events = new Event[] {mEventSisterBirth,mEventMotherMomBirth};
        setSpecialData(mPeople,events);
        Event expEvent = mEventSisterBirth;
        Event startEvent = DataCache.getInstance().getMapStartEvent();
        
        assertNotNull("No Event object returned",startEvent);
        assertEquals("Event Year -- Expected:" + expEvent.getYear() + " Actual:" + startEvent.getYear(),
                expEvent,startEvent);
    }
    
    @Test
    public void getPersonById() {
        for (Person expectedPerson : mPeople) {
            Person retrievedPerson = DataCache.getInstance().getPersonById(expectedPerson.getId());
            assertNotNull("No person object was found",retrievedPerson);
            assertEquals("Retrieved person was not the same as expected",expectedPerson,retrievedPerson);
        }
    }
    
    @Test
    public void getPersonByIdWrongId() {
        Person[] people = new Person[] {mPersonUser,mPersonFather,mPersonMother};
        setSpecialData(people,mEvents);
        Person retrievedPerson = DataCache.getInstance().getPersonById(mSisterPersonId);
        
        assertNull("Person found with ID that should not be in the Data Cache",retrievedPerson);
    }
    
    @Test
    public void getEventById() {
        for (Event expectedEvent : mEvents) {
            Event retrievedEvent = DataCache.getInstance().getEventById(expectedEvent.getId());
            assertNotNull("No event object was found",retrievedEvent);
            assertEquals("Retrieved event was not the same as expected",expectedEvent,retrievedEvent);
        }
    }
    
    @Test
    public void getEventByIdWrongId() {
        Event[] events = new Event[] {mEventUserBirth,mEventFatherBirth,mEventMotherBirth};
        setSpecialData(mPeople,events);
        Event retrievedEvent = DataCache.getInstance().getEventById(mSisterBirthId);
        
        assertNull("Person found with ID that should not be in the Data Cache",retrievedEvent);
    }
    
    @Test
    public void getMarkerColorDefaultEvents() {
        Event[] events = new Event[] {mEventFatherBirth,mEventFatherMrg,mEventFatherDeath};
        setSpecialData(mPeople,events);
        testMapColorAssignments();
    }
    
    @Test
    public void getMarkerColorLessThanMax() {
        Event[] events = new Event[] {mEventUserBirth,mEventUserMisc1,mEventUserMisc2,mEventUserMisc3,mEventUserMisc4};
        setSpecialData(mPeople,events);
        testMapColorAssignments();
    }
    
    @Test
    public void getMarkerColorMoreThanMax() {
        testMapColorAssignments();
    }
    
   private void testMapColorAssignments() {
       MapColor[] mapColors = MapColor.values();
       Map<String,List<String>> eventTypes = new HashMap<>();
       int[] colorCount = new int[mapColors.length];
    
       for (Event event : DataCache.getInstance().getEvents()) {
           String type = event.getEventType();
           float color = DataCache.getInstance().getMarkerColor(type.toUpperCase());
        
           if (!eventTypes.containsKey(type.toUpperCase())) {
               eventTypes.put(type.toUpperCase(),new ArrayList<String>());
               for (int i = 0; i < mapColors.length; i++) {
                   if (mapColors[i].getColorHue() == color) {
                       colorCount[i]++;
                       break;
                   }
               }
           }
           eventTypes.get(type.toUpperCase()).add(type);
       }
       
       checkColorAssignments(colorCount);
       checkDupTypes(eventTypes);
   }
    
    private void checkColorAssignments(int[] colorCount) {
        int prevCount = colorCount[0];
        for (int i = 1; i < colorCount.length; i++) {
            boolean correctCount = prevCount >= colorCount[i];
            if (!correctCount) {
                StringBuilder failMessage = new StringBuilder();
                failMessage.append("Colors were not assigned in the correct order. Count array: [");
                for (int count : colorCount) failMessage.append(colorCount[count]).append(",");
                failMessage.deleteCharAt(failMessage.length() - 1).append("]");
                fail(failMessage.toString());
            }
        }
    }
    
    private void checkDupTypes(Map<String,List<String>> eventTypes) {
        for (Map.Entry<String,List<String>> typeEntry : eventTypes.entrySet()) {
            if (typeEntry.getValue().size() > 1) {
                Iterator<String> itr = typeEntry.getValue().iterator();
                String type = itr.next();
                float color = DataCache.getInstance().getMarkerColor(type.toUpperCase());
                while (itr.hasNext()) {
                    type = itr.next();
                    float checkColor = DataCache.getInstance().getMarkerColor(type.toUpperCase());
                    assertEquals("Same event types had different colors",color,checkColor,0.0);
                }
            }
        }
    }
    
    @Test
    public void getPersonEventsNoEvents() {
        setSpecialData(mPeople,new Event[0]);
        List<Event> events = DataCache.getInstance().getPersonEvents(mUserPersonId);
        assertNotNull("No list returned",events);
        assertEquals("Events where returned when none were added",0,events.size());
    }
    
    @Test
    public void getPersonEventsOneEvents() {
        List<Event> events = DataCache.getInstance().getPersonEvents(mSisterPersonId);
        assertNotNull("No list returned",events);
        assertEquals("Only one event was expected",1,events.size());
        checkPersonEvents(new Event[] {mEventSisterBirth},events);
    }
    
    @Test
    public void getPersonEventsMultipleEvents() {
        List<Event> actualEvents = DataCache.getInstance().getPersonEvents(mUserPersonId);
        assertNotNull("No list returned",actualEvents);
        assertEquals("Only six events were expected",6,actualEvents.size());
        Event[] expectedEvents = new Event[] {mEventUserBirth,mEventUserMisc1,mEventUserMisc1Dup,
                                                mEventUserMisc2,mEventUserMisc3,mEventUserMisc4};
        checkPersonEvents(expectedEvents,actualEvents);
        
        actualEvents = DataCache.getInstance().getPersonEvents(mMotherPersonId);
        assertNotNull("No list returned",actualEvents);
        assertEquals("Only three events were expected",3,actualEvents.size());
        expectedEvents = new Event[] {mEventMotherBirth,mEventMotherMrg,mEventMotherDeath};
        checkPersonEvents(expectedEvents,actualEvents);
    }
    
    private void checkPersonEvents(Event[] expectedEvents,List<Event> actualEvents) {
        for (Event expEvent : expectedEvents) {
            boolean hasEvent = false;
            for (Event actEvent : actualEvents) {
                if (expEvent.equals(actEvent)) { hasEvent = true; break; }
            }
            if (!hasEvent) {
                Person person = DataCache.getInstance().getPersonById(expEvent.getPersonID());
                fail(person.getFirstName() + " " + person.getLastName() + " " + expEvent.getEventType() + " was not found");
            }
        }
    }
    
    // TODO Test ordering of DataCache Person Events List
    @Test
    public void eventOrderingNormal() {
        List<Event> orderedEvents = DataCache.getInstance().getPersonEvents(mFatherPersonId);
        
        assertNotNull("No list returned",orderedEvents);
        assertEquals("EXPECTED Size:8 ACTUAL Size:" + orderedEvents.size(),8,orderedEvents.size());
        
        checkOrder(mEventFatherBirth,0,orderedEvents);
        checkOrder(mEventFatherMisc2,1,orderedEvents);
        checkOrder(mEventFatherMisc1,2,orderedEvents);
        checkOrder(mEventFatherMisc3,3,orderedEvents);
        checkOrder(mEventFatherMrg,4,orderedEvents);
        checkOrder(mEventFatherMisc4,5,orderedEvents);
        checkOrder(mEventFatherMisc5,6,orderedEvents);
        checkOrder(mEventFatherDeath,7,orderedEvents);
    }
    
    @Test
    public void eventOrderTypeDup() {
        List<Event> orderedEvents = DataCache.getInstance().getPersonEvents(mUserPersonId);
    
        assertNotNull("No list returned",orderedEvents);
        assertEquals("EXPECTED Size:8 ACTUAL Size:" + orderedEvents.size(),6,orderedEvents.size());
    
        checkOrder(mEventUserBirth,0,orderedEvents);
        checkOrder(mEventUserMisc1,1,orderedEvents);
        checkOrder(mEventUserMisc1Dup,2,orderedEvents);
        checkOrder(mEventUserMisc3,3,orderedEvents);
        checkOrder(mEventUserMisc4,4,orderedEvents);
        checkOrder(mEventUserMisc2,5,orderedEvents);
    }
    
    private void checkOrder(Event expected,int actualIndex,List<Event> orderedEvents) {
        assertEquals(compareString(expected,orderedEvents.get(actualIndex)),expected,orderedEvents.get(actualIndex));
    }
    
    private String compareString(Event expected,Event actual) {
        return "EXPECTED Type: " + expected.getEventType() + " ~ Year: " + expected.getYear() + " ~~ ACTUAL Type: "
                + actual.getEventType() + " ~ Year: " + actual.getYear();
    }
    
    @Test
    public void getPersonChildrenNoChildren() {
        List<Person> children = DataCache.getInstance().getPersonChildren(mUserPersonId);
        assertNotNull("No list returned",children);
        assertEquals("People where returned when none were added",0,children.size());
    }
    
    @Test
    public void getPersonChildrenOneChild() {
        List<Person> children = DataCache.getInstance().getPersonChildren(mMotherMomPersonId);
        assertNotNull("No list returned",children);
        assertEquals("Only one child was expected",1,children.size());
        checkPersonChildren(new Person[] {mPersonMother},children);
    }
    
    @Test
    public void getPersonChildrenMultipleChildren() {
        List<Person> actualChildren = DataCache.getInstance().getPersonChildren(mFatherPersonId);
        assertNotNull("No list returned",actualChildren);
        assertEquals("Only five children were expected",5,actualChildren.size());
        Person[] expectedChildren = new Person[] {mPersonBrother1,mPersonBrother2,mPersonSister,mPersonUser,mPersonBrother3};
        checkPersonChildren(expectedChildren,actualChildren);
    }
    
    private void checkPersonChildren(Person[] expectedChildren,List<Person> actualChildren) {
        for (Person expChild : expectedChildren) {
            boolean hasChild = false;
            for (Person actChild : actualChildren) {
                if (expChild.equals(actChild)) { hasChild = true; break; }
            }
            if (!hasChild) {
                fail(expChild.getFirstName() + " " + expChild.getLastName() + " was not found");
            }
        }
    }
    
    @Test
    public void getUserPaternalAncestorsNone() {
        setSpecialData(new Person[0],new Event[0]);
        Set<String> ancestors = DataCache.getInstance().getUserPaternalAncestors();
        assertNotNull("No list returned",ancestors);
        assertEquals("People returned when none were added",0,ancestors.size());
    }
    
    @Test
    public void getUserPaternalAncestorsNormal() {
        Set<String> actAncestors = DataCache.getInstance().getUserPaternalAncestors();
        assertNotNull("No list returned",actAncestors);
        assertEquals("Only three ancestors were expected",3,actAncestors.size());
        Person[] expAncestors = new Person[] {mPersonFather,mPersonFatherDad,mPersonFatherMom};
        checkAncestors(expAncestors,actAncestors);
    }
    
    @Test
    public void getUserMaternalAncestorsNone() {
        setSpecialData(new Person[0],new Event[0]);
        Set<String> ancestors = DataCache.getInstance().getUserMaternalAncestors();
        assertNotNull("No list returned",ancestors);
        assertEquals("People returned when none were added",0,ancestors.size());
    }
    
    @Test
    public void getUserMaternalAncestorsNormal() {
        Set<String> actAncestors = DataCache.getInstance().getUserMaternalAncestors();
        assertNotNull("No list returned",actAncestors);
        assertEquals("Only three ancestors were expected",3,actAncestors.size());
        Person[] expAncestors = new Person[] {mPersonMother,mPersonMotherDad,mPersonMotherMom};
        checkAncestors(expAncestors,actAncestors);
    }
    
    private void checkAncestors(Person[] expAncestors,Set<String> actAncestors) {
        for (Person expPerson : expAncestors) {
            boolean hasAncestor = false;
            for (String personId : actAncestors) {
                Person actPerson = DataCache.getInstance().getPersonById(personId);
                if (expPerson.equals(actPerson)) { hasAncestor = true; break; }
            }
            if (!hasAncestor) {
                fail(expPerson.getFirstName() + " " + expPerson.getLastName() + " was not found");
            }
        }
    }
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Person Objects ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private String mUsername = "kingpark";
    
    private String mUserPersonId = "d13c-8bb9-76b0-6d0f";
    private String mUserFirstName = "Parker";
    private String mUserLastName = "King";
    private char mUserGender = 'm';
    private String mUserFatherId = "2bb8-1f38-1776-447f";
    private String mUserMotherId = "35a9-cbb5-34d9-7d9a";
    private String mUserSpouseId = "";
    private Person mPersonUser = new Person(mUserPersonId,mUsername,mUserFirstName,mUserLastName,
                                          mUserGender,mUserFatherId,mUserMotherId,mUserSpouseId);
    
    private String mBrother1PersonId = "926f-1112-d0c3-e947";
    private String mBrother1FirstName = "Andrew";
    private String mBrother1LastName = "King";
    private char mBrother1Gender = 'm';
    private Person mPersonBrother1 = new Person(mBrother1PersonId,mUsername,mBrother1FirstName,mBrother1LastName,
                                                      mBrother1Gender,mUserFatherId,mUserMotherId,mUserSpouseId);
    
    private String mBrother2PersonId = "35ba-2b9a-4d30-f473";
    private String mBrother2FirstName = "Kyle";
    private String mBrother2LastName = "King";
    private char mBrother2Gender = 'm';
    private Person mPersonBrother2 = new Person(mBrother2PersonId,mUsername,mBrother2FirstName,mBrother2LastName,
                                                      mBrother2Gender,mUserFatherId,mUserMotherId,mUserSpouseId);
    
    private String mSisterPersonId = "11c5-cd08-a1dc-a816";
    private String mSisterFirstName = "Amanda";
    private String mSisterLastName = "King";
    private char mSisterGender = 'f';
    private Person mPersonSister = new Person(mSisterPersonId,mUsername,mSisterFirstName,mSisterLastName,
                                                mSisterGender,mUserFatherId,mUserMotherId,mUserSpouseId);
    
    private String mBrother3PersonId = "2393-f2db-937f-e6ba";
    private String mBrother3FirstName = "Dillon";
    private String mBrother3LastName = "King";
    private char mBrother3Gender = 'm';
    private Person mPersonBrother3 = new Person(mBrother3PersonId,mUsername,mBrother3FirstName,mBrother3LastName,
                                                      mBrother3Gender,mUserFatherId,mUserMotherId,mUserSpouseId);
    
    private String mFatherPersonId = "2bb8-1f38-1776-447f";
    private String mFatherFirstName = "Sol";
    private String mFatherLastName = "Nigro";
    private char mFatherGender = 'm';
    private String mFatherFatherId = "3416-8db3-184a-f963";
    private String mFatherMotherId = "ebbb-33cd-8e73-c12c";
    private String mFatherSpouseId = "35a9-cbb5-34d9-7d9a";
    private Person mPersonFather = new Person(mFatherPersonId,mUsername,mFatherFirstName,mFatherLastName,
                                          mFatherGender,mFatherFatherId,mFatherMotherId,mFatherSpouseId);
    
    private String mMotherPersonId = "35a9-cbb5-34d9-7d9a";
    private String mMotherFirstName = "Criselda";
    private String mMotherLastName = "Seeger";
    private char mMotherGender = 'f';
    private String mMotherFatherId = "f5fb-8d99-2197-da8d";
    private String mMotherMotherId = "d7a1-4ad0-7ecd-a63c";
    private String mMotherSpouseId = "2bb8-1f38-1776-447f";
    private Person mPersonMother = new Person(mMotherPersonId,mUsername,mMotherFirstName,mMotherLastName,
                                          mMotherGender,mMotherFatherId,mMotherMotherId,mMotherSpouseId);
    
    private String mFatherDadPersonId = "3416-8db3-184a-f963";
    private String mFatherDadFirstName = "Nick";
    private String mFatherDadLastName = "Cosgrove";
    private char mFatherDadGender = 'm';
    private String mFatherDadFatherId = "";
    private String mFatherDadMotherId = "";
    private String mFatherDadSpouseId = "ebbb-33cd-8e73-c12c";
    private Person mPersonFatherDad = new Person(mFatherDadPersonId,mUsername,mFatherDadFirstName,mFatherDadLastName,
                                          mFatherDadGender,mFatherDadFatherId,mFatherDadMotherId,mFatherDadSpouseId);
    
    private String mFatherMomPersonId = "ebbb-33cd-8e73-c12c";
    private String mFatherMomFirstName = "Burma";
    private String mFatherMomLastName = "Lugo";
    private char mFatherMomGender = 'f';
    private String mFatherMomFatherId = "";
    private String mFatherMomMotherId = "";
    private String mFatherMomSpouseId = "3416-8db3-184a-f963";
    private Person mPersonFatherMom = new Person(mFatherMomPersonId,mUsername,mFatherMomFirstName,mFatherMomLastName,
                                          mFatherMomGender,mFatherMomFatherId,mFatherMomMotherId,mFatherMomSpouseId);
    
    private String mMotherDadPersonId = "f5fb-8d99-2197-da8d";
    private String mMotherDadFirstName = "Brandon";
    private String mMotherDadLastName = "Cubbage";
    private char mMotherDadGender = 'm';
    private String mMotherDadFatherId = "";
    private String mMotherDadMotherId = "";
    private String mMotherDadSpouseId = "d7a1-4ad0-7ecd-a63c";
    private Person mPersonMotherDad = new Person(mMotherDadPersonId,mUsername,mMotherDadFirstName,mMotherDadLastName,
                                          mMotherDadGender,mMotherDadFatherId,mMotherDadMotherId,mMotherDadSpouseId);
    
    private String mMotherMomPersonId = "d7a1-4ad0-7ecd-a63c";
    private String mMotherMomFirstName = "Elnora";
    private String mMotherMomLastName = "Paxton";
    private char mMotherMomGender = 'f';
    private String mMotherMomFatherId = "";
    private String mMotherMomMotherId = "";
    private String mMotherMomSpouseId = "f5fb-8d99-2197-da8d";
    private Person mPersonMotherMom = new Person(mMotherMomPersonId,mUsername,mMotherMomFirstName,mMotherMomLastName,
                                          mMotherMomGender,mMotherMomFatherId,mMotherMomMotherId,mMotherMomSpouseId);
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Event Objects ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private String mUserBirthId = "3805-d6ce-904e-997e";
    private float mUserBirthLat = 2.8167f;
    private float mUserBirthLng = -59.333f;
    private String mUserBirthCountry = "Brazil";
    private String mUserBirthCity = "Boa Vista";
    private String mUserBirthType = "birth";
    private int mUserBirthYear = 1994;
    Event mEventUserBirth = new Event(mUserBirthId,mUsername,mUserPersonId,mUserBirthLat,mUserBirthLng,
                                       mUserBirthCountry,mUserBirthCity,mUserBirthType,mUserBirthYear);
    
    private String mUserMisc1Id = "337c-85c8-6ad2-ea96";
    private float mUserMisc1Lat = -11.033f;
    private float mUserMisc1Lng = -37.5167f;
    private String mUserMisc1Country = "Brazil";
    private String mUserMisc1City = "Salvador";
    private String mUserMisc1Type = "First High School Play";
    private int mUserMisc1Year = 2010;
    Event mEventUserMisc1 = new Event(mUserMisc1Id,mUsername,mUserPersonId,mUserMisc1Lat,mUserMisc1Lng,
            mUserMisc1Country,mUserMisc1City,mUserMisc1Type,mUserMisc1Year);
    
    private String mUserMisc1DupId = "bd01-5f25-158a-4139";
    private float mUserMisc1DupLat = -11.0333f;
    private float mUserMisc1DupLng = -37.5167f;
    private String mUserMisc1DupCountry = "Brazil";
    private String mUserMisc1DupCity = "Salvador";
    private String mUserMisc1DupType = "FIRST HIGH SCHOOL PLAY";
    private int mUserMisc1DupYear = 2010;
    Event mEventUserMisc1Dup = new Event(mUserMisc1DupId,mUsername,mUserPersonId,mUserMisc1DupLat,mUserMisc1DupLng,
            mUserMisc1DupCountry,mUserMisc1DupCity,mUserMisc1DupType,mUserMisc1DupYear);
    
    private String mUserMisc2Id = "d944-9000-d11f-b866";
    private float mUserMisc2Lat = 54.9833f;
    private float mUserMisc2Lng = 73.3667f;
    private String mUserMisc2Country = "Russia";
    private String mUserMisc2City = "Omsk";
    private String mUserMisc2Type = "BYU Graduation";
    private int mUserMisc2Year = 2021;
    Event mEventUserMisc2 = new Event(mUserMisc2Id,mUsername,mUserPersonId,mUserMisc2Lat,mUserMisc2Lng,
            mUserMisc2Country,mUserMisc2City,mUserMisc2Type,mUserMisc2Year);
    
    private String mUserMisc3Id = "9298-dd17-5025-36a5";
    private float mUserMisc3Lat = 41.15f;
    private float mUserMisc3Lng = -103.2f;
    private String mUserMisc3Country = "United States";
    private String mUserMisc3City = "Cheyenne";
    private String mUserMisc3Type = "High School Graduation";
    private int mUserMisc3Year = 2013;
    Event mEventUserMisc3 = new Event(mUserMisc3Id,mUsername,mUserPersonId,mUserMisc3Lat,mUserMisc3Lng,
            mUserMisc3Country,mUserMisc3City,mUserMisc3Type,mUserMisc3Year);
    
    private String mUserMisc4Id = "2157-c405-19c5-9e39";
    private float mUserMisc4Lat = -4.8667f;
    private float mUserMisc4Lng = 119.4167f;
    private String mUserMisc4Country = "Indonesia";
    private String mUserMisc4City = "Makassar";
    private String mUserMisc4Type = "Started Mission";
    private int mUserMisc4Year = 2013;
    Event mEventUserMisc4 = new Event(mUserMisc4Id,mUsername,mUserPersonId,mUserMisc4Lat,mUserMisc4Lng,
            mUserMisc4Country,mUserMisc4City,mUserMisc4Type,mUserMisc4Year);
    
    private String mBrother1BirthId = "fe83-33eb-2272-f4dc";
    private float mBrother1BirthLat = 35.9f;
    private float mBrother1BirthLng = 14.5167f;
    private String mBrother1BirthCountry = "Malta";
    private String mBrother1BirthCity = "Valletta";
    private String mBrother1BirthType = "birth";
    private int mBrother1BirthYear = 1991;
    Event mEventBrother1Birth = new Event(mBrother1BirthId,mUsername,mBrother1PersonId,mBrother1BirthLat,mBrother1BirthLng,
            mBrother1BirthCountry,mBrother1BirthCity,mBrother1BirthType,mBrother1BirthYear);
    
    private String mBrother2BirthId = "4e14-bd4f-295a-8d85";
    private float mBrother2BirthLat = 36.0667f;
    private float mBrother2BirthLng = 120.3833f;
    private String mBrother2BirthCountry = "China";
    private String mBrother2BirthCity = "Qingdao";
    private String mBrother2BirthType = "birth";
    private int mBrother2BirthYear = 1992;
    Event mEventBrother2Birth = new Event(mBrother2BirthId,mUsername,mBrother2PersonId,mBrother2BirthLat,mBrother2BirthLng,
            mBrother2BirthCountry,mBrother2BirthCity,mBrother2BirthType,mBrother2BirthYear);
    
    private String mSisterBirthId = "8021-2d55-53a5-1c17";
    private float mSisterBirthLat = 6.8f;
    private float mSisterBirthLng = -57.8333f;
    private String mSisterBirthCountry = "Guyana";
    private String mSisterBirthCity = "Georgetown";
    private String mSisterBirthType = "birth";
    private int mSisterBirthYear = 1990;
    Event mEventSisterBirth = new Event(mSisterBirthId,mUsername,mSisterPersonId,mSisterBirthLat,mSisterBirthLng,
            mSisterBirthCountry,mSisterBirthCity,mSisterBirthType,mSisterBirthYear);
    
    private String mBrother3BirthId = "e632-1d4a-8e70-4265";
    private float mBrother3BirthLat = 22.25f;
    private float mBrother3BirthLng = -96.1333f;
    private String mBrother3BirthCountry = "Mexico";
    private String mBrother3BirthCity = "Tampico";
    private String mBrother3BirthType = "birth";
    private int mBrother3BirthYear = 1998;
    Event mEventBrother3Birth = new Event(mBrother3BirthId,mUsername,mBrother3PersonId,mBrother3BirthLat,mBrother3BirthLng,
            mBrother3BirthCountry,mBrother3BirthCity,mBrother3BirthType,mBrother3BirthYear);
    
    private String mFatherBirthId = "2d71-2a26-800d-dea2";
    private float mFatherBirthLat = -40.85f;
    private float mFatherBirthLng = -70.7f;
    private String mFatherBirthCountry = "Argentina";
    private String mFatherBirthCity = "San Carlos de Bariloche";
    private String mFatherBirthType = "BIRTH";
    private int mFatherBirthYear = 1956;
    Event mEventFatherBirth = new Event(mFatherBirthId,mUsername,mFatherPersonId,mFatherBirthLat,mFatherBirthLng,
            mFatherBirthCountry,mFatherBirthCity,mFatherBirthType,mFatherBirthYear);
    
    private String mFatherMrgId = "f2f4-6303-39d6-6008";
    private float mFatherMrgLat = -66.433f;
    private float mFatherMrgLng = -67.8667f;
    private String mFatherMrgCountry = "United Kingdom";
    private String mFatherMrgCity = "Rothera";
    private String mFatherMrgType = "Marriage";
    private int mFatherMrgYear = 1985;
    Event mEventFatherMrg = new Event(mFatherMrgId,mUsername,mFatherPersonId,mFatherMrgLat,mFatherMrgLng,
            mFatherMrgCountry,mFatherMrgCity,mFatherMrgType,mFatherMrgYear);
    
    private String mFatherDeathId = "22bb-44fe-c149-2f27";
    private float mFatherDeathLat = 27.4833f;
    private float mFatherDeathLng = 95.0f;
    private String mFatherDeathCountry = "India";
    private String mFatherDeathCity = "Dibrugarh";
    private String mFatherDeathType = "death";
    private int mFatherDeathYear = 2039;
    Event mEventFatherDeath = new Event(mFatherDeathId,mUsername,mFatherPersonId,mFatherDeathLat,mFatherDeathLng,
            mFatherDeathCountry,mFatherDeathCity,mFatherDeathType,mFatherDeathYear);
    
    private String mFatherMisc1Id = "76fe-8a15-6e99-3da6";
    private float mFatherMisc1Lat = 53.5333f;
    private float mFatherMisc1Lng = -112.5f;
    private String mFatherMisc1Country = "Canada";
    private String mFatherMisc1City = "Edmonton";
    private String mFatherMisc1Type = "Graduate high school";
    private int mFatherMisc1Year = 1974;
    Event mEventFatherMisc1 = new Event(mFatherMisc1Id,mUsername,mFatherPersonId,mFatherMisc1Lat,mFatherMisc1Lng,
            mFatherMisc1Country,mFatherMisc1City,mFatherMisc1Type,mFatherMisc1Year);
    
    private String mFatherMisc2Id = "f40f-5ae4-c819-20a2";
    private float mFatherMisc2Lat = 33.533f;
    private float mFatherMisc2Lng = -6.4167f;
    private String mFatherMisc2Country = "Morocco";
    private String mFatherMisc2City = "Casablanca";
    private String mFatherMisc2Type = "First Car";
    private int mFatherMisc2Year = 1972;
    Event mEventFatherMisc2 = new Event(mFatherMisc2Id,mUsername,mFatherPersonId,mFatherMisc2Lat,mFatherMisc2Lng,
            mFatherMisc2Country,mFatherMisc2City,mFatherMisc2Type,mFatherMisc2Year);
    
    private String mFatherMisc3Id = "d7e5-9e3a-c7cc-9607";
    private float mFatherMisc3Lat = 13.5167f;
    private float mFatherMisc3Lng = 2.1f;
    private String mFatherMisc3Country = "Niger";
    private String mFatherMisc3City = "Niamey";
    private String mFatherMisc3Type = "Design Contest 2nd Place";
    private int mFatherMisc3Year = 1985;
    Event mEventFatherMisc3 = new Event(mFatherMisc3Id,mUsername,mFatherPersonId,mFatherMisc3Lat,mFatherMisc3Lng,
            mFatherMisc3Country,mFatherMisc3City,mFatherMisc3Type,mFatherMisc3Year);
    
    private String mFatherMisc4Id = "2af5-7f90-f730-d098";
    private float mFatherMisc4Lat = 51.4833f;
    private float mFatherMisc4Lng = -2.8167f;
    private String mFatherMisc4Country = "United Kingdom";
    private String mFatherMisc4City = "Cardiff";
    private String mFatherMisc4Type = "Ran a 10k";
    private int mFatherMisc4Year = 1985;
    Event mEventFatherMisc4 = new Event(mFatherMisc4Id,mUsername,mFatherPersonId,mFatherMisc4Lat,mFatherMisc4Lng,
            mFatherMisc4Country,mFatherMisc4City,mFatherMisc4Type,mFatherMisc4Year);
    
    private String mFatherMisc5Id = "a147-d0df-8aa9-9413";
    private float mFatherMisc5Lat = 51.4833f;
    private float mFatherMisc5Lng = -2.8167f;
    private String mFatherMisc5Country = "United Kingdom";
    private String mFatherMisc5City = "Cardiff";
    private String mFatherMisc5Type = "Joined Wood Working Club";
    private int mFatherMisc5Year = 2039;
    Event mEventFatherMisc5 = new Event(mFatherMisc5Id,mUsername,mFatherPersonId,mFatherMisc5Lat,mFatherMisc5Lng,
            mFatherMisc5Country,mFatherMisc5City,mFatherMisc5Type,mFatherMisc5Year);
    
    private String mMotherBirthId = "3f12-78e9-9daa-df9c";
    private float mMotherBirthLat = 48.4333f;
    private float mMotherBirthLng = -122.6333f;
    private String mMotherBirthCountry = "Canada";
    private String mMotherBirthCity = "Victoria";
    private String mMotherBirthType = "Birth";
    private int mMotherBirthYear = 1964;
    Event mEventMotherBirth = new Event(mMotherBirthId,mUsername,mMotherPersonId,mMotherBirthLat,mMotherBirthLng,
            mMotherBirthCountry,mMotherBirthCity,mMotherBirthType,mMotherBirthYear);
    
    private String mMotherMrgId = "265e-e772-9c80-43e1";
    private float mMotherMrgLat = -66.433f;
    private float mMotherMrgLng = -67.8667f;
    private String mMotherMrgCountry = "United Kingdom";
    private String mMotherMrgCity = "Rothera";
    private String mMotherMrgType = "marriage";
    private int mMotherMrgYear = 1985;
    Event mEventMotherMrg = new Event(mMotherMrgId,mUsername,mMotherPersonId,mMotherMrgLat,mMotherMrgLng,
            mMotherMrgCountry,mMotherMrgCity,mMotherMrgType,mMotherMrgYear);
    
    private String mMotherDeathId = "cc87-e238-45c2-f05f";
    private float mMotherDeathLat = 33.2333f;
    private float mMotherDeathLng = 131.6f;
    private String mMotherDeathCountry = "Japan";
    private String mMotherDeathCity = "Oita";
    private String mMotherDeathType = "DEATH";
    private int mMotherDeathYear = 2045;
    Event mEventMotherDeath = new Event(mMotherDeathId,mUsername,mMotherPersonId,mMotherDeathLat,mMotherDeathLng,
            mMotherDeathCountry,mMotherDeathCity,mMotherDeathType,mMotherDeathYear);
    
    private String mFatherDadBirthId = "8a56-1150-a5b3-5227";
    private float mFatherDadBirthLat = -6.9167f;
    private float mFatherDadBirthLng = -33.1667f;
    private String mFatherDadBirthCountry = "Brazil";
    private String mFatherDadBirthCity = "João Pessoa";
    private String mFatherDadBirthType = "birth";
    private int mFatherDadBirthYear = 1932;
    Event mEventFatherDadBirth = new Event(mFatherDadBirthId,mUsername,mFatherDadPersonId,mFatherDadBirthLat,
            mFatherDadBirthLng,mFatherDadBirthCountry,mFatherDadBirthCity,mFatherDadBirthType,mFatherDadBirthYear);
    
    private String mFatherDadMrgId = "fc63-2067-87d8-3557";
    private float mFatherDadMrgLat = -1.8167f;
    private float mFatherDadMrgLng = -78.1167f;
    private String mFatherDadMrgCountry = "Ecuador";
    private String mFatherDadMrgCity = "Guayaquil";
    private String mFatherDadMrgType = "MARRIAGE";
    private int mFatherDadMrgYear = 1949;
    Event mEventFatherDadMrg = new Event(mFatherDadMrgId,mUsername,mFatherDadPersonId,mFatherDadMrgLat,
            mFatherDadMrgLng,mFatherDadMrgCountry,mFatherDadMrgCity,mFatherDadMrgType,mFatherDadMrgYear);
    
    private String mFatherDadDeathId = "a437-8cd6-71e3-f024";
    private float mFatherDadDeathLat = 38.05f;
    private float mFatherDadDeathLng = 114.5f;
    private String mFatherDadDeathCountry = "China";
    private String mFatherDadDeathCity = "Shijiazhuang";
    private String mFatherDadDeathType = "Death";
    private int mFatherDadDeathYear = 2007;
    Event mEventFatherDadDeath = new Event(mFatherDadDeathId,mUsername,mFatherDadPersonId,mFatherDadDeathLat,
            mFatherDadDeathLng,mFatherDadDeathCountry,mFatherDadDeathCity,mFatherDadDeathType,mFatherDadDeathYear);
    
    private String mFatherMomBirthId = "8c46-07e2-570e-69c9";
    private float mFatherMomBirthLat = 79.9833f;
    private float mFatherMomBirthLng = -84.0667f;
    private String mFatherMomBirthCountry = "Canada";
    private String mFatherMomBirthCity = "Eureka";
    private String mFatherMomBirthType = "birth";
    private int mFatherMomBirthYear = 1922;
    Event mEventFatherMomBirth = new Event(mFatherMomBirthId,mUsername,mFatherMomPersonId,mFatherMomBirthLat,
            mFatherMomBirthLng,mFatherMomBirthCountry,mFatherMomBirthCity,mFatherMomBirthType,mFatherMomBirthYear);
    
    private String mFatherMomMrgId = "6eb7-561e-f00b-54a9";
    private float mFatherMomMrgLat = -1.8167f;
    private float mFatherMomMrgLng = -78.1167f;
    private String mFatherMomMrgCountry = "Ecuador";
    private String mFatherMomMrgCity = "Guayaquil";
    private String mFatherMomMrgType = "marriage";
    private int mFatherMomMrgYear = 1949;
    Event mEventFatherMomMrg = new Event(mFatherMomMrgId,mUsername,mFatherMomPersonId,mFatherMomMrgLat,
            mFatherMomMrgLng,mFatherMomMrgCountry,mFatherMomMrgCity,mFatherMomMrgType,mFatherMomMrgYear);
    
    private String mFatherMomDeathId = "d473-fedf-e3df-edee";
    private float mFatherMomDeathLat = 34.6833f;
    private float mFatherMomDeathLng = 135.2f;
    private String mFatherMomDeathCountry = "Japan";
    private String mFatherMomDeathCity = "Kobe";
    private String mFatherMomDeathType = "death";
    private int mFatherMomDeathYear = 1986;
    Event mEventFatherMomDeath = new Event(mFatherMomDeathId,mUsername,mFatherMomPersonId,mFatherMomDeathLat,
            mFatherMomDeathLng,mFatherMomDeathCountry,mFatherMomDeathCity,mFatherMomDeathType,mFatherMomDeathYear);
    
    private String mMotherDadBirthId = "c358-f522-72d2-540f";
    private float mMotherDadBirthLat = 39.5333f;
    private float mMotherDadBirthLng = -118.1833f;
    private String mMotherDadBirthCountry = "United States";
    private String mMotherDadBirthCity = "Reno";
    private String mMotherDadBirthType = "Birth";
    private int mMotherDadBirthYear = 1922;
    Event mEventMotherDadBirth = new Event(mMotherDadBirthId,mUsername,mMotherDadPersonId,mMotherDadBirthLat,
            mMotherDadBirthLng,mMotherDadBirthCountry,mMotherDadBirthCity,mMotherDadBirthType,mMotherDadBirthYear);
    
    private String mMotherDadMrgId = "a492-0731-cd6a-8cca";
    private float mMotherDadMrgLat = 39.0167f;
    private float mMotherDadMrgLng = 125.733f;
    private String mMotherDadMrgCountry = "North Korea";
    private String mMotherDadMrgCity = "Pyongyang";
    private String mMotherDadMrgType = "Marriage";
    private int mMotherDadMrgYear = 1961;
    Event mEventMotherDadMrg = new Event(mMotherDadMrgId,mUsername,mMotherDadPersonId,mMotherDadMrgLat,
            mMotherDadMrgLng,mMotherDadMrgCountry,mMotherDadMrgCity,mMotherDadMrgType,mMotherDadMrgYear);
    
    private String mMotherDadDeathId = "66d6-8507-0636-62d2";
    private float mMotherDadDeathLat = 27.4667f;
    private float mMotherDadDeathLng = 89.65f;
    private String mMotherDadDeathCountry = "Bhutan";
    private String mMotherDadDeathCity = "Thimphu";
    private String mMotherDadDeathType = "Death";
    private int mMotherDadDeathYear = 1987;
    Event mEventMotherDadDeath = new Event(mMotherDadDeathId,mUsername,mMotherDadPersonId,mMotherDadDeathLat,
            mMotherDadDeathLng,mMotherDadDeathCountry,mMotherDadDeathCity,mMotherDadDeathType,mMotherDadDeathYear);
    
    private String mMotherMomBirthId = "0a1b-5819-735e-d12e";
    private float mMotherMomBirthLat = 70.2f;
    private float mMotherMomBirthLng = -147.4833f;
    private String mMotherMomBirthCountry = "United States";
    private String mMotherMomBirthCity = "Deadhorse";
    private String mMotherMomBirthType = "BIRTH";
    private int mMotherMomBirthYear = 1923;
    Event mEventMotherMomBirth = new Event(mMotherMomBirthId,mUsername,mMotherMomPersonId,mMotherMomBirthLat,
            mMotherMomBirthLng,mMotherMomBirthCountry,mMotherMomBirthCity,mMotherMomBirthType,mMotherMomBirthYear);
    
    private String mMotherMomMrgId = "ad05-d347-1f17-e4d6";
    private float mMotherMomMrgLat = 39.0167f;
    private float mMotherMomMrgLng = 125.7333f;
    private String mMotherMomMrgCountry = "North Korea";
    private String mMotherMomMrgCity = "Pyongyang";
    private String mMotherMomMrgType = "MARRIAGE";
    private int mMotherMomMrgYear = 1961;
    Event mEventMotherMomMrg = new Event(mMotherMomMrgId,mUsername,mMotherMomPersonId,mMotherMomMrgLat,
            mMotherMomMrgLng,mMotherMomMrgCountry,mMotherMomMrgCity,mMotherMomMrgType,mMotherMomMrgYear);
    
    private String mMotherMomDeathId = "c78b-d3d5-d63d-d1af";
    private float mMotherMomDeathLat = 50.45f;
    private float mMotherMomDeathLng = 30.5167f;
    private String mMotherMomDeathCountry = "Ukraine";
    private String mMotherMomDeathCity = "Kiev";
    private String mMotherMomDeathType = "DEATH";
    private int mMotherMomDeathYear = 2005;
    Event mEventMotherMomDeath = new Event(mMotherMomDeathId,mUsername,mMotherMomPersonId,mMotherMomDeathLat,
            mMotherMomDeathLng,mMotherMomDeathCountry,mMotherMomDeathCity,mMotherMomDeathType,mMotherMomDeathYear);
    
    Person[] mPeople = new Person[] {mPersonUser,mPersonBrother1,mPersonBrother2,mPersonSister,mPersonBrother3,
            mPersonFather,mPersonMother,mPersonFatherDad,mPersonFatherMom,mPersonMotherDad,mPersonMotherMom};
    Event[] mEvents = new Event[] {mEventUserBirth,mEventUserMisc1,mEventUserMisc1Dup,mEventUserMisc2,mEventUserMisc3,
            mEventUserMisc4,mEventBrother1Birth,mEventBrother2Birth,mEventSisterBirth,mEventBrother3Birth,
            mEventFatherBirth,mEventFatherMrg,mEventFatherDeath,mEventFatherMisc1,mEventFatherMisc2,
            mEventFatherMisc3,mEventFatherMisc4,mEventFatherMisc5,mEventMotherBirth,mEventMotherMrg,mEventMotherDeath,
            mEventFatherDadBirth,mEventFatherDadMrg,mEventFatherDadDeath,mEventFatherMomBirth,mEventFatherMomMrg,
            mEventFatherMomDeath,mEventMotherDadBirth,mEventMotherDadMrg,mEventMotherDadDeath,mEventMotherMomBirth,
            mEventMotherMomMrg,mEventMotherMomDeath};
}