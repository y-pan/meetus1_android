package com.example.yun.meetup.activities;

import android.content.Intent;
import android.net.Network;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yun.meetup.R;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.UserInfo;

import org.w3c.dom.Text;

public class AdminEventDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView textViewAdminEventAddress;
    private TextView textViewAdminEventDate;
    private TextView textViewAdminEventHost;
    private TextView textViewAdminEventDescription;
    private TextView textViewAdminEventSubtitle;
    private TextView textViewAdminMemberName;

    private AppCompatImageButton buttonAdminContactMember;

    private ConstraintLayout constraintLayoutAdminEventDetailLoading;

    private ListView listViewMembers;

    private String event_id;

    private String host_email;

    private LinearLayout linearLayoutAdminMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_details);

        toolbar = findViewById(R.id.admin_toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_admin_toolbar);

        constraintLayoutAdminEventDetailLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutAdminEventDetailsLoading);
        textViewAdminEventAddress = (TextView) findViewById(R.id.txt_admin_detail_event_address);
        textViewAdminEventDate = (TextView) findViewById(R.id.txt_admin_detail_event_date);
        textViewAdminEventHost = (TextView) findViewById(R.id.txt_admin_detail_event_host);
        textViewAdminEventDescription = (TextView) findViewById(R.id.txt_admin_description);
        textViewAdminEventSubtitle = (TextView) findViewById(R.id.txt_admin_subtitle);

        linearLayoutAdminMembers = (LinearLayout) findViewById(R.id.linearLayoutAdminMembers);



        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        event_id = getIntent().getExtras().getString("event_id");

        new AdminEventDetailsTask().execute(event_id);


    }

    @Override
    protected void onResume() {
        constraintLayoutAdminEventDetailLoading.setVisibility(View.VISIBLE);

        if (linearLayoutAdminMembers.getChildCount() > 0){
            linearLayoutAdminMembers.removeAllViews();
            new AdminEventDetailsTask().execute(event_id);
        }


        super.onResume();
    }

    public void handleOnClickContactHost(View view) {
        /* Create the Intent */
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                /* Fill it with Data */
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{host_email});

                /* Send it off to the Activity-Chooser */
        this.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void handleOnClickAdminDelete(View view) {
        constraintLayoutAdminEventDetailLoading.setVisibility(View.VISIBLE);
        new AdminDeleteEventTask().execute(event_id);
    }

    public void handleOnClickAdminEdit(View view) {
        Intent intent = new Intent(this, AdminUpdateEventActivity.class);
        intent.putExtra("event_id", event_id);
        startActivity(intent);
    }

    public void hideViews(){
        constraintLayoutAdminEventDetailLoading.setVisibility(View.GONE);
    }

    private class AdminEventDetailsTask extends AsyncTask<String, Void, APIResult>{

        @Override
        protected APIResult doInBackground(String... strings) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.getEventById(strings[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if (!apiResult.isResultSuccess()){
                Toast.makeText(AdminEventDetailsActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG).show();
            }
            else{
                Event event = (Event) apiResult.getResultEntity();

                host_email = event.getUserInfo().getEmail();

                collapsingToolbarLayout.setTitle(event.getTitle());
                textViewAdminEventAddress.setText(event.getAddress());
                textViewAdminEventDate.setText(event.getDate());
                textViewAdminEventHost.setText(event.getUserInfo().getName());
                textViewAdminEventDescription.setText(event.getDescription());
                textViewAdminEventSubtitle.setText(event.getSubtitle());

                for (UserInfo member : event.getMembers()){

                    final UserInfo selectedMember = member;

                    View memberView = getLayoutInflater().inflate(R.layout.lv_item_admin_member, null);
                    textViewAdminMemberName = (TextView) memberView.findViewById(R.id.textViewAdminMemberName);
                    buttonAdminContactMember = (AppCompatImageButton) memberView.findViewById(R.id.buttonAdminContactMember);

                    buttonAdminContactMember.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String memberEmail = selectedMember.getEmail();

                            /* Create the Intent */
                            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                            /* Fill it with Data */
                            emailIntent.setType("plain/text");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{memberEmail});

                            /* Send it off to the Activity-Chooser */
                            AdminEventDetailsActivity.this.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                    });

                    textViewAdminMemberName.setText(selectedMember.getName());

                    linearLayoutAdminMembers.addView(memberView);
                }

            }
        }
    }

    private class AdminDeleteEventTask extends AsyncTask<String, Void, APIResult>{

        @Override
        protected APIResult doInBackground(String... strings) {
           NetworkManager networkManager = new NetworkManager();
           return networkManager.deleteEvent(strings[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if (!apiResult.isResultSuccess()){
                Toast.makeText(AdminEventDetailsActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(AdminEventDetailsActivity.this, "Event deleted succesfully!", Toast.LENGTH_LONG).show();
                AdminEventDetailsActivity.this.finish();
            }

        }
    }
}
