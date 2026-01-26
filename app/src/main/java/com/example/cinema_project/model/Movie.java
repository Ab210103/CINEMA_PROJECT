package com.example.cinema_project.model;

public class Movie {

    private int code;                 // Primary Key
    private String title;           // Movie title
    private String description;     // Movie description / synopsis
    private String length;          // Movie duration, e.g., "2h 30m"
    private String genre;           // Genre
    private String imagePoster;     // Poster image from DB (BLOB)
    private String imageBanner;     // Banner image from DB (BLOB)
    private int staffID;            // Foreign Key to Staff

    // Constructor
    public Movie(String title, String description, String length, String genre,
                 String imagePoster, String imageBanner) {
        this.title = title;
        this.description = description;
        this.length = length;
        this.genre = genre;
        this.imagePoster = imagePoster;
        this.imageBanner = imageBanner;
        //this.staffID = staffID;
    }
    public Movie(){}

    // Getters
    public int getId() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLength() { return length; }
    public String getGenre() { return genre; }
    public String getImagePoster() { return imagePoster; }
    public String getImageBanner() { return imageBanner; }
    public int getStaffID() { return staffID; }

    // Setters (optional, if you want to update fields)
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLength(String length) { this.length = length; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setImagePoster(String imagePoster) { this.imagePoster = imagePoster; }
    public void setImageBanner(String imageBanner) { this.imageBanner = imageBanner; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "moviecode=" + code +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", length='" + length + '\'' +
                ", genre='" + genre + '\'' +
                ", userid='" + staffID + '\'' +
                ", banner='" + imageBanner + '\'' +
                ", image='" + imagePoster + '\'' +
                '}';
    }

}
