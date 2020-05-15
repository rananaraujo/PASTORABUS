package com.example.pastorabus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    public static int RESULT_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }



    public void consult_bus(View view){
        Log.d("MainActivy", "Clique Editar");
        Intent intent = new Intent(this, Stop_activity.class);
        startActivityForResult(intent, RESULT_EDIT);
    }


    public void share_location(View view){
        Log.d("MainActivy", "Clique Editar");
        Intent intent = new Intent(this, ShareLocation.class);
        startActivityForResult(intent, RESULT_EDIT);
    }


}
