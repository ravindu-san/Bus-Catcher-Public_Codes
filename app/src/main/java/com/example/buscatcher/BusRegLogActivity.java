package com.example.buscatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusRegLogActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mRouteNo;//text inputs
    private Button mLogin;//login button

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner ;

    String email = null;
    String password = null;
    String routeNo = null;
    String routeNum="null";
    int n =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_reg_log);

        //When a user successfully signed in
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //get current signed-in user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null) {

                    String uid = user.getUid();//get user id
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users").child("Buses").child(routeNum).child(uid);
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {//username exist in bus category

                                Toast.makeText(BusRegLogActivity.this,"Welcome!",Toast.LENGTH_SHORT).show();
                                //to bus map activity
                                Intent intent = new Intent(BusRegLogActivity.this, BusMapsActivity.class);
                                startActivity(intent);
                                finish();
                                return;

                            }
                            else {//if not registered as bus
                                if(n!=0)
                                    Toast.makeText(BusRegLogActivity.this, "Oops!! You haven't registered yet", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        mLogin = (Button) findViewById(R.id.login);

        //when login button is pressed
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //read input Email, password and route field values
                mEmail = (EditText) findViewById(R.id.email);
                mPassword = (EditText) findViewById(R.id.password);
                mRouteNo = (EditText) findViewById(R.id.routeNo);

                if (correctlyFilled()) {
                    routeNum = routeNo;

                    SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putString("bus_route_no", routeNo);//user has registered as a bus
                    edt.commit();

                    //sign in with email and password
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(BusRegLogActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if authentication failed
                            if (!task.isSuccessful()) {
                                Toast.makeText(BusRegLogActivity.this, "Sign In Error!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListner);
        n=1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListner);
    }

    //check user entered values
    private boolean correctlyFilled()
    {
        boolean correct = false;
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        routeNo = mRouteNo.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(BusRegLogActivity.this,"Empty Email !!!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(BusRegLogActivity.this,"Empty Password!!!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(routeNo)) {
            Toast.makeText(BusRegLogActivity.this,"Empty route number!!!",Toast.LENGTH_SHORT).show();
        }
        else {
            correct = true;

        }
        return correct;
    }

}
