package com.orion.pasienqu_2.models;

import java.util.List;

public class MedicalSatuSehatModel {
    private String record_uuid;
    private long record_date;
    private double weight;
    private double temperature;
    private int blood_pressure_systolic;
    private int blood_pressure_diastolic;
    private String anamnesa;
    private String physical_exam;
    private String diagnosa;
    private String therapy;
    private String id_encounter;
    private String location;
    private String organization_ihs;
    private String location_ihs;
    private String patient_codename;
    private String identification_no;
    private String patient_ihs;
    private String patient_uuid;
    private List<RecordDiagnosaModel> icd_ids;
    private boolean selected;
    private String client_id;
    private String client_secret;
    private String token;
    private String isPosteded;

    public MedicalSatuSehatModel(String record_uuid, long record_date, double weight, double temperature, int blood_pressure_systolic, int blood_pressure_diastolic, String anamnesa, String physical_exam, String diagnosa, String therapy, String id_encounter, String location, String organization_ihs, String location_ihs, String patient_codename, String identification_no, String patient_ihs, String patient_uuid, String client_id, String client_secret, String token, String isPosteded) {
        this.record_uuid = record_uuid;
        this.record_date = record_date;
        this.weight = weight;
        this.temperature = temperature;
        this.blood_pressure_systolic = blood_pressure_systolic;
        this.blood_pressure_diastolic = blood_pressure_diastolic;
        this.anamnesa = anamnesa;
        this.physical_exam = physical_exam;
        this.diagnosa = diagnosa;
        this.therapy = therapy;
        this.id_encounter = id_encounter;
        this.location = location;
        this.organization_ihs = organization_ihs;
        this.location_ihs = location_ihs;
        this.patient_codename = patient_codename;
        this.identification_no = identification_no;
        this.patient_ihs = patient_ihs;
        this.patient_uuid = patient_uuid;
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.token = token;
        this.isPosteded = isPosteded;
    }

    public String getRecord_uuid() {
        return record_uuid;
    }

    public void setRecord_uuid(String record_uuid) {
        this.record_uuid = record_uuid;
    }

    public long getRecord_date() {
        return record_date;
    }

    public void setRecord_date(long record_date) {
        this.record_date = record_date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getBlood_pressure_systolic() {
        return blood_pressure_systolic;
    }

    public void setBlood_pressure_systolic(int blood_pressure_systolic) {
        this.blood_pressure_systolic = blood_pressure_systolic;
    }

    public int getBlood_pressure_diastolic() {
        return blood_pressure_diastolic;
    }

    public void setBlood_pressure_diastolic(int blood_pressure_diastolic) {
        this.blood_pressure_diastolic = blood_pressure_diastolic;
    }

    public String getAnamnesa() {
        return anamnesa;
    }

    public void setAnamnesa(String anamnesa) {
        this.anamnesa = anamnesa;
    }

    public String getPhysical_exam() {
        return physical_exam;
    }

    public void setPhysical_exam(String physical_exam) {
        this.physical_exam = physical_exam;
    }

    public String getDiagnosa() {
        return diagnosa;
    }

    public void setDiagnosa(String diagnosa) {
        this.diagnosa = diagnosa;
    }

    public String getTherapy() {
        return therapy;
    }

    public void setTherapy(String therapy) {
        this.therapy = therapy;
    }

    public String getId_encounter() {
        return id_encounter;
    }

    public void setId_encounter(String id_encounter) {
        this.id_encounter = id_encounter;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganization_ihs() {
        return organization_ihs;
    }

    public void setOrganization_ihs(String organization_ihs) {
        this.organization_ihs = organization_ihs;
    }

    public String getLocation_ihs() {
        return location_ihs;
    }

    public void setLocation_ihs(String location_ihs) {
        this.location_ihs = location_ihs;
    }

    public String getPatient_codename() {
        return patient_codename;
    }

    public void setPatient_codename(String patient_codename) {
        this.patient_codename = patient_codename;
    }

    public String getIdentification_no() {
        return identification_no;
    }

    public void setIdentification_no(String identification_no) {
        this.identification_no = identification_no;
    }

    public String getPatient_ihs() {
        return patient_ihs;
    }

    public void setPatient_ihs(String patient_ihs) {
        this.patient_ihs = patient_ihs;
    }

    public String getPatient_uuid() {
        return patient_uuid;
    }

    public void setPatient_uuid(String patient_uuid) {
        this.patient_uuid = patient_uuid;
    }

    public List<RecordDiagnosaModel> getIcd_ids() {
        return icd_ids;
    }

    public void setIcd_ids(List<RecordDiagnosaModel> icd_ids) {
        this.icd_ids = icd_ids;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getisPosteded() {
        return isPosteded;
    }

    public void setisPosteded(String isPosteded) {
        this.isPosteded = isPosteded;
    }
}
