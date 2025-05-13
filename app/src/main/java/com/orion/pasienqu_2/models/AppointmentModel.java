package com.orion.pasienqu_2.models;

public class AppointmentModel {
    private int id;
    private String uuid;
    private long appointment_date;
    private int work_location_id;
    private int patient_id;
    private String reminder;
    private String notes;

    private String reminder_text;
    private String work_location_text;
    private String patient_text;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(long appointment_date) {
        this.appointment_date = appointment_date;
    }

    public int getWork_location_id() {
        return work_location_id;
    }

    public void setWork_location_id(int work_location_id) {
        this.work_location_id = work_location_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getReminder_text() {
        return reminder_text;
    }

    public void setReminder_text(String reminder_text) {
        this.reminder_text = reminder_text;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getWork_location_text() {
        return work_location_text;
    }

    public void setWork_location_text(String work_location_text) {
        this.work_location_text = work_location_text;
    }

    public String getPatient_text() {
        return patient_text;
    }

    public void setPatient_text(String patient_text) {
        this.patient_text = patient_text;
    }

    public AppointmentModel(int id, String uuid, long appointment_date, int work_location_id, int patient_id, String reminder, String notes) {
        this.id = id;
        this.uuid = uuid;
        this.appointment_date = appointment_date;
        this.work_location_id = work_location_id;
        this.patient_id = patient_id;
        this.reminder = reminder;
        this.notes = notes;
        this.reminder_text = "";
        this.work_location_text = "";
        this.patient_text = "";
    }

    public AppointmentModel() {
        this.id = 0;
        this.uuid = "";
        this.appointment_date = 0;
        this.work_location_id = 0;
        this.patient_id = 0;
        this.reminder = "";
        this.notes = "";
        this.reminder_text = "";
        this.work_location_text = "";
        this.patient_text = "";
    }

}