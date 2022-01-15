package com.example.b07project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PreviouslyVisitedDoctors extends AppCompatActivity {
    Patient patient;
    String patientName;
    ListView pastDoctorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previously_visited_doctors);

        Intent intent = getIntent();
        patientName = intent.getStringExtra(DoctorScreen.EXTRA_MESSAGE);

        DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference();
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patient = dataSnapshot.child("patients").child(patientName).getValue(Patient.class);
                pastDoctorView = (ListView)findViewById(R.id.pastDoctorListView);
                ArrayList<String> doctorNameList = new ArrayList<String>();
                if(patient != null && patient.prevAppointments != null) {
                    for (Appointment a : patient.prevAppointments) {
                        if (!doctorNameList.contains(a.docName)) {
                            doctorNameList.add(a.docName);
                        }
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(PreviouslyVisitedDoctors.this, android.R.layout.simple_list_item_1, doctorNameList);
                pastDoctorView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", " cancelled");
            }

        });
    }
}