package com.app.trackingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // auto-generated variables
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DialogClass dc;

    // variables for step counter sensor
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount;

    // variables for requesting, receiving and working with location updates
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean allowedTracking = true;
    ActivityResultLauncher<String[]> locationPermissionRequest;

    private void terminateApp() {
        System.exit(0);
    }

    // user interface variables
    private TextView textLeft, textCenter, textRight;
    private Button btnStart, btnStop;
    private com.app.trackingapp.Timer T;
    private StatusDialog sd;

    // variables for saving coordinates and time
    private boolean isRunning = false;
    private List<LatLng> backtrack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // do not turn off the screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /// TODO move code so that in mapready
        // check if app has fine location access
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // got permission
                        allowedTracking = true;
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // got only approximate location
                        allowedTracking = false;
                        dc =  new DialogClass("Location Permission", "This message appeared because you only granted" +
                                " approximate Location access. But in order to properly use this Application you need to provice " +
                                "fine Location access.\n");
                        dc.show(getSupportFragmentManager(), "Test");
                        terminateApp();
                    } else {
                        allowedTracking = false;
                        terminateApp();
                    }
                }
        );

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
                    if (location != null && isRunning) {
                        // save time and coordinates
                        backtrack.add(new LatLng(location.getLatitude(), location.getLongitude()));

                        // set speed
                        textLeft.setText(getGeschwindigkeit(location) + " km/h");

                        // build new camera position and animate the new camera position because we are moving
                        CameraPosition mylocation =
                                new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(15.5f)
                                        .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mylocation), 2000, null);
                        System.out.println("Debug: got location");
                    } else if(location != null && !isRunning && backtrack.size() == 0) {
                        CameraPosition mylocation =
                                new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(15.5f)
                                        .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mylocation), 2000, null);
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

        // initialize Timer object
        T = new com.app.trackingapp.Timer(this, textCenter);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                backtrack = new ArrayList<>();
                T.initTimer();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;
                T.stopTimer();
                drawRoute();
                sd = new StatusDialog(getCalories(), T.toString(), String.valueOf(stepCount), getDistance(), getParent());
                sd.show(getSupportFragmentManager(), "endOfRun");
            }
        });

        // initialize step counter sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if(sensorEvent.sensor == sensor && isRunning) {
                        stepCount = (int) sensorEvent.values[0];
                        textRight.setText("Steps " + stepCount);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensor = null;
            Toast.makeText(this, "step counter sensor not present!", Toast.LENGTH_LONG).show();
        }
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

        if(allowedTracking) {
            // show location on map
            mMap.setMyLocationEnabled(true);

            // request location updates
            // on location update locationCallback method will be called
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private double getGeschwindigkeit(Location location){
        return (double) location.getSpeed();
    }

    private void drawRoute() {
        LatLng []arr = new LatLng[backtrack.size()];
        backtrack.toArray(arr);

        Polyline pl = this.mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(arr));


        pl.setColor(0xff2781f5);
        pl.setWidth(25);

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                pl.remove();
            }
        });

        Toast.makeText(this, "click on the route to remove it", Toast.LENGTH_LONG).show();
    }


    private String getDistance() {
        return "";
    }

    private String getCalories() {
        return "";
    }

}
