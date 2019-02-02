package com.prosolstech.sugartrade.model;

public class SeasonModel {

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeason_id() {
        return season_id;
    }

    public void setSeason_id(String season_id) {
        this.season_id = season_id;
    }

    public String getSeason_year() {
        return season_year;
    }

    public void setSeason_year(String season_year) {
        this.season_year = season_year;
    }

    String season_id;
    String season_year;
}
