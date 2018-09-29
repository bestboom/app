package com.app.dlike.models;

import android.os.Parcel;

import com.app.dlike.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by moses on 8/19/18.
 */

public class Comment extends Discussion {

    public Comment(){

    }
    protected Comment(Parcel in) {
        super(in);
    }
}
