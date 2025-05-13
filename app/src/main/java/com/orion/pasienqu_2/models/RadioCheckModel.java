package com.orion.pasienqu_2.models;

public class RadioCheckModel {
    private String position;
    private String value;
    private String label;

    public RadioCheckModel(String position, String value, String label) {
        this.position = position;
        this.value = value;
        this.label = label;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
}
