package com.orion.pasienqu_2.models;

public class SyncDownModel {
    int id;
    String model;
    long last_update;

    public SyncDownModel(int id, String model, long last_update) {
        this.id = id;
        this.model = model;
        this.last_update = last_update;
    }

    public SyncDownModel() {
        this.id = 0;
        this.model = "";
        this.last_update = 0;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getLast_update() {
        return last_update;
    }

    public void setLast_update(long last_update) {
        this.last_update = last_update;
    }
}
