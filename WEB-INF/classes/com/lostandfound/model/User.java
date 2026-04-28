package com.lostandfound.model;

public class User {
    private int id;
    private String email;
    private String password;
    private String fullName;
    private String role; // 'USER' or 'ADMIN'
    private boolean verified;

    public User() {}

    public User(int id, String email, String password, String fullName, String role, boolean verified) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.verified = verified;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
