package com.kingpark.familymapclient.network;

import com.google.gson.Gson;
import com.kingpark.familymapclient.network.request.LoginRequest;
import com.kingpark.familymapclient.network.request.RegisterRequest;
import com.kingpark.familymapclient.network.result.EventResult;
import com.kingpark.familymapclient.network.result.LoginResult;
import com.kingpark.familymapclient.network.result.PersonResult;
import com.kingpark.familymapclient.network.result.RegisterResult;
import com.kingpark.familymapclient.network.result.Result;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.*;

public class ServerProxyTest {
    private String serverHost = "127.0.0.1";
    private int portNumber = 8080;
    
    private void clearDB() {
        Result result = null;
        try {
            URL url = new URL("http://" + serverHost + ":" +
                    portNumber + "/clear");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
    
            http.setRequestMethod("POST");
            http.setDoOutput(false);
            http.addRequestProperty("Accept","application/json");
            http.connect();
    
            Scanner s = new Scanner(http.getInputStream()).useDelimiter("\\A");
            String respBody = s.hasNext() ? s.next() : "";
            result = new Gson().fromJson(respBody,PersonResult.class);
        } catch (IOException e) {
            fail(e.toString());
        }
        
        if (result == null) {
            fail("Clear result came back null");
        } else if (!result.isSuccess()) {
            fail("Clear failed: " + result.getMessage());
        }
    }
    
    private void loadData() {
        Result result = null;
        try {
            URL url = new URL("http://" + serverHost + ":" +
                    portNumber + "/load");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
        
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept","application/json");
            http.connect();
    
            OutputStream reqBody = http.getOutputStream();
            Writer writer = new OutputStreamWriter(reqBody);
            writer.write(loadRequest);
            writer.close();
            reqBody.close();
        
            Scanner s = new Scanner(http.getInputStream()).useDelimiter("\\A");
            String respBody = s.hasNext() ? s.next() : "";
            result = new Gson().fromJson(respBody,PersonResult.class);
        } catch (IOException e) {
            fail(e.toString());
        }
    
        if (result == null) {
            fail("Load result came back null");
        } else if (!result.isSuccess()) {
            fail("Load failed: " + result.getMessage());
        }
    }
    
    @Test
    public void registerUserTest() {
        clearDB();
        
        RegisterRequest request = new RegisterRequest(user,userPass,
                userEmail,userFirst,userLast,'m');
        RegisterResult result = new ServerProxy(serverHost,portNumber).register(request);
    
        assertNotNull(result);
        assertTrue("Login failed: " + result.getMessage(),result.isSuccess());
        assertNotNull(result.getAuthToken());
        assertEquals("Incorrect username, " + result.getUsername() + " was returned",
                user,result.getUsername());
        assertNotNull(result.getPersonId());
    }
    
