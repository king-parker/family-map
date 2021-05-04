package com.kingpark.familymapclient.network.result;

public class RegisterResult extends LoginResult {
    public RegisterResult(String authToken,String username,String personId) {
        super(authToken,username,personId);
    }
    
    public RegisterResult(String message) {
        super(message);
    }
}
