package com.app.dlike.models;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    private String name;
    private Metadata metadata;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "FollowModel{" +
                "name='" + name + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}


class Metadata{
    private Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return profile.toString();
    }
}

class Profile{
    @SerializedName("profile_image")
    private String avatar;
    private String website;
    private String about;
    @SerializedName("cover_image")
    private String cover;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "avatar='" + avatar + '\'' +
                ", website='" + website + '\'' +
                ", about='" + about + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
