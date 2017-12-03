package com.example.yun.meetup.managers;

import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.providers.ApiProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkManager {

    private static ApiProvider apiProvider = new ApiProvider();

    public String login(UserInfo userInfo){

        JSONObject json = new JSONObject();

        try {
            // create data to send to server
            json.put("email", userInfo.getEmail());
            json.put("password", userInfo.getPassword());

            return apiProvider.sendRequest("/user/login", "POST", json);

        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String register(UserInfo userInfo){

        JSONObject json = new JSONObject();

        try{
            // create data to send to server
            json.put("email",userInfo.getEmail());
            json.put("name",userInfo.getFirstName() + " " + userInfo.getLastName());
            json.put("password", userInfo.getPassword());

            return apiProvider.sendRequest("/user/register", "POST", json);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
