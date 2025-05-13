package com.orion.pasienqu_2.models;

import android.content.Context;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientModel {
    private int id;
    private String uuid;
    private String patient_id;
    private String name;
    private String first_name;
    private String surname;
    private long register_date;
    private int gender_id;
    private long date_of_birth;
    private int age;
    private int month;
    private String identification_no;
    private String email;
    private String occupation;
    private String contact_no;
    private String address_street_1;
    private String address_street_2;
    private String patient_remark_1;
    private String patient_remark_2;
    private String gender;
    private List<RecordModel> medical_record_ids;
    private int patient_type_id;
    private String description;
    private String patient_ihs;

    public PatientModel(int id, String uuid, String patient_id, String name, String first_name, String surname,
                        long register_date, int gender_id, long date_of_birth, int age, int month,
                        String identification_no, String email, String occupation, String contact_no,
                        String address_street_1, String address_street_2, String patient_remark_1, String patient_remark_2, int patient_type_id, String description) {
        this.id = id;
        this.uuid = uuid;
        this.patient_id = patient_id;
        this.name = name;
        this.first_name = first_name;
        this.surname = surname;
        this.register_date = register_date;
        this.gender_id = gender_id;
        this.date_of_birth = date_of_birth;
        this.age = age;
        this.month = month;
        this.identification_no = identification_no;
        this.email = email;
        this.occupation = occupation;
        this.contact_no = contact_no;
        this.address_street_1 = address_street_1;
        this.address_street_2 = address_street_2;
        this.patient_remark_1 = patient_remark_1;
        this.patient_remark_2 = patient_remark_2;
        this.medical_record_ids = new ArrayList<>();
        this.patient_type_id = patient_type_id;
        this.description = description;
    }


    public PatientModel() {
        this.id = 0;
        this.uuid = "";
        this.patient_id = "";
        this.name = "";
        this.first_name = "";
        this.surname = "";
        this.register_date = 0;
        this.gender_id = 0;
        this.date_of_birth = 0;
        this.age = 0;
        this.month = 0;
        this.identification_no = "";
        this.email = "";
        this.occupation = "";
        this.contact_no = "";
        this.address_street_1 = "";
        this.address_street_2 = "";
        this.patient_remark_1 = "";
        this.patient_remark_2 = "";
        this.medical_record_ids = new ArrayList<>();
        this.patient_type_id = 0;
        this.description = "";
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

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getRegister_date() {
        return register_date;
    }

    public void setRegister_date(long register_date) {
        this.register_date = register_date;
    }

    public int getGender_id() {
        return gender_id;
    }

    public void setGender_id(int gender_id) {
        this.gender_id = gender_id;
    }

    public long getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(long date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getIdentification_no() {
        return identification_no;
    }

    public void setIdentification_no(String identification_no) {
        this.identification_no = identification_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getAddress_street_1() {
        return address_street_1;
    }

    public void setAddress_street_1(String address_street_1) {
        this.address_street_1 = address_street_1;
    }

    public String getAddress_street_2() {
        return address_street_2;
    }

    public void setAddress_street_2(String address_street_2) {
        this.address_street_2 = address_street_2;
    }

    public String getPatient_remark_1() {
        return patient_remark_1;
    }

    public void setPatient_remark_1(String patient_remark_1) {
        this.patient_remark_1 = patient_remark_1;
    }

    public String getPatient_remark_2() {
        return patient_remark_2;
    }

    public void setPatient_remark_2(String patient_remark_2) {
        this.patient_remark_2 = patient_remark_2;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGenderStr(Context context) {
        if (this.gender_id == 1) {
            return (context.getString(R.string.gender_male));
        } else if (this.gender_id == 2) {
            return (context.getString(R.string.gender_female));
        }
        return null;
    }

    public String getGenderInitialStr(Context context){
        if (this.gender_id == 1) {
            return (context.getString(R.string.gender_male_Initial));
        } else if (this.gender_id == 2) {
            return (context.getString(R.string.gender_female_Initial));
        }
        return null;
    }

    public String getPatientNameId(){
        return  "("+ this.patient_id+") " +this.first_name+" "+ this.surname;
    }

    public String getPatientNameGenderAge(Context context){
        return  this.first_name+" "+ this.surname+" ("+ this.getGenderStr(context)+", "+this.age+")";
    }
    public String getIDPatientNameGenderAge(Context context){
        return  this.patient_id+" - "+this.first_name+" "+ this.surname+" ("+ this.getGenderStr(context)+", "+getAge(this.getDate_of_birth())+")";
    }

    public List<RecordModel> getMedical_record_ids() {
        return medical_record_ids;
    }

    public void setMedical_record_ids(List<RecordModel> medical_record_ids) {
        this.medical_record_ids = medical_record_ids;
    }

    public int getPatient_type_id() {
        return patient_type_id;
    }

    public void setPatient_type_id(int patient_type_id) {
        this.patient_type_id = patient_type_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getpatientTypeStr(Context context) {
        if (this.patient_type_id == 1) {
            return (context.getString(R.string.patient_type_general));
        }else if (this.patient_type_id == 2) {
            return (context.getString(R.string.patient_type_bpjs));
        }else if (this.patient_type_id == 3) {
            return (context.getString(R.string.patient_type_inhealth));
        }else if (this.patient_type_id == 4) {
            return (context.getString(R.string.patient_type_insurance));
        }
        return null;
    }

    public String getAge(long birthDate){
        long LDateNow = Global.serverNowLong();
        Date dateNow, dateBirth;
        dateBirth = new Date(Integer.parseInt(Global.getTahun(birthDate)), Integer.parseInt(Global.getBulan(birthDate)),
                Integer.parseInt(Global.getHari(birthDate)));
        dateNow = new Date(Integer.parseInt(Global.getTahun(LDateNow)), Integer.parseInt(Global.getBulan(LDateNow)),
                Integer.parseInt(Global.getHari(LDateNow)));

        return String.valueOf(Global.getUmur(dateNow, dateBirth, true));
    }

    public String getPatient_ihs() {
        return patient_ihs;
    }

    public void setPatient_ihs(String patient_ihs) {
        this.patient_ihs = patient_ihs;
    }
}