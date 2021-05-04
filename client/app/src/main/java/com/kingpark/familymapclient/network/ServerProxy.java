package com.kingpark.familymapclient.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingpark.familymapclient.network.request.LoginRequest;
import com.kingpark.familymapclient.network.request.RegisterRequest;
import com.kingpark.familymapclient.network.result.EventResult;
import com.kingpark.familymapclient.network.result.LoginResult;
import com.kingpark.familymapclient.network.result.PersonResult;
import com.kingpark.familymapclient.network.result.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ServerProxy {
    //~~~~~ Static Members ~~~~~
    private static String gServerHostName;
    private static int gServerPortNumber;
    
    public static void setProxyServer(String serverHostName,int serverPortNumber) {
        gServerHostName = serverHostName;
        gServerPortNumber = serverPortNumber;
    }
    
    public static String getServerHostName() {
        return gServerHostName;
    }
    
    public static int getServerPortNumber() {
        return gServerPortNumber;
    }
    
    //~~~~~ Local Members ~~~~~
    private final String TAG = "ServerProxy";
    
    public ServerProxy() {}
    
    public ServerProxy(String serverHostName,int serverPortNumber) {
        gServerHostName = serverHostName;
        gServerPortNumber = serverPortNumber;
    }
    
    public RegisterResult register(RegisterRequest request) {
        try {
            HttpURLConnection http = executePostRequest(request,"/register");
            http.getResponseCode();
            return parseResult(http,RegisterResult.class);
        } catch (IOException e) {
            Log.e(TAG,"Error: Client request for /user/",e);
            return new RegisterResult(e.getMessage());
        }
    }
    
    public LoginResult login(LoginRequest request) {
        try {
            HttpURLConnection http = executePostRequest(request,"/login");
            return parseResult(http,LoginResult.class);
        } catch (IOException e) {
            Log.e(TAG,"Error: Client request for /user/",e);
            return new LoginResult(e.getMessage());
        }
    }
    
    public PersonResult getPeople(String authToken) {
        try {
            HttpURLConnection http = executeGetRequest(authToken,"/person");
            return parseResult(http,PersonResult.class);
        } catch (IOException e) {
            Log.e(TAG,"Error: Client request for /user/",e);
            return new PersonResult(e.getMessage());
        }
    }
    
    public EventResult getEvents(String authToken) {
        try {
            HttpURLConnection http = executeGetRequest(authToken,"/event");
            return parseResult(http,EventResult.class);
        } catch (IOException e) {
            Log.e(TAG,"Error: Client request for /user/",e);
            return new EventResult(e.getMessage());
        }
    }
    
    private HttpURLConnection executePostRequest(LoginRequest request,String userService)
            throws IOException {
        String jsonStr = writeToJson(request);
        logRequestBody(jsonStr);
    
            URL url = new URL("http://" + gServerHostName + ":" +
                    gServerPortNumber + "/user" + userService);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            setPostMethod(http);
            attachRequestBody(http,jsonStr);

            logResult(http);
            return http;
    }
    
    private HttpURLConnection executeGetRequest(String authToken,String getService)
            throws IOException {
        URL url = new URL("http://" + gServerHostName + ":" +
                gServerPortNumber + getService);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        setGetMethod(http,authToken);

        logResult(http);
        return http;
    }
    
    private String writeToJson(LoginRequest request) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(request);
    }
    
    private void setPostMethod(HttpURLConnection http) throws IOException {
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.addRequestProperty("Content-Type","application/json");
        http.addRequestProperty("Accept","application/json");
        http.connect();
    }
    
    private void setGetMethod(HttpURLConnection http,String authToken) throws IOException {
        http.setRequestMethod("GET");
        http.setDoOutput(false);
        http.addRequestProperty("Authorization",authToken);
        http.addRequestProperty("Accept","application/json");
        http.connect();
    }
    
    private void attachRequestBody(HttpURLConnection http,String jsonStr) throws IOException {
        OutputStream reqBody = http.getOutputStream();
        Writer writer = new OutputStreamWriter(reqBody);
        writer.write(jsonStr);
        writer.close();
        reqBody.close();
    }
    
    private <R> R parseResult(HttpURLConnection http,Class<R> rClass) throws IOException {
        InputStream response;
        if (http.getResponseCode() < 400) response = http.getInputStream();
        else response = http.getErrorStream();
        Scanner s = new Scanner(response).useDelimiter("\\A");
        String respBody = s.hasNext() ? s.next() : "";
        logResponseBody(respBody);
        return new Gson().fromJson(respBody,rClass);
    }
    
    private void logRequestBody(String jsonStr) {
        Log.v(TAG,"Request Body\n" + jsonStr);
    }
    
    private void logResponseBody(String jsonStr) {
        Log.v(TAG,"Response Body\n" + jsonStr);
    }
    
    private void logResult(HttpURLConnection http) throws IOException {
        if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Log.i(TAG,"/User Success");
        } else if (http.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            Log.i(TAG,"Error: " + http.getResponseMessage());
        } else {
            Log.e(TAG,"Error: " + http.getResponseMessage());
        }
    }
}
