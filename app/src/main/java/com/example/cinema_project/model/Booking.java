package com.example.cinema_project.model;

public class Booking {
    private int BookingID;
    private String BookingDate;
    private String BookingTime;
    private int TicketQuantity;
    private String seat;
    private String PaymentType;
    private double TotalPrice;
    private int user_id;
    private int movie_code;


    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public int getMcode() {
        return movie_code;
    }

    public void setMcode(int mcode) {
        this.movie_code = mcode;
    }

    public void setBId(int id) {
        this.BookingID = id;
    }

    public int getBId() {
        return BookingID;
    }

    public void setDate(String date) {
        this.BookingDate = date;
    }

    public String getDate() {
        return BookingDate;
    }

    public void setTime(String time) {
        this.BookingTime = time;
    }

    public String getTime() {
        return BookingTime;
    }

    public int getQuantity() {
        return TicketQuantity;
    }

    public void setQuantity(int quantity) {
        this.TicketQuantity = quantity;
    }

    public String getTypepayment() {
        return PaymentType;
    }

    public void setTypepayment(String typepayment) {
        this.PaymentType = typepayment;
    }

    public double getTotal() {
        return TotalPrice;
    }

    public void setTotal(double total) {
        this.TotalPrice = total;
    }

    public void setUserid(int userid) {
        this.user_id = userid;
    }

    public int getUserid() {
        return user_id;
    }
}
