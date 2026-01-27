package com.example.cinema_project.model;

public class History {
    private int BookingID;
    private String title;
    private String BookingDate;
    private String BookingTime;

    public History(int bookingId, String movieTitle, String date, String time) {
        this.BookingID = bookingId;
        this.title = movieTitle;
        this.BookingDate = date;
        this.BookingTime = time;
    }

    public int getBookingId() {
        return BookingID;
    }

    public void setBookingDate(String bookingDate) {
        BookingDate = bookingDate;
    }

    public String getMovieTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return BookingDate;
    }

    public void setBookingID(int bookingID) {
        BookingID = bookingID;
    }

    public String getTime() {
        return BookingTime;
    }

    public String getBookingTime() {
        return BookingTime;
    }
}
