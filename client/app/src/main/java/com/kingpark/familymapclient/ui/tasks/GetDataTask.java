package com.kingpark.familymapclient.ui.tasks;

import android.os.AsyncTask;

import com.kingpark.familymapclient.model.DataCache;
import com.kingpark.familymapclient.model.Event;
import com.kingpark.familymapclient.model.Person;
import com.kingpark.familymapclient.network.ServerProxy;
import com.kingpark.familymapclient.network.result.DataResult;
import com.kingpark.familymapclient.network.result.EventResult;
import com.kingpark.familymapclient.network.result.PersonResult;

public class GetDataTask extends AsyncTask<String,Void,DataResult> {
    public interface Listener {
        void onGetDataSuccess();
        void onGetDataFail(String message);
    }
    
    private Listener mListener;
    
    public GetDataTask(Listener listener) {
        mListener = listener;
    }
    
    
    @Override
    protected DataResult doInBackground(String... loginInfo) {
        assert loginInfo.length == 2;
        assert ServerProxy.getServerHostName() != null;
        assert ServerProxy.getServerPortNumber() > 0;
        
        String authToken = loginInfo[0];
        String userPersonId = loginInfo[1];
        
        String result = loadPersonData(authToken);
        if (result != null) return new DataResult(result,false);
    
        result = loadEventData(authToken);
        if (result != null) return new DataResult(result,false);
    
        result = configureData(userPersonId);
        if (result != null) return new DataResult(result,false);
        
        return new DataResult("Data retrieval successful",true);
    }
    
    private String loadPersonData(String authToken) {
        DataCache dataCache = DataCache.getInstance();
    
        PersonResult personR = new ServerProxy().getPeople(authToken);
        if (personR == null) return "Error: Null Result";
        if (!personR.isSuccess()) return personR.getMessage();
        
        Person[] people = personR.getPeople();
        if (people.length == 0) return "Data retrieval failed. No person data for user";
        dataCache.setPeople(people);
        
        return null;
    }
    
    private String loadEventData(String authToken) {
        DataCache dataCache = DataCache.getInstance();
    
        EventResult eventR = new ServerProxy().getEvents(authToken);
        if (eventR == null) return "Error: Null Result";
        if (!eventR.isSuccess()) return eventR.getMessage();
    
        Event[] events = eventR.getEvents();
        if (events.length == 0) return "Data retrieval failed. No event data for user";
        dataCache.setEvents(events);
        
        return null;
    }
    
    private String configureData(String userPersonID) {
        DataResult result = DataCache.getInstance().configureData(userPersonID);
        if (result == null) return "Error: Null Result";
        if (!result.isSuccess()) return result.getMessage();
        else return null;
    }
    
    @Override
    protected void onPostExecute(DataResult result) {
        if (result.isSuccess()) {
            mListener.onGetDataSuccess();
        } else {
            mListener.onGetDataFail(result.getMessage());
        }
    }
}
