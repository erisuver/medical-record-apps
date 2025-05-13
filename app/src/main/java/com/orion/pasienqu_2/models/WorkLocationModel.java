package com.orion.pasienqu_2.models;

public class WorkLocationModel {
    private int id;
    private String uuid;
    private String name;
    private String location;
    private String remarks;
    private String organization_ihs;
    private String location_ihs;
    private String client_id;
    private String client_secret;
    private long last_generate_token;
    private String token;

    public WorkLocationModel(int id, String uuid, String name, String location, String remarks) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.remarks = remarks;
    }

    public WorkLocationModel() {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.remarks = remarks;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getOrganization_ihs() {
        return organization_ihs;
    }

    public void setOrganization_ihs(String organization_ihs) {
        this.organization_ihs = organization_ihs;
    }

    public String getLocation_ihs() {
        return location_ihs;
    }

    public void setLocation_ihs(String location_ihs) {
        this.location_ihs = location_ihs;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public long getLast_generate_token() {
        return last_generate_token;
    }

    public void setLast_generate_token(long last_generate_token) {
        this.last_generate_token = last_generate_token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
