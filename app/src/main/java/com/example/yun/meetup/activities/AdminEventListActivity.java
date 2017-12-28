package com.example.yun.meetup.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.List;

public class AdminEventListActivity extends AppCompatActivity {

    private ListView listViewAdminEvents;
    private ConstraintLayout constraintLayoutAdminEventListLoading;
    private List<Event> listEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("All events");

        listViewAdminEvents = (ListView) findViewById(R.id.listViewAdminEvents);
        constraintLayoutAdminEventListLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutAdminEventListLoading);

        listViewAdminEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AdminEventListActivity.this, AdminEventDetailsActivity.class);
                intent.putExtra("event_id", listEvents.get(position).get_id());
                startActivity(intent);
            }
        });

        new AdminEventListTask().execute();

    }

    @Override
    protected void onResume() {
        constraintLayoutAdminEventListLoading.setVisibility(View.VISIBLE);
        new AdminEventListTask().execute();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        constraintLayoutAdminEventListLoading.setVisibility(View.VISIBLE);
        new AdminEventListTask().execute();
        super.onRestart();
    }

    public void hideViews(){
        constraintLayoutAdminEventListLoading.setVisibility(View.GONE);
    }

    private class AdminEventListTask extends AsyncTask<Void, Void, APIResult>{

        @Override
        protected APIResult doInBackground(Void... voids) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.getAllEvents();
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if (!apiResult.isResultSuccess()){
                Toast.makeText(AdminEventListActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG).show();
            }
            else{
                listEvents = (List<Event>) apiResult.getResultEntity();

                EventListViewAdapter adapter = new EventListViewAdapter(listEvents, AdminEventListActivity.this);
                listViewAdminEvents.setAdapter(adapter);
            }
        }
    }
}
