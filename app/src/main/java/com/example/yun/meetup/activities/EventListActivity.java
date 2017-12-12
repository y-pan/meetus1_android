package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.yun.meetup.R;
import com.example.yun.meetup.adapters.EventListViewAdapter;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.requests.EventListRequest;

import java.util.Arrays;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private ConstraintLayout constraintLayoutEventListLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        listViewEvents = (ListView) findViewById(R.id.listViewEvents);
        constraintLayoutEventListLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutEventListLoading);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Event List");

        /*Button that calls the Create an Event Activity*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventListActivity.this, CreateEventActivity.class);
                startActivity(intent);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

////          DUMMY DATA
//
//        Event[] eventList = new Event[]{
//                new Event(),
//                new Event(),
//                new Event(),
//                new Event(),
//                new Event()
//        };
//
//        UserInfo userInfo;
//        userInfo = new UserInfo();
//        userInfo.setName("Nice Professor");
//
//        eventList[0].set_id("111");
//        eventList[0].setTitle("Nice Project Class");
//        eventList[0].setUserInfo(userInfo);
//        eventList[0].setDate("01/01/2013");
//        eventList[0].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
//        eventList[1].set_id("222");
//        eventList[1].setTitle("Nice Project Class");
//        eventList[1].setUserInfo(userInfo);
//        eventList[1].setDate("01/01/2013");
//        eventList[1].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
//        eventList[2].set_id("333");
//        eventList[2].setTitle("Nice Project Class");
//        eventList[2].setUserInfo(userInfo);
//        eventList[2].setDate("01/01/2013");
//        eventList[2].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
//        eventList[3].set_id("444");
//        eventList[3].setTitle("Nice Project Class");
//        eventList[3].setUserInfo(userInfo);
//        eventList[3].setDate("01/01/2013");
//        eventList[3].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
//        eventList[4].set_id("555");
//        eventList[4].setTitle("Nice Project Class");
//        eventList[4].setUserInfo(userInfo);
//        eventList[4].setDate("01/01/2013");
//        eventList[4].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
//
//        // DUMMY DATA END

        EventListRequest eventListRequest = new EventListRequest();

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        eventListRequest.setHost_id(sharedPreferences.getString("id", null));

        new EventListTask().execute(eventListRequest);


    }

    private class EventListTask extends AsyncTask<EventListRequest, Void, APIResult>{

        @Override
        protected APIResult doInBackground(EventListRequest... eventListRequests) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.getHostedEvents(eventListRequests[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            if (apiResult.isResultSuccess()){

                List<Event> listEvents = (List<Event>) apiResult.getResultEntity();

                EventListViewAdapter adapter = new EventListViewAdapter(listEvents, getApplicationContext());

                listViewEvents.setAdapter(adapter);

                constraintLayoutEventListLoading.setVisibility(View.GONE);

            }
        }
    }

}
