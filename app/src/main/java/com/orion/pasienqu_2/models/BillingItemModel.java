package com.orion.pasienqu_2.models;

public class BillingItemModel {
    private int id;
    private String uuid;
    private int header_id;
    private String header_uuid;
    private String name;
    private double amount;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public BillingItemModel(int id, String uuid, int header_id, String header_uuid, String name, double amount) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.amount = amount;
        this.header_id = header_id;
        this.header_uuid = header_uuid;
    }

    public BillingItemModel() {
        this.id = 0;
        this.uuid = "";
        this.name = "";
        this.amount = 0;
        this.header_id = 0;
        this.header_uuid = "";
    }

    public int getHeader_id() {
        return header_id;
    }

    public void setHeader_id(int header_id) {
        this.header_id = header_id;
    }

    public String getHeader_uuid() {
        return header_uuid;
    }

    public void setHeader_uuid(String header_uuid) {
        this.header_uuid = header_uuid;
    }
}
