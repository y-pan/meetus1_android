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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

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
    private Button postUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        lat = (EditText)findViewById(R.id.txtLat);
        lon = (EditText)findViewById(R.id.txtLon);
        res = (TextView) findViewById(R.id.tvResult);
        sendButton = (Button)findViewById(R.id.btnSend);
        postUserButton = (Button)findViewById(R.id.btnPostUser);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Get data",Toast.LENGTH_SHORT).show();
                new GetDataTask().execute("https://meetup1.herokuapp.com/api/user");
            }
        });

        postUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Post Data",Toast.LENGTH_SHORT).show();
                new PostDataTask().execute("https://meetup1.herokuapp.com/api/user");
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
/**********************************************************************************************************/

    class GetDataTask extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(FirstActivity.this);
            progressDialog.setMessage("Loading data...");
            progressDialog.show();
        }
        /* todo: get json instead of string */
        @Override
        protected String doInBackground(String... strings) {
            try {
                return Lib.getData(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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
    class PostDataTask extends AsyncTask<String, Void, String>{
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
            try {
                return Lib.postUserData(strings[0]);
            } catch (IOException e) {
                return "Network Error!";
            } catch (JSONException e) {
                return "Data Invalid!";
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            res.setText(result);
            if(progressDialog != null){
                progressDialog.dismiss();
            }
        }
    }

}
