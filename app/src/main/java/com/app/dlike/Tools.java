package com.app.dlike;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.app.dlike.activities.MainActivity;
import com.app.dlike.activities.NotificationActivity;
import com.app.dlike.activities.ProfileViewActivity;
import com.app.dlike.api.Steem;
import com.app.dlike.http.RetrofitClient;
import com.app.dlike.models.FollowCount;
import com.app.dlike.utilities.AppPreference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by hardip on 12/2/18.
 */

public class Tools {
    private static final boolean production = false;
    private static final String PREFERENCE_ACCESS_TOKEN = "access_token";
    private static final String PREFERENCE_USERNAME = "username";
    private static final String PREFERENCE_EXPIRES_AT = "expires_at";
    private static final String PREFERENCE_REFRESH_TOKEN = "refresh_token";
    public static final String STEEM_CONNECT_BASE_URL = "https://steemconnect.com/api/";
    public static final String STEEM_PLUS_BASE_URL = "https://steemplus-api.herokuapp.com/api/get-mentions/";
    public static final String STEEM_API_BASE_URL = "https://api.steemjs.com";
    public static final String DEFAULT_USERNAME = "";
    public static final String DEFAULT_START = "a";
    public static final String BLOG = "blog";
    public static final int FETCH_LIMIT_FOLLOW = 200;
    public static final String VOTE = "vote";
    public static final String COMMENT = "comment";
    public static final String TRANSFER = "transfer";

    public static OkHttpClient getClient(final Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request.Builder ongoing = chain.request().newBuilder();
                    if (isLoggedIn(context)) {
                        ongoing.addHeader("Authorization", "Bearer " + getAccessToken(context));
                    }
                    return chain.proceed(ongoing.build());
                }).build();
    }

    public static Retrofit createRetrofit(Context context) {
        return new Retrofit.Builder()
                .baseUrl(STEEM_CONNECT_BASE_URL)
                .client(getClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Steem getSteem(Context context) {
        return createRetrofit(context).create(Steem.class);
    }

    public static boolean isLoggedIn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .contains(PREFERENCE_REFRESH_TOKEN);
    }

    public static boolean tokenExpired(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(PREFERENCE_EXPIRES_AT, System.currentTimeMillis()) <= System.currentTimeMillis();
    }

    public static String getAccessToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFERENCE_ACCESS_TOKEN, "");
    }

    public static String getRefreshToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFERENCE_REFRESH_TOKEN, "");
    }

    public static String getUsername(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFERENCE_USERNAME, DEFAULT_USERNAME);
    }

    public static void showProfile(final View view, final String username) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ProfileViewActivity.class);
                intent.putExtra(ProfileViewActivity.EXTRA_USERNAME, username);
                view.getContext().startActivity(intent);
            }
        });
    }

    public static void setAuthentication(Context context, String accessToken, String refreshToken, String username) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(PREFERENCE_ACCESS_TOKEN, accessToken);
        editor.putString(PREFERENCE_USERNAME, username);
        editor.putString(PREFERENCE_REFRESH_TOKEN, refreshToken);
        long expiry = 604800000;
        editor.putLong(PREFERENCE_EXPIRES_AT, System.currentTimeMillis() + expiry);
        editor.apply();
    }

    public static void clearAuthentication(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .remove(PREFERENCE_ACCESS_TOKEN)
                .remove(PREFERENCE_USERNAME)
                .remove(PREFERENCE_EXPIRES_AT)
                .remove(PREFERENCE_REFRESH_TOKEN)
                .apply();
    }

    public static void showToast(final Context context, final String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetAvail(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPermissionGranted(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean isPermissionGrantedgLOC(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]"
                + "+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void openSettings(Activity context, String msg) {
        showToast(context, msg);

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        Log.d("lastv", lastVal);
        return lastVal;
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (mLocationManager != null) {
            return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            return false;
        }
    }

    public static Calendar getDateFromMillis(String milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return calendar;
//        return formatter.format(calendar.getTime());
    }

    public static String getDateFromMillisString(String milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
//        return calendar;
        return formatter.format(calendar.getTime());
    }

    public static void log(String TAG, String message){
        if(!production){
            if(message != null){
                Log.e(TAG, message);
            }
        }
    }

    public static final String mentions = "";


    public static void getImage(String url, ImageView img, String alt, boolean avatar){
        //AppConstants.log(TAG, "Image/" + url);

        if (avatar) {
            url = "https://steemitimages.com/u/" + url + "/avatar";
        }
        String TAG = "picasso get image";
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .placeholder(getDrawable(alt))
                .into(img, new Callback() {
                    @Override
                    public void onSuccess() {
                        //log(TAG,"Image Loaded");
                    }

                    @Override
                    public void onError(Exception e) {
                        log(TAG,e.getMessage());
                    }
                });
    }

    public static Drawable getDrawable(String name){
        return TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRect(String.valueOf(name.charAt(0)),  ColorGenerator.MATERIAL.getRandomColor());
    }

    public static void showPush(Context context, String msg, String title){

        int notificationId = (int) (System.currentTimeMillis()%10000);
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "DLIKE";// The user-visible name of the channel.
        String GROUP_KEY = "com.app.dlike.NOTiFICATION";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String TAG = "tools";
        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";

        Intent intent = new Intent(context, NotificationActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, notificationId);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon_large);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.app_icon)
                    .setChannelId(CHANNEL_ID)
                    .build();


            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);

// Issue the notification.
            mNotificationManager.notify(notificationId , notification);
        }else{

            Notification newMessageNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setLargeIcon(bitmap)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .build();
            int SUMMARY_ID = 0;

            Notification summaryNotification =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle(title)
                            //set content text to support devices running API level < 24
                            .setContentText("Two new messages")
                            .setSmallIcon(R.drawable.app_icon)
                            //build summary info into InboxStyle template
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine("You have multiple notification"))
                            //specify which group this notification belongs to
                            .setGroup(GROUP_KEY_WORK_EMAIL)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)
                            .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificationId, newMessageNotification);
            notificationManager.notify(SUMMARY_ID, summaryNotification);

        }

        log(TAG, notificationId + "");

    }

    public static long convertDate(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }
}
