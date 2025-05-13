package com.orion.pasienqu_2.models;

import java.util.List;

public class LovPatientSatuSehatModel {
    private String name;
    private String birth_date;
    private String city;
    private String patient_ihs_number;
    private boolean selected;

    public LovPatientSatuSehatModel(String name, String birth_date, String city, String patient_ihs_number) {
        this.name = name;
        this.birth_date = birth_date;
        this.city = city;
        this.patient_ihs_number = patient_ihs_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPatient_ihs_number() {
        return patient_ihs_number;
    }

    public void setPatient_ihs_number(String patient_ihs_number) {
        this.patient_ihs_number = patient_ihs_number;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
