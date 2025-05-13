package com.orion.pasienqu_2.models;

public class SubaccountModel {
    private int id;
    private String uuid;
    private String name;
    private String login;
    private String password;

    public SubaccountModel(int id, String uuid, String name, String login, String password) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public SubaccountModel() {
        this.id = 0;
        this.uuid = "";
        this.name = "";
        this.login = "";
        this.password = "";
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