    @Test
    public void registerDupUsernameTest() {
        clearDB();
        loadData();
        String placeholder = "test";
        
        RegisterRequest request = new RegisterRequest(user,placeholder,placeholder,placeholder,
                placeholder,'m');
        RegisterResult result = new ServerProxy(serverHost,portNumber).register(request);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred because Username already used",
                result.getMessage());
    }
    
    @Test
    public void registerDupEmailTest() {
        clearDB();
        loadData();
        String placeholder = "test";
    
        RegisterRequest request = new RegisterRequest(placeholder,placeholder,userEmail,placeholder,
                placeholder,'m');
        RegisterResult result = new ServerProxy(serverHost,portNumber).register(request);
    
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred because email already used",
                result.getMessage());
    }
    
    @Test
    public void loginUserSuccessTest() {
        clearDB();
        loadData();
        
        LoginRequest request = new LoginRequest(user,userPass);
        LoginResult result = new ServerProxy(serverHost,portNumber).login(request);
        
        assertNotNull(result);
        assertTrue("Login failed: " + result.getMessage(),result.isSuccess());
        assertNotNull(result.getAuthToken());
        assertEquals("Incorrect username, " + result.getUsername() + " was returned",
                user,result.getUsername());
        assertEquals("Incorrect personID, " + result.getPersonId() + " was returned",
                userId,result.getPersonId());
    }
    
    @Test
    public void loginUserUnregistered() {
        clearDB();
        loadData();
    
        LoginRequest request = new LoginRequest("bad","bad");
        LoginResult result = new ServerProxy(serverHost,portNumber).login(request);
    
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred from invalid value",result.getMessage());
    }
    
    @Test
    public void getPeopleSuccessTest() {
        clearDB();
        loadData();
    
        LoginRequest request = new LoginRequest(user,userPass);
        LoginResult lResult = new ServerProxy(serverHost,portNumber).login(request);
        
        String authToken = lResult.getAuthToken();
        PersonResult result = new ServerProxy().getPeople(authToken);
        
        assertNotNull(result);
        assertEquals("Expected num of People: 8 Actual num of People: " +
                result.getPeople().length,8,result.getPeople().length);
    }
    
    @Test
    public void getPeopleBadAuthTokenTest() {
        clearDB();
        loadData();
    
    
        ServerProxy server = new ServerProxy(serverHost,portNumber);
    
        String badToken = "";
        PersonResult result = server.getPeople(badToken);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred from missing auth token",
                result.getMessage());
    
        badToken = "bad_token";
        result = server.getPeople(badToken);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred from invalid auth token",
                result.getMessage());
        
        LoginResult loginResult = server.login(new LoginRequest(user,userPass));
        assertNotNull(loginResult);
        assertTrue(loginResult.isSuccess());
        
        result = server.getPeople(badToken);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred from invalid auth token",
                result.getMessage());
    }
    
    @Test
    public void getPeopleNoPeople() {
        ServerProxy server = new ServerProxy(serverHost,portNumber);
        LoginResult loginResult = server.login(new LoginRequest(user2,userPass2));
        assertNotNull(loginResult);
        assertTrue(loginResult.isSuccess());
    
        PersonResult result = server.getPeople(loginResult.getAuthToken());
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(0,result.getPeople().length);
    }
    
    @Test
    public void getEventsSuccessTest() {
        clearDB();
        loadData();
    
        LoginRequest request = new LoginRequest(user,userPass);
        LoginResult lResult = new ServerProxy(serverHost,portNumber).login(request);
    
        String authToken = lResult.getAuthToken();
        EventResult result = new ServerProxy().getEvents(authToken);
    
        assertNotNull(result);
        assertEquals("Expected num of Events: 16 Actual num of Events: " +
                result.getEvents().length,16,result.getEvents().length);
    }
    
    @Test
    public void getEventsBadAuthTokenTest() {
        clearDB();
        loadData();
        
        
        ServerProxy server = new ServerProxy(serverHost,portNumber);
        
        String badToken = "";
        EventResult result = server.getEvents(badToken);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred from missing auth token",
                result.getMessage());
        
        badToken = "bad_token";
        result = server.getEvents(badToken);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred from invalid auth token",
                result.getMessage());
        
        LoginResult loginResult = server.login(new LoginRequest(user,userPass));
        assertNotNull(loginResult);
        assertTrue(loginResult.isSuccess());
        
        result = server.getEvents(badToken);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Error: Bad Request occurred from invalid auth token",
                result.getMessage());
    }
    
    @Test
    public void getEventNoPeople() {
        ServerProxy server = new ServerProxy(serverHost,portNumber);
        LoginResult loginResult = server.login(new LoginRequest(user2,userPass2));
        assertNotNull(loginResult);
        assertTrue(loginResult.isSuccess());
        
        EventResult result = server.getEvents(loginResult.getAuthToken());
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(0,result.getEvents().length);
    }
    
    private String user = "kingpark";
    private String userPass = "myPass";
    private String userEmail = "test@test.com";
    private String userId = "1234_5468_90ab_cdef";
    private String userFirst = "Parker";
    private String userLast = "King";
    private String spouseId = "1111_1111_1111_1111";
    private String spouseFirst = "Hannah";
    private String spouseLast = "King";
    private String fatherId = "2222_2222_2222_2222";
    private String fatherFirst = "Steven";
    private String fatherLast = "King";
    private String motherId = "3333_3333_3333_3333";
    private String motherFirst = "Nancy";
    private String motherLast = "Comstock";
    private String fatherDadId = "4444_4444_4444_4444";
    private String fatherDadFirst = "Wesley";
    private String fatherDadLast = "King";
    private String fatherMomId = "5555_5555_5555_5555";
    private String fatherMomFirst = "Ursla";
    private String fatherMomLast = "Eichler";
    private String motherDadId = "6666_6666_6666_6666";
    private String motherDadFirst = "Herber";
    private String motherDadLast = "Comstock";
    private String motherMomId = "7777_7777_7777_7777";
    private String motherMomFirst = "Myrna";
    private String motherMomLast = "Jepson";
    
    private String user2 = "guitar_girl";
    private String userPass2 = "guitar";
    private String userEmail2 = "fake@test.com";
    private String userId2 = "cdef_5468_90ab_1234";
    private String userFirst2 = "Hannah";
    private String userLast2 = "King";
    
    private String birthEvent = "birth";
    private String marriageEvent = "marriage";
    private String deathEvent = "death";
    private String idUserBirth = "1111_1111_1111_1111";
    private String idUserMarriage = "1111_1111_1111_1112";
    private String idUserEvent1 = "1111_1111_1111_1113";
    private String idUserEvent2 = "1111_1111_1111_1114";
    private String idUserDeath = "1111_1111_1111_1115";
    private String idSpouseBirth = "1111_1111_1111_1116";
    private String idFatherBirth = "1111_1111_1111_1117";
    private String idMotherDeath = "1111_1111_1111_1118";
    private String idFatherDadEvent = "1111_1111_1111_1119";
    private String idFatherDadMarriage = "1111_1111_1111_1110";
    private String idFatherMomEvent1 = "1111_1111_1111_1121";
    private String idFatherMomEvent2 = "1111_1111_1111_1122";
    private String idMotherDadEvent = "1111_1111_1111_1123";
    private String idMotherDadMarriage = "1111_1111_1111_1124";
    private String idMotherMomEvent1 = "1111_1111_1111_11125";
    private String idMotherMomEvent2 = "1111_1111_1111_11126";
    
    private String loadRequest = "{\n" +
            "   \"users\":[\n" +
            "      {\n" +
            "         \"userName\":\"" + user + "\",\n" +
            "         \"password\":\"" + userPass + "\",\n" +
            "         \"email\":\"" + userEmail + "\",\n" +
            "         \"firstName\":\"" + userFirst + "\",\n" +
            "         \"lastName\":\"" + userLast + "\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"" + userId + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"userName\":\"" + user2 + "\",\n" +
            "         \"password\":\"" + userPass2 + "\",\n" +
            "         \"email\":\"" + userEmail2 + "\",\n" +
            "         \"firstName\":\"" + userFirst2 + "\",\n" +
            "         \"lastName\":\"" + userLast2 + "\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"" + userId2 + "\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"persons\":[\n" +
            "      {\n" +
            "         \"firstName\":\"" + userFirst + "\",\n" +
            "         \"lastName\":\"" + userLast + "\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"" + userId + "\",\n" +
            "         \"spouseID\":\"" + spouseId + "\",\n" +
            "         \"fatherID\":\"" + fatherId + "\",\n" +
            "         \"motherID\":\"" + motherId + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"" + spouseFirst + "\",\n" +
            "         \"lastName\":\"" + spouseLast + "\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"" + spouseId + "\",\n" +
            "         \"spouseID\":\"" + userId + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"" + fatherFirst + "\",\n" +
            "         \"lastName\":\"" + fatherLast + "\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"" + fatherId + "\",\n" +
            "         \"spouseID\":\"" + motherId + "\",\n" +
            "         \"fatherID\":\"" + fatherDadId + "\",\n" +
            "         \"motherID\":\"" + fatherMomId + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"" + motherFirst + "\",\n" +
            "         \"lastName\":\"" + motherLast + "\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"" + motherId + "\",\n" +
            "         \"spouseID\":\"" + fatherId + "\",\n" +
            "         \"fatherID\":\"" + motherDadId + "\",\n" +
            "         \"motherID\":\"" + motherMomId + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"" + fatherDadFirst + "\",\n" +
            "         \"lastName\":\"" + fatherDadLast + "\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"" + fatherDadId + "\",\n" +
            "         \"spouseID\":\"" + fatherMomId + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"" + fatherMomFirst + "\",\n" +
            "         \"lastName\":\"" + fatherMomLast + "\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"" + fatherMomId + "\",\n" +
            "         \"spouseID\":\"" + fatherDadId + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"" + motherDadFirst + "\",\n" +
            "         \"lastName\":\"" + motherDadLast + "\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"" + motherDadId + "\",\n" +
            "         \"spouseID\":\"" + motherMomId + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"" + motherMomFirst + "\",\n" +
            "         \"lastName\":\"" + motherMomLast + "\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"" + motherMomId + "\",\n" +
            "         \"spouseID\":\"" + motherDadId + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"events\":[\n" +
            "      {\n" +
            "         \"eventType\":\"" + birthEvent + "\",\n" +
            "         \"personID\":\"" + userId + "\",\n" +
            "         \"city\":\"Melbourne\",\n" +
            "         \"country\":\"Australia\",\n" +
            "         \"latitude\":-36.1833,\n" +
            "         \"longitude\":144.9667,\n" +
            "         \"year\":1970,\n" +
            "         \"eventID\":\"" + idUserBirth + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"" + marriageEvent + "\",\n" +
            "         \"personID\":\"" + userId + "\",\n" +
            "         \"city\":\"Los Angeles\",\n" +
            "         \"country\":\"United States\",\n" +
            "         \"latitude\":34.0500,\n" +
            "         \"longitude\":-117.7500,\n" +
            "         \"year\":2012,\n" +
            "         \"eventID\":\"" + idUserMarriage + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"completed asteroids\",\n" +
            "         \"personID\":\"" + userId + "\",\n" +
            "         \"city\":\"Qaanaaq\",\n" +
            "         \"country\":\"Denmark\",\n" +
            "         \"latitude\":77.4667,\n" +
            "         \"longitude\":-68.7667,\n" +
            "         \"year\":2014,\n" +
            "         \"eventID\":\"" + idUserEvent1 + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"COMPLETED ASTEROIDS\",\n" +
            "         \"personID\":\"" + userId + "\",\n" +
            "         \"city\":\"Qaanaaq\",\n" +
            "         \"country\":\"Denmark\",\n" +
            "         \"latitude\":74.4667,\n" +
            "         \"longitude\":-60.7667,\n" +
            "         \"year\":2014,\n" +
            "         \"eventID\":\"" + idUserEvent2 + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"" + deathEvent + "\",\n" +
            "         \"personID\":\"" + userId + "\",\n" +
            "         \"city\":\"Provo\",\n" +
            "         \"country\":\"United States\",\n" +
            "         \"latitude\":40.2444,\n" +
            "         \"longitude\":111.6608,\n" +
            "         \"year\":2015,\n" +
            "         \"eventID\":\"" + idUserDeath + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"" + birthEvent + "\",\n" +
            "         \"personID\":\"" + spouseId + "\",\n" +
            "         \"city\":\"Hakodate\",\n" +
            "         \"country\":\"Japan\",\n" +
            "         \"latitude\":41.7667,\n" +
            "         \"longitude\":140.7333,\n" +
            "         \"year\":1970,\n" +
            "         \"eventID\":\"" + idSpouseBirth + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"" + birthEvent + "\",\n" +
            "         \"personID\":\"" + fatherId + "\",\n" +
            "         \"city\":\"Bratsk\",\n" +
            "         \"country\":\"Russia\",\n" +
            "         \"latitude\":56.1167,\n" +
            "         \"longitude\":101.6000,\n" +
            "         \"year\":1948,\n" +
            "         \"eventID\":\"" + idFatherBirth + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"" + deathEvent + "\",\n" +
            "         \"personID\":\"" + motherId + "\",\n" +
            "         \"city\":\"Birmingham\",\n" +
            "         \"country\":\"United Kingdom\",\n" +
            "         \"latitude\":52.4833,\n" +
            "         \"longitude\":-0.1000,\n" +
            "         \"year\":2017,\n" +
            "         \"eventID\":\"" + idMotherDeath + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Graduated from BYU\",\n" +
            "         \"personID\":\"" + fatherDadId + "\",\n" +
            "         \"country\": \"United States\",\n" +
            "         \"city\": \"Provo\",\n" +
            "         \"latitude\": 40.2338,\n" +
            "         \"longitude\": -111.6585,\n" +
            "         \"year\":1879,\n" +
            "         \"eventID\":\"" + idFatherDadEvent + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"" + marriageEvent + "\",\n" +
            "         \"personID\":\"" + fatherDadId + "\",\n" +
            "         \"country\": \"North Korea\",\n" +
            "         \"city\": \"Wonsan\",\n" +
            "         \"latitude\": 39.15,\n" +
            "         \"longitude\": 127.45,\n" +
            "         \"year\":1895,\n" +
            "         \"eventID\":\"" + idFatherDadMarriage + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Did a backflip\",\n" +
            "         \"personID\":\"" + fatherMomId + "\",\n" +
            "         \"country\": \"Mexico\",\n" +
            "         \"city\": \"Mexicali\",\n" +
            "         \"latitude\": 32.6667,\n" +
            "         \"longitude\": -114.5333,\n" +
            "         \"year\":1890,\n" +
            "         \"eventID\":\"" + idFatherMomEvent1 + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"learned Java\",\n" +
            "         \"personID\":\"" + fatherMomId + "\",\n" +
            "         \"country\": \"Algeria\",\n" +
            "         \"city\": \"Algiers\",\n" +
            "         \"latitude\": 36.7667,\n" +
            "         \"longitude\": 3.2167,\n" +
            "         \"year\":1890,\n" +
            "         \"eventID\":\"" + idFatherMomEvent2 + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Caught a frog\",\n" +
            "         \"personID\":\"" + motherDadId + "\",\n" +
            "         \"country\": \"Bahamas\",\n" +
            "         \"city\": \"Nassau\",\n" +
            "         \"latitude\": 25.0667,\n" +
            "         \"longitude\": -76.6667,\n" +
            "         \"year\":1993,\n" +
            "         \"eventID\":\"" + idMotherDadEvent + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"" + marriageEvent + "\",\n" +
            "         \"personID\":\"" + motherDadId + "\",\n" +
            "         \"country\": \"Ghana\",\n" +
            "         \"city\": \"Tamale\",\n" +
            "         \"latitude\": 9.4,\n" +
            "         \"longitude\": 0.85,\n" +
            "         \"year\":1997,\n" +
            "         \"eventID\":\"" + idMotherDadMarriage + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Ate Brazilian Barbecue\",\n" +
            "         \"personID\":\"" + motherMomId + "\",\n" +
            "         \"country\": \"Brazil\",\n" +
            "         \"city\": \"Curitiba\",\n" +
            "         \"latitude\": -24.5833,\n" +
            "         \"longitude\": -48.75,\n" +
            "         \"year\":2012,\n" +
            "         \"eventID\":\"" + idMotherMomEvent1 + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Learned to Surf\",\n" +
            "         \"personID\":\"" + motherMomId + "\",\n" +
            "         \"country\": \"Australia\",\n" +
            "         \"city\": \"Gold Coast\",\n" +
            "         \"latitude\": -27.9833,\n" +
            "         \"longitude\": 153.4,\n" +
            "         \"year\": 2000,\n" +
            "         \"eventID\":\"" + idMotherMomEvent2 + "\",\n" +
            "         \"associatedUsername\":\"" + user + "\"\n" +
            "      }\n" +
            "   ]\n" +
            "}\n" +
            "\n";
}