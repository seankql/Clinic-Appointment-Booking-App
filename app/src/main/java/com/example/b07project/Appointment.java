package com.example.b07project;

import android.util.Log;

import java.io.Serializable;

public class Appointment implements Serializable, Comparable<Appointment>{
    String time;
    String date;
    int day, month, year;
    String name; //patient
    String docName;

    public Appointment(){

    }
    public Appointment(String t, int day, int month, int year, String docName, String name){
        time = t;
        this.day = day;
        this.month = month;
        this.year = year;
        date = day + "/" + month + "/" + year;
        this.name = name;
        this.docName = docName;
    }

    @Override
    public String toString(){
        return time + ", " + date + ", Doctor: " + docName;
    }

    @Override
    public int hashCode(){
        return 13*getDay()+17*getMonth()+23*getYear();
    }

    //Appointments are equal if they are at the same time with the same doctor
    //A doctor cannot have an appointment with two different patients at the same time
    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(o.getClass() != getClass()){
            return false;
        }
        Appointment a = (Appointment)o;
        if(a.getDay() != getDay() || a.getMonth() != getMonth() || a.getYear() != getYear()){
            return false;
        }
        if(!a.getDocName().equals(getDocName())){
            return false;
        }
        if(!a.getTime().equals(getTime())){
            return false;
        }
        return true;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStartTime(String time){
        //Log.i("time", "time" + Integer.parseInt(time.substring(0,1)));
        if(time.charAt(1) == ':'){
            return Integer.parseInt(time.substring(0,1));
        }
        return Integer.parseInt(time.substring(0,2));
    }

    @Override
    public int compareTo(Appointment appointment) {
        //Log.i("sort", "sorting" + this.toString() + " with: " + appointment.toString());
        if(year < appointment.getYear() || year == appointment.getYear() && month < appointment.getMonth() ||
                year == appointment.getYear() && month == appointment.getMonth() && day < appointment.getDay() ||
                year == appointment.getYear() && month == appointment.getMonth() && day == appointment.getDay() &&
                        getStartTime(time) < appointment.getStartTime(appointment.getTime())){
            //Log.i("result", "less");
            return -1;
        }

        if(year == appointment.getYear() && month == appointment.getMonth() && day == appointment.getDay() &&
                getStartTime(time) == appointment.getStartTime(appointment.getTime())){
            //Log.i("result", "equal");
            return 0;
        }
        //Log.i("result", "greater");
        return 1;
    }
}
