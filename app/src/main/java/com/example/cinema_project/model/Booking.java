package com.example.cinema_project.model;

public class Booking {
    private int Bid;
    private String date;
    private String time;
    private int quantity;
    private String typepayment;
    private double total;
    private int userid;
    private int mcode;

    public int getMcode() {
        return mcode;
    }

    public void setMcode(int mcode) {
        this.mcode = mcode;
    }

    public void setBId(int id) {
        this.Bid = id;
    }

    public int getBId() {
        return Bid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTypepayment() {
        return typepayment;
    }

    public void setTypepayment(String typepayment) {
        this.typepayment = typepayment;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }
}
