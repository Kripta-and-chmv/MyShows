package com.example.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class DisplayMessageActivity extends AppCompatActivity {

    public static JSONObject profile;

    @Override
    protected void onResume(){
        super.onResume();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        try {
            profile = new JSONObject(message);
        } catch (JSONException e) {
            profile = new JSONObject();
        }

        JSONObject user = profile.optJSONObject("result").optJSONObject("user");
        String avatar = user.optString("avatar");

        ImageView bindImage = (ImageView)findViewById(R.id.imageView);
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(bindImage);
        downloadTask.execute(avatar);

        TextView textView = findViewById(R.id.textView);
        textView.setText("Welcome, " + user.optString("login") + "!");
    }

    public void viewProfile(View view)
    {
        final Intent intent = new Intent(this, ProfileInfo.class);

        startActivity(intent);
    }

    public void viewShows(View view)
    {
        final Intent intent = new Intent(this, SeriesList.class);

        Thread httpThread = new Thread(new Runnable() {
            public void run() {
                JSONObject data = new JSONObject();

                try {
                    JSONObject params = new JSONObject();
                    params.put("login", profile.optJSONObject("result").optJSONObject("user").optString("login"));
                    data.put("jsonrpc", "2.0");
                    data.put("method", "profile.Shows");
                    data.put("id", 1);
                    data.put("params", params);


                    String mybla = Requests.sendJSON(Requests.urlMyShows, data);
                    JSONObject response = new JSONObject();

                    response = new JSONObject(mybla);
                    if(!mybla.equals("") && response.optJSONObject("error") == null)
                    {
                        intent.putExtra(MainActivity.EXTRA_MESSAGE, mybla);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        httpThread.start();
    }
}
