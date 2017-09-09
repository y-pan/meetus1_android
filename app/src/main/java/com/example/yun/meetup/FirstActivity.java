package com.example.yun.meetup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FirstActivity extends AppCompatActivity {

    private TextView res;
    private EditText lat, lon;
    private Button sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        lat = (EditText)findViewById(R.id.txtLat);
        lon = (EditText)findViewById(R.id.txtLon);
        res = (TextView) findViewById(R.id.tvResult);
        sendButton = (Button)findViewById(R.id.btnSend);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Meetup","Clicked");


                Toast.makeText(getApplicationContext(), "Date Sent",Toast.LENGTH_SHORT).show();
                new GetDataTask().execute("https://meetup1.herokuapp.com/api/user");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return false;
    }

    class GetDataTask extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            progressDialog = new ProgressDialog(FirstActivity.this);
            progressDialog.setMessage("Loading data...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            try {
                // Initialize and config request, then connect to server
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000); /*milliseconds*/
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type","application/json");// header
                urlConnection.connect();

                //Read response from server
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line=bufferedReader.readLine())!=null){
                    result.append(line).append("\n");
                }


            } catch (MalformedURLException e) {
                return "Network error!";
            } catch (IOException e) {
                return "Network error!";
            }
            return result.toString();
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            // set data response to textView
            res.setText(result);

            // cancel progres dialog
            if(progressDialog != null){
                progressDialog.dismiss();
            }
        }

    }
}
