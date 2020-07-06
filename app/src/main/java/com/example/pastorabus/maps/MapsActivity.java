package com.example.pastorabus.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pastorabus.Bus_activity;
import com.example.pastorabus.MainActivity;
import com.example.pastorabus.R;
import com.example.pastorabus.ShareLocation;
import com.example.pastorabus.Stop_activity;
import com.example.pastorabus.model.LocationData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat;
    private double lng;
    private double lat_aux;
    private double lng_aux;
    double latitude;
    double longetude;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    private static final int LOCATION_REQUEST = 500;
    private List<LocationData> locationDataList = new ArrayList<LocationData>();
    private List<LocationData> locationDataListMed = new ArrayList<LocationData>();
    public static int RESULT_EDIT = 2;
    ArrayList<LatLng> listPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        listPoint = new ArrayList<>();
        setContentView(R.layout.activity_main2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(6.0f);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng origin3 = new LatLng(-4.970159, -39.016139);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin3, 15));

        inicializarFireBase();
        int delay = 5000;
        int interval = 10000;
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

                   }

                for(int i = 0; i < locationDataList.size(); i++){
                    if(locationDataList.get(i).horario >= new Date().getTime() -60000 ){
                        locationDataListMed.add(locationDataList.get(i));
                    }
                }

                if(locationDataListMed.size() == 0 ){
                    AlertDialog.Builder megBox = new AlertDialog.Builder(MapsActivity.this);
                    megBox.setTitle("Ônibus fora do horário de rota");
                    megBox.setMessage("Para consultar o intenerário do ônibus consulte o itém ônibus no menu de opções");
                    megBox.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MapsActivity.this, Bus_activity.class);
                            startActivityForResult(intent, RESULT_EDIT);
                            return;

                        }
                    });
                    megBox.show();
                    return;

                }

                if(locationDataListMed.size() == 1){

                    LatLng origin = new LatLng(locationDataListMed.get(0).latitude,  locationDataListMed.get(0).longitude);
                    Intent receberPosicao = getIntent();
                    lat = receberPosicao.getDoubleExtra("lat", lat);
                    lng = receberPosicao.getDoubleExtra("lng", lng);
                    LatLng origin2 = new LatLng( lat, lng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
                    lat_aux=locationDataListMed.get(0).latitude;
                    lng_aux=locationDataListMed.get(0).longitude;
                    mMap.addMarker(new MarkerOptions().position(origin).title("Posição do Ônibus" + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));
                    return;
                }

                locationDataList.clear();

                List<Double> list_lat = new ArrayList<Double>();
                List<Double> list_long= new ArrayList<Double>();

                for(int w = 0; w < locationDataListMed.size(); w++){
                    list_lat.add(locationDataListMed.get(w).latitude);
                    list_long.add(locationDataListMed.get(w).longitude);
                }
                locationDataListMed.clear();


                for(int i=0; i < list_lat.size(); i++) {
                    for(int j=i+1; j < list_lat.size(); j++) {
                        double latitude1 = list_lat.get(i);
                        double latitude2 = list_lat.get(j);
                        if (latitude1 > latitude2) {
                            list_lat.set(i, latitude2);
                            list_lat.set(j, latitude1);
                        }
                    }
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


                    latitude  = ((list_lat.get(meio_lat) + list_lat.get(meio_lat + 1))/2);
                    longetude = ((list_long.get(meio_long) + list_long.get(meio_long + 1))/2);
                       LatLng origin = new LatLng(latitude, longetude);
                    Intent receberPosicao = getIntent();
                    lat = receberPosicao.getDoubleExtra("lat", lat);
                    lng = receberPosicao.getDoubleExtra("lng", lng);
                    Log.i("teste", lat +"!!!!!!!!!!!!!!!!!"+ lng);
                    LatLng origin2 = new LatLng( lat, lng);
                    mMap.addMarker(new MarkerOptions().position(origin).title("Posição do Ônibus " + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));


                }else if(meio_lat % 2 == 0 && meio_long % 2 != 0) {
                    latitude = ((list_lat.get(meio_lat) + list_lat.get(meio_lat + 1)) / 2);
                    longetude = list_long.get(meio_long);
                    Intent receberPosicao = getIntent();
                    lat = receberPosicao.getDoubleExtra("lat", lat);
                    lng = receberPosicao.getDoubleExtra("lng", lng);
                    LatLng origin2 = new LatLng( lat, lng);
                    Log.i("teste", lat +"!!!!!!!!!!!!!!!!!"+ lng);
                    LatLng origin = new LatLng(latitude, longetude);
                    mMap.addMarker(new MarkerOptions().position(origin).title("Posição do Ônibus" + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

                }else if (meio_lat % 2 != 0 && meio_long % 2 != 0){
                    latitude = list_lat.get(meio_lat);
                    longetude = list_long.get(meio_long);
                    LatLng origin = new LatLng(latitude,longetude);
                    Intent receberPosicao = getIntent();
                    lat = receberPosicao.getDoubleExtra("lat", lat);
                    lng = receberPosicao.getDoubleExtra("lng", lng);
                    LatLng origin2 = new LatLng( lat, lng);
                    Log.i("teste", lat +"!!!!!!!!!!!!!!!!!"+ lng);
                    mMap.addMarker(new MarkerOptions().position(origin).title("Posição do Ônibus" + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

                } else if(meio_lat % 2 != 0 && meio_long % 2 == 0){
                    latitude = list_lat.get(meio_lat);
                    longetude = ((list_long.get(meio_lat) + list_long.get(meio_lat + 1))/2);
                    LatLng origin = new LatLng(latitude,longetude);
                    Intent receberPosicao = getIntent();
                    lat = receberPosicao.getDoubleExtra("lat", lat);
                    lng = receberPosicao.getDoubleExtra("lng", lng);

                    Log.i("teste", lat +"!!!!!!!!!!!!!!!!!"+ lng);
                    LatLng origin2 = new LatLng( lat, lng);

                    mMap.addMarker(new MarkerOptions().position(origin).title("Posição do Ônibus " + latitude + longetude).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

                }


                list_lat.clear();
                list_long.clear();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private String getRequestUrl(LatLng origin1, LatLng origin2){
     String str_org = "origin=" + origin1.latitude +","+origin1.longitude;
     String str_dest = "destination=" + origin2.latitude +","+origin2.longitude;
     String sensor = "sensor=false";
     String mode = "mode=driving";
     String API_KEY = "AIzaSyCwsz-P8GsMymhKlBDPSzI12n9bvoObG34";
     String param = str_org + "&" +str_dest + "&" + sensor + "&" +mode + "&key=" + API_KEY ;
     String output ="json";
     String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"+ param;
     return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStream1 = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStream1);

            StringBuffer stringBuffer = new StringBuffer();
            String line ="";

            while ((line = bufferedReader.readLine())!= null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStream1.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }


    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}