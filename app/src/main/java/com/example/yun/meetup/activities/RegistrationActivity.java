package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yun.meetup.R;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.UserInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayoutRegistrationLoading;

    private EditText editTextRegistrationEmail;
    private EditText editTextRegistrationFirstName;
    private EditText editTextRegistrationLastName;
    private EditText editTextRegistrationPassword;
    private EditText editTextRegistrationConfirmPassword;

    private TextView textViewRegistrationErrorEmail;
    private TextView textViewRegistrationErrorFirstName;
    private TextView textViewRegistrationErrorLastName;
    private TextView textViewRegistrationErrorPassword;
    private TextView textViewRegistrationErrorConfirmPassword;
    private TextView textViewRegistrationError;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        constraintLayoutRegistrationLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoginLoading);

        editTextRegistrationEmail = (EditText) findViewById(R.id.editTextRegistrationEmail);
        editTextRegistrationFirstName = (EditText) findViewById(R.id.editTextRegistrationFirstName);
        editTextRegistrationLastName = (EditText) findViewById(R.id.editTextRegistrationLastName);
        editTextRegistrationPassword = (EditText) findViewById(R.id.editTextRegistrationPassword);
        editTextRegistrationConfirmPassword = (EditText) findViewById(R.id.editTextRegistrationConfirmPassword);

        textViewRegistrationError = (TextView) findViewById(R.id.textViewRegistrationError);
        textViewRegistrationErrorFirstName = (TextView) findViewById(R.id.textViewRegistrationErrorFirstName);
        textViewRegistrationErrorLastName = (TextView) findViewById(R.id.textViewRegistrationErrorLastName);
        textViewRegistrationErrorEmail = (TextView) findViewById(R.id.textViewRegistrationErrorEmail);
        textViewRegistrationErrorPassword = (TextView) findViewById(R.id.textViewRegistrationErrorPassword);
        textViewRegistrationErrorConfirmPassword = (TextView) findViewById(R.id.textViewRegistrationErrorConfirmPassword);

        hideViews();
    }

    public void handleOnClickSignin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void handleOnClickRegister(View view) {
        hideViews();

        boolean error = false;

        if (editTextRegistrationEmail.getText().toString().isEmpty()){
            textViewRegistrationErrorEmail.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextRegistrationFirstName.getText().toString().isEmpty()){
            textViewRegistrationErrorFirstName.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextRegistrationLastName.getText().toString().isEmpty()){
            textViewRegistrationErrorLastName.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextRegistrationPassword.getText().toString().isEmpty()){
            textViewRegistrationErrorPassword.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextRegistrationConfirmPassword.getText().toString().isEmpty()){
            textViewRegistrationErrorConfirmPassword.setText("Please confirm password");
            textViewRegistrationErrorConfirmPassword.setVisibility(View.VISIBLE);
            error = true;
        }
        if (!editTextRegistrationConfirmPassword.getText().toString().equals(editTextRegistrationPassword.getText().toString())){
            textViewRegistrationErrorConfirmPassword.setText("Passwords do not match!");
            textViewRegistrationErrorConfirmPassword.setVisibility(View.VISIBLE);
            error = true;
        }

        if (!error){
            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(editTextRegistrationEmail.getText().toString());
            userInfo.setFirstName(editTextRegistrationFirstName.getText().toString());
            userInfo.setLastName(editTextRegistrationLastName.getText().toString());
            userInfo.setPassword(editTextRegistrationPassword.getText().toString());

            constraintLayoutRegistrationLoading.setVisibility(View.VISIBLE);

            new RegistrationTask().execute(userInfo);
        }
    }

    public void hideViews(){
        textViewRegistrationErrorEmail.setVisibility(View.GONE);
        textViewRegistrationErrorFirstName.setVisibility(View.GONE);
        textViewRegistrationErrorLastName.setVisibility(View.GONE);
        textViewRegistrationErrorPassword.setVisibility(View.GONE);
        textViewRegistrationErrorConfirmPassword.setVisibility(View.GONE);

        constraintLayoutRegistrationLoading.setVisibility(View.GONE);
    }

    private class RegistrationTask extends AsyncTask<UserInfo, Void, String>{

        @Override
        protected String doInBackground(UserInfo... userInfos) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.register(userInfos[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            hideViews();
            try {
                JSONObject responseJSON = new JSONObject(s);
                if (!responseJSON.isNull("data")){

                    JSONObject dataJSON = responseJSON.getJSONObject("data");

                    SharedPreferences sharedPref = RegistrationActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("id", dataJSON.getString("_id"));
                    editor.putString("email", dataJSON.getString("email"));
                    editor.putString("name", dataJSON.getString("name"));
                    editor.commit();

                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else if (responseJSON.getString("err").equals("Email is already used")){
                    textViewRegistrationError.setText("Wrong email or password. Please try again");
                }
                else{
                    textViewRegistrationError.setText("Registration failed. Please try again");
                }
            }
            catch (JSONException e) {
                textViewRegistrationError.setText("Registration failed. Please try again");
            }
        }
    }
}
