package com.prosolstech.sugartrade.model;

public class BuyBidModel {
    String id;
    String current_req_quantity;

    public String getCurrent_req_quantity() {
        return current_req_quantity;
    }

    public void setCurrent_req_quantity(String current_req_quantity) {
        this.current_req_quantity = current_req_quantity;
    }

    public String getAcquired_quantity() {
        return acquired_quantity;
    }

    public void setAcquired_quantity(String acquired_quantity) {
        this.acquired_quantity = acquired_quantity;
    }

    String acquired_quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValidity_time() {
        return validity_time;
    }

    public void setValidity_time(String validity_time) {
        this.validity_time = validity_time;
    }

    public String getBid_start_time() {
        return bid_start_time;
    }

    public void setBid_start_time(String bid_start_time) {
        this.bid_start_time = bid_start_time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getClaimed() {
        return claimed;
    }

    public void setClaimed(String claimed) {
        this.claimed = claimed;
    }

    public String getAvailable_qty() {
        return available_qty;
    }

    public void setAvailable_qty(String available_qty) {
        this.available_qty = available_qty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    String validity_time;
    String bid_start_time;
    String category;
    String claimed;
    String available_qty;
    String type;
    String company_name;
    String is_favorite;
    String date;
    String isTimerTuuning;
    String endTime;
    String bidEndTime;
    String is_delelted;


    public String getIs_delelted() {
        return is_delelted;
    }

    public void setIs_delelted(String is_delelted) {
        this.is_delelted = is_delelted;
    }

    public String getBidEndTime() {
        return bidEndTime;
    }

    public void setBidEndTime(String bidEndTime) {
        this.bidEndTime = bidEndTime;
    }

    public String getIsTimerTuuning() {
        return isTimerTuuning;
    }

    public void setIsTimerTuuning(String isTimerTuuning) {
        this.isTimerTuuning = isTimerTuuning;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(String is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getPrice_per_qtl() {
        return price_per_qtl;
    }

    public void setPrice_per_qtl(String price_per_qtl) {
        this.price_per_qtl = price_per_qtl;
    }

    String price_per_qtl;

}
