package com.example.yun.meetup.requests;

/**
 * Created by alessio on 18-Dec-17.
 */

public class ParticipateToEventRequest {
    private String event_id;
    private String user_id;

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
