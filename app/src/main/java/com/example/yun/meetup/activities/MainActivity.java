package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.yun.meetup.R;

public class MainActivity extends AppCompatActivity {

    private TextView textViewMainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMainTitle = (TextView) findViewById(R.id.textViewMainTitle);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        textViewMainTitle.setText("Welcome, " + sharedPref.getString("name", "") + "!");

    }

    public void handleOnClickLogout(View view) {

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void handleOnClickCreateEvent(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    public void handleOnClickHostedEvents(View view) {
        Intent intent = new Intent(this, EventListActivity.class);
        startActivity(intent);
    }
}
