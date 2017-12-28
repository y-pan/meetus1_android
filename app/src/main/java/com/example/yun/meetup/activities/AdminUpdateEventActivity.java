package com.example.yun.meetup.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.yun.meetup.R;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.requests.UpdateEventRequest;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdminUpdateEventActivity extends AppCompatActivity {

    private EditText editTextAdminUpdateTitle;
    private EditText editTextAdminUpdateDate;
    private EditText editTextAdminUpdateAddress;
    private EditText editTextAdminUpdateDescription;
    private EditText editTextAdminUpdateSubtitle;

    private TextView textViewErrorAdminTitle;
    private TextView textViewErrorAdminDate;
    private TextView textViewErrorAdminAddress;
    private TextView textViewErrorAdminUpdate;

    private ConstraintLayout constraintLayoutAdminUpdateLoading;

    private String event_id;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_event);

        editTextAdminUpdateTitle = (EditText) findViewById(R.id.edt_admin_update_event_title);
        editTextAdminUpdateDate = (EditText) findViewById(R.id.edt_admin_update_event_date);
        editTextAdminUpdateAddress = (EditText) findViewById(R.id.edt_admin_update_event_address);
        editTextAdminUpdateDescription = (EditText) findViewById(R.id.edt_admin_update_event_description);
        editTextAdminUpdateSubtitle = (EditText) findViewById(R.id.edt_admin_update_event_subtitle);

        textViewErrorAdminTitle = (TextView) findViewById(R.id.txt_admin_error_update_event_title);
        textViewErrorAdminDate = (TextView) findViewById(R.id.txt_admin_error_update_event_date);
        textViewErrorAdminAddress = (TextView) findViewById(R.id.txt_admin_error_update_event_address);
        textViewErrorAdminUpdate = (TextView) findViewById(R.id.txt_admin_error_update_event);

        editTextAdminUpdateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        constraintLayoutAdminUpdateLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutAdminUpdateLoading);

        event_id = getIntent().getExtras().getString("event_id");

        new AdminEventDetailsTask().execute(event_id);


    }

    private void showDateTimePicker() {
         /*
        * Making the DateTimePicker
        * */
        new DatePickerDialog(AdminUpdateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(AdminUpdateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.CANADA);

                        editTextAdminUpdateDate.setText(sdf.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
    }

    public void hideViews(){
        constraintLayoutAdminUpdateLoading.setVisibility(View.GONE);
    }

    public void handleOnClickAdminUpdate(View view) {

        boolean error = false;

        if (editTextAdminUpdateTitle.getText().toString().isEmpty()) {
            textViewErrorAdminTitle.setText("Please provide a valid Title for your event!");
            textViewErrorAdminTitle.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextAdminUpdateDate.getText().toString().isEmpty()) {
            textViewErrorAdminDate.setText("Please provide a Date for your event!");
            textViewErrorAdminDate.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextAdminUpdateAddress.getText().toString().isEmpty()) {
            textViewErrorAdminAddress.setText("Please provide an Address for your event!");
            textViewErrorAdminAddress.setVisibility(View.VISIBLE);
            error = true;
        }

        if (!error) {
            constraintLayoutAdminUpdateLoading.setVisibility(View.VISIBLE);
            UpdateEventRequest updateEventRequest = new UpdateEventRequest();
            updateEventRequest.set_id(event_id);
            updateEventRequest.setTitle(editTextAdminUpdateTitle.getText().toString());
            updateEventRequest.setSubtitle(editTextAdminUpdateSubtitle.getText().toString());
            updateEventRequest.setDescription(editTextAdminUpdateDescription.getText().toString());
            updateEventRequest.setDate(editTextAdminUpdateDate.getText().toString());
            updateEventRequest.setAddress(editTextAdminUpdateAddress.getText().toString());
            new AdminUpdateEventActivity.AdminValidateAddressTask().execute(updateEventRequest);
        }
    }

    private class AdminEventDetailsTask extends AsyncTask<String, Void, APIResult>{

        @Override
        protected APIResult doInBackground(String... strings) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.getEventById(event_id);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if(!apiResult.isResultSuccess()){
                Toast.makeText(AdminUpdateEventActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG).show();
            }
            else{
                Event event = (Event) apiResult.getResultEntity();

                editTextAdminUpdateTitle.setText(event.getTitle());
                editTextAdminUpdateAddress.setText(event.getAddress());
                editTextAdminUpdateDate.setText(event.getDate());
                editTextAdminUpdateDescription.setText(event.getDescription());
                editTextAdminUpdateSubtitle.setText(event.getSubtitle());
            }
        }
    }

    private class AdminValidateAddressTask extends AsyncTask<UpdateEventRequest, Void, APIResult> {

        @Override
        protected APIResult doInBackground(UpdateEventRequest... requests) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.validateEventAddress(requests[0]);
        }

        @Override
        protected void onPostExecute(APIResult result) {
            if (result.isResultSuccess()) {
                new AdminUpdateEventActivity.AdminUpdateEventTask().execute((UpdateEventRequest) result.getResultEntity());
            }
            else {
                hideViews();
                Toast.makeText(AdminUpdateEventActivity.this, result.getResultMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AdminUpdateEventTask extends AsyncTask<UpdateEventRequest, Void, APIResult> {

        @Override
        protected APIResult doInBackground(UpdateEventRequest... requests) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.updateEvent(requests[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if (apiResult.isResultSuccess()) {
                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_OK, returnIntent);
                AdminUpdateEventActivity.this.finish();
            }
            else {
                Toast.makeText(AdminUpdateEventActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
