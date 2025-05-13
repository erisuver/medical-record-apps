package com.orion.pasienqu_2.models;

public class SatuSehatTokenModel {
    private int id;
    private String token;
    private long last_update;

    public SatuSehatTokenModel(int id, String token, long last_update) {
        this.id = id;
        this.token = token;
        this.last_update = last_update;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getLast_update() {
        return last_update;
    }

    public void setLast_update(long last_update) {
        this.last_update = last_update;
    }
}
