package com.app.dlike.models;

import com.app.dlike.Tools;
import com.google.gson.Gson;

public class MentionModel {
    private String title;
    private String url;
    private String author;
    private String body;
    private String permlink;
    private String created;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPermlink() {
        return permlink;
    }

    public void setPermlink(String permlink)
    {
        this.permlink = permlink;
    }

    @Override
    public String toString() {
        return "MentionModel{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", body='" + body + '\'' +
                ", permlink='" + permlink + '\'' +
                ", created='" + created + '\'' +
                '}';
    }

    public static MentionModel gsonToMention(String value){
        try{
            return new Gson().fromJson(value, MentionModel.class);
        }catch (Exception ex){
            Tools.log("ConError", ex.toString());
            return null;
        }
    }
}
