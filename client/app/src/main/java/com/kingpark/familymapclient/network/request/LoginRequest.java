package com.kingpark.familymapclient.network.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("userName")
    private String mUsername;
    @SerializedName("password")
    private String mPassword;
    
    public LoginRequest(String username,String password) {
        mUsername = username;
        mPassword = password;
    }
    
    public String getUsername() {
        return mUsername;
    }
    
    public String getPassword() {
        return mPassword;
    }
}
