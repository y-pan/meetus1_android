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
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.yun.meetup.R;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.requests.LoginRequest;

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

    private ScrollView scrollViewLogin;



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

        scrollViewLogin = (ScrollView) findViewById(R.id.scrollViewLogin);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", null);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
        if (id != null){
            if (isAdmin){
                Intent intent = new Intent(this, AdminMainActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

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
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(editTextLoginEmail.getText().toString());
            loginRequest.setPassword(editTextLoginPassword.getText().toString());

            constraintLayoutLoginLoading.setVisibility(View.VISIBLE);
            scrollViewLogin.setClickable(false);

            new LoginTask().execute(loginRequest);
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
        scrollViewLogin.setClickable(true);
    }

    private class LoginTask extends AsyncTask<LoginRequest, Void, APIResult>{

        @Override
        protected APIResult doInBackground(LoginRequest... loginRequests) {

            NetworkManager networkManager = new NetworkManager();
            return networkManager.login(loginRequests[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if (!apiResult.isResultSuccess()){
                textViewLoginError.setText(apiResult.getResultMessage());
            }
            else{
                UserInfo result = (UserInfo) apiResult.getResultEntity();
                SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("id", result.get_id());
                editor.putString("email", result.getEmail());
                editor.putString("name", result.getName());
                editor.putBoolean("isAdmin", result.isAdmin());
                editor.commit();

                if (result.isAdmin()){
                    Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        }
    }
}
