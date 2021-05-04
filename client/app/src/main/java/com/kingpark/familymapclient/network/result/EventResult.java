package com.kingpark.familymapclient.network.result;

import com.google.gson.annotations.SerializedName;
import com.kingpark.familymapclient.model.Event;

public class EventResult extends Result {
    @SerializedName("data")
    private Event[] mEvents;
    
    public EventResult(Event[] events) {
        super();
        mEvents = events;
    }
    
    public EventResult(String message) {
        super(message);
    }
    
    public Event[] getEvents() {
        return mEvents;
    }
}
