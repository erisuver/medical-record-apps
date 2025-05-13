package com.orion.pasienqu_2.models;

import java.io.Serializable;

public class FilterModel implements Serializable {
    private String name;
    private String value;
    private String tipe_filter;

    public FilterModel(String name, String value, String tipe_filter) {
        this.name = name;
        this.value = value;
        this.tipe_filter = tipe_filter;
    }

    public FilterModel() {
        this.name = "";
        this.value = "";
        this.tipe_filter = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTipe_filter() {
        return tipe_filter;
    }

    public void setTipe_filter(String tipe_filter) {
        this.tipe_filter = tipe_filter;
    }
}
