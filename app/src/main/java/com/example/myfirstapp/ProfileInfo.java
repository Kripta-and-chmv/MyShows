package com.example.myfirstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

public class ProfileInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        //Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //JSONObject profile = new JSONObject();
        JSONObject user = DisplayMessageActivity.profile.optJSONObject("result").optJSONObject("user");
        JSONObject stats = DisplayMessageActivity.profile.optJSONObject("result").optJSONObject("stats");
        TextView username = findViewById(R.id.username);
        username.setText("Username: " + user.optString("login"));
        TextView episodes = findViewById(R.id.totalEpisodes);
        episodes.setText("Watched episodes: " + stats.optString("watchedEpisodes"));
        TextView hours = findViewById(R.id.wastedHours);
        hours.setText("Hours wasted: " + stats.optString("totalHours"));
        TextView days = findViewById(R.id.wastedDays);
        days.setText("Hours wasted: " + stats.optString("watchedDays"));

        ImageView avatar = (ImageView)findViewById(R.id.avatarImg);
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(avatar);
        downloadTask.execute(user.optString("avatar"));
    }

    @Override
    public void onBackPressed() {
        finish();
        return;
    }
}
