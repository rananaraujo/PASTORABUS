package com.example.pastorabus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Stop_activity extends AppCompatActivity {
    public static int RESULT_EDIT = 2;
    private double lat;
    private double lng;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        AlertDialog.Builder megBox = new AlertDialog.Builder(this);
        megBox.setTitle("Aviso");
        megBox.setMessage("É nescessário selecionar qual parada é a mais adequada para você esperar pelo transporte universitário");
        megBox.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;

            }
        });
        megBox.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stop_activity, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
