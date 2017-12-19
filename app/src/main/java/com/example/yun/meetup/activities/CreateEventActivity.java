package com.example.yun.meetup.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.requests.CreateEventRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayoutLoading;

    private EditText editTextCreateEventTitle;
    private EditText editTextCreateEventSubtitle;
    private EditText editTextCreateEventDate;
    private EditText editTextCreateEventAddress;
    private EditText editTextCreateEventDescription;

    private TextView textViewErrorCreateEventTitle;
    private TextView textViewErrorCreateEventDate;
    private TextView textViewErrorCreateEventAddress;
    private TextView textViewErrorCreateEvent;


    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        constraintLayoutLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoading);

        editTextCreateEventTitle = (EditText) findViewById(R.id.editTextCreateEventTitle);
        textViewErrorCreateEventTitle = (TextView) findViewById(R.id.textViewErrorCreateEventTitle);

        editTextCreateEventSubtitle = (EditText) findViewById(R.id.editTextCreateEventSubtitle);

        editTextCreateEventDate = (EditText) findViewById(R.id.editTextCreateEventDate);
        editTextCreateEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
        textViewErrorCreateEventDate = (TextView) findViewById(R.id.textViewErrorCreateEventDate);

        editTextCreateEventAddress = (EditText) findViewById(R.id.editTextCreateEventAddress);
        textViewErrorCreateEventAddress = (TextView) findViewById(R.id.textViewErrorCreateEventAddress);

        editTextCreateEventDescription = (EditText) findViewById(R.id.editTextCreateEventDescription);

        textViewErrorCreateEvent = (TextView) findViewById(R.id.textViewErrorCreateEvent);
    }

    private void showDateTimePicker() {
        /*
        * Making the DateTimePicker
        * */
        new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.CANADA);

                        editTextCreateEventDate.setText(sdf.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
    }

    public void handleOnClickCreateEvent(View view) {
        hideViews();

        boolean error = false;

        if (editTextCreateEventTitle.getText().toString().isEmpty()) {
            textViewErrorCreateEventTitle.setText("Please provide a valid Title for your event!");
            textViewErrorCreateEventTitle.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextCreateEventDate.getText().toString().isEmpty()) {
            textViewErrorCreateEventDate.setText("Please provide a Date for your event!");
            textViewErrorCreateEventDate.setVisibility(View.VISIBLE);
            error = true;
        }
        if (editTextCreateEventAddress.getText().toString().isEmpty()) {
            textViewErrorCreateEventAddress.setText("Please provide an Address for your event!");
            textViewErrorCreateEventAddress.setVisibility(View.VISIBLE);
            error = true;
        }

        if (!error) {
            CreateEventRequest createEventRequest = new CreateEventRequest();

            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            createEventRequest.setHost_id(sharedPreferences.getString("id", null));
            createEventRequest.setTitle(editTextCreateEventTitle.getText().toString());
            createEventRequest.setSubtitle(editTextCreateEventSubtitle.getText().toString());
            createEventRequest.setDate(editTextCreateEventDate.getText().toString());
            createEventRequest.setAddress(editTextCreateEventAddress.getText().toString());
            createEventRequest.setDescription(editTextCreateEventDescription.getText().toString());

            constraintLayoutLoading.setVisibility(View.VISIBLE);

            new ValidateAddressTask().execute(createEventRequest);
        }

    }

    public void hideViews() {
        textViewErrorCreateEventAddress.setVisibility(View.GONE);
        textViewErrorCreateEventDate.setVisibility(View.GONE);
        textViewErrorCreateEventTitle.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.GONE);
        textViewErrorCreateEvent.setVisibility(View.GONE);
    }

    private class ValidateAddressTask extends AsyncTask<CreateEventRequest, Void, APIResult> {

        @Override
        protected APIResult doInBackground(CreateEventRequest... createEventRequests) {

            NetworkManager networkManager = new NetworkManager();
            return networkManager.validateEventAddress(createEventRequests[0]);
        }

        @Override
        protected void onPostExecute(APIResult result) {
            if (result.isResultSuccess()) {
                new CreateEventTask().execute((CreateEventRequest)result.getResultEntity());
            }
            else {
                hideViews();
                textViewErrorCreateEventAddress.setText(result != null ? result.getResultMessage() : "Please contact admin staff!");
                textViewErrorCreateEventAddress.setVisibility(View.VISIBLE);
            }
        }
    }

    private class CreateEventTask extends AsyncTask<CreateEventRequest, Void, APIResult> {

        @Override
        protected APIResult doInBackground(CreateEventRequest... createEventRequests) {

            NetworkManager networkManager = new NetworkManager();
            return networkManager.createEvent(createEventRequests[0]);
        }

        @Override
        protected void onPostExecute(APIResult result) {

            hideViews();

            if (result.isResultSuccess()) {
                Toast.makeText(CreateEventActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                CreateEventActivity.this.finish();
            } else {
                textViewErrorCreateEvent.setText(result != null ? result.getResultMessage() : "Please contact admin staff!");
                textViewErrorCreateEvent.setVisibility(View.VISIBLE);
            }
        }
    }
}
