package com.example.cinema_project.model;

public class History {
    private int id;
    private String moviename;
    private String Date;
    private String Time;

    public History(int id,String moviename,String date,String Time) {
        this.id = id;
        this.moviename = moviename;
        this.Date = date;
        this.Time = Time;
    }

    public int getId() {
        return id;
    }

    public String getMoviename() {
        return moviename;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }
}
