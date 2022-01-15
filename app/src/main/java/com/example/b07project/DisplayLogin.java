package com.example.b07project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public abstract class DisplayLogin extends AppCompatActivity {
    Presenter presenter;
    Switch s;
    public static final String EXTRA_MESSAGE = "info";

    public void switchClick(View view){
        EditText input_name = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText input_gender = (EditText) findViewById(R.id.editTextTextPersonName2);
        TextView name = (TextView) findViewById(R.id.textView2);
        TextView gender = (TextView) findViewById(R.id.textView3);
        Button login = (Button) findViewById(R.id.button3);
        Button create = (Button) findViewById(R.id.button);
        TextView error = (TextView) findViewById(R.id.textView7);
        if(s.isChecked()){
            //hide new user fields (enter name, gender, date of birth)
            //show login button
            input_name.setVisibility(View.INVISIBLE);
            input_gender.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            gender.setVisibility(View.INVISIBLE);
            login.setVisibility(View.VISIBLE);
            create.setVisibility(View.INVISIBLE);
            error.setVisibility(View.INVISIBLE);
            Log.i("info", "checked");
        }
        else{
            //show new user fields
            //hide login button
            input_name.setVisibility(View.VISIBLE);
            input_gender.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            gender.setVisibility(View.VISIBLE);
            login.setVisibility(View.INVISIBLE);
            create.setVisibility(View.VISIBLE);
            error.setVisibility(View.INVISIBLE);
            Log.i("info", "unchecked");
        }
    }

    public abstract void logIn();

    public void displayMessage(String message){
        TextView error = (TextView) findViewById(R.id.textView7);
        error.setVisibility(View.VISIBLE);
        error.setText(message);
    }

    public void hideMessage(){
        TextView error = (TextView) findViewById(R.id.textView7);
        error.setVisibility(View.INVISIBLE);
    }

    public String getUsername(){
        EditText input_user = (EditText) findViewById(R.id.editTextTextPersonName4);
        return input_user.getText().toString();
    }

    public String getPassword(){
        EditText input_password = (EditText) findViewById(R.id.editTextTextPassword);
        return input_password.getText().toString();
    }

}
