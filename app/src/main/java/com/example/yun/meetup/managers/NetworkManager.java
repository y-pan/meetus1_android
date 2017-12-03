package com.example.yun.meetup.managers;

import com.example.yun.meetup.models.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by alessio on 02-Dec-17.
 */

public class NetworkManager {

    private static final String BASE_URL = "https://meetus01.herokuapp.com/api";

    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    public String login(UserInfo userInfo){

        String urlPath = getAbsoluteUrl("/user/login");

        StringBuilder result = new StringBuilder();

        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        JSONObject json = new JSONObject();

        try {
            // create data to send to server
            json.put("email", userInfo.getEmail());
            json.put("password", userInfo.getPassword());

            // Initialize and config request, then connect to server
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");// header
            urlConnection.connect();

            //Write data into server
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(json.toString());
            bufferedWriter.flush();

            // read response from server
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bufferedWriter != null){
                try {
                    bufferedWriter.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toString();
    }

    public String register(UserInfo userInfo){
        String urlPath = getAbsoluteUrl("/user/register");

        StringBuilder result = new StringBuilder();

        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        JSONObject json = new JSONObject();

        try{
            // create data to send to server
            json.put("email",userInfo.getEmail());
            json.put("name",userInfo.getFirstName() + " " + userInfo.getLastName());
            json.put("password", userInfo.getPassword());

            // Initialize and config request, then connect to server
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");// header
            urlConnection.connect();

            //Write data into server
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(json.toString());
            bufferedWriter.flush();

            // read response from server
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = bufferedReader.readLine())!= null){
                result.append(line).append("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bufferedWriter != null){
                try {
                    bufferedWriter.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toString();
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
