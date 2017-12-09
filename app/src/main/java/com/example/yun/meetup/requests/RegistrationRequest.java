package com.example.yun.meetup.requests;

import java.util.List;

/**
 * Created by alessio on 08-Dec-17.
 */

public class RegistrationRequest {

    private String email;
    private String password;
    private String name;
    private double[] loc;
    private String[] eventCodes;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getLoc() {
        return loc;
    }

    public void setLoc(double[] loc) {
        this.loc = loc;
    }

    public String[] getEventCodes() {
        return eventCodes;
    }

    public void setEventCodes(String[] eventCodes) {
        this.eventCodes = eventCodes;
    }
}
