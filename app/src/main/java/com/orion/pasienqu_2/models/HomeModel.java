package com.orion.pasienqu_2.models;

public class HomeModel {
    String type;
    double total;
    double this_month;

    public HomeModel(String type, double total, double this_month) {
        this.type = type;
        this.total = total;
        this.this_month = this_month;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getThis_month() {
        return this_month;
    }

    public void setThis_month(double this_month) {
        this.this_month = this_month;
    }
}
