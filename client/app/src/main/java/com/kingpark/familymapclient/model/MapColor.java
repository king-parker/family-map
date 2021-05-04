package com.kingpark.familymapclient.model;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public enum MapColor {
    RED(BitmapDescriptorFactory.HUE_RED),
    BLUE(BitmapDescriptorFactory.HUE_BLUE),
    GREEN(BitmapDescriptorFactory.HUE_GREEN),
    ORANGE(BitmapDescriptorFactory.HUE_ORANGE),
    VIOLET(BitmapDescriptorFactory.HUE_VIOLET),
    YELLOW(BitmapDescriptorFactory.HUE_YELLOW),
    MAGENTA(BitmapDescriptorFactory.HUE_MAGENTA),
    AZURE(BitmapDescriptorFactory.HUE_AZURE),
    ROSE(BitmapDescriptorFactory.HUE_ROSE),
    CYAN(BitmapDescriptorFactory.HUE_CYAN);
    
    private float mColorHue;
    
    MapColor(float colorHue) {
        mColorHue = colorHue;
    }
    
    public float getColorHue() {
        return mColorHue;
    }
}
