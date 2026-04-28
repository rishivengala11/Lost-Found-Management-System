package com.lostandfound.model;

import java.sql.Timestamp;

/**
 * Model class representing a Lost Item record.
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class LostItem {

    private int id;
    private int userId;
    private String itemName;
    private String category;
    private String description;
    private String dateLost;
    private String location;
    private String reporterName;
    private String contact;
    private String status;
    private String verificationStatus; // PENDING, APPROVED, REJECTED
    private boolean claimRequested;
    private Timestamp createdAt;

    // Default constructor
    public LostItem() {
    }

    // Parameterized constructor
    public LostItem(int userId, String itemName, String category, String description,
            String dateLost, String location, String reporterName,
            String contact) {
        this.userId = userId;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.dateLost = dateLost;
        this.location = location;
        this.reporterName = reporterName;
        this.contact = contact;
        this.status = "Lost";
        this.claimRequested = false;
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

    public String getDateLost() {
        return dateLost;
    }

    public void setDateLost(String dateLost) {
        this.dateLost = dateLost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
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

    public boolean isClaimRequested() {
        return claimRequested;
    }

    public void setClaimRequested(boolean claimRequested) {
        this.claimRequested = claimRequested;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LostItem{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", dateLost='" + dateLost + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
