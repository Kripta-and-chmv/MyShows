package com.example.myfirstapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;

public class SeriesList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_list);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        try {
            JSONArray shows = new JSONObject(message).getJSONArray("result");
            final Hashtable<String, String> namesId = new Hashtable<String, String>();

            for(int i = 0; i < shows.length(); ++i)
            {
                JSONObject show = shows.getJSONObject(i);
                namesId.put(show.optJSONObject("show").optString("titleOriginal"),
                            show.optJSONObject("show").optString("id"));
            }

            ListView showsLV = (ListView) findViewById(R.id.seriesList);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    Collections.list(namesId.keys()));
            showsLV.setAdapter(adapter);

            final Intent intentShow = new Intent(this, Show.class);

            showsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String item = adapter.getItem(i);

                    Thread httpThread = new Thread(new Runnable() {
                        public void run() {
                        JSONObject data = new JSONObject();

                        try {
                            JSONObject params = new JSONObject();
                            params.put("showId", namesId.get(item));
                            params.put("withEpisodes", true);
                            data.put("jsonrpc", "2.0");
                            data.put("method", "shows.GetById");
                            data.put("id", 1);
                            data.put("params", params);

                            String mybla = Requests.sendJSON(Requests.urlMyShows, data);
                            JSONObject response = new JSONObject();
                            response = new JSONObject(mybla);

                            if(!mybla.equals("") && response.optJSONObject("error") == null)
                            {
                                intentShow.putExtra(MainActivity.EXTRA_MESSAGE, mybla);
                                startActivity(intentShow);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        }
                    });
                    httpThread.start();
                }
            });

            EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    adapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void allShows(View view)
    {
        final Intent intent = new Intent(this, AddNewShow.class);
        startActivity(intent);
    }

}
