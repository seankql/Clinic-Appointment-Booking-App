package com.example.b07project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class User {
    String name;
    String gender;
    String username;
    String password;
    List<Appointment> nextAppointments;

    public void addAppointment(Appointment a){
        if(nextAppointments == null){
            nextAppointments = new ArrayList<Appointment>();
        }
        nextAppointments.add(a);
        Collections.sort(nextAppointments);
    }

    public void removeAppointment(Appointment a){
        if(nextAppointments == null){
            return;
        }
        nextAppointments.remove(a);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Appointment> getNextAppointments() {
        return nextAppointments;
    }

    public void setNextAppointments(List<Appointment> nextAppointments) {
        this.nextAppointments = nextAppointments;
    }
}
