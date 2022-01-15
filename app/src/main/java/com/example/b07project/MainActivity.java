package com.example.b07project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity{


    private static final String EXTRA_MESSAGE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


     //Called when the user taps the Send button
    public void patientLogIn(View view) {
        Intent intent = new Intent(this, DisplayPatientLogin.class);
        startActivity(intent);
    }




    public void doctorLogIn(View view){
        Intent intent = new Intent(this, DisplayDoctorLogin.class);
        startActivity(intent);
    }
}