package com.orion.pasienqu_2.models;

import android.content.Context;

import com.orion.pasienqu_2.R;

public class GenderModel {
    private int id;
    private String uuid;
    private String name;

    public GenderModel(int id, String uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    public GenderModel() {
        this.id = 0;
        this.uuid = "";
        this.name = "";
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

    public String getName(Context context) {
        if (this.id == 1) {
            return (context.getString(R.string.gender_male));
        } else if (this.id == 2) {
            return (context.getString(R.string.gender_female));
        }
        return name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
