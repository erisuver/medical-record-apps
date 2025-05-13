package com.orion.pasienqu_2.models;

import java.io.Serializable;

public class LovCheckModel implements Serializable {
    private String value;
    private String label;
    private boolean state;

    public LovCheckModel(String value, String label, boolean state) {
        this.value = value;
        this.label = label;
        this.state = state;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
