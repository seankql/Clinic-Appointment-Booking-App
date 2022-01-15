package com.example.b07project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class PatientScreen extends AppCompatActivity {

    BroadcastReceiver minuteUpdateReceiver;
    HashSet<Doctor> doctors;
    HashSet<String> genders;
    HashSet<String> specializations;
    DatePicker datePicker;
    TextView scrollName;
    ScrollView scrollView;
    ScrollView doctorView;
    ScrollView specializationView;
    ScrollView genderView;
    ScrollView timeView;
    TextView name_text;
    Button create_appointment;
    Button book_appointment;
    Button view_appointment;
    Patient patient;
    String user;
    Doctor doctor;
    String appTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_screen);

        appTime = "";
        patient = null;
        doctor = new Doctor();
        genders = new HashSet<String>();
        specializations = new HashSet<String>();
        doctors = new HashSet<Doctor>();

        view_appointment = (Button) findViewById(R.id.button6);
        create_appointment = (Button) findViewById(R.id.button5);
        book_appointment = (Button) findViewById(R.id.button4);
        datePicker = (DatePicker) findViewById(R.id.datePicker1);
        scrollName = (TextView) findViewById(R.id.textView9);
        doctorView = (ScrollView) findViewById(R.id.scrollView3);
        specializationView = (ScrollView) findViewById(R.id.scrollView5);
        timeView = (ScrollView) findViewById(R.id.scrollView7);
        genderView = (ScrollView) findViewById(R.id.scrollView4);
        scrollView = (ScrollView) findViewById(R.id.scrollView2);
        name_text = (TextView) findViewById(R.id.textView8);
        scrollView.setVisibility(View.VISIBLE);
        doctorView.setVisibility(View.INVISIBLE);
        specializationView.setVisibility(View.INVISIBLE);
        genderView.setVisibility(View.INVISIBLE);
        timeView.setVisibility(View.INVISIBLE);
        datePicker.setVisibility(View.INVISIBLE);
        create_appointment.setVisibility(View.INVISIBLE);
        book_appointment.setVisibility(View.VISIBLE);
        view_appointment.setVisibility(View.INVISIBLE);
        scrollName.setText("My Appointments: ");

        Intent intent = getIntent();
        //String message = intent.getStringExtra(DisplayPatientLogin.EXTRA_MESSAGE);
        user = intent.getStringExtra(DisplayPatientLogin.EXTRA_MESSAGE);
        //get information from database for username
        Log.i("user", user);

        removeOverdueAppointments();
        DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference("patients");
        patientRef.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patient = dataSnapshot.getValue(Patient.class);//save patient info
                if(patient == null){
                    //means patient was deleted while logged in...
                    finish();
                }
                else{
                    name_text.setText("Patient: " + patient.getName());
                    LinearLayout ll = (LinearLayout) findViewById(R.id.linearlayout1);//where the text will be added
                    ll.removeAllViews();
                    if(patient.getNextAppointments() != null){
                        Collections.sort(patient.getNextAppointments());
                        Log.i("app", patient.getNextAppointments().toString());
                        for(Appointment a : patient.getNextAppointments()){
                            Button t = new Button(getApplicationContext());
                            t.setTextSize(24);
                            t.setText("Appointment at: " + a.getTime() + ", on " + a.getDate() + ", with Doctor: " + a.getDocName());
                            ll.addView(t);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", " cancelled");
            }
        });
        DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference("doctors");
        doctorRef.addValueEventListener(new ValueEventListener() {
            LinearLayout ll = (LinearLayout) findViewById(R.id.linearlayout2);//where the text will be added
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ll.removeAllViews();
                doctors.clear();
                genders.clear();
                specializations.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){//add doctors
                    Doctor d = child.getValue(Doctor.class);
                    //Log.i("doc", d.toString());
                    doctors.add(d);
                    genders.add(d.getGender());
                    specializations.add(d.getSpecialization());
                    Button t = new Button(getApplicationContext());
                    t.setTextSize(24);
                    t.setText(d.getName() + ", Gender: " + d.getGender() + ", Specialization: " + d.getSpecialization());
                    t.setOnClickListener(PatientScreen.this::pickDate);
                    ll.addView(t);
                }
                cleanAppointments(doctors, patient);
                setupGenderAndSpec();
                removeOverdueAppointments();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", " cancelled");
            }
        });

    }
    //remove appointments with doctors that do not exist anymore.
    public void cleanAppointments(HashSet<Doctor> doctors, Patient patient){
        if(patient == null || patient.getNextAppointments() == null){
            return;
        }
        boolean found = false;
        List<Appointment> newAppointments = new ArrayList<Appointment>();
        for(Appointment a : patient.getNextAppointments()){
            for(Doctor d : doctors){
                if(d.getUsername().equals(a.getDocName())){
                    found = true;
                }
            }
            if(found){
                newAppointments.add(a);
            }
            found = false;
        }
        patient.setNextAppointments(newAppointments);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("patients");
        ref.child(patient.getUsername()).setValue(patient);
    }

    public void setupGenderAndSpec(){
        //setup genders and specializations for filtering purposes
        LinearLayout gend = (LinearLayout) findViewById(R.id.linearlayout3);
        LinearLayout specs = (LinearLayout) findViewById(R.id.linearlayout4);
        gend.removeAllViews();
        specs.removeAllViews();
        for(String s : genders){
            CheckBox b = new CheckBox(this);
            b.setText(s);
            b.setChecked(true);
            b.setOnClickListener(this::updateDoctors);
            gend.addView(b);
        }
        for(String s : specializations){
            CheckBox b = new CheckBox(this);
            b.setText(s);
            b.setChecked(true);
            b.setOnClickListener(this::updateDoctors);
            specs.addView(b);
        }

    }

    public void updateDoctors(View view){//add to the onClick of every checkbox
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearlayout2);//where the text will be added
        LinearLayout gend = (LinearLayout) findViewById(R.id.linearlayout3);
        LinearLayout specs = (LinearLayout) findViewById(R.id.linearlayout4);
        ll.removeAllViews();
        if(doctors == null){
            return;
        }
        for(Doctor d : doctors){//add doctors
            //Log.i("doc", d.getGender());
            boolean gendValid = false;
            boolean specValid = false;
            for(int i = 0; i < gend.getChildCount(); i++){
                View v = gend.getChildAt(i);
                if(v instanceof CheckBox){
                    if(((CheckBox) v).isChecked() && d.getGender().equals(((CheckBox) v).getText())){
                        gendValid = true;
                        break;
                    }
                }
            }
            for(int i = 0; i < specs.getChildCount(); i++){
                View v = specs.getChildAt(i);
                if(v instanceof CheckBox){
                    if(((CheckBox) v).isChecked() && d.getSpecialization().equals(((CheckBox) v).getText())){
                        specValid = true;
                        break;
                    }
                }
            }
            if(gendValid && specValid){
                Button t = new Button(getApplicationContext());
                t.setTextSize(24);
                t.setText(d.getName() + ", Gender: " + d.getGender() + ", Specialization: " + d.getSpecialization());
                t.setOnClickListener(PatientScreen.this::pickDate);
                ll.addView(t);
            }
        }
    }

    public void viewAppointments(View view){//exit out of doctor list
        scrollView.setVisibility(View.VISIBLE);
        doctorView.setVisibility(View.INVISIBLE);
        specializationView.setVisibility(View.INVISIBLE);
        genderView.setVisibility(View.INVISIBLE);
        datePicker.setVisibility(View.INVISIBLE);
        timeView.setVisibility(View.INVISIBLE);
        scrollName.setVisibility(View.VISIBLE);
        name_text.setVisibility(View.VISIBLE);
        create_appointment.setVisibility(View.INVISIBLE);
        book_appointment.setVisibility(View.VISIBLE);
        view_appointment.setVisibility(View.INVISIBLE);
        scrollName.setText("My Appointments: ");
    }

    public void createAppointment(View view){
        setupGenderAndSpec();
        updateDoctors(view);
        view_appointment.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        doctorView.setVisibility(View.VISIBLE);
        specializationView.setVisibility(View.VISIBLE);
        genderView.setVisibility(View.VISIBLE);
        datePicker.setVisibility(View.INVISIBLE);
        timeView.setVisibility(View.INVISIBLE);
        scrollName.setVisibility(View.VISIBLE);
        name_text.setVisibility(View.VISIBLE);
        create_appointment.setVisibility(View.INVISIBLE);
        scrollName.setText("Doctors: ");
    }



    public void pickDate(View view){
        Button b = (Button) view;
        if(b == null){
            return;
        }
        //get the doctor name of the button:
        String docName = "";
        for(int i = 0; i < b.getText().length(); i++){
            if(b.getText().charAt(i) == ',') {
                break;
            }
            docName += b.getText().charAt(i);
        }
        Log.i("doc", docName);
        for(Doctor d : doctors){
            if(d.getName().equals(docName)){
                doctor = d;
                break;
            }
        }
        doctorView.setVisibility(View.INVISIBLE);
        genderView.setVisibility(View.INVISIBLE);
        specializationView.setVisibility(View.INVISIBLE);

        Log.i("pickdate", "click received");
        datePicker.setVisibility(View.VISIBLE);
        scrollName.setVisibility(View.INVISIBLE);
        name_text.setVisibility(View.INVISIBLE);
        timeView.setVisibility(View.VISIBLE);
        LinearLayout timeSlots = (LinearLayout) findViewById(R.id.linearlayout6);
        timeSlots.removeAllViews();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                book_appointment.setVisibility(View.VISIBLE);
                create_appointment.setVisibility(View.INVISIBLE);
                timeSlots.removeAllViews();
                boolean addedTimeslot = false;
                Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                for(int i = 0; i < 8; i++){
                    Button time = new Button(getApplicationContext());
                    time.setTextSize(24);
                    int t1 = 9+i;
                    int t2 = 9+i+1;

                    Log.i("calendar", calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DAY_OF_MONTH));
                    if(calendar.get(Calendar.YEAR) < year || calendar.get(Calendar.YEAR) <= year && calendar.get(Calendar.MONTH) < month ||
                            calendar.get(Calendar.YEAR) <= year && calendar.get(Calendar.MONTH) <= month && calendar.get(Calendar.DAY_OF_MONTH) < dayOfMonth ||
                            calendar.get(Calendar.YEAR) <= year && calendar.get(Calendar.MONTH) <= month && calendar.get(Calendar.DAY_OF_MONTH) <= dayOfMonth &&
                            calendar.get(Calendar.HOUR_OF_DAY) < t1){
                        Log.i("valid", "yes");
                        time.setText(t1 + ":00-" + t2 + ":00");

                        if(doctor.getNextAppointments() != null){
                            Log.i("app", doctor.getNextAppointments().toString());
                            if(!doctor.getNextAppointments().contains(new Appointment(time.getText().toString(), dayOfMonth, month+1, year, doctor.getUsername(), patient.getUsername()))){
                                time.setOnClickListener(PatientScreen.this::showSaveAppointment);
                                timeSlots.addView(time);
                                addedTimeslot = true;
                            }
                        }
                        else{
                            time.setOnClickListener(PatientScreen.this::showSaveAppointment);
                            timeSlots.addView(time);
                            addedTimeslot = true;
                        }
                    }
                    if(!addedTimeslot){
                        //for user experience, add crossed out text for the time that has passed. User cannot click
                        time.setText(t1 + ":00-" + t2 + ":00");
                        time.setPaintFlags(time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        time.setEnabled(false);
                        time.setBackgroundColor(Color.LTGRAY);
                        timeSlots.addView(time);
                    }
                    addedTimeslot = false;
                }
            }
        });

    }

    public void showSaveAppointment(View view){
        boolean hasOverlap = false;
        book_appointment.setVisibility(View.INVISIBLE);
        create_appointment.setVisibility(View.VISIBLE);
        //create_appointment.setOnClickListener(this::addAppointment);
        Button b = (Button) view;
        if(b == null){
            return;
        }
        //save a temporary appointment time for when user clicks create appointment button
        appTime = b.getText().toString();
        //Log.i("appointment", appTime + " " + datePicker.getDayOfMonth() + " " + (datePicker.getMonth()+1) + " " +  datePicker.getYear() + " " + doctor);

        //check if patient has appointment at this time
        if (patient.getNextAppointments() != null) {
            for (Appointment a : patient.getNextAppointments()) {
                if (a.getTime().equals(appTime) && a.getDay() == datePicker.getDayOfMonth() && a.getMonth() == datePicker.getMonth() + 1 && a.getYear() == datePicker.getYear()) {
                    //set colour of create to RED (#ff0000)
                    create_appointment.setBackgroundColor(Color.LTGRAY);
                    //disable create button
                    create_appointment.setEnabled(false);
                    hasOverlap = true;
                    break;
                }
            }
        }
        if(!hasOverlap){
            //set colour of create to GREEN (#00ff00)
            create_appointment.setBackgroundColor(Color.GREEN);
            //enable create button
            create_appointment.setEnabled(true);
        }
    }
    public void addAppointment(View view){
        //add appointment to doctor and patient, update Firebase
        Appointment a = new Appointment(appTime, datePicker.getDayOfMonth(), datePicker.getMonth()+1, datePicker.getYear(), doctor.getUsername(), patient.getUsername());
        Appointment b = new Appointment(appTime, datePicker.getDayOfMonth(), datePicker.getMonth()+1, datePicker.getYear(), doctor.getUsername(), patient.getUsername());
        patient.addAppointment(a);
        doctor.addAppointment(b);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("patients");
        ref.child(patient.getUsername()).setValue(patient);
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("doctors");
        ref2.child(doctor.getUsername()).setValue(doctor);

        book_appointment.setVisibility(View.VISIBLE);
        create_appointment.setVisibility(View.INVISIBLE);
        viewAppointments(view);
    }

    public void removeOverdueAppointments(){
        Calendar c = Calendar.getInstance();
        Log.i("time","time changed" + c.get(Calendar.HOUR_OF_DAY));//24h time
        Appointment tempAppointment = new Appointment(c.get(Calendar.HOUR_OF_DAY) + ":", c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR), "", "");
        List<Appointment> newAppointments = new ArrayList<Appointment>();
        if(patient != null && patient.getNextAppointments() != null){
            for(Appointment a : patient.getNextAppointments()){
                if(a.compareTo(tempAppointment) <= 0){
                    //patient.removeAppointment(a);
                    patient.addPrevAppointment(a);
                    if(doctors != null) {
                        for (Doctor d : doctors) {
                            if (d.getUsername().equals(a.getDocName())) {
                                doctor = d;
                                patient.addDoctor(d.getUsername());
                                break;
                            }
                        }
                        doctor.removeAppointment(a);
                        doctor.addPatient(patient.getUsername());
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("doctors");
                        ref2.child(doctor.getUsername()).setValue(doctor);
                    }
                }
                else{
                    newAppointments.add(a);
                }
            }
            HashSet<Appointment> temp = new HashSet<Appointment>(patient.getNextAppointments());
            HashSet<Appointment> temp2 = new HashSet<Appointment>(newAppointments);
            if(temp.equals(temp2)) {
                return; // do not update if nothing was removed (IMPORTANT)
            }
            patient.setNextAppointments(newAppointments);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("patients");
            ref.child(patient.getUsername()).setValue(patient);
            //DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("doctors");
            //ref2.child(doctor.getUsername()).setValue(doctor);
        }
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
        //removeOverdueAppointments();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(minuteUpdateReceiver);
    }
}