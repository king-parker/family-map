package com.kingpark.familymapclient.model;

import androidx.annotation.Nullable;

import java.util.Set;

public class Settings {
    // Line Settings
    private boolean mLifeStoryLines;
    private boolean mFamilyTreeLines;
    private boolean mSpouseLines;
    // Event Settings
    private boolean mFatherSide;
    private boolean mMotherSide;
    private boolean mMaleEvents;
    private boolean mFemaleEvents;
    
    public Settings() {
        mLifeStoryLines = true;
        mFamilyTreeLines = true;
        mSpouseLines = true;
        mFatherSide = true;
        mMotherSide = true;
        mMaleEvents = true;
        mFemaleEvents = true;
    }
    
    public Settings(boolean lifeStoryLines,boolean familyTreeLines,boolean spouseLines,boolean fatherSide,boolean motherSide,
                    boolean maleEvents,boolean femaleEvents) {
        mLifeStoryLines = lifeStoryLines;
        mFamilyTreeLines = familyTreeLines;
        mSpouseLines = spouseLines;
        mFatherSide = fatherSide;
        mMotherSide = motherSide;
        mMaleEvents = maleEvents;
        mFemaleEvents = femaleEvents;
    }
    
    public Settings(boolean[] mapSettings) {
        this();
        if (mapSettings == null || mapSettings.length < 1) return;
        
        switch (mapSettings.length) {
            case (7):
                mFemaleEvents = mapSettings[6];
            case (6):
                mMaleEvents = mapSettings[5];
            case (5):
                mMotherSide = mapSettings[4];
            case (4):
                mFatherSide = mapSettings[3];
            case (3):
                mSpouseLines = mapSettings[2];
            case (2):
                mFamilyTreeLines = mapSettings[1];
            case (1):
                mLifeStoryLines = mapSettings[0];
                break;
        }
    }
    
    public boolean isLifeStoryLines() {
        return mLifeStoryLines;
    }
    
    public void setLifeStoryLines(boolean lifeStoryLines) {
        mLifeStoryLines = lifeStoryLines;
    }
    
    public boolean isFamilyTreeLines() {
        return mFamilyTreeLines;
    }
    
    public void setFamilyTreeLines(boolean familyTreeLines) {
        mFamilyTreeLines = familyTreeLines;
    }
    
    public boolean isSpouseLines() {
        return mSpouseLines;
    }
    
    public void setSpouseLines(boolean spouseLines) {
        mSpouseLines = spouseLines;
    }
    
    public boolean isFatherSide() {
        return mFatherSide;
    }
    
    public void setFatherSide(boolean fatherSide) {
        mFatherSide = fatherSide;
    }
    
    public boolean isMotherSide() {
        return mMotherSide;
    }
    
    public void setMotherSide(boolean motherSide) {
        mMotherSide = motherSide;
    }
    
    public boolean isMaleEvents() {
        return mMaleEvents;
    }
    
    public void setMaleEvents(boolean maleEvents) {
        mMaleEvents = maleEvents;
    }
    
    public boolean isFemaleEvents() {
        return mFemaleEvents;
    }
    
    public void setFemaleEvents(boolean femaleEvents) {
        mFemaleEvents = femaleEvents;
    }
    
    public boolean[] toArray() {
        return new boolean[] {mLifeStoryLines,mFamilyTreeLines,mSpouseLines,mFatherSide,mMotherSide,mMaleEvents,mFemaleEvents};
    }
    
    @Override
    public int hashCode() {
        return boolToInt(mLifeStoryLines) + boolToInt(mFamilyTreeLines) + boolToInt(mSpouseLines) + boolToInt(mFatherSide) +
                                                boolToInt(mMotherSide) + boolToInt(mMaleEvents) + boolToInt(mFemaleEvents);
    }
    
    private int boolToInt(boolean bool) {
        if (bool) return 1;
        else return 0;
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Settings) {
            Settings objSettings = (Settings) obj;
            return objSettings.isLifeStoryLines() == isLifeStoryLines() &&
                    objSettings.isFamilyTreeLines() == isFamilyTreeLines() &&
                    objSettings.isSpouseLines() == isSpouseLines() &&
                    objSettings.isFatherSide() == isFatherSide() &&
                    objSettings.isMotherSide() == isMotherSide() &&
                    objSettings.isMaleEvents() == isMaleEvents() &&
                    objSettings.isFemaleEvents() == isFemaleEvents();
        } else if (obj instanceof boolean[]) {
            boolean[] objSettings = (boolean[]) obj;
            return objSettings[0] == isLifeStoryLines() &&
                    objSettings[1] == isFamilyTreeLines() &&
                    objSettings[2] == isSpouseLines() &&
                    objSettings[3] == isFatherSide() &&
                    objSettings[4] == isMotherSide() &&
                    objSettings[5] == isMaleEvents() &&
                    objSettings[6] == isFemaleEvents();
        } else {
            return false;
        }
    }
}
