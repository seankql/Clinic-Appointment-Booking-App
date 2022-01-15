package com.example.b07project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Patient extends User implements Serializable {

    String dateOfBirth;
    List<Appointment> prevAppointments;
    List<String> doctors;

    public Patient(){

    }

    public Patient(String n, String g, String dob, String u, String p){
        name = n;
        gender = g;
        dateOfBirth = dob;
        username = u;
        password = p;
        prevAppointments  = new ArrayList<Appointment>();
        nextAppointments = new ArrayList<Appointment>();
        doctors = new ArrayList<String>();
    }

    @Override
    public String toString(){
        return "Patient{"+
                "name='"+name+'\''+
                ", gender='"+gender+'\''+
                ", dateOfBirth='"+dateOfBirth+'\''+
                '}';
    }

    public void addPrevAppointment(Appointment a){
        if(prevAppointments == null){
            prevAppointments = new ArrayList<Appointment>();
        }
        prevAppointments.add(a);
    }

    public void addDoctor(String d){
        if(doctors == null){
            doctors = new ArrayList<String>();
        }
        for(String doc : doctors){
            if(doc.equals(d)){
                return;//do not add duplicate doctors.
            }
        }
        doctors.add(d);
    }

    public List<Appointment> getPrevAppointments() {
        return prevAppointments;
    }

    public void setPrevAppointments(List<Appointment> prevAppointments) {
        this.prevAppointments = prevAppointments;
    }

    public List<String> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<String> doctors) {
        this.doctors = doctors;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


}
