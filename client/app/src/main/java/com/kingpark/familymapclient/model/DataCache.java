package com.kingpark.familymapclient.model;

import com.kingpark.familymapclient.network.result.DataResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataCache {
    private static DataCache gInstance;
    
    public static DataCache getInstance() {
        if (gInstance == null) gInstance = new DataCache();
        return gInstance;
    }
    
    private boolean mIsLoggedIn;
    private String mHostName;
    private int mPortNumber;
    private Map<String,Person> mPeople;
    private Map<String,Event> mEvents;
    private String mUsername;
    private Person mUserPerson;
    private Event mMapStartEvent;
    private Map<String,MapColor> mEventTypeColors;
    private Map<String,List<Event>> mPersonEvents;
    private Set<String> mUserPaternalAncestors;
    private Set<String> mUserMaternalAncestors;
    private Map<String,List<Person>> mPersonChildren;
    private Settings mSettings;
    
    private DataCache() {
        clearCache();
    }
    
    public void clearCache() {
        mIsLoggedIn = false;
        if (mHostName == null) mHostName = "10.0.2.2";
        if (mPortNumber < 1) mPortNumber = 8080;
        mPeople = new HashMap<>();
        mEvents = new HashMap<>();
        if (mUsername == null) mUsername = "";
        mUserPerson = null;
        mMapStartEvent = null;
        mEventTypeColors = new HashMap<>();
        mPersonEvents = new HashMap<>();
        mUserPaternalAncestors = new HashSet<>();
        mUserMaternalAncestors = new HashSet<>();
        mPersonChildren = new HashMap<>();
        mSettings = new Settings();
    }
    
    //~~~~~~~~~~ Setting Data ~~~~~~~~~~
    public void setLoggedIn(boolean isLoggedIn) {
        mIsLoggedIn = isLoggedIn;
    }
    
    public void setHostName(String hostName) {
        mHostName = hostName;
    }
    
    public void setPortNumber(int portNumber) {
        mPortNumber = portNumber;
    }
    
    public void setPeople(Person[] people) {
        mPeople = new HashMap<>();
        for (Person person : people) mPeople.put(person.getId(),person);
    }
    
    public void setEvents(Event[] events) {
        mEvents = new HashMap<>();
        for (Event event : events) {
            mEvents.put(event.getId(),event);
        }
    }
    
    public DataResult configureData(String userPersonId) {
        if (!setUserPerson(userPersonId)) {
            return new DataResult("Could not find person data for the user",false);
        }
    
        setPersonEvents();
        setPersonChildren();
        
        setMapMarkerColors();
        
        setUserPaternalAncestors();
        setUserMaternalAncestors();
    
        if (!setStartEvent()) {
            return new DataResult("Could not set an initial event to focus map on",false);
        }
        
        setLoggedIn(true);
        return new DataResult("Success",true);
    }
    
    public boolean setUsername(String username) {
        if (mUserPerson != null) {
            mUsername = mUserPerson.getUsername();
            return false;
        }
        mUsername = username;
        return true;
    }
    
    private boolean setUserPerson(String userPersonId) {
        for (Person user : getAllPeople()) {
            if (user.getId().equals(userPersonId)) {
                setUsername(user.getUsername());
                mUserPerson = user;
                return true;
            }
        }
        return false;
    }
    
    private boolean setStartEvent() {
        Event startEvent = null;
        for (Event event : getEvents()) {
            if (startEvent != null) startEvent = eventUserRelationCompare(startEvent,event);
            else startEvent = event;
        }
        
        if (startEvent != null) {
            mMapStartEvent = startEvent;
            return true;
        } else return false;
    }
    
    private Event eventUserRelationCompare(Event currStartEvent,Event compareEvent) {
        String userPersonId = mUserPerson.getId();
        boolean isCurrUserEvent = currStartEvent.getPersonID().equals(userPersonId);
        boolean isNewUserEvent = compareEvent.getPersonID().equals(userPersonId);
        
        if (isNewUserEvent) {
            if (isCurrUserEvent) return similarEventCompare(currStartEvent,compareEvent);
            else return compareEvent;
        } else {
            if (isCurrUserEvent) return currStartEvent;
            else return similarEventCompare(currStartEvent,compareEvent);
        }
    }
    
    private Event similarEventCompare(Event currStartEvent,Event compareEvent) {
        String typeBirth = "birth";
        if (currStartEvent.getEventType().toLowerCase().contains(typeBirth)) {
            if (compareEvent.getEventType().toLowerCase().contains(typeBirth)) {
                return getLaterBirth(currStartEvent,compareEvent);
            } else return currStartEvent;
        } else {
            if (compareEvent.getEventType().toLowerCase().contains(typeBirth)) return compareEvent;
            else {
                return getEarlierEvent(currStartEvent,compareEvent);
            }
        }
    }
    
    private Event getLaterBirth(Event currStartEvent,Event compareEvent) {
        if (currStartEvent.getYear() >= compareEvent.getYear()) return currStartEvent;
        else return compareEvent;
    }
    
    private Event getEarlierEvent(Event currStartEvent,Event compareEvent) {
        if (currStartEvent.getYear() >= compareEvent.getYear()) return compareEvent;
        else return currStartEvent;
    }
    
    private void setMapMarkerColors() {
        mEventTypeColors = new HashMap<>();
        MapColor[] mapColors = MapColor.values();
        setDefaultColors();
        int colorIndex = mEventTypeColors.size();
        
        for (Event event : getAllEvents()) {
            String eventType = event.getEventType().toUpperCase();
            if (!mEventTypeColors.containsKey(eventType)) {
                mEventTypeColors.put(eventType,mapColors[colorIndex]);
                colorIndex = nextColorIndex(colorIndex);
            }
        }
    }
    
    private void setDefaultColors() {
        MapColor[] mapColors = MapColor.values();
        mEventTypeColors.put("BIRTH",mapColors[0]);
        mEventTypeColors.put("MARRIAGE",mapColors[1]);
        mEventTypeColors.put("DEATH",mapColors[2]);
    }
    
    private int nextColorIndex(int prevIndex) {
        int size = MapColor.values().length;
        return (prevIndex + 1) % size;
    }
    
    private void setPersonEvents() {
        mPersonEvents = new HashMap<>();
        
        for (Person person : getAllPeople()) {
            List<Event> events = new ArrayList<>();
            for (Event event : getAllEvents()) {
                if (person.getId().equals(event.getPersonID())) events.add(event);
            }
            Collections.sort(events);
            mPersonEvents.put(person.getId(),events);
        }
    }
    
    private void setUserPaternalAncestors() {
        mUserPaternalAncestors = setAncestors(mUserPerson.getFatherID());
    }
    
    private void setUserMaternalAncestors() {
        mUserMaternalAncestors = setAncestors(mUserPerson.getMotherID());
    }
    
    private Set<String> setAncestors(String addAncestorId) {
        Person ancestor = getPersonById(addAncestorId);
        Set<String> ancestorList = new HashSet<>();
        
        if (ancestor != null) {
            ancestorList.addAll(setAncestors(ancestor.getFatherID()));
            ancestorList.addAll(setAncestors(ancestor.getMotherID()));
            ancestorList.add(addAncestorId);
        }
        
        return ancestorList;
    }
    
    private void setPersonChildren() {
        for (Person child : getAllPeople()) {
            if (child.getFatherID() != null) addAsChild(child.getFatherID(),child);
            if (child.getMotherID() != null) addAsChild(child.getMotherID(),child);
            if (mPersonChildren.get(child.getId()) == null) mPersonChildren.put(child.getId(),new ArrayList<Person>());
        }
    }
    
    private void addAsChild(String parentId,Person child) {
        if (!mPersonChildren.containsKey(parentId)) mPersonChildren.put(parentId,new ArrayList<Person>());
        mPersonChildren.get(parentId).add(child);
    }
    
    public void setSettings(boolean[] settings) {
        mSettings = new Settings(settings);
    }
    
    public void logout() {
        clearCache();
    }
    
    //~~~~~~~~~~ Retrieving Data ~~~~~~~~~~
    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }
    
    public String getHostName() {
        return mHostName;
    }
    
    public int getPortNumber() {
        return mPortNumber;
    }
    
    public List<Person> getPeople() {
        List<Person> people = new ArrayList<>();
        for (Person person : mPeople.values()) if (checkPersonSettings(person.getId())) people.add(person);
        return people;
    }
    
    private List<Person> getAllPeople() {
        return Arrays.asList(mPeople.values().toArray(new Person[0]));
    }
    
    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        for (Event event : mEvents.values()) if (checkEventSettings(event.getPersonID())) events.add(event);
        return events;
    }
    
    private List<Event> getAllEvents() {
        return Arrays.asList(mEvents.values().toArray(new Event[0]));
    }
    
    public String getUsername() {
        return mUsername;
    }
    
    public Person getUserPerson() {
        return mUserPerson;
    }
    
    public Event getMapStartEvent() {
        return mMapStartEvent;
    }
    
    public Person getPersonById(String personId) {
        return mPeople.get(personId);
    }
    
    public Event getEventById(String eventId) {
        return mEvents.get(eventId);
    }
    
    public float getMarkerColor(String eventType) {
        MapColor color = mEventTypeColors.get(eventType.toUpperCase());
        assert color != null;
        return color.getColorHue();
    }
    
    public List<Event> getPersonEvents(String personId) {
        List<Event> checkedEvents = new ArrayList<>();
        if (checkPersonSettings(personId)){
            List<Event> personEvents = mPersonEvents.get(personId);
            for (Event event : personEvents) if (checkEventSettings(event.getPersonID())) checkedEvents.add(event);
        }
        return checkedEvents;
    }
    
    public List<Person> getPersonChildren(String personId) {
        List<Person> checkedChildren = new ArrayList<>();
        if (checkPersonSettings(personId)) {
            List<Person> personChildren = mPersonChildren.get(personId);
            for (Person child : personChildren) if (checkPersonSettings(child.getId())) checkedChildren.add(child);
        }
        return checkedChildren;
    }
    
    public Set<String> getUserPaternalAncestors() {
        return getAncestors(mUserPaternalAncestors);
    }
    
    public Set<String> getUserMaternalAncestors() {
        return getAncestors(mUserMaternalAncestors);
    }
    
    private Set<String> getAncestors(Set<String> userAncestors) {
        Set<String> ancestors = new HashSet<>();
        for (String ancestorId : userAncestors) if (checkPersonSettings(ancestorId)) ancestors.add(ancestorId);
        return ancestors;
    }
    
    public Settings getSettings() {
        return mSettings;
    }
    
    //~~~~~~~~~~ Settings Logic for Getters ~~~~~~~~~~
    public boolean checkPersonSettings(String personId) {
        if (!isIncludedAncestor(personId)) return false;
        if (!isIncludedGender(personId)) return false;
        return true;
    }
    
    private boolean checkEventSettings(String eventPersonId) {
        return checkPersonSettings(eventPersonId);
    }
    
    private boolean isIncludedAncestor(String personId) {
        return includedPaternalSide(personId) || includedMaternalSide(personId) ||
                mUserPerson.getId().equals(personId) || mUserPerson.getSpouseID().equals(personId);
    }
    
    private boolean includedPaternalSide(String personId) {
        if (mSettings.isFatherSide()) return includedAncestor(personId,mUserPaternalAncestors);
        else return false;
    }
    
    private boolean includedMaternalSide(String personId) {
        if (mSettings.isMotherSide()) return includedAncestor(personId,mUserMaternalAncestors);
        else return false;
    }
    
    private boolean includedAncestor(String personId,Set<String> userAncestors) {
        Person person = getPersonById(personId);
        if (person == null) return false;
        String fatherId = person.getFatherID();
        if (fatherId == null) fatherId = "";
        String motherId = person.getMotherID();
        if (motherId == null) motherId = "";
        
        for (String ancestorId : userAncestors) {
            if (personId.equals(ancestorId)) return true;
            if (fatherId.equals(ancestorId)) return true;
            if (motherId.equals(ancestorId)) return true;
        }
        
        return false;
    }
    
    private boolean isIncludedGender(String personId) {
        return includedMale(personId) || includedFemale(personId);
    }
    
    private boolean includedMale(String personId) {
        if (mSettings.isMaleEvents()) return getPersonById(personId).getGender() == 'm';
        else return false;
    }
    
    private boolean includedFemale(String personId) {
        if (mSettings.isFemaleEvents()) return getPersonById(personId).getGender() == 'f';
        else return false;
    }
}
