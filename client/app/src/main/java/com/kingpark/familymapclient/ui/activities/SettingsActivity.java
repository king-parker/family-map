package com.kingpark.familymapclient.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kingpark.familymapclient.R;
import com.kingpark.familymapclient.model.DataCache;

public class SettingsActivity extends AppCompatActivity {
    private final String TAG = "SettingsActivity";
    public static final String EXTRA_SETTINGS = "com.kingpark.familymapclient.settings_settings";
    public static final String EXTRA_IS_LOGOUT = "com.kingpark.familymapclient.settings_is_logout";
    
    private DataCache mData;
    
    private SwitchCompat mIsLifeStory;
    private SwitchCompat mIsFamilyTree;
    private SwitchCompat mIsSpouse;
    private SwitchCompat mIsFatherSide;
    private SwitchCompat mIsMotherSide;
    private SwitchCompat mIsMaleEvents;
    private SwitchCompat mIsFemaleEvents;
    private TextView mLogoutTitle;
    private TextView mLogoutDescription;
    
    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext,SettingsActivity.class);
    }
    
    public static boolean isLogout(Intent result) {
        return result.getBooleanExtra(EXTRA_IS_LOGOUT,false);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mData = DataCache.getInstance();
        
        assert getIntent() != null;
        
        mIsLifeStory = findViewById(R.id.switch_life_story);
        mIsLifeStory.setChecked(mData.getSettings().isLifeStoryLines());
        mIsLifeStory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView,boolean isChecked) {
                mData.getSettings().setLifeStoryLines(isChecked);
            }
        });
    
        mIsFamilyTree = findViewById(R.id.switch_family_tree);
        mIsFamilyTree.setChecked(mData.getSettings().isFamilyTreeLines());
        mIsFamilyTree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView,boolean isChecked) {
                mData.getSettings().setFamilyTreeLines(isChecked);
            }
        });
    
        mIsSpouse = findViewById(R.id.switch_spouse);
        mIsSpouse.setChecked(mData.getSettings().isSpouseLines());
        mIsSpouse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView,boolean isChecked) {
                mData.getSettings().setSpouseLines(isChecked);
            }
        });
    
        mIsFatherSide = findViewById(R.id.switch_father_side);
        mIsFatherSide.setChecked(mData.getSettings().isFatherSide());
        mIsFatherSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView,boolean isChecked) {
                mData.getSettings().setFatherSide(isChecked);
            }
        });
    
        mIsMotherSide = findViewById(R.id.switch_mother_side);
        mIsMotherSide.setChecked(mData.getSettings().isMotherSide());
        mIsMotherSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView,boolean isChecked) {
                mData.getSettings().setMotherSide(isChecked);
            }
        });
    
        mIsMaleEvents = findViewById(R.id.switch_male_events);
        mIsMaleEvents.setChecked(mData.getSettings().isMaleEvents());
        mIsMaleEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView,boolean isChecked) {
                mData.getSettings().setMaleEvents(isChecked);
            }
        });
    
        mIsFemaleEvents = findViewById(R.id.switch_female_events);
        mIsFemaleEvents.setChecked(mData.getSettings().isFemaleEvents());
        mIsFemaleEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView,boolean isChecked) {
                mData.getSettings().setFemaleEvents(isChecked);
            }
        });
        
        mLogoutTitle = findViewById(R.id.title_logout);
        mLogoutTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        mLogoutDescription = findViewById(R.id.description_logout);
        mLogoutDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }
    
    private void returnSettings(boolean isLogout) {
        Intent data = new Intent();
        data.putExtra(EXTRA_IS_LOGOUT,isLogout);
        setResult(RESULT_OK,data);
    }
    
    private void logout() {
        returnSettings(true);
        finish();
    }
}