package com.orion.pasienqu_2.models;

public class LoginCompanyModel {
    int id;
    int country_id;
    String name;
    String product;
    String zip;
    String member_type;
    String expired_date;
    String register_date;
    String billing_period;
    String street;
    String street2;
    String phone;
    String latest_activation_date;
    String grace_period_date;
    String autogenerate_patient_id;
    String email;
    boolean is_trial;
    String trial_end_date;
    String purchase_token;
    String subsription_date;

    public LoginCompanyModel() {
        this.id = 0;
        this.country_id = 0;
        this.name = "";
        this.product = "";
        this.zip = "";
        this.member_type = "";
        this.expired_date = "";
        this.register_date = "";
        this.billing_period = "";
        this.street = "";
        this.street2 = "";
        this.phone = "";
        this.latest_activation_date = "";
        this.grace_period_date = "";
        this.autogenerate_patient_id = "";
        this.email = "";
        this.is_trial = true;
        this.trial_end_date = "";
        this.purchase_token = "";
        this.subsription_date = "";
    }

    public LoginCompanyModel(int id, int country_id, String name, String product, String zip, String member_type,
                             String expired_date, String register_date, String billing_period, String street,
                             String street2, String phone, String latest_activation_date, String grace_period_date,
                             String autogenerate_patient_id, String email, String trial_end_date, String purchase_token) {
        this.id = id;
        this.country_id = country_id;
        this.name = name;
        this.product = product;
        this.zip = zip;
        this.member_type = member_type;
        this.expired_date = expired_date;
        this.register_date = register_date;
        this.billing_period = billing_period;
        this.street = street;
        this.street2 = street2;
        this.phone = phone;
        this.latest_activation_date = latest_activation_date;
        this.grace_period_date = grace_period_date;
        this.autogenerate_patient_id = autogenerate_patient_id;
        this.email = email;
        this.trial_end_date = trial_end_date;
        this.purchase_token = purchase_token;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getMember_type() {
        return member_type;
    }

    public void setMember_type(String member_type) {
        this.member_type = member_type;
    }

    public String getExpired_date() {
        return expired_date;
    }

    public void setExpired_date(String expired_date) {
        this.expired_date = expired_date;
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public String getBilling_period() {
        return billing_period;
    }

    public void setBilling_period(String billing_period) {
        this.billing_period = billing_period;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLatest_activation_date() {
        return latest_activation_date;
    }

    public void setLatest_activation_date(String latest_activation_date) {
        this.latest_activation_date = latest_activation_date;
    }

    public String getGrace_period_date() {
        return grace_period_date;
    }

    public void setGrace_period_date(String grace_period_date) {
        this.grace_period_date = grace_period_date;
    }

    public String getAutogenerate_patient_id() {
        return autogenerate_patient_id;
    }

    public void setAutogenerate_patient_id(String autogenerate_patient_id) {
        this.autogenerate_patient_id = autogenerate_patient_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIs_trial() {
        return is_trial;
    }

    public void setIs_trial(boolean is_trial) {
        this.is_trial = is_trial;
    }

    public String getTrial_end_date() {
        return trial_end_date;
    }

    public void setTrial_end_date(String trial_end_date) {
        this.trial_end_date = trial_end_date;
    }

    public String getPurchase_token() {
        return purchase_token;
    }

    public void setPurchase_token(String purchase_token) {
        this.purchase_token = purchase_token;
    }

    public String getSubsription_date() {
        return subsription_date;
    }

    public void setSubsription_date(String subsription_date) {
        this.subsription_date = subsription_date;
    }
}
