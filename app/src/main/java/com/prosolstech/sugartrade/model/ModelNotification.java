package com.prosolstech.sugartrade.model;

public class ModelNotification {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    private String title;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String body;
}
