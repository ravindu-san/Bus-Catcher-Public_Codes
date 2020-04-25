package com.example.buscatcher;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PassengerRegLogActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mConfPassword;//text inputs
    private Button mLogin, mRegistration;//buttons

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    String email = null;
    String password  = null;
    String ConfPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_reg_log);

        //when user signed in
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null) {
                    Intent intent = new Intent(PassengerRegLogActivity.this,NavDrawerActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mConfPassword =(EditText) findViewById(R.id.confPassword);

        mLogin = (Button) findViewById(R.id.login);
        mRegistration = (Button) findViewById(R.id.registration);

        //registration button clicked
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(correctlyFilled()) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(PassengerRegLogActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(PassengerRegLogActivity.this, "Sign Up Error!", Toast.LENGTH_SHORT).show();
                            } else {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Passengers").child(user_id);
                                current_user_db.setValue(true);
                            }

                        }
                    });
                }
            }
        });

        //login button clicked
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(correctlyFilled()) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(PassengerRegLogActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(PassengerRegLogActivity.this, "Sign In Error!", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListner);
    }

    //check user inputs
    private boolean correctlyFilled()
    {
        boolean correct = false;
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        ConfPassword = mConfPassword.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(PassengerRegLogActivity.this,"Empty Email !!!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(PassengerRegLogActivity.this,"Empty Password!!!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ConfPassword)) {
            Toast.makeText(PassengerRegLogActivity.this,"Please confirm password!!!",Toast.LENGTH_SHORT).show();
        }
        else if(!ConfPassword.equals(password)){
            Toast.makeText(PassengerRegLogActivity.this, "Please check your password again!!!", Toast.LENGTH_SHORT).show();

        }
        else {
            correct = true;

        }
        return correct;
    }


}

