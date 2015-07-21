package com.example.whiterabbit;


import java.util.ArrayList;

public class InvitationInfoActivity {
    private String title;
    private String time;
    private String date;
    private String location;
    private ArrayList<String> with;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWith(ArrayList<String> with) {
        this.with = with;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<String> getWith() {
        return with;
    }
}
