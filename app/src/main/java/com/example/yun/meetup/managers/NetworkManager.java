package com.example.yun.meetup.managers;

import android.text.Html;

import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.providers.ApiProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NetworkManager {

    private static final String GOOGLE_API_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String GOOGLE_API_KEY = "AIzaSyAcujkeJzWDW0S31u_tCf2o9B3K0e15Z-U";
    private static ApiProvider apiProvider = new ApiProvider();

    public UserInfo login(UserInfo userInfo) {

        JSONObject json = new JSONObject();

        try {
            // create data to send to server
            json.put("email", userInfo.getEmail());
            json.put("password", userInfo.getPassword());

            String response = apiProvider.sendRequest("/user/login", "POST", json);

            if (response != null) {
                JSONObject responseJSON = new JSONObject(response);
                if (!responseJSON.isNull("data")) {

                    JSONObject dataJSON = responseJSON.getJSONObject("data");
                    UserInfo userInfoResult = new UserInfo();
                    userInfoResult.setID(dataJSON.getString("_id"));
                    userInfoResult.setEmail(dataJSON.getString("email"));
                    userInfoResult.setFullName(dataJSON.getString("name"));

                    return userInfoResult;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public UserInfo register(UserInfo userInfo) {

        JSONObject json = new JSONObject();

        try {
            // create data to send to server
            json.put("email", userInfo.getEmail());
            json.put("name", userInfo.getFirstName() + " " + userInfo.getLastName());
            json.put("password", userInfo.getPassword());

            String response = apiProvider.sendRequest("/user/register", "POST", json);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                JSONObject dataJSON = responseJSON.getJSONObject("data");
                UserInfo userInfoResult = new UserInfo();
                userInfoResult.setID(dataJSON.getString("_id"));
                userInfoResult.setEmail(dataJSON.getString("email"));
                userInfoResult.setFullName(dataJSON.getString("name"));

                return userInfoResult;

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public APIResult validateEventAddress(Event event) {

        try {
            String response = apiProvider.sendRequest(GOOGLE_API_GEOCODE_URL + "?key=" + GOOGLE_API_KEY + "&address=" + URLEncoder.encode(event.getAddress(), "UTF-8"), "GET", null);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("status")) {

                if (responseJSON.getString("status").equals("OK")) {
                    JSONObject result = responseJSON.getJSONArray("results").getJSONObject(0);
                    event.setAddress(result.getString("formatted_address"));
                    event.setLatitude(Float.parseFloat(result.getJSONObject("geometry").getJSONObject("location").getString("lat")));
                    event.setLongitude(Float.parseFloat(result.getJSONObject("geometry").getJSONObject("location").getString("lng")));
                    return new APIResult(true, APIResult.RESULT_SUCCESS, event);
                } else if (responseJSON.getString("status").equals("ZERO_RESULTS")) {
                    return new APIResult(false, "No address location found. Please enter a valid address!", null);
                }

            } else if (!responseJSON.isNull("err")) {
                return new APIResult(false, responseJSON.getString("err"), null);
            }
        } catch (JSONException e) {
            return new APIResult(false, e.getMessage(), null);
        } catch (UnsupportedEncodingException e) {
            return new APIResult(false, e.getMessage(), null);
        }

        return new APIResult(false, "Fatal error, please contact the admin staff!", null);
    }

    public APIResult createEvent(Event event) {

        JSONObject json = new JSONObject();

        try {
            // create data to send to server
            json.put("host_id", event.getHostID());
            json.put("title", event.getTitle());
            json.put("subtitle", event.getSubtitle());
            json.put("date", event.getDate());
            json.put("address", event.getAddress());
            json.put("discription", event.getDescription());
            json.put("latitude", event.getLatitude());
            json.put("longitude", event.getLongitude());

            String response = apiProvider.sendRequest("/event", "POST", json);

            JSONObject responseJSON = new JSONObject(response);

            if (!responseJSON.isNull("data")) {

                JSONObject dataJSON = responseJSON.getJSONObject("data");
                event.setID(dataJSON.getString("_id"));
                return new APIResult(true, APIResult.RESULT_SUCCESS, event);

            } else if (!responseJSON.isNull("err")) {
                return new APIResult(false, responseJSON.getString("err"), null);
            }
        } catch (JSONException e) {
            return new APIResult(false, e.getMessage(), null);
        }

        return new APIResult(false, "Fatal error, please contact the admin staff!", null);
    }


}
