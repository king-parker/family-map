package com.kingpark.familymapclient.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.kingpark.familymapclient.R;
import com.kingpark.familymapclient.model.DataCache;
import com.kingpark.familymapclient.model.Event;
import com.kingpark.familymapclient.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends AppCompatActivity {
    private final String TAG = "PersonActivity";
    private static final String ARG_PERSON_ID = "com.kingpark.familymapclient.person_id";
    
    private TextView mFirstNameView;
    private TextView mLastNameView;
    private TextView mGenderView;
    private ExpandableListView mPersonDataView;
    
    private DataCache mData;
    
    public static Intent newIntent(Context packageContext,String personId) {
        Intent intent = new Intent(packageContext,PersonActivity.class);
        intent.putExtra(ARG_PERSON_ID,personId);
        
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Iconify.with(new FontAwesomeModule());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mData = DataCache.getInstance();
    
        if (!getIntent().hasExtra(ARG_PERSON_ID)) return;
        String personId = getIntent().getStringExtra(ARG_PERSON_ID);
        if (personId == null) return;
    
        Person activityPerson = mData.getPersonById(personId);
    
        mFirstNameView = findViewById(R.id.person_first_name);
        mFirstNameView.setText(activityPerson.getFirstName());
    
        mLastNameView = findViewById(R.id.person_last_name);
        mLastNameView.setText(activityPerson.getLastName());
    
        mGenderView = findViewById(R.id.person_gender);
        String gender = "Error";
        if (Character.toLowerCase(activityPerson.getGender()) == 'm') gender = "Male";
        if (Character.toLowerCase(activityPerson.getGender()) == 'f') gender = "Female";
        mGenderView.setText(gender);
        
        mPersonDataView = findViewById(R.id.expandable_person_data_list);
        mPersonDataView.setAdapter(new PersonDataAdapter(personId));
    }
    
    private class PersonDataAdapter extends BaseExpandableListAdapter {
        private static final int LIFE_EVENTS_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;
        
        private final Person mPerson;
        private final List<Event> mLifeEvents;
        private final List<Person> mFamily;
        private final List<String> mRelationships;
        
        PersonDataAdapter(String personId) {
            mPerson = mData.getPersonById(personId);
            
            mLifeEvents = mData.getPersonEvents(personId);
    
            List<Person> family = new ArrayList<>();
            
            String fatherId = mPerson.getFatherID();
            boolean hasFather;
            if (fatherId != null && !fatherId.isEmpty() && mData.checkPersonSettings(fatherId)) {
                family.add(mData.getPersonById(fatherId));
                hasFather = true;
            } else hasFather = false;
    
            String motherId = mPerson.getMotherID();
            boolean hasMother;
            if (motherId != null && !motherId.isEmpty() && mData.checkPersonSettings(motherId)) {
                family.add(mData.getPersonById(motherId));
                hasMother = true;
            } else hasMother = false;
    
            String spouseId = mPerson.getSpouseID();
            boolean hasSpouse;
            if (spouseId != null && !spouseId.isEmpty() && mData.checkPersonSettings(spouseId)) {
                family.add(mData.getPersonById(spouseId));
                hasSpouse = true;
            } else hasSpouse = false;
    
            List<Person> children = mData.getPersonChildren(personId);
            boolean hasChildren;
            if (children != null && !children.isEmpty()) {
                family.addAll(children);
                hasChildren = true;
            } else hasChildren = false;
            
            mFamily = family;
    
            List<String> familyRelationships = new ArrayList<>();
    
            if (hasFather) familyRelationships.add("Father");
            if (hasMother) familyRelationships.add("Mother");
            if (hasSpouse) familyRelationships.add("Spouse");
            if (hasChildren) while (familyRelationships.size() < mFamily.size()) familyRelationships.add("Child");
    
            mRelationships = familyRelationships;
        }
    
        @Override
        public int getGroupCount() {
            return 2;
        }
    
        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return mLifeEvents.size();
                case FAMILY_GROUP_POSITION:
                    return mFamily.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }
    
        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return getString(R.string.group_title_life_events);
                case FAMILY_GROUP_POSITION:
                    return getString(R.string.group_title_family);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }
    
        @Override
        public Object getChild(int groupPosition,int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return mLifeEvents.get(childPosition);
                case FAMILY_GROUP_POSITION:
                    return mFamily.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }
    
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
    
        @Override
        public long getChildId(int groupPosition,int childPosition) {
            return childPosition;
        }
    
        @Override
        public boolean hasStableIds() {
            return false;
        }
    
        @Override
        public View getGroupView(int groupPosition,boolean isExpanded,View convertView,ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.group_person_data,parent,false);
            }
            TextView titleView = convertView.findViewById(R.id.group_item_title);
    
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.group_title_life_events);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.group_title_family);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            
            return convertView;
        }
    
        @Override
        public View getChildView(int groupPosition,int childPosition,boolean isLastChild,View convertView,ViewGroup parent) {
            View itemView;
            
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.item_event,parent,false);
                    initializeLifeEventView(itemView,childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.item_person,parent,false);
                    initializeFamilyView(itemView,childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            
            return itemView;
        }
    
        private void initializeLifeEventView(View lifeEventItemView,final int childPosition) {
            final Event event = mLifeEvents.get(childPosition);
            
            ImageView eventMarker = lifeEventItemView.findViewById(R.id.item_event_icon);
            eventMarker.setImageDrawable(makeEventMarker(event.getEventType()));
            
            TextView eventDescription = lifeEventItemView.findViewById(R.id.item_event_description);
            String description = event.getDescriptionStr() + " in " + event.getLocationStr();
            eventDescription.setText(description);
            
            TextView eventPerson = lifeEventItemView.findViewById(R.id.item_event_person);
            eventPerson.setText(mPerson.getNameStr());
            
            lifeEventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startEventActivity(event.getId());
                }
            });
        }
        
        private void startEventActivity(String eventId) {
            startActivity(EventActivity.newIntent(PersonActivity.this,eventId));
        }
    
        private Drawable makeEventMarker(String eventType) {
            FontAwesomeIcons mapMarker = FontAwesomeIcons.fa_map_marker;
            float[] hue = new float[] {mData.getMarkerColor(eventType),0.7f,.5f};
            int color = ColorUtils.HSLToColor(hue);
            int dpSize = 40;
    
            return new IconDrawable(PersonActivity.this,mapMarker).color(color).sizeDp(dpSize);
        }
    
        private void initializeFamilyView(View familyItemView,final int childPosition) {
            final Person person = mFamily.get(childPosition);
            
            ImageView eventIcon = familyItemView.findViewById(R.id.item_person_icon);
            eventIcon.setImageDrawable(makeEventIcon(person.getGender()));
    
            TextView eventDescription = familyItemView.findViewById(R.id.item_person_name);
            eventDescription.setText(person.getNameStr());
    
            TextView eventPerson = familyItemView.findViewById(R.id.item_person_relationship);
            eventPerson.setText(mRelationships.get(childPosition));
            
            familyItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPersonActivity(person.getId());
                }
            });
        }
    
        private void startPersonActivity(String personId) {
            startActivity(PersonActivity.newIntent(PersonActivity.this,personId));
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
                throw new IllegalArgumentException("Unrecognized gender character: " + gender);
            }
        
            return new IconDrawable(PersonActivity.this,imageIcon).colorRes(colorId).sizeDp(dpSize);
        }
    
        @Override
        public boolean isChildSelectable(int groupPosition,int childPosition) {
            return true;
        }
    }
}