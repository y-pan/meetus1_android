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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayoutLoading;

    EditText edtTitle;
    EditText edtSubTitle;
    EditText edtDate;
    EditText edtAddress;
    EditText edtDescription;

    TextView txtErrorTitle;
    TextView txtErrorDate;
    TextView txtErrorAddress;

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        constraintLayoutLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoading);

        edtTitle = (EditText) findViewById(R.id.edit_event_title);
        txtErrorTitle = (TextView) findViewById(R.id.txt_error_title);

        edtSubTitle = (EditText) findViewById(R.id.edit_event_subtitle);

        edtDate = (EditText) findViewById(R.id.edt_event_date);
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
        txtErrorDate = (TextView) findViewById(R.id.txt_error_date);

        edtAddress = (EditText) findViewById(R.id.edt_address);
        txtErrorAddress = (TextView) findViewById(R.id.txt_error_address);

        edtDescription = (EditText) findViewById(R.id.edt_description);
    }

    public void showDateTimePicker() {
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

                        edtDate.setText(sdf.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
    }

    public void handleOnClickCreateEvent(View view) {
        hideViews();

        boolean error = false;

        if (edtTitle.getText().toString().isEmpty()) {
            txtErrorTitle.setText("Please provide a valid Title for your event!");
            txtErrorTitle.setVisibility(View.VISIBLE);
            error = true;
        }
        if (edtDate.getText().toString().isEmpty()) {
            txtErrorDate.setText("Please provide a Date for your event!");
            txtErrorDate.setVisibility(View.VISIBLE);
            error = true;
        }
        if (edtAddress.getText().toString().isEmpty()) {
            txtErrorAddress.setText("Please provide an Address for your event!");
            txtErrorAddress.setVisibility(View.VISIBLE);
            error = true;
        }

        if (!error) {
            Event event = new Event();

            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            event.setHostID(sharedPreferences.getString("id", null));
            event.setTitle(edtTitle.getText().toString());
            event.setSubtitle(edtSubTitle.getText().toString());
            event.setDate(edtDate.getText().toString());
            event.setAddress(edtAddress.getText().toString());
            event.setDescription(edtDescription.getText().toString());

            constraintLayoutLoading.setVisibility(View.VISIBLE);

            new ValidateAddressTask().execute(event);
        }

    }

    public void hideViews() {
        txtErrorAddress.setVisibility(View.GONE);
        txtErrorDate.setVisibility(View.GONE);
        txtErrorTitle.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.GONE);
    }

    private class ValidateAddressTask extends AsyncTask<Event, Void, APIResult> {

        @Override
        protected APIResult doInBackground(Event... events) {

            NetworkManager networkManager = new NetworkManager();
            return networkManager.validateEventAddress(events[0]);
        }

        @Override
        protected void onPostExecute(APIResult result) {
            if (result.isResultSuccess()) {
                new CreateEventTask().execute((Event)result.getResultEntity());
            } else {
                hideViews();
                txtErrorAddress.setText(result != null ? result.getResultMessage() : "Please contact admin staff!");
                txtErrorAddress.setVisibility(View.VISIBLE);
            }
        }
    }

    private class CreateEventTask extends AsyncTask<Event, Void, APIResult> {

        @Override
        protected APIResult doInBackground(Event... events) {

            NetworkManager networkManager = new NetworkManager();
            return networkManager.createEvent(events[0]);
        }

        @Override
        protected void onPostExecute(APIResult result) {

            hideViews();

            if (result.isResultSuccess()) {
                Toast.makeText(CreateEventActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                CreateEventActivity.this.finish();
            } else {
                txtErrorTitle.setText(result != null ? result.getResultMessage() : "Please contact admin staff!");
                txtErrorTitle.setVisibility(View.VISIBLE);
            }
        }
    }
}
