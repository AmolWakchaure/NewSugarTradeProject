package com.prosolstech.sugartrade.model;

public class MyBidRequestedList {



    private int id;
    private String requestedTime;
    private String availQty;
    private String season;
    private String grade;
    private String reqty;
    private String priceQty;
    private String isinteredstre;
    private String tenderPrice;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTenderPrice() {
        return tenderPrice;
    }

    public void setTenderPrice(String tenderPrice) {
        this.tenderPrice = tenderPrice;
    }

    public String getIsinteredstre() {
        return isinteredstre;
    }

    public void setIsinteredstre(String isinteredstre) {
        this.isinteredstre = isinteredstre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(String requestedTime) {
        this.requestedTime = requestedTime;
    }

    public String getAvailQty() {
        return availQty;
    }

    public void setAvailQty(String availQty) {
        this.availQty = availQty;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getReqty() {
        return reqty;
    }

    public void setReqty(String reqty) {
        this.reqty = reqty;
    }

    public String getPriceQty() {
        return priceQty;
    }

    public void setPriceQty(String priceQty) {
        this.priceQty = priceQty;
    }
}
