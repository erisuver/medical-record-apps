package com.orion.pasienqu_2.models;

public class PractitionerModel {
    private int id;
    private int company_id;
    private String nomor_ihs;

    public PractitionerModel(int id, int company_id, String nomor_ihs) {
        this.id = id;
        this.company_id = company_id;
        this.nomor_ihs = nomor_ihs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getNomor_ihs() {
        return nomor_ihs;
    }

    public void setNomor_ihs(String nomor_ihs) {
        this.nomor_ihs = nomor_ihs;
    }
}
