package com.example.prm392_miniproject.models;

public class Account {
    private String username;
    private String email;
    private String password; // New field for password

    // Constructor
    public Account(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password; // Getter for password
    }

    // Convert Account to JSON string
    public String toJson() {
        return "{ \"username\": \"" + username + "\", \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
    }

    public static Account fromJson(String jsonString) {
        String username = jsonString.split("\"username\":")[1].split(",")[0].replace("\"", "").trim();
        String email = jsonString.split("\"email\":")[1].split(",")[0].replace("\"", "").trim();
        String password = jsonString.split("\"password\":")[1].replace("}", "").replace("\"", "").trim();
        return new Account(username, email, password);
    }
}