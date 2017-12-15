package com.example.yun.meetup.activities;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.yun.meetup.R;

import static java.security.AccessController.getContext;


public class EventDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fabParticipate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        fabParticipate = findViewById(R.id.fab_event_detail_participate);

//        This code should run after get the EVENT NAME or from Intent or Database
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle("EVENT NAME");

//        END

        //TODO:
//        Participate of an Event button
//        If the User will alraedy checked to participate of this event, the icon color shouldo be WHITE.
//        Otherwise it is gonna be GREY

        /*        
        if(isParticipating){
            fabParticipate.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_person_add_white_24dp));
        }else{
            fabParticipate.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_person_add_grey_500_24dp));
        }*/
    }
}
