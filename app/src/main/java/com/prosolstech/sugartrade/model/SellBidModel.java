package com.prosolstech.sugartrade.model;

import java.util.Comparator;

public class SellBidModel {
    String id;

    public String getProduction_year() {
        return production_year;
    }

    public void setProduction_year(String production_year) {
        this.production_year = production_year;
    }

    String production_year;

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
    String isIntrested;
    String original_qty;
    String is_favorite;
    String date;
    String time;
    String isTimerRunning;
    String end_bid_time;
    String is_deleted;

    public String getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(String is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getEnd_bid_time() {
        return end_bid_time;
    }

    public void setEnd_bid_time(String end_bid_time) {
        this.end_bid_time = end_bid_time;
    }

    public String getIsTimerRunning() {
        return isTimerRunning;
    }

    public void setIsTimerRunning(String isTimerRunning) {
        this.isTimerRunning = isTimerRunning;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    String endtime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(String is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getOriginal_qty() {
        return original_qty;
    }

    public void setOriginal_qty(String original_qty) {
        this.original_qty = original_qty;
    }

    public String getIsIntrested() {
        return isIntrested;
    }

    public void setIsIntrested(String isIntrested) {
        this.isIntrested = isIntrested;
    }

    public String getPrice_per_qtl() {
        return price_per_qtl;
    }

    public void setPrice_per_qtl(String price_per_qtl) {
        this.price_per_qtl = price_per_qtl;
    }

    String price_per_qtl;


    //category
    public static Comparator<SellBidModel> SortByCategory = new Comparator<SellBidModel>()
    {

        public int compare(SellBidModel s1,SellBidModel s2)
        {
            String bookName1 = s1.getCategory().toUpperCase();
            String bookName2 = s2.getCategory().toUpperCase();

            //descending order
            return bookName2.compareTo(bookName1);
        }
    };
    //price low to high
    public static Comparator<SellBidModel> priceLowTohigh = new Comparator<SellBidModel>()
    {

        public int compare(SellBidModel s1,SellBidModel s2)
        {
            double price1 = Double.valueOf(s1.getPrice_per_qtl());
            double price2 = Double.valueOf(s2.getPrice_per_qtl());

            if (price1 == price2)
                return 0;
            else if (price1 > price2)
                return 1;
            else
                return -1;
        }
    };
    //price high to low
    public static Comparator<SellBidModel> priceHighTolow = new Comparator<SellBidModel>()
    {

        public int compare(SellBidModel s1,SellBidModel s2)
        {
            double price1 = Double.valueOf(s1.getPrice_per_qtl());
            double price2 = Double.valueOf(s2.getPrice_per_qtl());

            if (price1 == price2)
                return 0;
            else if (price1 < price2)
                return 1;
            else
                return -1;
        }
    };
}
