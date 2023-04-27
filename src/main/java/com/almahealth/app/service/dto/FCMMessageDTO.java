package com.almahealth.app.service.dto;

public class FCMMessageDTO {

    private String title;

    private String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FCMMessageDTO title(String title) {
        this.setTitle(title);
        return this;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public FCMMessageDTO date(String date) {
        this.setDate(date);
        return this;
    }
}
