package com.example.yun.meetup.requests;

/**
 * Created by alessio on 16-Dec-17.
 */

public class SearchEventsRequest {

    private double latitude = 0;
    private double longitude = 0;
    private double distance = 50000;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
