package com.kingpark.familymapclient.ui.tasks;

import android.os.AsyncTask;

import com.kingpark.familymapclient.network.ServerProxy;
import com.kingpark.familymapclient.network.request.RegisterRequest;
import com.kingpark.familymapclient.network.result.RegisterResult;

public class RegisterTask extends AsyncTask<RegisterRequest,Void,RegisterResult> {
    public interface Listener {
        void onRegisterFail(String message);
        void onRegisterSuccess(String authToken,String username,String personID);
    }
    
    private Listener mListener;
    
    public RegisterTask(RegisterTask.Listener listener) {
        mListener = listener;
    }
    
    @Override
    protected RegisterResult doInBackground(RegisterRequest... request) {
        if (request.length != 1) {
            return new RegisterResult("Error: Must pass exactly one login request");
        }
    
        if (ServerProxy.getServerHostName() == null || ServerProxy.getServerPortNumber() <= 0) {
            return new RegisterResult("Error: Host Name and Port Number for server " +
                    "has not been set");
        }
    
        return new ServerProxy().register(request[0]);
    }
    
    @Override
    protected void onPostExecute(RegisterResult result) {
        if (result.isSuccess()) {
            mListener.onRegisterSuccess(result.getAuthToken(),result.getUsername(),
                    result.getPersonId());
        } else {
            mListener.onRegisterFail(result.getMessage());
        }
    }
}
