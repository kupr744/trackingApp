package com.app.trackingapp;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.app.trackingapp.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // auto-generated variables
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    // variables for requesting, receiving and working with location updates
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    // user interface variables
    private TextView textLeft, textCenter, textRight, textGeschwindigkeit;
    private Button btnStart, btnStop;
    private int cnt = 0;

    // variables for saving coordinates and time
    private boolean backtracking = false;
    private HashMap<String, Location> backtrack = new HashMap<>();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm")
                                            .withLocale(Locale.GERMANY)
                                            .withZone(ZoneId.systemDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // required for requesting and receiving location updates
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // request system to receive location updates every minute
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(60 * 1000);

        // method invoked every minute then save time and coordinates in hashmap
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    System.out.println("Debug: location null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // save time and coordinates
                        backtrack.put(dtf.format(Instant.now()), location);

                        // build new camera position and animate the new camera position because we are moving
                        CameraPosition mylocation =
                                new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(15.5f)
                                        .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mylocation), 2000, null);
                        System.out.println("Debug: got location");
                    }
                }
            }
        };

        // initialie textviews and buttons
        textLeft = findViewById(R.id.textViewLeft);
        textCenter = findViewById(R.id.textViewCenter);
        textRight = findViewById(R.id.textViewRight);
        textGeschwindigkeit = findViewById(R.id.tv_geschwindigkeit);
        btnStart = findViewById(R.id.buttonLeft);
        btnStop = findViewById(R.id.buttonRight);

        // set some text
        textLeft.setText("- km/h");
        textCenter.setText("Zeit " + dtf.format(Instant.now()));
        textRight.setText("Schritte " + cnt);

        // listen to button click
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtracking = true;
                backtrack = new HashMap<>();
                cnt++;
                textRight.setText("Schritte " + cnt);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtracking = false;
            }
        });
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, null);
    }

    public double getGeschwindigkeit(Location location){
        double d = Double.valueOf(location.getSpeed());
        return d;
    }
}
