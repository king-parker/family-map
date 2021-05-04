package com.kingpark.familymapclient.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
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

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int PERSON_POSITION = 0;
    private static final int EVENT_POSITION = 1;
    
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private SearchDataAdapter mSearchDataAdapter;
    private DataCache mData;
    
    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext,SearchActivity.class);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Iconify.with(new FontAwesomeModule());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mData = DataCache.getInstance();
        
        mRecyclerView = findViewById(R.id.search_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        
        mSearchView = findViewById(R.id.search_input);
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = mSearchView.getQuery().toString();
                Log.d(TAG,"Search: \"" + search + "\"");
                mSearchDataAdapter = new SearchDataAdapter(search);
                mRecyclerView.setAdapter(mSearchDataAdapter);
            }
        });
    }
    
    private class SearchDataAdapter extends RecyclerView.Adapter<MapDataViewHolder> {
        private final List<Person> searchPeople;
        private final List<Event> searchEvents;
    
        SearchDataAdapter(String searchString) {
            List<Person> matchPeople = new ArrayList<>();
            List<Event> matchEvents = new ArrayList<>();
            
            searchString = searchString.toLowerCase();
            
            for (Person person : mData.getPeople()) {
                if (person.getFirstName().toLowerCase().contains(searchString)) matchPeople.add(person);
                else if (person.getLastName().toLowerCase().contains(searchString)) matchPeople.add(person);
            }
            
            for (Event event :mData.getEvents()) {
                if (event.getCountry().toLowerCase().contains(searchString)) matchEvents.add(event);
                else if (event.getCity().toLowerCase().contains(searchString)) matchEvents.add(event);
                else if (event.getEventType().toLowerCase().contains(searchString)) matchEvents.add(event);
                else if (String.valueOf(event.getYear()).contains(searchString)) matchEvents.add(event);
            }
        
            searchPeople = matchPeople;
            searchEvents = matchEvents;
        }
        
        @Override
        public int getItemViewType(int position) {
            return position < searchPeople.size() ? PERSON_POSITION : EVENT_POSITION;
        }
    
        @NonNull
        @Override
        public MapDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
            View view;
            
            if (viewType == PERSON_POSITION) view = getLayoutInflater().inflate(R.layout.item_person,parent,false);
            else view = getLayoutInflater().inflate(R.layout.item_event,parent,false);
            
            return new MapDataViewHolder(view,viewType);
        }
    
        @Override
        public void onBindViewHolder(@NonNull MapDataViewHolder holder,int position) {
            if (position < searchPeople.size()) holder.bind(searchPeople.get(position));
            else holder.bind(searchEvents.get(position - searchPeople.size()));
        }
    
        @Override
        public int getItemCount() {
            return searchPeople.size() + searchEvents.size();
        }
    }
    
    private class MapDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mItemDescription;
        private final TextView mItemSubInfo;
        private final ImageView mItemIcon;
        
        private final int mViewType;
        private Person mPerson;
        private Event mEvent;
    
        MapDataViewHolder(View view,int viewType) {
            super(view);
            mViewType = viewType;
            
            itemView.setOnClickListener(this);
            
            if (mViewType == PERSON_POSITION) {
                mItemDescription = findViewById(R.id.item_person_name);
                mItemSubInfo = null;
                mItemIcon = findViewById(R.id.item_person_icon);
            } else {
                mItemDescription = findViewById(R.id.item_event_description);
                mItemSubInfo = findViewById(R.id.item_event_person);
                mItemIcon = findViewById(R.id.item_event_icon);
            }
            
        }
    
        private void bind(Person person) {
            mPerson = person;
            String name = person.getFirstName() + " " + person.getLastName();
            mItemDescription.setText(name);
            mItemIcon.setImageDrawable(makePersonIcon(person.getGender()));
        }
    
        private Drawable makePersonIcon(char gender) {
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
        
            return new IconDrawable(SearchActivity.this,imageIcon).colorRes(colorId).sizeDp(dpSize);
        }
    
        private void bind(Event event) {
            mEvent = event;
            String description = event.getDescriptionStr() + " in " + event.getLocationStr();
            mItemDescription.setText(description);
            Person person = mData.getPersonById(event.getPersonID());
            String name = person.getFirstName() + " " + person.getLastName();
            mItemSubInfo.setText(name);
            mItemIcon.setImageDrawable(makeEventIcon(event.getEventType()));
        }
    
        private Drawable makeEventIcon(String eventType) {
            FontAwesomeIcons mapMarker = FontAwesomeIcons.fa_map_marker;
            float[] hue = new float[] {mData.getMarkerColor(eventType),0.7f,.5f};
            int color = ColorUtils.HSLToColor(hue);
            int dpSize = 40;
        
            return new IconDrawable(SearchActivity.this,mapMarker).color(color).sizeDp(dpSize);
        }
    
        @Override
        public void onClick(View view) {
            if (mViewType == PERSON_POSITION) {
                startActivity(PersonActivity.newIntent(SearchActivity.this,mPerson.getId()));
            } else {
                startActivity(EventActivity.newIntent(SearchActivity.this,mEvent.getId()));
            }
        }
    }
}