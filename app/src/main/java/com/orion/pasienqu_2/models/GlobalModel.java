package com.orion.pasienqu_2.models;

public class GlobalModel {
    private String uuid;
    private boolean active;

    public GlobalModel(String uuid, boolean active) {
        this.uuid = uuid;
        this.active = active;
    }

    public GlobalModel() {
        this.uuid = "";
        this.active = false;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
