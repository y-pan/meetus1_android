package com.example.yun.meetup.activities;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yun.meetup.R;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;

import static java.security.AccessController.getContext;


public class EventDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fabParticipate;

    private ConstraintLayout constraintLayoutDetailsLoading;
    private TextView textViewDetailAddress;
    private TextView textViewDetailDate;
    private TextView textViewDetailHostName;
    private TextView textViewDetailSubtitle;
    private TextView textViewDetailDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        constraintLayoutDetailsLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutDetailsLoading);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        fabParticipate = findViewById(R.id.fab_event_detail_participate);

        textViewDetailAddress = (TextView) findViewById(R.id.txt_detail_event_address);
        textViewDetailDate = (TextView) findViewById(R.id.txt_detail_event_date);
        textViewDetailHostName = (TextView) findViewById(R.id.txt_detail_event_host);
        textViewDetailSubtitle = (TextView) findViewById(R.id.txt_subtitle);
        textViewDetailDescription = (TextView) findViewById(R.id.txt_description);



        String eventId = getIntent().getExtras().getString("eventId");





//        This code should run after get the EVENT NAME or from Intent or Database
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        new GetEventTask().execute(eventId);

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

    public void hideViews(){
        constraintLayoutDetailsLoading.setVisibility(View.GONE);
    }

    private class GetEventTask extends AsyncTask<String, Void, APIResult>{

        @Override
        protected APIResult doInBackground(String... strings) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.getEventById(strings[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if (apiResult.getResultEntity() == null){
                Toast.makeText(EventDetailsActivity.this, "Error retrieving details of event: please try again", Toast.LENGTH_LONG).show();
            }
            else{
                Event event = (Event) apiResult.getResultEntity();

                collapsingToolbarLayout.setTitle(event.getTitle().toUpperCase());

                textViewDetailAddress.setText(event.getAddress());
                textViewDetailDate.setText(event.getDate());
                textViewDetailHostName.setText(event.getUserInfo().getName());
                textViewDetailSubtitle.setText(event.getSubtitle());
                textViewDetailDescription.setText(event.getDescription());

            }
        }
    }
}
