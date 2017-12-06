package com.example.yun.meetup.managers;

import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.providers.ApiProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkManager {

    private static ApiProvider apiProvider = new ApiProvider();

    public UserInfo login(UserInfo userInfo){

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
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public UserInfo register(UserInfo userInfo){

        JSONObject json = new JSONObject();

        try{
            // create data to send to server
            json.put("email",userInfo.getEmail());
            json.put("name",userInfo.getFirstName() + " " + userInfo.getLastName());
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
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }


}
