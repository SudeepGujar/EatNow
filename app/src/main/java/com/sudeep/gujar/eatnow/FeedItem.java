package com.sudeep.gujar.eatnow;

/**
 * Created by GUJAR on 2/10/2015.
 */
public class FeedItem {
    private String title;
    private String thumbnail;
    private int distance;
    private String address;
    private String rank;
    private String venueID;

    public String getTitle() {
        return title;
    }

    public String getVenueID() {
        return venueID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVenueID(String venueID) {
        this.venueID = venueID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setDist(int dist) {
        this.distance = dist;
    }

    public String getDist() {
        return ""+distance+" m";
    }

    public String getAddress() {
        return address;
    }

    public String getRank() { return ""+rank; }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

}
