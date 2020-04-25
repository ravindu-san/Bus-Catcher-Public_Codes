package com.example.buscatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mPassenger, mBus;//buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPassenger = (Button) findViewById(R.id.passenger);
        mBus = (Button) findViewById(R.id.bus);

        //passenger button is clicked
        mPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,PassengerRegLogActivity.class);
                startActivity(intent);
                finish();

            }

        });

        //bus button is cicked
        mBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,BusRegLogActivity.class);
                startActivity(intent);
                finish();

            }

        });

    }

    //back button is pressed
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);

    }
}
