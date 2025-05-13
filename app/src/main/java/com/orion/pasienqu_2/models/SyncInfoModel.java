package com.orion.pasienqu_2.models;

public class SyncInfoModel {
    int id;
    String model;
    byte[] content;
    String command;
    String uuidModel;//untuk keperluan edit dan delete

    public SyncInfoModel(int id, String model, byte[] content, String command, String uuidModel) {
        this.id = id;
        this.model = model;
        this.content = content;
        this.command = command;
        this.uuidModel = uuidModel;
    }

    public SyncInfoModel() {
        this.id = 0;
        this.model = "";
        this.command = "";
        this.uuidModel = "";
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUuidModel() {
        return uuidModel;
    }

    public void setUuidModel(String uuidModel) {
        this.uuidModel = uuidModel;
    }
}
