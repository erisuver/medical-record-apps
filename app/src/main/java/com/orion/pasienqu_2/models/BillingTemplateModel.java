package com.orion.pasienqu_2.models;

public class BillingTemplateModel {
    private int id;
    private String uuid;
    private String name;
    private String items;
    private String mode;

    public BillingTemplateModel(int id, String uuid, String name, String items) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.items = items;
    }

    public BillingTemplateModel() {
        this.id = 0;
        this.uuid = "";
        this.name = "";
        this.items = "";
        this.mode = "";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
