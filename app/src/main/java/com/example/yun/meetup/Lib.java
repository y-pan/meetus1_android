package com.example.yun.meetup;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Lib {

    public static String postUserData(String urlPath)throws IOException, JSONException{
        StringBuilder result = new StringBuilder();
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        JSONObject json = new JSONObject();
        try{
            // create data to send to server

            json.put("email","cat@gmail.com");
            json.put("name","Kyti");
            json.put("number","number");
            json.put("password","111");
            json.put("groupCodes",new String[]{"111","222","abc"});
            /* todo: Operation failed - ValidationError:loc:Cast to Array failed for value, seperate into lat and lon ??? */
            json.put("loc",new Number[]{54.762686,-79.295182});
            //json.put("loc",new String[]{"54.762686","-79.295182"});

            // Initialize and config request, then connect to server
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000); /*milliseconds*/
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("POST");
            //urlConnection.setRequestProperty("Accept","application/json");
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
        }finally {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
        }

        return result.toString();
    }

    public static String getData(String urlPath) throws IOException{
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            // Initialize and config request, then connect to server
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000); /*milliseconds*/
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type","application/json");// header
            urlConnection.connect();

            //Read response from server
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line=bufferedReader.readLine())!=null){
                result.append(line).append("\n");
            }

        } finally {
            if(bufferedReader != null){
                bufferedReader.close();
            }
        }
        return result.toString();
    }
}
