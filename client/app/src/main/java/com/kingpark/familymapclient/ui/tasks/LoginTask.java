package com.kingpark.familymapclient.ui.tasks;

import android.os.AsyncTask;

import com.kingpark.familymapclient.network.ServerProxy;
import com.kingpark.familymapclient.network.request.LoginRequest;
import com.kingpark.familymapclient.network.result.LoginResult;

public class LoginTask extends AsyncTask<LoginRequest,Void,LoginResult> {
    public interface Listener {
        void onLoginFail(String message);
        void onLoginSuccess(String authToken,String username,String personID);
    }
    
    private Listener mListener;
    
    public LoginTask(Listener listener) {
        mListener = listener;
    }
    
    @Override
    protected LoginResult doInBackground(LoginRequest... request) {
        if (request.length != 1) {
            return new LoginResult("Error: Must pass exactly one login request");
        }
        
        if (ServerProxy.getServerHostName() == null || ServerProxy.getServerPortNumber() <= 0) {
            return new LoginResult("Error: Host Name and Port Number for server has not been set");
        }
        
        return new ServerProxy().login(request[0]);
    }
    
    @Override
    protected void onPostExecute(LoginResult result) {
        if (result.isSuccess()) {
            mListener.onLoginSuccess(result.getAuthToken(),result.getUsername(),
                    result.getPersonId());
        } else {
            mListener.onLoginFail(result.getMessage());
        }
    }
}
