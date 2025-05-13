package com.orion.pasienqu_2.models;


public class LoginModel {
    int id;
    String email;
    String password;
    String oldPassword;
    String confirmPassword;
    String newPassword;
    String name;
    String billingPeriod;
    String usePin;
    String pinProtection;
//    OdooUser odooUser;    //erik tutup odoo 040325

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public String getUsePin() {
        return usePin;
    }

    public void setUsePin(String usePin) {
        this.usePin = usePin;
    }

    public String getPinProtection() {
        return pinProtection;
    }

    public void setPinProtection(String pinProtection) {
        this.pinProtection = pinProtection;
    }

    public LoginModel(int id, String email, String password, String oldPassword, String confirmPassword, String newPassword, String name, String billingPeriod, String usePin, String pinProtection) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.oldPassword = oldPassword;
        this.confirmPassword = confirmPassword;
        this.newPassword = newPassword;
        this.name = name;
        this.billingPeriod = billingPeriod;
        this.usePin = usePin;
        this.pinProtection = pinProtection;
//        this.odooUser = new OdooUser();   //erik tutup odoo 040325
    }

    public LoginModel() {
        this.id = 0;
        this.email = "";
        this.password = "";
        this.oldPassword = "";
        this.confirmPassword = "";
        this.newPassword = "";
        this.name = "";
        this.billingPeriod = "";
        this.usePin = "";
        this.pinProtection = "";

//        this.odooUser = new OdooUser();   //erik tutup odoo 040325
    }



/*//erik tutup odoo 040325
    public OdooUser getOdooUser() {
        return odooUser;
    }

    public void setOdooUser(OdooUser odooUser) {
        this.odooUser = odooUser;
    }*/
}
