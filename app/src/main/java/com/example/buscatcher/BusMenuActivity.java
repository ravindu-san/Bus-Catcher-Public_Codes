package com.example.buscatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class BusMenuActivity extends AppCompatActivity  {

    private Button mRoute01, mRoute02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_menu);

        //store user entered values
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("activity_executed", true);
        edt.putString("bus_or_passenger","passenger");
        edt.commit();

        mRoute01 = (Button) findViewById(R.id.route01);
        //route one button is clicked
        mRoute01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BusMenuActivity.this,PassengerMapsActivity.class);
                intent.putExtra("route_number","1");
                startActivity(intent);
                finish();

            }

        });

        mRoute02 = (Button) findViewById(R.id.route02);
        //route two button is clicked
        mRoute02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BusMenuActivity.this,PassengerMapsActivity.class);
                intent.putExtra("route_number","2");
                startActivity(intent);
                finish();

            }
        });
    }

}

