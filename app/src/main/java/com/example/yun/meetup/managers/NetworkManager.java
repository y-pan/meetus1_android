package com.example.yun.meetup.managers;

import android.text.Html;

import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.providers.ApiProvider;
import com.example.yun.meetup.requests.CreateEventRequest;
import com.example.yun.meetup.requests.EventListRequest;
import com.example.yun.meetup.requests.LoginRequest;
import com.example.yun.meetup.requests.ParticipateToEventRequest;
import com.example.yun.meetup.requests.RegistrationRequest;
import com.example.yun.meetup.requests.SearchEventsRequest;
import com.example.yun.meetup.requests.UpdateEventRequest;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {

    private static final String GOOGLE_API_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String GOOGLE_API_KEY = "AIzaSyAcujkeJzWDW0S31u_tCf2o9B3K0e15Z-U";
    private static ApiProvider apiProvider = new ApiProvider();

    public APIResult login(LoginRequest loginRequest) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson .toJson(loginRequest);

        APIResult apiResult = new APIResult(false, "Login failed: please try again", null);

        try{

            String response = apiProvider.sendRequest("/user/login", "POST", json);

            JSONObject responseJSON = new JSONObject(response);
            if (!responseJSON.isNull("data")) {

                UserInfo userInfo = gson.fromJson(responseJSON.getJSONObject("data").toString(), UserInfo.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, userInfo);
            }
            else if (!responseJSON.isNull("err") && responseJSON.getString("err").equals("User not found")){
                apiResult = new APIResult(false, "Wrong email/password. Please try again", null);
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult register(RegistrationRequest registrationRequest) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson .toJson(registrationRequest);

        APIResult apiResult = new APIResult(false, "Registration failed: please try again", null);

        try {

            String response = apiProvider.sendRequest("/user/register", "POST", json);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                UserInfo userInfo = gson.fromJson(responseJSON.getJSONObject("data").toString(), UserInfo.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, userInfo);

            }
            else if (!responseJSON.isNull("err") && responseJSON.getString("err").equals("Email is already used")){
                apiResult = new APIResult(false, "Email is already used: please try again", null);
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult validateEventAddress(CreateEventRequest createEventRequest) {

        APIResult apiResult = new APIResult(false, "Failed converting address to location: please try again", null);

        try {
            String response = apiProvider.sendRequest(GOOGLE_API_GEOCODE_URL + "?key=" + GOOGLE_API_KEY + "&address=" + URLEncoder.encode(createEventRequest.getAddress(), "UTF-8"), "GET", null);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("status")) {

                if (responseJSON.getString("status").equals("OK")) {
                    JSONObject result = responseJSON.getJSONArray("results").getJSONObject(0);
                    createEventRequest.setAddress(result.getString("formatted_address"));
                    createEventRequest.setLatitude(Float.parseFloat(result.getJSONObject("geometry").getJSONObject("location").getString("lat")));
                    createEventRequest.setLongitude(Float.parseFloat(result.getJSONObject("geometry").getJSONObject("location").getString("lng")));
                    apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, createEventRequest);
                }
                else if (responseJSON.getString("status").equals("ZERO_RESULTS")) {
                    apiResult = new APIResult(false, "No address location found. Please enter a valid address!", null);
                }

            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult validateEventAddress(UpdateEventRequest request) {

        APIResult apiResult = new APIResult(false, "Failed converting address to location: please try again", null);

        try {
            String response = apiProvider.sendRequest(GOOGLE_API_GEOCODE_URL + "?key=" + GOOGLE_API_KEY + "&address=" + URLEncoder.encode(request.getAddress(), "UTF-8"), "GET", null);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("status")) {

                if (responseJSON.getString("status").equals("OK")) {
                    JSONObject result = responseJSON.getJSONArray("results").getJSONObject(0);
                    request.setAddress(result.getString("formatted_address"));
                    request.setLatitude(Float.parseFloat(result.getJSONObject("geometry").getJSONObject("location").getString("lat")));
                    request.setLongitude(Float.parseFloat(result.getJSONObject("geometry").getJSONObject("location").getString("lng")));
                    apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, request);
                }
                else if (responseJSON.getString("status").equals("ZERO_RESULTS")) {
                    apiResult = new APIResult(false, "No address location found. Please enter a valid address!", null);
                }

            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult createEvent(CreateEventRequest createEventRequest) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson .toJson(createEventRequest);

        APIResult apiResult = new APIResult(false, "Failed creating the event: please try again", null);

        try {

            String response = apiProvider.sendRequest("/event", "POST", json);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                Event event = gson.fromJson(responseJSON.getJSONObject("data").toString(), Event.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, event);

            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult updateEvent(UpdateEventRequest request) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(request);

        APIResult apiResult = new APIResult(false, "Failed updating the event: please try again", null);

        try {

            String response = apiProvider.sendRequest("/event/update", "POST", json);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {
                Event event = gson.fromJson(responseJSON.getJSONObject("data").toString(), Event.class);
                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, event);
            } else if (!responseJSON.isNull("err")) {
                apiResult = new APIResult(false, responseJSON.getString("err"), null);
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult getHostedEvents(EventListRequest eventListRequest){

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson .toJson(eventListRequest);

        APIResult apiResult = new APIResult(false, "Failed getting list of hosted events: please try again", null);

        try {

            String response = apiProvider.sendRequest("/host_event", "POST", json);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                Event[] arrayEvent = gson.fromJson(responseJSON.getJSONArray("data").toString(), Event[].class);

                if (arrayEvent.length > 0){
                    response = apiProvider.sendRequest("/user?id=" + eventListRequest.getHost_id(), "GET", null);

                    responseJSON = new JSONObject(response);

                    if (!responseJSON.isNull("data")) {

                        UserInfo userInfo = gson.fromJson(responseJSON.getJSONObject("data").toString(), UserInfo.class);

                        List<Event> listEvents = new ArrayList<>();

                        for (Event event : arrayEvent){
                            event.setUserInfo(userInfo);
                            listEvents.add(event);
                        }

                        apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, listEvents);
                    }
                }

            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult searchEvents(SearchEventsRequest searchEventsRequest){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        APIResult apiResult = new APIResult(false, "Failed getting events around you: please try again", null);

        try {

            String response = apiProvider.sendRequest("/event/search?latitude=" + searchEventsRequest.getLatitude() + "&longitude=" + searchEventsRequest.getLongitude() + "&distance=" + searchEventsRequest.getDistance(), "GET", null);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                Event[] arrayEvent = gson.fromJson(responseJSON.getJSONArray("data").toString(), Event[].class);

                if (arrayEvent.length > 0){

                    List<Event> listEvents = new ArrayList<>();

                    for (Event event : arrayEvent){
                        listEvents.add(event);
                    }

                    apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, listEvents);
                }

            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult getUserById(String id){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        APIResult apiResult = new APIResult(false, "Failed getting user info: please try again", null);

        try{
            String response = apiProvider.sendRequest("/user?id=" + id, "GET", null);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")){
                UserInfo userInfo = gson.fromJson(responseJSON.getJSONObject("data").toString(), UserInfo.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, userInfo);

            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;

    }

    public APIResult getEventById(String id){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        APIResult apiResult = new APIResult(false, "Failed getting events details: please try again", null);

        try{
            String response = apiProvider.sendRequest("/event?id=" + id, "GET", null);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                Event event = gson.fromJson(responseJSON.getJSONObject("data").toString(), Event.class);

                APIResult hostResult = getUserById(event.getHost_id());

                if (hostResult.isResultSuccess()){
                    event.setUserInfo((UserInfo) hostResult.getResultEntity());

                    if (!responseJSON.getJSONObject("data").isNull("members")){
                       String[] memberIds = gson.fromJson(responseJSON.getJSONObject("data").getJSONArray("members").toString(), String[].class);

                       for (String memberId : memberIds){
                           APIResult memberResult = getUserById(memberId);

                           if (memberResult.isResultSuccess()){
                               event.getMembers().add((UserInfo) memberResult.getResultEntity());
                           }
                       }

                       apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, event);
                    }
                }

            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult participateToEvent(ParticipateToEventRequest participateToEventRequest){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson .toJson(participateToEventRequest);

        APIResult apiResult = new APIResult(false, "Error participating to the event: please try again", null);

        try{
            String response = apiProvider.sendRequest("/event/subscribe", "POST", json);

            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("data")){
                Event event = gson.fromJson(jsonObject.getJSONObject("data").toString(), Event.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, event);
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult getSubscribedEvents(String guest_id){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        APIResult apiResult = new APIResult(false, "Error getting list of subscribed events: please try again", null);

        try{
            String response = apiProvider.sendRequest("/guest_event?id=" + guest_id, "GET", null);

            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("data")){
                Event[] events = gson.fromJson(jsonObject.getJSONArray("data").toString(), Event[].class);

                if (events.length > 0){

                    List<Event> listEvents = new ArrayList<>();

                    for (Event event : events){
                        response = apiProvider.sendRequest("/user?id=" + event.getHost_id(), "GET", null);

                        jsonObject = new JSONObject(response);

                        if (!jsonObject.isNull("data")){
                            UserInfo userInfo = gson.fromJson(jsonObject.getJSONObject("data").toString(), UserInfo.class);
                            event.setUserInfo(userInfo);
                        }

                        listEvents.add(event);
                    }

                    apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, listEvents);

                }
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult deleteEvent(String event_id){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        APIResult apiResult = new APIResult(false, "Error getting list of subscribed events: please try again", null);

        try {
            String response = apiProvider.sendRequest("/event?id=" + event_id, "DELETE", null);

            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("data")) {
                Event event = gson.fromJson(jsonObject.getJSONObject("data").toString(), Event.class);

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, event);
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    public APIResult getAllEvents(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        APIResult apiResult = new APIResult(false, "Error getting list of events: please try again", null);

        try {
            String response = apiProvider.sendRequest("/events", "GET", null);

            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                Event[] results = gson.fromJson(jsonArray.toString(), Event[].class);

                List<Event> events = new ArrayList<>();

                for (Event event : results){

                    try{
                        response = apiProvider.sendRequest("/user?id=" + event.getHost_id(), "GET", null);

                        JSONObject responseJSON = new JSONObject(response);

                        if (!responseJSON.isNull("data")) {

                            UserInfo userInfo = gson.fromJson(responseJSON.getJSONObject("data").toString(), UserInfo.class);

                            event.setUserInfo(userInfo);
                        }
                    }
                    catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    events.add(event);
                }

                apiResult = new APIResult(true, APIResult.RESULT_SUCCESS, events);
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

}
