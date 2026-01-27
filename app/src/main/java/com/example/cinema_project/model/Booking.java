package com.example.cinema_project.model;

public class Booking {
    private int BookingID;
    private String BookingDate;
    private String BookingTime;
    private int TicketQuantity;
    private String seat;
    private String PaymentType;
    private double TotalPrice;
    private Customer user;
    private Movie movie_code;


    public Customer getUser() {
        return user;
    }

    public Movie getMovie_code() {
        return movie_code;
    }

    // ✅ ADD THESE HELPERS
    public int getUserid() {
        return user != null ? user.getId() : 0;
    }

    public int getMcode() {
        return movie_code != null ? movie_code.getId() : 0;
    }

    public String getMovieTitle() {
        return movie_code != null ? movie_code.getTitle() : "Unknown Movie";
    }

    public String getSeat() {
        return seat;
    }

    public int getBId() {
        return BookingID;
    }

    public String getDate() {
        return BookingDate;
    }

    public String getTime() {
        return BookingTime;
    }

    public int getQuantity() {
        return TicketQuantity;
    }

    public String getTypepayment() {
        return PaymentType;
    }

    public double getTotal() {
        return TotalPrice;
    }
}
