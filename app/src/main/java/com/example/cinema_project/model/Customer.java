package com.example.cinema_project.model;

public class Customer {
    private int id;
    private String email, username, password, token, role, gender, profession, phone,lease;

    public Customer() { }

    public Customer(int id, String username, String email, String password,
                    String gender, String profession, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.profession = profession;
        this.phone = phoneNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public String getPhoneNumber() { return phone; }
    public void setPhoneNumber(String phoneNumber) { this.phone = phoneNumber; }
}
