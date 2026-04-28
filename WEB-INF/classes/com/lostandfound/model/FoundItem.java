package com.lostandfound.model;

import java.sql.Timestamp;

/**
 * Model class representing a Found Item record.
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class FoundItem {

    private int id;
    private int userId;
    private String itemName;
    private String category;
    private String description;
    private String dateFound;
    private String location;
    private String finderName;
    private String contact;
    private String status;
    private String verificationStatus; // PENDING, APPROVED, REJECTED
    private boolean returnRequested;
    private Timestamp createdAt;

    // Default constructor
    public FoundItem() {
    }

    // Parameterized constructor
    public FoundItem(int userId, String itemName, String category, String description,
            String dateFound, String location, String finderName,
            String contact) {
        this.userId = userId;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.dateFound = dateFound;
        this.location = location;
        this.finderName = finderName;
        this.contact = contact;
        this.status = "Found";
        this.verificationStatus = "PENDING";
        this.returnRequested = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateFound() {
        return dateFound;
    }

    public void setDateFound(String dateFound) {
        this.dateFound = dateFound;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFinderName() {
        return finderName;
    }

    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public boolean isReturnRequested() {
        return returnRequested;
    }

    public void setReturnRequested(boolean returnRequested) {
        this.returnRequested = returnRequested;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "FoundItem{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", dateFound='" + dateFound + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
