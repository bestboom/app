package com.app.dlike.models;

import com.google.gson.annotations.SerializedName;

public class FollowModel {
    private String follower;
    private String following;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    @Override
    public String toString() {
        return "FollowModel{" +
                "follower='" + follower + '\'' +
                ", following='" + following + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}