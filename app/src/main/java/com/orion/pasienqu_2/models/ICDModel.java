package com.orion.pasienqu_2.models;

public class ICDModel {
    private int id;
    private String uuid;
    private String code;
    private String name;
    private String remarks;

    public ICDModel(int id, String uuid, String code, String name, String remarks) {
        this.id = id;
        this.uuid = uuid;
        this.code = code;
        this.name = name;
        this.remarks = remarks;
    }

    public ICDModel() {
        this.id = 0;
        this.uuid = "";
        this.code = "";
        this.name = "";
        this.remarks = "";
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
