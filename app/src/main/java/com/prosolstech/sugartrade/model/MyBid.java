package com.prosolstech.sugartrade.model;

import java.io.Serializable;

public class MyBid  implements Serializable{



    private String id;
    private String companyName;
    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
