package com.example.yun.meetup.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.yun.meetup.R;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.requests.SearchEventsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class SearchEventsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ConstraintLayout constraintLayoutMapLoading;

    private LatLng currentLocation;

    private double latitude;
    private double longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        constraintLayoutMapLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutMapLoading);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        latitude = Double.parseDouble(sharedPref.getString("latitude", "0"));
        longitude = Double.parseDouble(sharedPref.getString("longitude", "0"));

        currentLocation = new LatLng(latitude, longitude);
//        currentLocation = new LatLng(43.684201, -79.318706);



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        constraintLayoutMapLoading.setVisibility(View.VISIBLE);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(SearchEventsActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventId", marker.getSnippet());
                startActivity(intent);
                return true;
            }
        });

        SearchEventsRequest searchEventsRequest = new SearchEventsRequest();
        searchEventsRequest.setLatitude(currentLocation.latitude);
        searchEventsRequest.setLongitude(currentLocation.longitude);

        new SearchEventsTask().execute(searchEventsRequest);


    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (currentLocation != null){
//            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map);
//            mapFragment.getMapAsync(this);
//        }
//    }

    public void hideViews(){
        constraintLayoutMapLoading.setVisibility(View.GONE);
    }

    private class SearchEventsTask extends AsyncTask<SearchEventsRequest, Void, APIResult>{

        @Override
        protected APIResult doInBackground(SearchEventsRequest... searchEventsRequests) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.searchEvents(searchEventsRequests[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            hideViews();

            if (apiResult.getResultEntity() == null){
                Toast.makeText(SearchEventsActivity.this, apiResult.getResultMessage(), Toast.LENGTH_LONG);
            }
            else{
                List<Event> events = (List<Event>) apiResult.getResultEntity();

                for(Event event : events){
                    LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(location).title(event.getTitle()).snippet(event.get_id()));
                }
            }
        }
    }
}
