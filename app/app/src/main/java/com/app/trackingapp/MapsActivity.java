package com.app.trackingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.app.trackingapp.databinding.ActivityMapsBinding;
import com.app.trackingapp.ui.login.LoginActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // auto-generated variables
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    // database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private FirebaseAuth firebaseAuth;
    private String email;

    // variables for step counter sensor
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount;

    // variables for requesting, receiving and working with location updates
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean allowedTracking = true;

    // user interface variables
    private TextView textLeft, textCenter, textRight;
    private Button btnStart, btnStop;
    private com.app.trackingapp.Timer T;
    private StatusDialog sd;
    private Button btn_rangliste, btn_signout;
    private boolean once = true;

    // user variables
    private String jogger;
    private Double bodyweight = null;
    private Double Km;

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            email = bundle.getString("mail");
        }

        getUser();

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
                    } else if(location != null && !isRunning && backtrack.size() == 0 && once) {
                        once = false;
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
        btn_rangliste = findViewById(R.id.buttonRangliste);
        btn_signout = findViewById(R.id.signOut);

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
                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
            }
        });

        btnStop.setOnClickListener(view -> {
            btnStop.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            if(isRunning) {
                isRunning = false;
                T.stopTimer();
                drawRoute();
                if (sensor == null) stepCount = 0;
                double distance = getDistance();
                ref.child("users").child(jogger).child("km").setValue((Km + distance));
                sd = new StatusDialog(getCalories(distance), T.toString(), String.valueOf(stepCount), String.format("%.2f", distance), getParent());
                sd.show(getSupportFragmentManager(), "endOfRun");
            }
        });

        btn_rangliste.setOnClickListener(view -> {
            if(isRunning) {
                Toast.makeText(this, "end your run before checking your score!", Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(view.getContext(), RanglisteActivity.class);
                i.putExtra("email", email);
                i.putExtra("user", jogger);
                startActivity(i);
            }
        });

        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), LoginActivity.class);
                startActivity(i);
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

        mMap.setOnPolylineClickListener(polyline -> pl.remove());

        Toast.makeText(this, "click on the route to remove it", Toast.LENGTH_LONG).show();
    }

    private double getDistance() {
        double sum = 0;
        Helper help = new Helper();
        for(int i=0; i<backtrack.size()-1; i++) {
            sum += help.CalculationByDistance(backtrack.get(i), backtrack.get(i + 1));
        }
        return sum;
    }

    private double getCalories(double distance) {
        if(bodyweight == null) Toast.makeText(this, "couldn't get bodyweight from database" +
                "burned calories may be incorrect!", Toast.LENGTH_LONG).show();

        return Math.round(distance * bodyweight * 0.9 * 100.0) / 100.0;
    }

    private void getUser() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Debug: " + dataSnapshot.toString());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    for(DataSnapshot user : child.getChildren()) {
                        UserClass usr = (UserClass) user.getValue(UserClass.class);
                        if(usr.getEmail().equals(email)) {
                            jogger = usr.getUsername();
                            bodyweight = usr.getWeight();
                            Km = usr.getKm();
                        }
                    }
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "database connection was cancelled!", Toast.LENGTH_LONG).show();
            }
        });

    }

}

