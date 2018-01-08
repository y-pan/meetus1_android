package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yun.meetup.R;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.requests.ParticipateToEventRequest;

import java.util.ArrayList;
import java.util.List;


public class EventDetailsActivity extends AppCompatActivity {

    static final int UPDATE_EVENT_REQUEST = 1;  // The request code
    ArrayAdapter<String> listviewAdapter;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fabParticipate;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabDelete;
    private ConstraintLayout constraintLayoutDetailsLoading;
    private TextView textViewDetailAddress;
    private TextView textViewDetailDate;
    private TextView textViewDetailHostName;
    private TextView textViewDetailSubtitle;
    private TextView textViewDetailDescription;
    private ListView listViewSubscribedUsers;
    private String userId;
    private String eventId;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        constraintLayoutDetailsLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutDetailsLoading);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        fabParticipate = findViewById(R.id.fab_event_detail_participate);
        fabEdit = (FloatingActionButton) findViewById(R.id.fab_edit_event_details);
        fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete_event_details);

        textViewDetailAddress = (TextView) findViewById(R.id.txt_detail_event_address);
        textViewDetailDate = (TextView) findViewById(R.id.txt_detail_event_date);
        textViewDetailHostName = (TextView) findViewById(R.id.txt_detail_event_host);
        textViewDetailSubtitle = (TextView) findViewById(R.id.txt_subtitle);
        textViewDetailDescription = (TextView) findViewById(R.id.txt_description);
        listViewSubscribedUsers = (ListView) findViewById(R.id.lv_detail_subscribed_users);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("id", null);


        eventId = getIntent().getExtras().getString("eventId");

//        This code should run after get the EVENT NAME or from Intent or Database
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        new GetEventTask().execute(eventId);
    }

    @Override
    protected void onResume() {
        constraintLayoutDetailsLoading.setVisibility(View.VISIBLE);
        new GetEventTask().execute(eventId);
        super.onResume();
    }

    public void hideViews() {
        constraintLayoutDetailsLoading.setVisibility(View.GONE);
    }

    public void handleOnClickParticipate(View view) {

        constraintLayoutDetailsLoading.setVisibility(View.VISIBLE);

        ParticipateToEventRequest participateToEventRequest = new ParticipateToEventRequest();
        participateToEventRequest.setEvent_id(eventId);
        participateToEventRequest.setUser_id(userId);
        new ParticipateToEventTask().execute(participateToEventRequest);
    }

    public void handleOnClickUpdate(View view) {
        Intent intent = new Intent(this, EventUpdateActivity.class);
        intent.putExtra("event", event);
        startActivityForResult(intent, UPDATE_EVENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_EVENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                constraintLayoutDetailsLoading.setVisibility(View.VISIBLE);
                new GetEventTask().execute(eventId);
            }
        }
    }

    public void handleOnClickDelete(View view) {
        new DeleteEventTask().execute(eventId);
    }

    private class GetEventTask extends AsyncTask<String, Void, APIResult> {

        @Override
        protected APIResult doInBackground(String... strings) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.getEventById(strings[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if (apiResult.getResultEntity() == null) {
                Toast.makeText(EventDetailsActivity.this, "Error retrieving details of event: please try again", Toast.LENGTH_LONG).show();
            } else {
                event = (Event) apiResult.getResultEntity();

                collapsingToolbarLayout.setTitle(event.getTitle().toUpperCase());

                textViewDetailAddress.setText(event.getAddress());
                textViewDetailDate.setText(event.getDate());
                textViewDetailHostName.setText(event.getUserInfo().getName());
                textViewDetailSubtitle.setText(event.getSubtitle());
                textViewDetailDescription.setText(event.getDescription());

                if (userId.equals(event.getHost_id())) {
                    fabParticipate.setVisibility(View.GONE);
                } else {
                    fabEdit.setVisibility(View.GONE);
                    fabDelete.setVisibility(View.GONE);
                }

                List<String> listSubscribedUsers = new ArrayList<>();

                for (UserInfo member : event.getMembers()) {

                    listSubscribedUsers.add(member.getName());

                    if (userId.equals(member.get_id())) {
                        fabParticipate.setVisibility(View.GONE);
                    }
                }

                listviewAdapter = new ArrayAdapter<String>(EventDetailsActivity.this, android.R.layout.simple_list_item_1, listSubscribedUsers);
                listViewSubscribedUsers.setAdapter(listviewAdapter);
            }
        }
    }

    private class ParticipateToEventTask extends AsyncTask<ParticipateToEventRequest, Void, APIResult> {

        @Override
        protected APIResult doInBackground(ParticipateToEventRequest... participateToEventRequests) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.participateToEvent(participateToEventRequests[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            if (!apiResult.isResultSuccess()) {
                hideViews();
                Toast.makeText(EventDetailsActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG).show();
            } else {
                new GetEventTask().execute(eventId);
            }
        }
    }

    private class DeleteEventTask extends AsyncTask<String, Void, APIResult>{

        @Override
        protected APIResult doInBackground(String... strings) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.deleteEvent(strings[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {
            if (!apiResult.isResultSuccess()){
                Toast.makeText(EventDetailsActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG).show();
            }
            else{
                Event event = (Event) apiResult.getResultEntity();

                Toast.makeText(EventDetailsActivity.this, "Event deleted successfully!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
