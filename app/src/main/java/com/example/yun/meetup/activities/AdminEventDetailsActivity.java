package com.example.yun.meetup.activities;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.yun.meetup.R;

public class AdminEventDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_details);

        toolbar = findViewById(R.id.admin_toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_admin_toolbar);

        //        This code should run after get the EVENT NAME or from Intent or Database
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle("Event Name");

    }
}
