package com.orion.pasienqu_2.models;

public class ReminderModel {
    private int id;
    private int value_reminder;
    private String patient_name;
    private String location;
    private String custom_reminder;
    private long date_reminder;
    private String appointment;

    public ReminderModel(int id, int value_reminder, String patient_name, String location, String custom_reminder, long date_reminder, String appointment) {
        this.id = id;
        this.value_reminder = value_reminder;
        this.patient_name = patient_name;
        this.location = location;
        this.custom_reminder = custom_reminder;
        this.date_reminder = date_reminder;
        this.appointment = appointment;
    }

    public ReminderModel() {
        this.id = 0;
        this.value_reminder = 0;
        this.patient_name = "";
        this.location = "";
        this.custom_reminder = "";
        this.date_reminder = 0;
        this.appointment = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue_reminder() {
        return value_reminder;
    }

    public void setValue_reminder(int value_reminder) {
        this.value_reminder = value_reminder;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCustom_reminder() {
        return custom_reminder;
    }

    public void setCustom_reminder(String custom_reminder) {
        this.custom_reminder = custom_reminder;
    }

    public long getDate_reminder() {
        return date_reminder;
    }

    public void setDate_reminder(long date_reminder) {
        this.date_reminder = date_reminder;
    }

    public String getAppointment() {
        return appointment;
    }

    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }
}
