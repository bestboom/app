package com.app.dlike.activities;

import com.app.dlike.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.app.dlike.R;
import com.app.dlike.Tools;
import com.app.dlike.utilities.AppPreference;

public class SettingsActivity extends AppCompatActivity {

    Switch followers, mentions, upvote, transfer, replies;
    AppPreference app;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        app = new AppPreference(this);
        followers = findViewById(R.id.followers);
        mentions = findViewById(R.id.mentions);
        upvote = findViewById(R.id.upvote);
        transfer = findViewById(R.id.transfer);
        replies = findViewById(R.id.replies);
        
        populateScreen();
        listeners();
    }

    private void populateScreen() {
        followers.setChecked(app.getFollowersNotification());
        mentions.setChecked(app.getMentionsNotification());
        upvote.setChecked(app.getUpvotesNotification());
        transfer.setChecked(app.getTransferNotification());
        replies.setChecked(app.getRepliesNotification());
    }

    private void listeners(){
        followers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setFollowersNotification(isChecked);
            Tools.showToast(this, "Settings Saved");
        });
        mentions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setMentionsNotification(isChecked);
            Tools.showToast(this, "Settings Saved");
        });
        upvote.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setUpvotesNotification(isChecked);
            Tools.showToast(this, "Settings Saved");

        });
        transfer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setTransferNotification(isChecked);
            Tools.showToast(this, "Settings Saved");
        });
        replies.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setRepliesNotification(isChecked);
            Tools.showToast(this, "Settings Saved");
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}
