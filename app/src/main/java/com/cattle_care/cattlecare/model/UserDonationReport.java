package com.cattle_care.cattlecare.model;

public class UserDonationReport {

    private String amount;
    private String dateTime;
    private String name;

    public UserDonationReport(){}

    public UserDonationReport(String amount,String dateTime,String name){
        this.amount=amount;
        this.dateTime=dateTime;
        this.name=name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
