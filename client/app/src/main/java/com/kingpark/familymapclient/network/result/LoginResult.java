package com.kingpark.familymapclient.network.result;

import com.google.gson.annotations.SerializedName;

public class LoginResult extends Result {
    @SerializedName("authToken")
    private String mAuthToken;
    @SerializedName("userName")
    private String mUsername;
    @SerializedName("personID")
    private String mPersonId;
    
    public LoginResult(String authToken,String username,String personId) {
        super();
        mAuthToken = authToken;
        mUsername = username;
        mPersonId = personId;
    }
    
    public LoginResult(String message) {
        super(message);
    }
    
    public String getAuthToken() {
        return mAuthToken;
    }
    
    public String getUsername() {
        return mUsername;
    }
    
    public String getPersonId() {
        return mPersonId;
    }
}
