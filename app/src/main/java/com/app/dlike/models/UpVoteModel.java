package com.app.dlike.models;

import android.content.Context;

import com.app.dlike.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UpVoteModel {
    private String voter;
    private String author;
    private String permlink;
    private String weight;
    private String timestamp;


    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPermlink() {
        return permlink;
    }

    public void setPermlink(String permlink) {
        this.permlink = permlink;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UpVoteModel{" +
                "voter='" + voter + '\'' +
                ", author='" + author + '\'' +
                ", permalink='" + permlink + '\'' +
                ", weight='" + weight + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public static ArrayList<UpVoteModel> sorter(JsonArray jsonArray, Context context){
        //Tools.log("TEST", Tools.accountHistory);
        ArrayList<UpVoteModel> upvotes = new ArrayList<>();
        String timeStamp;
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
                        if(dd.equals(Tools.VOTE)){
                            UpVoteModel upVoteModel = new Gson().fromJson(ab.get(1).toString(), UpVoteModel.class);
                            upVoteModel.setTimestamp(timeStamp);
                            if(!upVoteModel.getVoter().equals(Tools.getUsername(context)))
                                upvotes.add(upVoteModel);
                            //Tools.log("Final", upVoteModel.toString());
                        }
                    }
                }

            }

        }catch (Exception ex){
            Tools.log("Upvote-Sorter", ex.toString());
        }

        return upvotes;
    }


    public static UpVoteModel gsonToUpvote(String value){
        try{
            return new Gson().fromJson(value, UpVoteModel.class);
        }catch (Exception ex){
            Tools.log("ConError", ex.toString());
            return null;
        }
    }
}
