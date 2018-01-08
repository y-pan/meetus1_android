package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yun.meetup.R;
import com.example.yun.meetup.adapters.EventListViewAdapter;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;

import java.util.ArrayList;
import java.util.List;

public class MySubscribedEventsActivity extends AppCompatActivity {

    private double latitude;
    private double longitude;

    private ConstraintLayout constraintLayoutSubscribedEventsLoading;
    private ListView listViewEvents;

    private List<Event> listEvents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscribed_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        constraintLayoutSubscribedEventsLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutSubscribedEventstLoading);

        listViewEvents = (ListView) findViewById(R.id.listview_my_subscribed_events);

        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MySubscribedEventsActivity.this, EventDetailsActivity.class);
                String eventId = listEvents.get(i).get_id();
                intent.putExtra("eventId", listEvents.get(i).get_id());
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_my_subscribed_events_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MySubscribedEventsActivity.this, SearchEventsActivity.class);
                startActivity(intent);

            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        String userID = sharedPref.getString("id", "");

        if (!userID.isEmpty()){
            new MySubscribedEventsTask().execute(sharedPref.getString("id", ""));
        }
    }

    private class MySubscribedEventsTask extends AsyncTask<String, Void, APIResult>{

        @Override
        protected APIResult doInBackground(String... strings) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.getSubscribedEvents(strings[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {
            
            hideViews();

            if (!apiResult.isResultSuccess()){
                Toast.makeText(MySubscribedEventsActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG).show();
            }
            else{
                listEvents = (List<Event>) apiResult.getResultEntity();

                EventListViewAdapter adapter = new EventListViewAdapter(listEvents, getApplicationContext());

                listViewEvents.setAdapter(adapter);

            }
            
        }
    }

    private void hideViews() {
        constraintLayoutSubscribedEventsLoading.setVisibility(View.GONE);
    }

}
