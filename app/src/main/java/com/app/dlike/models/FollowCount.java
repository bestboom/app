package com.app.dlike.models;

import com.google.gson.annotations.SerializedName;

public class FollowCount {
    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    @SerializedName("follower_count")
    private int followers;

    @SerializedName("following_count")
    private int following;

    @Override
    public String toString() {
        return "FollowCount{" +
                "followers=" + followers +
                ", following=" + following +
                '}';
    }
}
