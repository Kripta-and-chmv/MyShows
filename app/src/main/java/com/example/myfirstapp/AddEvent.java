package com.example.myfirstapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class AddEvent extends AppCompatActivity {

    private String title;
    private long dateInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        try {
            JSONObject data = new JSONObject(message);
            title = data.optString("title");
            String dateStr = data.optString("date");
            DateFormat format = new SimpleDateFormat("MMM/dd/yyyy");
            Date date = format.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR, 15);
            cal.set(Calendar.MINUTE, 30);
            dateInMillis = cal.getTimeInMillis();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addEvent(View view) {
        scheduleNotification(getNotification("New episode of " + title), dateInMillis);
        finish();
    }

    public void testEvent(View view) {
        Notification notification = getNotification("New episode of " + title);
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + 5000;

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        finish();
    }

    private void scheduleNotification(Notification notification, long dateInMillis) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + 5000;
       /* Calendar beginTime = Calendar.getInstance();
        beginTime.set(2017, 11, 28,
                3, 02, 0);*/

        //AlarmManager.ELAPSED_REALTIME_WAKEUP // RTC_WAKEUP
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, dateInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        CheckBox vibration = findViewById(R.id.checkBox);
        CheckBox sound = findViewById(R.id.checkBox2);

        long[] pattern = {0, 1000, 1000};
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("MyShows Remainder")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setLights(Color.RED, 3000, 3000);
        if(vibration.isChecked())
            builder.setVibrate(pattern);
        if(sound.isChecked())
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        return builder.build();
    }
}
