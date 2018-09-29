package com.app.dlike.models;

import android.content.Context;

import com.app.dlike.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class CommentModel {

    public String getParent_author() {
        return parent_author;
    }

    public void setParent_author(String parent_author) {
        this.parent_author = parent_author;
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

    public String getParent_permlink() {
        return parent_permlink;
    }

    public void setParent_permlink(String parent_permlink) {
        this.parent_permlink = parent_permlink;
    }

    public String getPermlink() {
        return permlink;
    }

    public void setPermlink(String permlink) {
        this.permlink = permlink;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String parent_author;
    private String author;
    private String body;
    private String parent_permlink;
    private String permlink;
    private String timestamp;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    private String created;


    @Override
    public String toString() {
        return "CommentModel{" +
                "parent_author='" + parent_author + '\'' +
                ", author='" + author + '\'' +
                ", body='" + body + '\'' +
                ", parent_permlink='" + parent_permlink + '\'' +
                ", permlink='" + permlink + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", created='" + created + '\'' +
                '}';
    }



    public static ArrayList<CommentModel> sorter(JsonArray jsonArray, Context context){
        //Tools.log("TEST", Tools.accountHistory);
        ArrayList<CommentModel> comments = new ArrayList<>();
        String timeStamp;
        if(jsonArray.size() == 0)
            return comments;

        try{
            for(int i = 0; i < jsonArray.size(); i++){
                JsonArray temp = new Gson().fromJson(jsonArray.get(i).toString(), JsonArray.class);
                JsonObject tempOb = (JsonObject) temp.get(1);
                if(tempOb.has("timestamp"))
                {
                    timeStamp = new Gson().fromJson(tempOb.get("timestamp").toString(), String.class);
                    if(tempOb.has("op")){
                        JsonArray ab = (JsonArray)tempOb.get("op");
                        String dd = new Gson().fromJson(ab.get(0).toString(), String.class);
                        if(dd.equals(Tools.COMMENT)){
                            CommentModel comment = new Gson().fromJson(ab.get(1).toString(), CommentModel.class);
                            if(comment.getParent_author().equals(Tools.getUsername(context))){
                                comment.setTimestamp(timeStamp);
                                comments.add(comment);
                            }

                            //Tools.log("Final", comment.toString());
                        }
                    }
                }

            }

        }catch (Exception ex){
            Tools.log("comment-Sorter", ex.toString());
        }

        return comments;
    }


    public static CommentModel gsonToComment(String value){
        try{
            return new Gson().fromJson(value, CommentModel.class);
        }catch (Exception ex){
            Tools.log("ConError", ex.toString());
            return null;
        }
    }
}
