package com.cattle_care.cattlecare.model;

public class UserReport {
    private String location;
    private String number;
    private String description;
    private String submitDateTime;
    private String imageURL;

    public UserReport(){}
    public UserReport(String location,String number,String description,String submitDateTime,String imageURL){
        this.description=description;
        this.location=location;
        this.number=number;
        this.imageURL=imageURL;
        this.submitDateTime=submitDateTime;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSubmitDateTime() {
        return submitDateTime;
    }

    public void setSubmitDateTime(String submitDateTime) {
        this.submitDateTime = submitDateTime;
    }
}
