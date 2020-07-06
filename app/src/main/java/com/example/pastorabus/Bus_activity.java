package com.example.pastorabus;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.example.pastorabus.maps.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class Bus_activity extends AppCompatActivity {
    public static int RESULT_EDIT = 2;
    private double lat;
    private double lng;
    public int CODIGO_PERMISSOES_REQUERIDAS;


    String[] appPermisoes ={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout2);
        NavigationView navigationView = findViewById(R.id.nav_view2);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }




    public void consult_bus(View view){
        Intent intent = new Intent(this, Stop_activity.class);
        startActivityForResult(intent, RESULT_EDIT);
    }

    public void share_location(View view){
        if(VerificarPermissoes()){
            Intent intent = new Intent(this, ShareLocation.class);
            startActivityForResult(intent, RESULT_EDIT);
        }

    }

    public  boolean VerificarPermissoes() {
        List<String> permissoesrequeridas = new ArrayList<>();

        for (String permissao : appPermisoes) {
            if (ContextCompat.checkSelfPermission(this, permissao) != PackageManager.PERMISSION_GRANTED) {

                permissoesrequeridas.add(permissao);
            }

        }if (!permissoesrequeridas.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissoesrequeridas.toArray(new String[permissoesrequeridas.size()]), CODIGO_PERMISSOES_REQUERIDAS);
            return false;
        }
        return true;
    }
    public void showPracaLeao(View view) {
        lat = -4.970119;
        lng = -39.016044;
        Intent intentMaps = new Intent(getApplicationContext(), MapsActivity.class);
        intentMaps.putExtra("lat",lat);
        intentMaps.putExtra("lng", lng);
        startActivity(intentMaps);
    }
    public void showAcCar(View view) {
        lat = -4.970130;
        lng = -39.019273;
        Intent intentMaps = new Intent(getApplicationContext(), MapsActivity.class);
        intentMaps.putExtra("lat",lat);
        intentMaps.putExtra("lng", lng);
        startActivity(intentMaps);

    }

    public void showConstrutec(View view) {
        lat = -4.970311;
        lng = -39.024250;
        Intent intentMaps = new Intent(getApplicationContext(), MapsActivity.class);
        intentMaps.putExtra("lat",lat);
        intentMaps.putExtra("lng", lng);
        startActivity(intentMaps);

    }

    public void showIFCE(View view) {
        lat = -4.978661;
        lng =  -39.058388;
        Intent intentMaps = new Intent(getApplicationContext(), MapsActivity.class);
        intentMaps.putExtra("lat",lat);
        intentMaps.putExtra("lng", lng);
        startActivity(intentMaps);
    }
    public void showUFC(View view) {
        lat = -4.978676;
        lng = -39.056599;
        Intent intentMaps = new Intent(getApplicationContext(), MapsActivity.class);
        intentMaps.putExtra("lat",lat);
        intentMaps.putExtra("lng", lng);
        startActivity(intentMaps);
    }



}
