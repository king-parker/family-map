package com.kingpark.familymapclient.network.result;

import com.google.gson.annotations.SerializedName;

public abstract class Result {
    @SerializedName("message")
    private String mMessage;
    @SerializedName("success")
    private boolean mIsSuccess;
    
    public Result(String message,boolean isSuccess) {
        mMessage = message;
        mIsSuccess = isSuccess;
    }
    
    public Result(String message) {
        mMessage = message;
        mIsSuccess = false;
    }
    
    protected Result() {
        mIsSuccess = true;
    }
    
    public String getMessage() {
        return mMessage;
    }
    
    public boolean isSuccess() {
        return mIsSuccess;
    }
}
