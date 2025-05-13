package com.orion.pasienqu_2.models;

public class CountryModel {
    private int id;
    private String uuid;
    private String name;
    private int flag;
    private int checked;

    public CountryModel(int id, String uuid, String name, int flag, int checked) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.flag = flag;
        this.checked = checked;
    }

    public CountryModel() {
        this.id = id;
        this.uuid = "";
        this.name = "";
        this.flag = 0;
        this.checked = 0;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
