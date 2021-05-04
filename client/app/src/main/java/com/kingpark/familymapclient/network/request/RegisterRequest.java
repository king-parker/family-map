package com.kingpark.familymapclient.network.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest extends LoginRequest {
    @SerializedName("email")
    private String mEmail;
    @SerializedName("firstName")
    private String mFirstName;
    @SerializedName("lastName")
    private String mLastName;
    @SerializedName("gender")
    private char mGender;
    
    public RegisterRequest(String username,String password,String email,String firstName,String lastName,char gender) {
        super(username,password);
        mEmail = email;
        mFirstName = firstName;
        mLastName = lastName;
        mGender = gender;
    }
    
    public String getEmail() {
        return mEmail;
    }
    
    public String getFirstName() {
        return mFirstName;
    }
    
    public String getLastName() {
        return mLastName;
    }
    
    public char getGender() {
        return mGender;
    }
}
