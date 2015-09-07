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

    private double latitude; // To hold the latitude of the user-defined location
    private double longitude; // To hold the longitude of the user-defined location

    private String objectId; // To hold the objectId of the event

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

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public void setObjectId(String objectId) { this.objectId = objectId; }

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

    public String getLocationShort() {
        String[] locationArray = location.split("\n");

        return locationArray[0];
    }

    public ArrayList<String> getWith() {
        return with;
    }

    public int getState() { return state; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getObjectId() { return objectId; }
}
