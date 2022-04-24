package com.app.trackingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private DialogClass dc;

    // variables for requesting, receiving and working with location updates
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // got permission

                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // got only approximate location
                            dc =  new DialogClass("Location Permission", "This message appeared because you only granted" +
                                    " approximate Location access. But in order to properly use this Application you need to provice " +
                                    "fine Location access.\n");
                            dc.show(getSupportFragmentManager(), "Test");
                            terminateApp();
                        } else {

                            terminateApp();
                        }
                    }
            );

    private void terminateApp() {
        System.exit(0);
    }


    // user interface variables
    private TextView textLeft, textCenter, textRight;
    private Button btnStart, btnStop;
    private com.app.trackingapp.Timer T = new com.app.trackingapp.Timer(this, textLeft);

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
                .setInterval(10 * 1000);

        // method invoked every minute then save time and coordinates in hashmap
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    System.out.println("Debug: location null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null && backtracking) {
                        // save time and coordinates
                        backtrack.put(dtf.format(Instant.now()), location);

                        // build new camera position and animate the new camera position because we are moving
                        CameraPosition mylocation =
                                new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(15.5f)
                                        .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mylocation), 2000, null);
                        System.out.println("Debug: got location");

                        // set speed
                        textLeft.setText(getGeschwindigkeit(location) + " km/h");
                    }
                }
            }
        };

        // initialie textviews and buttons
        textLeft = findViewById(R.id.textViewLeft);
        textCenter = findViewById(R.id.textViewCenter);
        textRight = findViewById(R.id.textViewRight);
        btnStart = findViewById(R.id.buttonLeft);
        btnStop = findViewById(R.id.buttonRight);

        // set some text
        textLeft.setText("- km/h");
        textCenter.setText("Time -");
        textRight.setText("Steps -");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtracking = true;
                backtrack = new HashMap<>();
                T.initTimer();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backtracking = false;
                T.stopTimer();
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
        return (double) location.getSpeed();
    }

}
