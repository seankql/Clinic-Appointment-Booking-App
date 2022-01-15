package com.example.b07project;

import junit.framework.TestCase;

import java.util.ArrayList;

public class PatientTest extends TestCase {

    public void testAddAppointment() {
        Patient p = new Patient("Bob", "Male", "12/12/2009", "bob123", "123");
        Doctor d = new Doctor("Doe", "Male", "Anesthesiology", "doe123", "123");
        Appointment a = new Appointment("12:00-1:00pm", 1,1,2021, d.getName(), p.getName());
        p.addAppointment(a);
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();
        appointments.add(a);
        assertEquals(p.getNextAppointments(), appointments);
    }

    public void testGetPrevAppointments() {

    }

    public void testSetPrevAppointments() {
    }

    public void testGetDoctors() {
    }

    public void testSetDoctors() {
    }

    public void testGetDateOfBirth() {
    }

    public void testSetDateOfBirth() {
    }
}