package com.example.imp_project.model;

import android.net.Uri;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Diary implements Serializable {

    private Integer id;

    private String title;

    private String modeEmoji;

    private String uri;

    private String description;

    private String password;

    private LocalDateTime date;

    private String location;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Diary(Integer id, String title, String modeEmoji, String uri, String description, String password, LocalDateTime date,String location) {
        this.id =  id;
        this.title = title;
        this.modeEmoji = modeEmoji;
        this.uri = uri;
        this.description = description;
        this.password = password;
        this.date = date;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModeEmoji() {
        return modeEmoji;
    }

    public void setModeEmoji(String modeEmoji) {
        this.modeEmoji = modeEmoji;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
