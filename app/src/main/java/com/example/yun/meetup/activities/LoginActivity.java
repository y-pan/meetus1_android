package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yun.meetup.R;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayoutLoginLoading;

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;

    private TextView textViewLoginError;
    private TextView textViewLoginErrorEmail;
    private TextView textViewLoginErrorPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        constraintLayoutLoginLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoginLoading);

        editTextLoginEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        textViewLoginError = (TextView) findViewById(R.id.textViewLoginError);
        textViewLoginErrorEmail = (TextView) findViewById(R.id.textViewLoginErrorEmail);
        textViewLoginErrorPassword = (TextView) findViewById(R.id.textViewloginErrorPassword);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("id", null);
        if (name != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            hideViews();
        }


    }

    public void handleOnClickLogin(View view) {
        hideViews();

        boolean error = false;

        if (editTextLoginEmail.getText().toString().isEmpty()){
            textViewLoginErrorEmail.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextLoginPassword.getText().toString().isEmpty()){
            textViewLoginErrorPassword.setVisibility(View.VISIBLE);
            error = true;
        }

        if (!error){
            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(editTextLoginEmail.getText().toString());
            userInfo.setPassword(editTextLoginPassword.getText().toString());

            constraintLayoutLoginLoading.setVisibility(View.VISIBLE);

            new LoginTask().execute(userInfo);
        }
    }

    public void handleOnClickCreateAccount(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void hideViews(){

        textViewLoginErrorEmail.setVisibility(View.GONE);
        textViewLoginErrorPassword.setVisibility(View.GONE);
        constraintLayoutLoginLoading.setVisibility(View.GONE);
    }

    private class LoginTask extends AsyncTask<UserInfo, Void, UserInfo>{

        @Override
        protected UserInfo doInBackground(UserInfo... userInfos) {

            NetworkManager networkManager = new NetworkManager();
            return networkManager.login(userInfos[0]);
        }

        @Override
        protected void onPostExecute(UserInfo result) {

            hideViews();

            if (result == null){
                textViewLoginError.setText("Login failed. Please try again");
            }
            else{
                SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("id", result.getID());
                editor.putString("email", result.getEmail());
                editor.putString("name", result.getFullName());
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }


        }
    }
}
