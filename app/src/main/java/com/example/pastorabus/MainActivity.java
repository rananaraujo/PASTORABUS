package com.example.pastorabus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static int RESULT_EDIT = 2;
    public int CODIGO_PERMISSOES_REQUERIDAS;

    String[] appPermisoes ={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        }
        if (!permissoesrequeridas.isEmpty()) {

            ActivityCompat.requestPermissions(this, permissoesrequeridas.toArray(new String[permissoesrequeridas.size()]), CODIGO_PERMISSOES_REQUERIDAS);
            return false;
        }
        return true;
    }

}
