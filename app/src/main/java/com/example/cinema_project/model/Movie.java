package com.example.cinema_project.model;

public class Movie {

    private int id;                 // Primary Key
    private String title;           // Movie title
    private String description;     // Movie description / synopsis
    private String length;          // Movie duration, e.g., "2h 30m"
    private String genre;           // Genre
    private byte[] imagePoster;     // Poster image from DB (BLOB)
    private byte[] imageBanner;     // Banner image from DB (BLOB)
    private int staffID;            // Foreign Key to Staff

    // Constructor
    public Movie(int id, String title, String description, String length, String genre,
                 byte[] imagePoster, byte[] imageBanner, int staffID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.length = length;
        this.genre = genre;
        this.imagePoster = imagePoster;
        this.imageBanner = imageBanner;
        this.staffID = staffID;
    }
    public Movie(){}

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLength() { return length; }
    public String getGenre() { return genre; }
    public byte[] getImagePoster() { return imagePoster; }
    public byte[] getImageBanner() { return imageBanner; }
    public int getStaffID() { return staffID; }

    // Setters (optional, if you want to update fields)
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLength(String length) { this.length = length; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setImagePoster(byte[] imagePoster) { this.imagePoster = imagePoster; }
    public void setImageBanner(byte[] imageBanner) { this.imageBanner = imageBanner; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

}
