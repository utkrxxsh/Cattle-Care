package com.cattle_care.cattlecare.model;

public class DriverCompletedReport {
    private String location;
    private String number;
    private String description;
    private String submitDateTime;
    private String imageURL;
    private String latitude;
    private String longitude;
    private String username;
    private String userphone;

    public DriverCompletedReport(){}

    public DriverCompletedReport(String location, String number, String description, String submitDateTime, String imageURL, String latitude, String longitude, String username, String userphone) {
        this.location = location;
        this.number = number;
        this.description = description;
        this.submitDateTime = submitDateTime;
        this.imageURL = imageURL;
        this.latitude = latitude;
        this.longitude = longitude;
        this.username = username;
        this.userphone = userphone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmitDateTime() {
        return submitDateTime;
    }

    public void setSubmitDateTime(String submitDateTime) {
        this.submitDateTime = submitDateTime;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }
}
