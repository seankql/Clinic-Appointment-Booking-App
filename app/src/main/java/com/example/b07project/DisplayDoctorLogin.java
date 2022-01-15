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


public class DisplayDoctorLogin extends DisplayLogin {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_login);
        s = (Switch) findViewById(R.id.switch1);
        s.setOnClickListener(this::switchC);
        Button login = (Button) findViewById(R.id.button3);
        login.setVisibility(View.INVISIBLE);
        Button create = (Button) findViewById(R.id.button);
        create.setOnClickListener(this::createDoctor);
        login.setOnClickListener(this::doctorLogIn);
        TextView error = (TextView) findViewById(R.id.textView7);
        error.setVisibility(View.INVISIBLE);
        TextView spec = (TextView) findViewById(R.id.textView4);
        spec.setText("Specialization");
        EditText input_spec = (EditText) findViewById(R.id.editTextTextPersonName3);
        input_spec.setHint("Enter your specialization");

        presenter = new Presenter(new Model("doctors"), this);
    }

    public void switchC(View view){
        EditText input_specialization = (EditText) findViewById(R.id.editTextTextPersonName3);
        TextView specialization = (TextView) findViewById(R.id.textView4);
        switchClick(view);
        if(s.isChecked()){
            input_specialization.setVisibility(View.INVISIBLE);
            specialization.setVisibility(View.INVISIBLE);
        }
        else{
            input_specialization.setVisibility(View.VISIBLE);
            specialization.setVisibility(View.VISIBLE);
        }
    }

    public void doctorLogIn(View view){
        presenter.checkPassword();
    }

    public void logIn(){
        Intent intent = new Intent(this, DoctorScreen.class);
        intent.putExtra(EXTRA_MESSAGE, getUsername()); //send the intent a message so that PatientScreen can load data from username
        startActivity(intent);
    }

    public void createDoctor(View view) {
        //Intent intent = new Intent(this, DoctorScreen.class);
        EditText input_name = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText input_gender = (EditText) findViewById(R.id.editTextTextPersonName2);
        EditText input_specialization = (EditText) findViewById(R.id.editTextTextPersonName3);
        EditText input_user = (EditText) findViewById(R.id.editTextTextPersonName4);
        EditText input_password = (EditText) findViewById(R.id.editTextTextPassword);
        String name = input_name.getText().toString();
        String gender = input_gender.getText().toString();
        String username = input_user.getText().toString();
        String password = input_password.getText().toString();
        String specialization = input_specialization.getText().toString();

        Doctor d = new Doctor(name, gender, specialization, username, password);

        presenter.createUser(d);
    }

}