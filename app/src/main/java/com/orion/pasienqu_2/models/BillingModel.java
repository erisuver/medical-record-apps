package com.orion.pasienqu_2.models;

import java.util.ArrayList;
import java.util.List;

public class BillingModel {
    private int id;
    private String uuid;
    long billing_date;
    int patient_id;
    String notes;
    double total_amount;
    String name;
    List<BillingItemModel> billing_item_ids;
    int medical_record_id;

    public BillingModel() {
        this.id = 0;
        this.uuid = "";
        this.billing_date = 0;
        this.patient_id = 0;
        this.notes = "";
        this.total_amount = 0;
        this.name = "";
        this.billing_item_ids = new ArrayList<>();
        this.medical_record_id = 0;
    }


    public BillingModel(int id, String uuid, long billing_date, int patient_id, String notes, double total_amount, String name) {
        this.id = id;
        this.uuid = uuid;
        this.billing_date = billing_date;
        this.patient_id = patient_id;
        this.notes = notes;
        this.total_amount = total_amount;
        this.name = name;
        this.billing_item_ids = new ArrayList<>();

    }

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

    public long getBilling_date() {
        return billing_date;
    }

    public void setBilling_date(long billing_date) {
        this.billing_date = billing_date;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BillingItemModel> getBilling_item_ids() {
        return billing_item_ids;
    }

    public void setBilling_item_ids(List<BillingItemModel> billing_item_ids) {
        this.billing_item_ids = billing_item_ids;
    }

    public int getMedical_record_id() {
        return medical_record_id;
    }

    public void setMedical_record_id(int medical_record_id) {
        this.medical_record_id = medical_record_id;
    }
}
