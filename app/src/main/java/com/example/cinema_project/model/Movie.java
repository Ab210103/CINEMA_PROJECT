package com.example.cinema_project.model;

public class Movie {

    private int moviecode;                 // Primary Key
    private String title;           // Movie title
    private String description;     // Movie description / synopsis
    private int length;          // Movie duration, e.g., "2h 30m"
    private String genre;           // Genre
    private String image;     // Poster image from DB (BLOB)
    private String banner;     // Banner image from DB (BLOB)
    private int user_id;            // Foreign Key to Staff

    // Constructor
    public Movie(String title, String description, int length, String genre,
                 String imagePoster, String imageBanner,int user_id) {
        this.title = title;
        this.description = description;
        this.length = length;
        this.genre = genre;
        this.image = imagePoster;
        this.banner = imageBanner;
        this.user_id = user_id;
    }
    public Movie(){}

    // Getters
    public int getId() { return moviecode; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getLength() { return length; }
    public String getGenre() { return genre; }
    public String getImagePoster() { return image; }
    public String getImageBanner() { return banner; }
    public int getStaffID() { return user_id; }

    // Setters (optional, if you want to update fields)
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLength(int length) { this.length = length; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setImagePoster(String imagePoster) { this.image = imagePoster; }
    public void setImageBanner(String imageBanner) { this.banner = imageBanner; }
    public void setStaffID(int staffID) { this.user_id = staffID; }

    public void setCode(int code) {
        this.moviecode = code;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "moviecode=" + moviecode +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", length='" + length + '\'' +
                ", genre='" + genre + '\'' +
                ", userid='" + user_id + '\'' +
                ", banner='" + banner + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

}
