package com.clinic.dto;

public class LoginResponse {
    private String username;
    private String role;
    private String fullname;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String username, String role, String fullname) {
        this.username = username;
        this.role = role;
        this.fullname = fullname;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
