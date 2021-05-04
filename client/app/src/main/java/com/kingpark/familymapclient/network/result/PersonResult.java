package com.kingpark.familymapclient.network.result;

import com.google.gson.annotations.SerializedName;
import com.kingpark.familymapclient.model.Person;

public class PersonResult extends Result {
    @SerializedName("data")
    private Person[] mPeople;
    
    public PersonResult(Person[] people) {
        super();
        mPeople = people;
    }
    
    public PersonResult(String message) {
        super(message);
    }
    
    public Person[] getPeople() {
        return mPeople;
    }
}
