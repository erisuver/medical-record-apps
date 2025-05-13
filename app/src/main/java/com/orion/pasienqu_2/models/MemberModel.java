package com.orion.pasienqu_2.models;

public class MemberModel {
    private int id;
    private String uuid;
    private String email;
    private String company_name;
    private String name;
    private String street;
    private String street2;
    private String phone;
    private String zip;

    public MemberModel(int id, String uuid, String email, String company_name, String name, String street, String street2, String phone, String zip) {
        this.id = id;
        this.uuid = uuid;
        this.email = email;
        this.company_name = company_name;
        this.name = name;
        this.street = street;
        this.street2 = street2;
        this.phone = phone;
        this.zip = zip;
    }

    public MemberModel() {
        this.id = 0;
        this.uuid = "";
        this.email = "";
        this.company_name = "";
        this.name = "";
        this.street = "";
        this.street2 = "";
        this.phone = "";
        this.zip = "";
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
