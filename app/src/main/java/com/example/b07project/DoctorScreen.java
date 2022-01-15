package com.example.b07project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

import java.util.HashSet;
import java.util.List;

public class DoctorScreen extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "";
    Doctor doctor;
    String user;
    ArrayAdapter arrayAdapter;
    ListView doctorAppointmentList;
    HashSet<Patient> patients;
    BroadcastReceiver minuteUpdateReceiver;
    Patient p;// to fetch the patient's info each time
    List<Patient> patientList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_screen);
        patients = new HashSet<Patient>();
        doctor = null;
        Intent intent = getIntent();
        user = intent.getStringExtra(DisplayDoctorLogin.EXTRA_MESSAGE);
        //get information from database for username
        Log.i("user", user);
        patientList = new ArrayList<Patient>();

        DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference();
        doctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                doctor = dataSnapshot.child("doctors").child(user).getValue(Doctor.class);
                if(doctor == null){
                    finish();//doctor was removed while logged in...
                }

                patients.clear();
                for(DataSnapshot child : dataSnapshot.child("patients").getChildren()){//add patients
                    Patient p = child.getValue(Patient.class);
                    patients.add(p);
                }
                cleanAppointments(patients, doctor);
                removeOverdueAppointments();
                updateAppointmentList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", " cancelled");
            }
        });
    }

    public void updateAppointmentList(){
        //Show list appointments the doctor has
        if(doctor == null || doctor.getNextAppointments() == null){
            return;
        }
        Collections.sort(doctor.getNextAppointments());
        Iterator<Appointment> appointments = doctor.getNextAppointments().iterator();
        doctorAppointmentList = (ListView) findViewById(R.id.appointmentListView);
        ArrayList<String> arrayList = new ArrayList<>();

        //ArrayList<Patient> appointmentPatients = new ArrayList<>();

        while (appointments.hasNext()) {
            Appointment appointment = appointments.next();
            String time = appointment.getTime();
            String date = appointment.getDate();
            String patientUsername = appointment.getName();
            // stores info from database into created patient object
            for(Patient patient : patients){
                if(patient.getUsername().equals(patientUsername)){
                    p = patient;
                    break;
                }
            }
            patientList.add(p);
            String patientAppointment = "\nAppointment at: " + time + ", on " + date + ", with Patient: " + patientUsername;
            String patientInfo = "\n\nPATIENT INFO:\nNAME: " + p.name + "\nGENDER:   "+ p.gender+"\nDATE OF BIRTH: "+p.dateOfBirth;

            patientAppointment+=patientInfo ;


            arrayList.add(patientAppointment);

        }
        arrayAdapter = new ArrayAdapter(DoctorScreen.this, android.R.layout.simple_list_item_1, arrayList);
        doctorAppointmentList.setAdapter(arrayAdapter);
        ////////////////////////
        doctorAppointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("user", patientList.get(position).username);
                Intent intent = new Intent(DoctorScreen.this, PreviouslyVisitedDoctors.class);
                intent.putExtra(EXTRA_MESSAGE, patientList.get(position).username);
                startActivity(intent);
            }
        });
        ////////////////////////
    }

    //remove appointments with patients that do not exist anymore.
    public void cleanAppointments(HashSet<Patient> patients, Doctor doctor){
        //potential issue: if doctor is logged in and patient adds appointment, the doctor will not get
        //updated before cleaning their appointments...
        //Solution: must get most recent doctor before calling this method.
        if(doctor == null || doctor.getNextAppointments() == null){
            return;
        }
        boolean found = false;
        List<Appointment> newAppointments = new ArrayList<Appointment>();
        for(Appointment a : doctor.getNextAppointments()){
            //Log.i("test", "Looking for: "+ a.getName());
            for(Patient p : patients){
                //Log.i("test", p.getUsername() + " " + a.getName());
                if(p.getUsername().equals(a.getName())){
                    found = true;
                }
            }
            if(found){
                newAppointments.add(a);
            }
            found = false;
        }
        HashSet<Appointment> temp = new HashSet<Appointment>(doctor.getNextAppointments());
        HashSet<Appointment> temp2 = new HashSet<Appointment>(newAppointments);
        if(temp.equals(temp2)) {
            return; // do not update if nothing was removed (IMPORTANT)
        }
        doctor.setNextAppointments(newAppointments);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("doctors");
        ref.child(doctor.getUsername()).child("nextAppointments").setValue(newAppointments);
    }


    public void startMinuteUpdater(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                removeOverdueAppointments();
            }
        };
        registerReceiver(minuteUpdateReceiver, intentFilter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        startMinuteUpdater();
        removeOverdueAppointments();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(minuteUpdateReceiver);
    }

    public void removeOverdueAppointments(){
        Patient tempPatient = new Patient();
        Calendar c = Calendar.getInstance();
        Log.i("time","time changed" + c.get(Calendar.HOUR_OF_DAY));//24h time
        Appointment tempAppointment = new Appointment(c.get(Calendar.HOUR_OF_DAY) + ":", c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR), "", "");
        List<Appointment> newAppointments = new ArrayList<Appointment>();
        if(doctor != null && doctor.getNextAppointments() != null){
            for(Appointment a : doctor.getNextAppointments()){
                if(a.compareTo(tempAppointment) <= 0){
                    if(patients != null) {
                        for (Patient p : patients) {
                            if (p.getUsername().equals(a.getName())) {
                                tempPatient = p;
                                tempPatient.removeAppointment(a);
                                tempPatient.addPrevAppointment(a);
                                tempPatient.addDoctor(doctor.getUsername());
                                doctor.addPatient(tempPatient.getUsername());
                                break;
                            }
                        }
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("patients");
                        ref.child(tempPatient.getUsername()).setValue(tempPatient);
                    }
                }
                else{
                    newAppointments.add(a);
                }
            }
            HashSet<Appointment> temp = new HashSet<Appointment>(doctor.getNextAppointments());
            HashSet<Appointment> temp2 = new HashSet<Appointment>(newAppointments);
            if(temp.equals(temp2)) {
                return; // do not update if nothing was removed (IMPORTANT)
            }
            doctor.setNextAppointments(newAppointments);
            //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("patients");
            //ref.child(tempPatient.getUsername()).setValue(tempPatient);
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("doctors");
            ref2.child(doctor.getUsername()).setValue(doctor);
        }
    }
}
