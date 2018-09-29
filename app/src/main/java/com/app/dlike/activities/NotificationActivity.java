package com.app.dlike.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.app.dlike.R;
import com.app.dlike.Tools;
import com.app.dlike.fragments.CommentNotificationFragment;
import com.app.dlike.fragments.FollowNotificationFragment;
import com.app.dlike.fragments.MentionNotificationFragment;
import com.app.dlike.fragments.RepliesNotificationFragment;
import com.app.dlike.fragments.RewardFragment;
import com.app.dlike.fragments.TransferNotificationFragment;
import com.app.dlike.fragments.UpvoteNotificationFragment;
import com.app.dlike.http.RetrofitClient;
import com.app.dlike.services.BackgroundService;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //Fragments

    TabLayout tabs;

    //Http
    public RetrofitClient retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabs = findViewById(R.id.tabs);
        retrofit = new RetrofitClient(this, Tools.STEEM_CONNECT_BASE_URL);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager();
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setupWithViewPager(mViewPager);
        startService(new Intent(this, BackgroundService.class));
    }


    private void setupViewPager() {
        mSectionsPagerAdapter.addFragment(new FollowNotificationFragment(), "Follows");
        mSectionsPagerAdapter.addFragment(new MentionNotificationFragment(), "Mentions");
        mSectionsPagerAdapter.addFragment(new UpvoteNotificationFragment(), "UpVotes");
        mSectionsPagerAdapter.addFragment(new CommentNotificationFragment(), "Comments");
        mSectionsPagerAdapter.addFragment(new RepliesNotificationFragment(),"Replies");
        mSectionsPagerAdapter.addFragment(new TransferNotificationFragment(), "Transfers");
        mSectionsPagerAdapter.addFragment(new RewardFragment(), "Rewards");
        mViewPager.setOffscreenPageLimit(7);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.home){
            onBackPressed();
        }

        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private ArrayList<String> mFragmentTitleList = new ArrayList<String>();


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
