package com.kingpark.familymapclient.ui.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.kingpark.familymapclient.model.DataCache;
import com.kingpark.familymapclient.model.Event;
import com.kingpark.familymapclient.model.Person;
import com.kingpark.familymapclient.R;
import com.kingpark.familymapclient.ui.activities.PersonActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private final String TAG = "MapFragment";
    public static final String ARG_EVENT_ID = "com.kingpark.familymapclient.map_event_id";
    
    private TextView mEventPerson;
    private TextView mEventDescription;
    private TextView mEventLocation;
    private ImageView mEventIcon;
    private GoogleMap mMap;
    
    private DataCache mData;
    private boolean[] mCheckSettings;
    private Set<Polyline> mEventLines;
    private Set<Marker> mEventMarkers;
    private Event mStartEvent;
    private String mEventId;
    private String mEventPersonId;
    private char mEventPersonGender;
    
    public static MapFragment newInstance(String focusEventId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID,focusEventId);
        
        fragment.setArguments(args);
        return fragment;
    }
    
    public static MapFragment newInstance() {
        return new MapFragment();
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        Log.d(TAG,"In onCreateView() for " + this.getActivity().getLocalClassName());
        View view = inflater.inflate(R.layout.fragment_map,container,false);
    
        mEventPerson = view.findViewById(R.id.event_person_name);
    
        mEventDescription = view.findViewById(R.id.event_description);
    
        mEventLocation = view.findViewById(R.id.event_location);
    
        mEventIcon = view.findViewById(R.id.event_icon);
        mEventIcon.setImageDrawable(makeEventIcon('z'));
        
        mData = DataCache.getInstance();
        mCheckSettings = mData.getSettings().toArray();
    
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"Resuming map fragment in " + this.getActivity().getLocalClassName());
        
        if (!mData.getSettings().equals(mCheckSettings)) {
            updateEventMarkers();
            updateRelationshipLines();
            mCheckSettings = mData.getSettings().toArray();
        }
    }
    
    private Event getArgStartEvent() {
        assert getArguments() != null  && getArguments().containsKey(ARG_EVENT_ID);
        String eventId = getArguments().getString(ARG_EVENT_ID);
        return mData.getEventById(eventId);
    }
    
    private void setEventDescriptionClickListeners() {
        mEventIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPersonActivity();
            }
        });
    
        mEventPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPersonActivity();
            }
        });
    
        mEventDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPersonActivity();
            }
        });
    
        mEventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPersonActivity();
            }
        });
    }
    
    private void startPersonActivity() {
        startActivity(PersonActivity.newIntent(MapFragment.this.getContext(),mEventPersonId));
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
    
        Bundle args = getArguments();
        Log.i(TAG,"Getting starting event for map");
        boolean hasEventArg = (args != null && args.containsKey(ARG_EVENT_ID));
        if (hasEventArg) {
            mStartEvent = getArgStartEvent();
            mEventId = mStartEvent.getId();
            setEventDescriptionView(mStartEvent);
        }
        else mStartEvent = mData.getMapStartEvent();
        
        addEventsToMap();
    
        LatLng startLoc = new LatLng(mStartEvent.getLatitude(),mStartEvent.getLongitude());
        Log.i(TAG,"Going to map start location.");
        Log.v(TAG,"Start location:" +
                "\n\t" + mData.getPersonById(mStartEvent.getPersonID()).getFirstName() +
                " " + mData.getPersonById(mStartEvent.getPersonID()).getLastName() +
                "\n\t" + mStartEvent.getEventType().toUpperCase() + ": " + mStartEvent.getYear() +
                "\n\t" + mStartEvent.getCity() + ", " + mStartEvent.getCountry() +
                "\n\tLat:" + startLoc.latitude + " Long:" + startLoc.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(startLoc));
    }
    
    @Override
    public boolean onMarkerClick(Marker marker) {
        String eventId = (String) marker.getTag();
        
        assert eventId != null;
        Event clickedEvent = mData.getEventById(eventId);
        mEventId = clickedEvent.getId();
        setEventDescriptionView(clickedEvent);
        
        return false;
    }
    
    private void setEventDescriptionView(Event selectedEvent) {
        mEventPersonId = selectedEvent.getPersonID();
        Person eventPerson = mData.getPersonById(mEventPersonId);
    
        String personName;
        if (eventPerson != null && mData.checkPersonSettings(mEventPersonId)) {
            personName = eventPerson.getNameStr();
            mEventPersonGender = eventPerson.getGender();
        } else {
            personName = "No Person Data Found";
            mEventPersonGender = 'z';
        }
    
        mEventPerson.setText(personName);
        mEventDescription.setText(selectedEvent.getDescriptionStr());
        mEventLocation.setText(selectedEvent.getLocationStr());
    
        mEventIcon.setImageDrawable(makeEventIcon(mEventPersonGender));
    
        drawRelationshipLines(selectedEvent);
    
        setEventDescriptionClickListeners();
    }
    
    private void updateEventMarkers() {
        clearMarkers();
        addEventsToMap();
    }
    
    private void addEventsToMap() {
        if (mEventMarkers != null) clearMarkers();
        else mEventMarkers = new HashSet<>();
        
        for (Event event : mData.getEvents()) {
            Log.i(TAG,"Creating Marker for " + event.getDescriptionStr() + " in " + event.getLocationStr());
            createEventMarker(event);
        }
    }
    
    private void createEventMarker(Event event) {
        LatLng location = new LatLng(event.getLatitude(),event.getLongitude());
        float color = mData.getMarkerColor(event.getEventType().toUpperCase());
        
        Marker marker = mMap.addMarker(new MarkerOptions().position(location)
                            .icon(BitmapDescriptorFactory.defaultMarker(color)));
        marker.setTag(event.getId());
        
        mEventMarkers.add(marker);
    }
    
    private Drawable makeEventIcon(char gender) {
        FontAwesomeIcons imageIcon;
        int colorId;
        int dpSize = 40;
        
        gender = Character.toLowerCase(gender);
        if (gender == 'm') {
            imageIcon = FontAwesomeIcons.fa_male;
            colorId = R.color.maleIcon;
        } else if (gender == 'f') {
            imageIcon = FontAwesomeIcons.fa_female;
            colorId = R.color.femaleIcon;
        } else {
            imageIcon = FontAwesomeIcons.fa_question_circle;
            colorId = R.color.noEventSelected;
        }
        
        return new IconDrawable(getActivity(),imageIcon).colorRes(colorId).sizeDp(dpSize);
    }
    
    private void updateRelationshipLines() {
        drawRelationshipLines(mData.getEventById(mEventId));
    }
    
    private void drawRelationshipLines(Event event) {
        if (mEventLines != null) clearLines();
        else mEventLines = new HashSet<>();
        if (mData.getSettings().isLifeStoryLines()) drawLifeStory(event);
        if (mData.getSettings().isFamilyTreeLines()) drawFamilyTree(event);
        if (mData.getSettings().isSpouseLines()) drawSpouseLine(event);
    }
    
    private void drawLifeStory(Event event) {
        List<Event> personEvents = mData.getPersonEvents(event.getPersonID());
        if (personEvents.size() > 1) {
            int color = 0xff0000ff;
            float width = 12f;
            
            Event startEvent = personEvents.get(0);
            Event endEvent;
            for (int i = 1; i < personEvents.size(); i++) {
                endEvent = personEvents.get(i);
                Log.d(TAG,"Drawing line from " + startEvent.getDescriptionStr() + " to " + endEvent.getDescriptionStr());
                drawLine(startEvent,endEvent,color,width);
                startEvent = endEvent;
            }
        }
    }
    
    private void drawFamilyTree(Event event) {
        float startWidth = 12f;
        
        Person startPerson = mData.getPersonById(event.getPersonID());
        drawFamilyTree_Helper(event,startPerson.getFatherID(),startWidth);
        drawFamilyTree_Helper(event,startPerson.getMotherID(),startWidth);
    }
    
    private void drawFamilyTree_Helper(Event currEvent,String parentId,float lineWidth) {
        int color = 0xffff0000;
        
        Event parentEvent = getBirthOrEarliestEvent(parentId);
        if (parentEvent == null) return;
        
        drawLine(currEvent,parentEvent,color,lineWidth);
    
        Person parentPerson = mData.getPersonById(parentEvent.getPersonID());
        drawFamilyTree_Helper(parentEvent,parentPerson.getFatherID(),lineWidth/2);
        drawFamilyTree_Helper(parentEvent,parentPerson.getMotherID(),lineWidth/2);
    }
    
    private void drawSpouseLine(Event event) {
        int color = 0xff00ff00;
        float width = 12f;
        Person eventPerson = mData.getPersonById(event.getPersonID());
        Person spouse = mData.getPersonById(eventPerson.getSpouseID());
        if (spouse != null) {
            Event spouseEvent = getBirthOrEarliestEvent(spouse);
            if (spouseEvent != null) drawLine(event,spouseEvent,color,width);
        }
    }
    
    private Event getBirthOrEarliestEvent(Person person) {
        if (person == null) return null;
        return getBirthOrEarliestEvent(person.getId());
    }
    
    private Event getBirthOrEarliestEvent(String personId) {
        if (personId == null || personId.isEmpty()) return null;
        
        Event earliestEvent = null;
        
        for (Event event : mData.getPersonEvents(personId)) {
            if (event.getEventType().toUpperCase().equals("BIRTH")) return event;
            if (earliestEvent != null) {
                earliestEvent  = (earliestEvent.getYear() <= event.getYear()) ? earliestEvent : event;
            } else earliestEvent = event;
        }
        
        return earliestEvent;
    }
    
    private void drawLine(Event startEvent,Event endEvent,int color,float width) {
        LatLng userBirth = new LatLng(startEvent.getLatitude(),startEvent.getLongitude());
        LatLng fatherBirth = new LatLng(endEvent.getLatitude(),endEvent.getLongitude());
        
        Polyline eventLine =  mMap.addPolyline(new PolylineOptions().add(userBirth,fatherBirth));
        eventLine.setColor(color);
        eventLine.setWidth(width);
        
        mEventLines.add(eventLine);
    }
    
    private void clearMarkers() {
        for (Marker marker : mEventMarkers) marker.remove();
    }
    
    private void clearLines() {
        for (Polyline line : mEventLines) line.remove();
    }
}