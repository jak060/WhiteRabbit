package com.example.whiterabbit;


import java.util.ArrayList;

// This class temporarily saves the information of the invitation
public class InvitationInfoActivity {
    private String title; // To hold the title
    private String time; // To hold the time
    private String date; // To hold the date
    private String location; // To hold the location
    private ArrayList<String> with; // To hold the invitees
    private int state; // To hold the state of the invitation.
                          // It can be accepted, declined, or inProgress

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

    public void setState(int state) { this.state = state; }

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

    public int getState() { return state; }
}
