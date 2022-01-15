package com.example.b07project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends User implements Serializable {

    String specialization;
    List<String> weeklyAvailabilities;
    List<String> patients;
    public Doctor(){

    }
    public Doctor(String n, String g, String spec, String u, String p){
        name = n;
        gender = g;
        username = u;
        password = p;
        specialization = spec;
        nextAppointments = new ArrayList<Appointment>();
        weeklyAvailabilities = new ArrayList<String>();
    }

    @Override
    public int hashCode(){
        int sum = 0;
        String userN = getUsername();
        for(int i = 0; i < userN.length(); i++){
            sum += userN.charAt(i);
        }
        return sum;
    }

    @Override
    public boolean equals(Object o){//doctors are equal if they share the same username
        if(o == null){
            return false;
        }
        if(o.getClass() != getClass()){
            return false;
        }
        Doctor d = (Doctor)o;
        if(!d.getUsername().equals(getUsername())){
            return false;
        }
        return true;
    }

    public void addPatient(String p){
        if(patients == null){
            patients = new ArrayList<String>();
        }
        for(String pat : patients){
            if(pat.equals(p)){
                return;//do not add duplicate patients.
            }
        }
        patients.add(p);
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<String> getWeeklyAvailabilities() {
        return weeklyAvailabilities;
    }

    public void setWeeklyAvailabilities(List<String> weeklyAvailabilities) {
        this.weeklyAvailabilities = weeklyAvailabilities;
    }

    public List<String> getPatients() {
        return patients;
    }

    public void setPatients(List<String> patients) {
        this.patients = patients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
