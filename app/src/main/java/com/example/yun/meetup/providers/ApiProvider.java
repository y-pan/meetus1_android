package com.example.yun.meetup.providers;

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
import java.util.HashMap;

/**
 * Created by alessio on 03-Dec-17.
 */

public class ApiProvider {

    private static final String BASE_URL = "https://meetus01.herokuapp.com/api";

    public String sendRequest(String absoluteURL, String method, String body) throws  IOException {

        // If provided URL is partial, append internal API BASE_URL prefix
        String urlPath = absoluteURL.startsWith("http") ? absoluteURL : getAbsoluteUrl(absoluteURL);

        StringBuilder result = new StringBuilder();

        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;

        // Initialize and config request, then connect to server
        URL url = new URL(urlPath);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(method);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.connect();

        //Write data into server
        if(body != null) {
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(body);
            bufferedWriter.flush();
        }

        // read response from server
        InputStream inputStream = urlConnection.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line).append("\n");
        }

        if(bufferedReader != null){
            bufferedReader.close();
        }
        if(bufferedWriter != null){
            bufferedWriter.close();
        }

        return result.toString();
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
