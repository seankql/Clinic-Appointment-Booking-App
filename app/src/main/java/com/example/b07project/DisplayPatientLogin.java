package com.example.b07project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DisplayPatientLogin extends DisplayLogin {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_login);
        s = (Switch) findViewById(R.id.switch1);
        s.setOnClickListener(this::switchC);
        Button login = (Button) findViewById(R.id.button3);
        Button create = (Button) findViewById(R.id.button);
        create.setOnClickListener(this::createPatient);
        login.setVisibility(View.INVISIBLE);
        login.setOnClickListener(this::patientLogIn);
        TextView error = (TextView) findViewById(R.id.textView7);
        error.setVisibility(View.INVISIBLE);
        TextView date = (TextView) findViewById(R.id.textView4);
        date.setText("Date of Birth");
        EditText input_date = (EditText) findViewById(R.id.editTextTextPersonName3);
        input_date.setHint("YYYY/MM/DD");

        presenter = new Presenter(new Model("patients"), this);
    }

    public void switchC(View view){
        EditText input_date = (EditText) findViewById(R.id.editTextTextPersonName3);
        TextView date = (TextView) findViewById(R.id.textView4);
        switchClick(view);
        if(s.isChecked()){
            input_date.setVisibility(View.INVISIBLE);
            date.setVisibility(View.INVISIBLE);
        }
        else{
            input_date.setVisibility(View.VISIBLE);
            date.setVisibility(View.VISIBLE);
        }
    }


    public void patientLogIn(View view){
        presenter.checkPassword();
    }

    public void logIn(){
        Intent intent = new Intent(this, PatientScreen.class);
        intent.putExtra(EXTRA_MESSAGE, getUsername()); //send the intent a message so that PatientScreen can load data from username
        startActivity(intent);
    }


    public void createPatient(View view) {
        //Intent intent = new Intent(this, PatientScreen.class);
        EditText input_name = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText input_gender = (EditText) findViewById(R.id.editTextTextPersonName2);
        EditText input_date = (EditText) findViewById(R.id.editTextTextPersonName3);
        EditText input_user = (EditText) findViewById(R.id.editTextTextPersonName4);
        EditText input_password = (EditText) findViewById(R.id.editTextTextPassword);
        String name = input_name.getText().toString();
        String gender = input_gender.getText().toString();
        String dateOfBirth = input_date.getText().toString();
        String username = input_user.getText().toString();
        String password = input_password.getText().toString();

        Patient p = new Patient(name, gender, dateOfBirth, username, password);

        presenter.createUser(p);

    }

}