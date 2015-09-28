package com.whiterabbitt.whiterabbitt;


import java.util.ArrayList;

// This class temporarily saves the information of the invitation
public class InvitationInfoActivity {
    private String title; // To hold the title
    private String time; // To hold the time
    private String date; // To hold the date
    private String location; // To hold the location
    private ArrayList<String> with; // To hold the invitees
    private int accepted; // To hold the total number of accepted
    private int declined; // To hold the total number of declined
    private String carrot; // To hold the number of carrots that will be rewarded

    private double latitude; // To hold the latitude of the user-defined location
    private double longitude; // To hold the longitude of the user-defined location

    private String objectId; // To hold the objectId of the event

    private String hostId; // To hold the objectId of the host of the current event

    private String geofenceFlag; // To hold the flag for the geofence of current event

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

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public void setObjectId(String objectId) { this.objectId = objectId; }

    public void setNumOfAccepted(int accepted) {
        this.accepted = accepted;
    }

    public void setNumOfDeclined(int declined) {
        this.declined= declined;
    }

    public void setCarrot(String carrot) {
        this.carrot = carrot;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setGeofenceFlag(String geofenceFlag) {
        this.geofenceFlag = geofenceFlag;
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

    public String getLocationShort() {
        String[] locationArray = location.split("\n");

        return locationArray[0];
    }

    public ArrayList<String> getWith() {
        return with;
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getObjectId() { return objectId; }

    public int getNumOfAccepted() {
        return accepted;
    }

    public int getNumOfDeclined() {
        return declined;
    }

    public String getCarrot() {
        return carrot;
    }

    public String getHostId() {
        return hostId;
    }

    public String getGeofenceFlag() { return geofenceFlag; }

}
