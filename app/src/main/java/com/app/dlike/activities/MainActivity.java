package com.app.dlike.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dlike.R;
import com.app.dlike.Tools;
import com.app.dlike.fragments.PostsFragment;
import com.app.dlike.services.BackgroundService;
import com.app.dlike.widgets.PostingFragment;
import com.squareup.picasso.Picasso;

public class MainActivity extends LoginRequestActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final int REQUEST_CODE_CREATE_POST = 1121;

    private PostingFragment postingFragment;
    private View postingFragmentLayout;
    private ViewGroup mainLayout;

    private ImageView userProfileImage;
    private TextView userProfileName;
    private MenuItem logoutItem;

    TextView name;
    ImageView avatar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        startService(new Intent(this, BackgroundService.class));
        mainLayout = findViewById(R.id.mainLayout);
        postingFragmentLayout = findViewById(R.id.postingFragmentLayout);
        postingFragment = (PostingFragment) getSupportFragmentManager().findFragmentById(R.id.postingFragment);
        postingFragment.setLayouts(postingFragmentLayout, mainLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home);

       setUpMenu();
        //Nav Drawer Header


        checkLoginStatus();
    }

    private void setUpMenu(){
        String username = Tools.getUsername(this);
        if (username != null && username != ""){
            View view = navigationView.getHeaderView(0);
            name = view.findViewById(R.id.name);
            name.setText(username);
            avatar = view.findViewById(R.id.imageView);
            Tools.getImage(username, avatar, username, true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.userAccount) {
            if (!Tools.isLoggedIn(this)) {
                requestLogin();
            } else {
                Intent intent = new Intent(this, UserAccountActivity.class);
                startActivity(intent);
            }
        }else if(id == R.id.notification)
            if (Tools.isLoggedIn(MainActivity.this)) {
                startActivity(new Intent(this, NotificationActivity.class));
            } else {
                requestLogin();
            }

        return true;
    }


    public void openDialog(View view) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);

        LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_dialog_post, null);

        RelativeLayout rlLink = ll.findViewById(R.id.rlLink);
        RelativeLayout rlUpload = ll.findViewById(R.id.mainLayout);
        RelativeLayout rlText = ll.findViewById(R.id.rlText);

        ImageView imgClose = ll.findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });

        rlLink.setOnClickListener(view13 -> {
            bottomSheetDialog.cancel();

            if (Tools.isLoggedIn(MainActivity.this)) {
                Intent in = new Intent(MainActivity.this, PostActivity.class);
                in.putExtra("type", "link");
                startActivityForResult(in, REQUEST_CODE_CREATE_POST);
            } else {
                requestLogin();
            }

        });

        rlUpload.setOnClickListener(view12 -> {
            bottomSheetDialog.cancel();
            if (Tools.isLoggedIn(MainActivity.this)) {
                Intent in = new Intent(MainActivity.this, PostActivity.class);
                in.putExtra("type", "upload");
                startActivityForResult(in, REQUEST_CODE_CREATE_POST);
            } else {
                requestLogin();
            }
        });

        rlText.setOnClickListener(view1 -> {
            bottomSheetDialog.cancel();
            if (Tools.isLoggedIn(MainActivity.this)) {
                Intent in = new Intent(MainActivity.this, PostActivity.class);
                in.putExtra("type", "text");
                startActivityForResult(in, REQUEST_CODE_CREATE_POST);
            } else {
                requestLogin();
            }
        });


        bottomSheetDialog.setContentView(ll);

        bottomSheetDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Tools.isLoggedIn(this)) {
            refresh();
        }
    }

    private void checkLoginStatus() {
//        boolean loggedIn = Tools.isLoggedIn(this);
//
//        logoutItem.setVisible(loggedIn);
//        if (loggedIn) {
//            Picasso.with(this)
//                    .load("https://steemitimages.com/u/" + Tools.getUsername(this) + "/avatar")
//                    .into(userProfileImage);
//            userProfileName.setText(Tools.getUsername(this));
//        } else {
//            userProfileName.setText("Login or Signup");
//            userProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile));
//        }
    }

    private void requestLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }

    public void refresh() {
        PostsFragment postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.postsFragment);

        assert postsFragment != null;
        postsFragment.loadDiscussions();
    }

    @Override
    public void loginSuccessful() {
        checkLoginStatus();
        refresh();
        setUpMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_POST) {
            if (resultCode == Activity.RESULT_OK) {
                postingFragment.show(data);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        if (!Tools.isLoggedIn(this)) {
            requestLogin();
        } else {
            int id = item.getItemId();

            if (id == R.id.profile) {
                startActivity(new Intent(this, UserAccountActivity.class));
            } else if (id == R.id.notification) {
                startActivity(new Intent(this, NotificationActivity.class));
            } else if (id == R.id.settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.logout) {
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Tools.clearAuthentication(MainActivity.this);
                                stopService(new Intent(MainActivity.this, BackgroundService.class));
                                finish();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
