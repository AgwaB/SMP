package com.example.leesd.smp;

/**
 * Created by leesd on 2018-05-06.
 */

public class ReviewListItem {
    private String name;
    private String profileUrl;
    private String when;
    private String review;
    private int time;
    private int rating;

    public ReviewListItem(){}

    public ReviewListItem(String name, String profileUrl, String when, String review, int time, int rating) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.when = when;
        this.review = review;
        this.time = time;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
