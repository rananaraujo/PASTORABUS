package com.example.pastorabus.maps;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.example.pastorabus.R;
import com.example.pastorabus.model.LocationData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class MapsActivity extends FragmentActivity   implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat;
    private double lng;
    double latitude;
    double longetude;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    private List<LocationData> locationDataList = new ArrayList<LocationData>();
    private List<LocationData> locationDataListMed = new ArrayList<LocationData>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        Intent receberPosicao = getIntent();
        lat = receberPosicao.getDoubleExtra("lat", lat);
        lng = receberPosicao.getDoubleExtra("lng", lng);

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
        mMap.setMinZoomPreference(6.0f);
        mMap.setMinZoomPreference(20.0f);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        inicializarFireBase();
        int delay = 5000;   // delay de 5 seg.
        int interval = 10000;  // intervalo de 1 seg.
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
              pesquisarLocalizacao();
            }
        }, delay, interval);
    }

    private void inicializarFireBase(){
        FirebaseApp.initializeApp(MapsActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference();
    }

    public void pesquisarLocalizacao() {
        Query query;
        query = mDatabase.child("Location").orderByChild(String.valueOf(new Date().getTime()));
        locationDataList.clear();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {

                       LocationData locationData = objSnapshot.getValue(LocationData.class);

                       locationDataList.add(locationData);
                       Log.i("teste", locationData.horario + " Vai da certo ");

                   }

                for(int i = 0; i < locationDataList.size(); i++){
                    if(locationDataList.get(i).horario >= new Date().getTime()  -60000 ){
                        locationDataListMed.add(locationDataList.get(i));

                    }
                }
                locationDataList.clear();

                List<Double> list_lat = new ArrayList<Double>();
                List<Double> list_long= new ArrayList<Double>();

                for(int w = 0; w < locationDataListMed.size(); w++){
                    list_lat.add(locationDataListMed.get(w).latitude);
                    list_long.add(locationDataListMed.get(w).longitude);

                    Log.i("teste", list_lat.get(w) + "teste latitude");

                }
                locationDataListMed.clear();


                for(int i=0; i < list_lat.size(); i++) {
                    for(int j=i+1; j < list_lat.size(); j++) {
                        double latitude1 = list_lat.get(i);
                        double latitude2 =  list_lat.get(j);
                        if(latitude1 > latitude2) {
                            list_lat.set(i, latitude2);
                            list_lat.set(j, latitude1);
                        }
                    }
                    Log.i("teste", list_lat.get(i) + "teste latitude ordenada");
                }

                for(int i=0; i < list_long.size()-1; i++) {
                    for(int j=i+1; j < list_long.size(); j++) {
                        double longetude1 = list_lat.get(i);
                        double longetude2 =  list_lat.get(j);
                        if(longetude1 > longetude2) {
                            list_lat.set(i, longetude2);
                            list_lat.set(j, longetude1);
                        }
                    }

                }

                int meio_lat = list_lat.size()/ 2;
                int meio_long = list_long.size()/ 2;

                if(meio_lat % 2 == 0 && meio_long % 2 == 0){
                    lat  = ((list_lat.get(meio_lat) + list_lat.get(meio_lat + 1))/2);
                    longetude = ((list_long.get(meio_long) + list_long.get(meio_long + 1))/2);

                    LatLng origin = new LatLng(lat, longetude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(origin).title("Estou aqui " + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

                }else if(meio_lat % 2 == 0 && meio_long % 2 != 0){
                    latitude  = ((list_lat.get(meio_lat) + list_lat.get(meio_lat + 1))/2);
                    longetude = list_long.get(meio_long);
                    mMap.clear();
                    LatLng origin = new LatLng(latitude,longetude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(origin).title("Estou aqui " + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
                    latitude = 0;
                    longetude = 0;
                }else if (meio_lat % 2 != 0 && meio_long % 2 != 0){
                    latitude = list_lat.get(meio_lat);
                    longetude = list_long.get(meio_long);
                    LatLng origin = new LatLng(latitude,longetude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(origin).title("Estou aqui " + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
                    latitude = 0;
                    longetude = 0;
                } else if(meio_lat % 2 != 0 && meio_long % 2 == 0){
                    latitude = list_lat.get(meio_lat);
                    longetude = ((list_long.get(meio_lat) + list_long.get(meio_lat + 1))/2);
                    LatLng origin = new LatLng(latitude,longetude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(origin).title("Estou aqui " + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
                    latitude = 0;
                    longetude = 0;
                }


                list_lat.clear();
                list_long.clear();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }







}
