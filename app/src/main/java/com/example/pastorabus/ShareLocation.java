package com.example.pastorabus;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.example.pastorabus.locationaddress.Constant;
import com.example.pastorabus.model.LocationData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShareLocation extends AppCompatActivity implements OnMapReadyCallback {
    FusedLocationProviderClient client;
    GoogleMap mMap;
    private static final String TAÇ = "MapsActivity";
    public double lat;
    public double lng;
    private int location_route;
    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference mDatabase;
    private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 50;
    private String GEOFENCE_ID = "SOME_GEOFANCE_ID";
    private int BACKGROUD_LOCATION_ACESS_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = LocationServices.getFusedLocationProviderClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        AlertDialog.Builder megBox = new AlertDialog.Builder(this);
        megBox.setTitle("Aviso sobre localização");
        megBox.setMessage("Para uma localização mais precisa do ônibus é necessário que o usuário permaneça com aplicativo PASTORABUS em funcionamento");
        megBox.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;

            }
        });
        megBox.show();


    }

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.setMinZoomPreference(6.0f);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }


    @Override
    protected void onResume() {

        super.onResume();
        int errorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (errorCode) {
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                Log.d("Teste", "show dialog");
                GoogleApiAvailability.getInstance().getErrorDialog(this, errorCode, 0, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        }
                ).show();
                break;
            case ConnectionResult.SUCCESS:
                Log.d("teste", "Google Play Services up-to-date");
                break;
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.i("Teste", location.getLatitude() + " " + location.getLongitude());

                    LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(origin).title("Sua posição compartilhada"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

                    /*Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .add(
                                    new LatLng(-4.970119, -39.016044),
                                    new LatLng(-4.970130, -39.019273),
                                    new LatLng(-4.970181, -39.021821),
                                    new LatLng(-4.970221, -39.022918),
                                    new LatLng(-4.970224, -39.023669),
                                    new LatLng(-4.970235, -39.023986),
                                    new LatLng(-4.970286, -39.024250),
                                    new LatLng(-4.970546, -39.024867),
                                    new LatLng(-4.970931, -39.026058),
                                    new LatLng(-4.971552, -39.027636),
                                    new LatLng(-4.972348, -39.029945),
                                    new LatLng(-4.975298, -39.035888),
                                    new LatLng(-4.975538, -39.036706),
                                    new LatLng(-4.975835, -39.037342),
                                    new LatLng(-4.977056, -39.039574),
                                    new LatLng(-4.977387, -39.040357),
                                    new LatLng(-4.978744, -39.042851),
                                    new LatLng(-4.978990, -39.043224),
                                    new LatLng(-4.979447, -39.043830),
                                    new LatLng(-4.979527, -39.044045),
                                    new LatLng(-4.979637, -39.045654),
                                    new LatLng(-4.979640, -39.045986),
                                    new LatLng(-4.979447, -39.046198),
                                    new LatLng(-4.978269, -39.047553),
                                    new LatLng(-4.978130, -39.047966),
                                    new LatLng(-4.978082, -39.048457),
                                    new LatLng(-4.978676, -39.056599),
                                    new LatLng(-4.978661, -39.058388)));
                    polyline1.setTag("A");

                    Polyline polyline2 = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .add(
                                    new LatLng(-4.978661, -39.058388),
                                    new LatLng(-4.978676, -39.056599),
                                    new LatLng(-4.978659, -39.056565),
                                    new LatLng(-4.978085, -39.048463),
                                    new LatLng(-4.978117, -39.047969),
                                    new LatLng(-4.978267, -39.047556),
                                    new LatLng(-4.979472, -39.046172),
                                    new LatLng(-4.979595, -39.045952),
                                    new LatLng(-4.979640, -39.045668),
                                    new LatLng(-4.979526, -39.044045),
                                    new LatLng(-4.979371, -39.043694),
                                    new LatLng(-4.977383, -39.040353),
                                    new LatLng(-4.975565, -39.036760),
                                    new LatLng(-4.975298, -39.035864),
                                    new LatLng(-4.972365, -39.029961),
                                    new LatLng(-4.971542, -39.027633),
                                    new LatLng(-4.971542, -39.027633),
                                    new LatLng(-4.970609, -39.025128),
                                    new LatLng(-4.971785, -39.024857),
                                    new LatLng(-4.972635, -39.023955),
                                    new LatLng(-4.972523, -39.016434),
                                    new LatLng(-4.972560, -39.016091),
                                    new LatLng(-4.971104, -39.016061),
                                    new LatLng(-4.970741, -39.016040),
                                    new LatLng(-4.970126, -39.016035)


                            ));
                    polyline2.setTag("A");*/

                } else {
                    Log.i("teste", "null");

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(ShareLocation.this,
                                10);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }

                }
            }
        });


        final LocationCallback locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;

                }


                mMap.clear();
                CircleOptions c1 = new CircleOptions();
                c1.center(new LatLng(-4.970119, -39.016044));
                c1.radius(GEOFENCE_RADIUS);
                c1.strokeColor(Color.argb(255, 255, 0, 0));
                c1.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c2 = new CircleOptions();
                c2.center(new LatLng(-4.970119, -39.016719));
                c2.radius(GEOFENCE_RADIUS);
                c2.strokeColor(Color.argb(255, 255, 0, 0));
                c2.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c3 = new CircleOptions();
                c3.center(new LatLng(-4.970133, -39.017295));
                c3.radius(GEOFENCE_RADIUS);
                c3.strokeColor(Color.argb(255, 255, 0, 0));
                c3.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c4 = new CircleOptions();
                c4.center(new LatLng(-4.970154, -39.017863));
                c4.radius(GEOFENCE_RADIUS);
                c4.strokeColor(Color.argb(255, 255, 0, 0));
                c4.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c5 = new CircleOptions();
                c5.center( new LatLng(-4.970144, -39.018346));
                c5.radius(GEOFENCE_RADIUS);
                c5.strokeColor(Color.argb(255, 255, 0, 0));
                c5.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c6 = new CircleOptions();
                c6.center( new LatLng(-4.970165, -39.018936));
                c6.radius(GEOFENCE_RADIUS);
                c6.strokeColor(Color.argb(255, 255, 0, 0));
                c6.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c7 = new CircleOptions();
                c7.center( new LatLng(-4.970165, -39.019537));
                c7.radius(GEOFENCE_RADIUS);
                c7.strokeColor(Color.argb(255, 255, 0, 0));
                c7.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c8 = new CircleOptions();
                c8.center( new LatLng(-4.970165, -39.020127));
                c8.radius(GEOFENCE_RADIUS);
                c8.strokeColor(Color.argb(255, 255, 0, 0));
                c8.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c9 = new CircleOptions();
                c9.center( new LatLng(-4.970197, -39.020610));
                c9.radius(GEOFENCE_RADIUS);
                c9.strokeColor(Color.argb(255, 255, 0, 0));
                c9.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c10 = new CircleOptions();
                c10.center( new LatLng(-4.970176, -39.021189));
                c10.radius(GEOFENCE_RADIUS);
                c10.strokeColor(Color.argb(255, 255, 0, 0));
                c10.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c11 = new CircleOptions();
                c11.center( new LatLng(-4.970229, -39.021801));
                c11.radius(GEOFENCE_RADIUS);
                c11.strokeColor(Color.argb(255, 255, 0, 0));
                c11.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c12 = new CircleOptions();
                c12.center( new LatLng(-4.970240, -39.022348));
                c12.radius(GEOFENCE_RADIUS);
                c12.strokeColor(Color.argb(255, 255, 0, 0));
                c12.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c13 = new CircleOptions();
                c13.center( new LatLng(-4.970240, -39.022884));
                c13.radius(GEOFENCE_RADIUS);
                c13.strokeColor(Color.argb(255, 255, 0, 0));
                c13.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c14 = new CircleOptions();
                c14.center( new LatLng(-4.970197, -39.023506));
                c14.radius(GEOFENCE_RADIUS);
                c14.strokeColor(Color.argb(255, 255, 0, 0));
                c14.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c15 = new CircleOptions();
                c15.center( new LatLng(-4.970256, -39.024076));
                c15.radius(GEOFENCE_RADIUS);
                c15.strokeColor(Color.argb(255, 255, 0, 0));
                c15.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c16 = new CircleOptions();
                c16.center( new LatLng(-4.970510, -39.024809));
                c16.radius(GEOFENCE_RADIUS);
                c16.strokeColor(Color.argb(255, 255, 0, 0));
                c16.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c17 = new CircleOptions();
                c17.center( new LatLng(-4.970763, -39.025614));
                c17.radius(GEOFENCE_RADIUS);
                c17.strokeColor(Color.argb(255, 255, 0, 0));
                c17.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c18 = new CircleOptions();
                c18.center( new LatLng(-4.970951, -39.026107));
                c18.radius(GEOFENCE_RADIUS);
                c18.strokeColor(Color.argb(255, 255, 0, 0));
                c18.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c19 = new CircleOptions();
                c19.center( new LatLng(-4.971095, -39.026558));
                c19.radius(GEOFENCE_RADIUS);
                c19.strokeColor(Color.argb(255, 255, 0, 0));
                c19.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c20 = new CircleOptions();
                c20.center( new LatLng(-4.971268, -39.027004));
                c20.radius(GEOFENCE_RADIUS);
                c20.strokeColor(Color.argb(255, 255, 0, 0));
                c20.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c21 = new CircleOptions();
                c21.center( new LatLng(-4.971539, -39.027644));
                c21.radius(GEOFENCE_RADIUS);
                c21.strokeColor(Color.argb(255, 255, 0, 0));
                c21.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c22 = new CircleOptions();
                c22.center( new LatLng(-4.971782, -39.028356));
                c22.radius(GEOFENCE_RADIUS);
                c22.strokeColor(Color.argb(255, 255, 0, 0));
                c22.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c23 = new CircleOptions();
                c23.center( new LatLng(-4.972038, -39.029124));
                c23.radius(GEOFENCE_RADIUS);
                c23.strokeColor(Color.argb(255, 255, 0, 0));
                c23.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c24 = new CircleOptions();
                c24.center( new LatLng(-4.972273, -39.029725));
                c24.radius(GEOFENCE_RADIUS);
                c24.strokeColor(Color.argb(255, 255, 0, 0));
                c24.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c25 = new CircleOptions();
                c25.center( new LatLng(-4.972538, -39.030291));
                c25.radius(GEOFENCE_RADIUS);
                c25.strokeColor(Color.argb(255, 255, 0, 0));
                c25.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c26= new CircleOptions();
                c26.center( new LatLng(-4.972872, -39.030911));
                c26.radius(GEOFENCE_RADIUS);
                c26.strokeColor(Color.argb(255, 255, 0, 0));
                c26.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c27= new CircleOptions();
                c27.center( new LatLng(-4.973150, -39.031455));
                c27.radius(GEOFENCE_RADIUS);
                c27.strokeColor(Color.argb(255, 255, 0, 0));
                c27.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c28= new CircleOptions();
                c28.center( new LatLng(-4.973388, -39.031991));
                c28.radius(GEOFENCE_RADIUS);
                c28.strokeColor(Color.argb(255, 255, 0, 0));
                c28.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c29= new CircleOptions();
                c29.center( new LatLng(-4.973674, -39.032562));
                c29.radius(GEOFENCE_RADIUS);
                c29.strokeColor(Color.argb(255, 255, 0, 0));
                c29.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c30= new CircleOptions();
                c30.center( new LatLng(-4.974207, -39.033623));
                c30.radius(GEOFENCE_RADIUS);
                c30.strokeColor(Color.argb(255, 255, 0, 0));
                c30.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c31= new CircleOptions();
                c31.center( new LatLng(-4.974207, -39.033623));
                c31.radius(GEOFENCE_RADIUS);
                c31.strokeColor(Color.argb(255, 255, 0, 0));
                c31.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c32= new CircleOptions();
                c32.center( new LatLng(-4.974482, -39.034167));
                c32.radius(GEOFENCE_RADIUS);
                c32.strokeColor(Color.argb(255, 255, 0, 0));
                c32.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c33= new CircleOptions();
                c33.center( new LatLng(-4.974482, -39.034167));
                c33.radius(GEOFENCE_RADIUS);
                c33.strokeColor(Color.argb(255, 255, 0, 0));
                c33.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c34= new CircleOptions();
                c34.center( new LatLng(-4.974731, -39.034690));
                c34.radius(GEOFENCE_RADIUS);
                c34.strokeColor(Color.argb(255, 255, 0, 0));
                c34.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c35= new CircleOptions();
                c35.center( new LatLng(-4.974969, -39.035141));
                c35.radius(GEOFENCE_RADIUS);
                c35.strokeColor(Color.argb(255, 255, 0, 0));
                c35.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c36= new CircleOptions();
                c36.center( new LatLng(-4.975177, -39.035608));
                c36.radius(GEOFENCE_RADIUS);
                c36.strokeColor(Color.argb(255, 255, 0, 0));
                c36.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c37= new CircleOptions();
                c37.center( new LatLng(-4.975372, -39.036166));
                c37.radius(GEOFENCE_RADIUS);
                c37.strokeColor(Color.argb(255, 255, 0, 0));
                c37.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c38= new CircleOptions();
                c38.center( new LatLng(-4.975535, -39.036670));
                c38.radius(GEOFENCE_RADIUS);
                c38.strokeColor(Color.argb(255, 255, 0, 0));
                c38.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c39= new CircleOptions();
                c39.center( new LatLng(-4.975770, -39.037223));
                c39.radius(GEOFENCE_RADIUS);
                c39.strokeColor(Color.argb(255, 255, 0, 0));
                c39.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c40= new CircleOptions();
                c40.center( new LatLng(-4.976053, -39.037719));
                c40.radius(GEOFENCE_RADIUS);
                c40.strokeColor(Color.argb(255, 255, 0, 0));
                c40.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c41= new CircleOptions();
                c41.center( new LatLng(-4.976355, -39.038223));
                c41.radius(GEOFENCE_RADIUS);
                c41.strokeColor(Color.argb(255, 255, 0, 0));
                c41.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c42= new CircleOptions();
                c42.center( new LatLng(-4.976622, -39.038746));
                c42.radius(GEOFENCE_RADIUS);
                c42.strokeColor(Color.argb(255, 255, 0, 0));
                c42.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c43= new CircleOptions();
                c43.center( new LatLng(-4.976895, -39.039245));
                c43.radius(GEOFENCE_RADIUS);
                c43.strokeColor(Color.argb(255, 255, 0, 0));
                c43.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c44= new CircleOptions();
                c44.center( new LatLng(-4.977144, -39.039792));
                c44.radius(GEOFENCE_RADIUS);
                c44.strokeColor(Color.argb(255, 255, 0, 0));
                c44.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c45= new CircleOptions();
                c45.center( new LatLng(-4.977363, -39.040315));
                c45.radius(GEOFENCE_RADIUS);
                c45.strokeColor(Color.argb(255, 255, 0, 0));
                c45.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c46= new CircleOptions();
                c46.center( new LatLng(-4.977630, -39.040771));
                c46.radius(GEOFENCE_RADIUS);
                c46.strokeColor(Color.argb(255, 255, 0, 0));
                c46.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c47= new CircleOptions();
                c47.center( new LatLng(-4.977830, -39.041141));
                c47.radius(GEOFENCE_RADIUS);
                c47.strokeColor(Color.argb(255, 255, 0, 0));
                c47.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c48= new CircleOptions();
                c48.center( new LatLng(-4.978081, -39.041610));
                c48.radius(GEOFENCE_RADIUS);
                c48.strokeColor(Color.argb(255, 255, 0, 0));
                c48.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c49= new CircleOptions();
                c49.center( new LatLng(-4.978308, -39.042045));
                c49.radius(GEOFENCE_RADIUS);
                c49.strokeColor(Color.argb(255, 255, 0, 0));
                c49.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c50= new CircleOptions();
                c50.center( new LatLng(-4.978530, -39.042437));
                c50.radius(GEOFENCE_RADIUS);
                c50.strokeColor(Color.argb(255, 255, 0, 0));
                c50.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c51= new CircleOptions();
                c51.center( new LatLng(-4.978770, -39.042874));
                c51.radius(GEOFENCE_RADIUS);
                c51.strokeColor(Color.argb(255, 255, 0, 0));
                c51.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c52= new CircleOptions();
                c52.center( new LatLng(-4.979008, -39.043252));
                c52.radius(GEOFENCE_RADIUS);
                c52.strokeColor(Color.argb(255, 255, 0, 0));
                c52.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c53= new CircleOptions();
                c53.center( new LatLng(-4.979283, -39.043577));
                c53.radius(GEOFENCE_RADIUS);
                c53.strokeColor(Color.argb(255, 255, 0, 0));
                c53.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c54= new CircleOptions();
                c54.center( new LatLng(-4.979502, -39.043912));
                c54.radius(GEOFENCE_RADIUS);
                c54.strokeColor(Color.argb(255, 255, 0, 0));
                c54.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c55= new CircleOptions();
                c55.center( new LatLng(-4.979553, -39.044352));
                c55.radius(GEOFENCE_RADIUS);
                c55.strokeColor(Color.argb(255, 255, 0, 0));
                c55.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c56= new CircleOptions();
                c56.center( new LatLng(-4.979561, -39.044677));
                c56.radius(GEOFENCE_RADIUS);
                c56.strokeColor(Color.argb(255, 255, 0, 0));
                c56.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c57= new CircleOptions();
                c57.center( new LatLng(-4.979614, -39.045216));
                c57.radius(GEOFENCE_RADIUS);
                c57.strokeColor(Color.argb(255, 255, 0, 0));
                c57.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c58= new CircleOptions();
                c58.center( new LatLng(-4.979611, -39.045793));
                c58.radius(GEOFENCE_RADIUS);
                c58.strokeColor(Color.argb(255, 255, 0, 0));
                c58.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c59= new CircleOptions();
                c59.center( new LatLng(-4.979491, -39.046155));
                c59.radius(GEOFENCE_RADIUS);
                c59.strokeColor(Color.argb(255, 255, 0, 0));
                c59.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c60= new CircleOptions();
                c60.center( new LatLng(-4.979192, -39.046463));
                c60.radius(GEOFENCE_RADIUS);
                c60.strokeColor(Color.argb(255, 255, 0, 0));
                c60.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c61= new CircleOptions();
                c61.center( new LatLng(-4.978906, -39.046766));
                c61.radius(GEOFENCE_RADIUS);
                c61.strokeColor(Color.argb(255, 255, 0, 0));
                c61.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c62= new CircleOptions();
                c62.center( new LatLng(-4.978639, -39.047085));
                c62.radius(GEOFENCE_RADIUS);
                c62.strokeColor(Color.argb(255, 255, 0, 0));
                c62.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c63= new CircleOptions();
                c63.center( new LatLng(-4.978380, -39.047383));
                c63.radius(GEOFENCE_RADIUS);
                c63.strokeColor(Color.argb(255, 255, 0, 0));
                c63.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c64= new CircleOptions();
                c64.center( new LatLng(-4.978161, -39.047823));
                c64.radius(GEOFENCE_RADIUS);
                c64.strokeColor(Color.argb(255, 255, 0, 0));
                c64.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c65= new CircleOptions();
                c65.center( new LatLng(-4.978092, -39.048349));
                c65.radius(GEOFENCE_RADIUS);
                c65.strokeColor(Color.argb(255, 255, 0, 0));
                c65.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c66= new CircleOptions();
                c66.center( new LatLng(-4.978113, -39.048867));
                c66.radius(GEOFENCE_RADIUS);
                c66.strokeColor(Color.argb(255, 255, 0, 0));
                c66.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c67= new CircleOptions();
                c67.center( new LatLng(-4.978161, -39.049328));
                c67.radius(GEOFENCE_RADIUS);
                c67.strokeColor(Color.argb(255, 255, 0, 0));
                c67.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c68= new CircleOptions();
                c68.center( new LatLng(-4.978177, -39.049671));
                c68.radius(GEOFENCE_RADIUS);
                c68.strokeColor(Color.argb(255, 255, 0, 0));
                c68.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c69= new CircleOptions();
                c69.center( new LatLng(-4.978214, -39.050095));
                c69.radius(GEOFENCE_RADIUS);
                c69.strokeColor(Color.argb(255, 255, 0, 0));
                c69.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c70= new CircleOptions();
                c70.center( new LatLng(-4.978225, -39.050487));
                c70.radius(GEOFENCE_RADIUS);
                c70.strokeColor(Color.argb(255, 255, 0, 0));
                c70.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c71= new CircleOptions();
                c71.center( new LatLng(-4.978265, -39.050972));
                c71.radius(GEOFENCE_RADIUS);
                c71.strokeColor(Color.argb(255, 255, 0, 0));
                c71.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c72= new CircleOptions();
                c72.center( new LatLng(-4.978268, -39.051339));
                c72.radius(GEOFENCE_RADIUS);
                c72.strokeColor(Color.argb(255, 255, 0, 0));
                c72.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c73= new CircleOptions();
                c73.center( new LatLng(-4.978311, -39.051792));
                c73.radius(GEOFENCE_RADIUS);
                c73.strokeColor(Color.argb(255, 255, 0, 0));
                c73.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c74= new CircleOptions();
                c74.center( new LatLng(-4.978354, -39.052243));
                c74.strokeColor(Color.argb(255, 255, 0, 0));
                c74.fillColor(Color.argb(64,255, 0, 0));



                CircleOptions c75= new CircleOptions();
                c75.center( new LatLng(-4.978389, -39.052691));
                c75.radius(GEOFENCE_RADIUS);
                c75.strokeColor(Color.argb(255, 255, 0, 0));
                c75.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c76= new CircleOptions();
                c76.center( new LatLng(-4.978225, -39.050487));
                c76.radius(GEOFENCE_RADIUS);
                c76.strokeColor(Color.argb(255, 255, 0, 0));
                c76.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c77= new CircleOptions();
                c77.center( new LatLng(-4.978425, -39.053196));
                c77.radius(GEOFENCE_RADIUS);
                c77.strokeColor(Color.argb(255, 255, 0, 0));
                c77.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c78= new CircleOptions();
                c78.center( new LatLng(-4.978474, -39.053717));
                c78.radius(GEOFENCE_RADIUS);
                c78.strokeColor(Color.argb(255, 255, 0, 0));
                c78.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c79= new CircleOptions();
                c79.center( new LatLng(-4.978506, -39.054275));
                c79.radius(GEOFENCE_RADIUS);
                c79.strokeColor(Color.argb(255, 255, 0, 0));
                c79.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c80= new CircleOptions();
                c80.center( new LatLng(-4.978527, -39.054779));
                c80.strokeColor(Color.argb(255, 255, 0, 0));
                c80.fillColor(Color.argb(64,255, 0, 0));



                CircleOptions c81= new CircleOptions();
                c81.center( new LatLng(-4.978580, -39.055498));
                c81.radius(GEOFENCE_RADIUS);
                c81.strokeColor(Color.argb(255, 255, 0, 0));
                c81.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c82= new CircleOptions();
                c82.center( new LatLng(-4.978623, -39.056045));
                c82.radius(GEOFENCE_RADIUS);
                c82.strokeColor(Color.argb(255, 255, 0, 0));
                c82.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c83= new CircleOptions();
                c83.center( new LatLng(-4.978666, -39.056560));
                c83.radius(GEOFENCE_RADIUS);
                c83.strokeColor(Color.argb(255, 255, 0, 0));
                c83.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c84= new CircleOptions();
                c84.center( new LatLng(-4.979083, -39.056528));
                c84.radius(GEOFENCE_RADIUS);
                c84.strokeColor(Color.argb(255, 255, 0, 0));
                c84.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c85= new CircleOptions();
                c85.center( new LatLng(-4.978655, -39.057123));
                c85.radius(GEOFENCE_RADIUS);
                c85.strokeColor(Color.argb(255, 255, 0, 0));
                c85.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c86= new CircleOptions();
                c86.center( new LatLng(-4.978698, -39.057810));
                c86.radius(GEOFENCE_RADIUS);
                c86.strokeColor(Color.argb(255, 255, 0, 0));
                c86.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c87= new CircleOptions();
                c87.center( new LatLng(-4.978698, -39.058395));
                c87.radius(GEOFENCE_RADIUS);
                c87.strokeColor(Color.argb(255, 255, 0, 0));
                c87.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c88= new CircleOptions();
                c88.center( new LatLng(-4.978206, -39.058416));
                c88.radius(GEOFENCE_RADIUS);
                c88.strokeColor(Color.argb(255, 255, 0, 0));
                c88.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c89= new CircleOptions();
                c89.center( new LatLng(-4.973855, -39.032881));
                c89.radius(GEOFENCE_RADIUS);
                c89.strokeColor(Color.argb(255, 255, 0, 0));
                c89.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c90= new CircleOptions();
                c90.center( new LatLng(-4.978586, -39.055225));
                c90.radius(GEOFENCE_RADIUS);
                c90.strokeColor(Color.argb(255, 255, 0, 0));
                c90.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c91= new CircleOptions();
                c91.center( new LatLng(-4.971056, -39.024979));
                c91.radius(GEOFENCE_RADIUS);
                c91.strokeColor(Color.argb(255, 255, 0, 0));
                c91.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c92= new CircleOptions();
                c92.center( new LatLng(-4.971826, -39.024840));
                c92.radius(GEOFENCE_RADIUS);
                c92.strokeColor(Color.argb(255, 255, 0, 0));
                c92.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c93= new CircleOptions();
                c93.center( new LatLng(-4.972531, -39.024625));
                c93.radius(GEOFENCE_RADIUS);
                c93.strokeColor(Color.argb(255, 255, 0, 0));
                c93.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c94= new CircleOptions();
                c94.center( new LatLng(-4.972584, -39.023252));
                c94.radius(GEOFENCE_RADIUS);
                c94.strokeColor(Color.argb(255, 255, 0, 0));
                c94.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c95= new CircleOptions();
                c95.center( new LatLng(-4.972584, -39.022007));
                c95.radius(GEOFENCE_RADIUS);
                c95.strokeColor(Color.argb(255, 255, 0, 0));
                c95 .fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c96= new CircleOptions();
                c96.center( new LatLng(-4.972557, -39.021100));
                c96.radius(GEOFENCE_RADIUS);
                c96.strokeColor(Color.argb(255, 255, 0, 0));
                c96 .fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c97= new CircleOptions();
                c97.center( new LatLng(-4.972552, -39.020231));
                c97.radius(GEOFENCE_RADIUS);
                c97.strokeColor(Color.argb(255, 255, 0, 0));
                c97 .fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c98= new CircleOptions();
                c98.center( new LatLng(-4.972515, -39.019437));
                c98.radius(GEOFENCE_RADIUS);
                c98.strokeColor(Color.argb(255, 255, 0, 0));
                c98 .fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c99= new CircleOptions();
                c99.center( new LatLng(-4.972520, -39.018584));
                c99.radius(GEOFENCE_RADIUS);
                c99.strokeColor(Color.argb(255, 255, 0, 0));
                c99 .fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c100= new CircleOptions();
                c100.center( new LatLng(-4.972520, -39.017629));
                c100.radius(GEOFENCE_RADIUS);
                c100.strokeColor(Color.argb(255, 255, 0, 0));
                c100.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c101= new CircleOptions();
                c101.center( new LatLng(-4.972491, -39.016554));
                c101.radius(GEOFENCE_RADIUS);
                c101.strokeColor(Color.argb(255, 255, 0, 0));
                c101.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c102 = new CircleOptions();
                c102.center( new LatLng(-4.972465, -39.016131));
                c102.radius(GEOFENCE_RADIUS);
                c102.strokeColor(Color.argb(255, 255, 0, 0));
                c102.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c103 = new CircleOptions();
                c103.center( new LatLng(-4.971744, -39.016115));
                c103.radius(GEOFENCE_RADIUS);
                c103.strokeColor(Color.argb(255, 255, 0, 0));
                c103.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c104 = new CircleOptions();
                c104.center( new LatLng(-4.970969, -39.016104));
                c104.radius(GEOFENCE_RADIUS);
                c104.strokeColor(Color.argb(255, 255, 0, 0));
                c104.fillColor(Color.argb(64,255, 0, 0));

                CircleOptions c105 = new CircleOptions();
                c105.center( new LatLng(-4.970419, -39.016066));
                c105.radius(GEOFENCE_RADIUS);
                c105.strokeColor(Color.argb(255, 255, 0, 0));
                c105.fillColor(Color.argb(64,255, 0, 0));


                CircleOptions c106 = new CircleOptions();
                c106.center( new LatLng(-4.967544, -39.010009));
                c106.radius(GEOFENCE_RADIUS);
                c106.strokeColor(Color.argb(255, 255, 0, 0));
                c106.fillColor(Color.argb(64,255, 0, 0));






                List<CircleOptions> listCircle = new ArrayList<>();
                listCircle.add(c1);
                listCircle.add(c2);
                listCircle.add(c3);
                listCircle.add(c4);
                listCircle.add(c5);
                listCircle.add(c6);
                listCircle.add(c7);
                listCircle.add(c8);
                listCircle.add(c9);
                listCircle.add(c10);
                listCircle.add(c11);
                listCircle.add(c12);
                listCircle.add(c13);
                listCircle.add(c14);
                listCircle.add(c15);
                listCircle.add(c16);
                listCircle.add(c17);
                listCircle.add(c18);
                listCircle.add(c19);
                listCircle.add(c20);
                listCircle.add(c21);
                listCircle.add(c22);
                listCircle.add(c23);
                listCircle.add(c24);
                listCircle.add(c25);
                listCircle.add(c26);
                listCircle.add(c27);
                listCircle.add(c28);
                listCircle.add(c29);
                listCircle.add(c30);
                listCircle.add(c31);
                listCircle.add(c32);
                listCircle.add(c33);
                listCircle.add(c34);
                listCircle.add(c35);
                listCircle.add(c36);
                listCircle.add(c37);
                listCircle.add(c38);
                listCircle.add(c39);
                listCircle.add(c40);
                listCircle.add(c41);
                listCircle.add(c42);
                listCircle.add(c43);
                listCircle.add(c44);
                listCircle.add(c45);
                listCircle.add(c46);
                listCircle.add(c47);
                listCircle.add(c48);
                listCircle.add(c49);
                listCircle.add(c50);
                listCircle.add(c51);
                listCircle.add(c52);
                listCircle.add(c53);
                listCircle.add(c54);
                listCircle.add(c55);
                listCircle.add(c56);
                listCircle.add(c57);
                listCircle.add(c58);
                listCircle.add(c59);
                listCircle.add(c60);
                listCircle.add(c61);
                listCircle.add(c62);
                listCircle.add(c63);
                listCircle.add(c64);
                listCircle.add(c65);
                listCircle.add(c66);
                listCircle.add(c67);
                listCircle.add(c68);
                listCircle.add(c69);
                listCircle.add(c70);
                listCircle.add(c72);
                listCircle.add(c72);
                listCircle.add(c73);
                listCircle.add(c74);
                listCircle.add(c75);
                listCircle.add(c76);
                listCircle.add(c77);
                listCircle.add(c78);
                listCircle.add(c79);
                listCircle.add(c80);
                listCircle.add(c81);
                listCircle.add(c82);
                listCircle.add(c83);
                listCircle.add(c84);
                listCircle.add(c85);
                listCircle.add(c86);
                listCircle.add(c87);
                listCircle.add(c88);
                listCircle.add(c89);
                listCircle.add(c90);
                listCircle.add(c91);
                listCircle.add(c92);
                listCircle.add(c93);
                listCircle.add(c94);
                listCircle.add(c95);
                listCircle.add(c96);
                listCircle.add(c97);
                listCircle.add(c98);
                listCircle.add(c99);
                listCircle.add(c100);
                listCircle.add(c101);
                listCircle.add(c102);
                listCircle.add(c103);
                listCircle.add(c104);
                listCircle.add(c105);
                listCircle.add(c106);

                for (Location location : locationResult.getLocations()) {

                    float[] distance = new float[2];
                    for(CircleOptions circle: listCircle) {
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                                circle.getCenter().latitude, circle.getCenter().longitude, distance);

                        if (distance[0] > circle.getRadius()) {

                        } else {
                            Toast.makeText(getBaseContext(), "Posição compartilhada com sucesso!", Toast.LENGTH_LONG).show();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                            // OU
                            SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");

                            Date data = new Date();

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(data);
                            Date data_atual = cal.getTime();

                            String hora_atual = dateFormat_hora.format(data_atual);
                            LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude(), new Date().getTime());
                            mDatabase.child("Location").child(String.valueOf(new Date().getTime())).setValue(locationData);
                            location_route = 1;
                        }
                    }
                    if(location_route != 1){
                        Toast.makeText(getBaseContext(), "Localização compartilhada fora da rota do ônibus. Por favor ir para rota do ônibus para compartilhar sua localização", Toast.LENGTH_LONG).show();
                    }
                    // startIntentService(location);
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.i("teste", locationAvailability.isLocationAvailable() + " NÃO ESTA ");
            }
        };

        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}



