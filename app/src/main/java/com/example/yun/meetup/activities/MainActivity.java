package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.yun.meetup.R;

public class MainActivity extends AppCompatActivity {

    private TextView textViewMainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMainTitle = (TextView) findViewById(R.id.textViewMainTitle);

        SharedPreferences sharedPref = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        textViewMainTitle.setText("Welcome, " + sharedPref.getString("name", "") + "!");

    }
}
