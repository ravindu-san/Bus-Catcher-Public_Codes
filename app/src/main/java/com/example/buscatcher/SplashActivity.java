package com.example.buscatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private static  int SPLASH_TIME_OUT = 3000;//splash window display for 3s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                //check whether user has already logged in
                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

                //if user has logged in
                if (pref.getBoolean("activity_executed", false)) {

                   String bus_or_pas = pref.getString("bus_or_passenger","bus");

                    switch (bus_or_pas){
                        //if user has logged as a bus
                        case("bus"):{
                            Intent intent = new Intent(SplashActivity.this, BusMapsActivity.class);
                            startActivity(intent);
                            finish();

                            break;
                        }

                        //if user has logged as passenger
                        case ("passenger"):{
                            Intent intent = new Intent(SplashActivity.this, NavDrawerActivity.class);
                            startActivity(intent);
                            finish();

                            break;
                        }
                    }

                    //if user hasn't logged in
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        },SPLASH_TIME_OUT);



    }


}
