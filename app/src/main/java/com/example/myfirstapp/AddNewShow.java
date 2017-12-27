package com.example.myfirstapp;

import android.content.Context;
import android.content.Intent;
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
import java.util.Hashtable;

public class AddNewShow extends AppCompatActivity {

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_show);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
    }

    public void searchShows(View view) {
        final TextView searchInput = findViewById(R.id.inputSearchInAll);

        final Context mainThis = this;
        final Hashtable<String, String> namesId = new Hashtable<String, String>();


        final ListView showsLV = (ListView) findViewById(R.id.allShows);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                listItems);
        showsLV.setAdapter(adapter);

        Thread httpThread = new Thread(new Runnable() {
            public void run() {
            JSONObject data = new JSONObject();

            try {
                JSONObject params = new JSONObject();
                params.put("query", searchInput.getText().toString());
                data.put("jsonrpc", "2.0");
                data.put("method", "shows.Search");
                data.put("id", 1);
                data.put("params", params);

                String mybla = Requests.sendJSON(Requests.urlMyShows, data);
                JSONObject response = new JSONObject();

                response = new JSONObject(mybla);
                if(!((!mybla.equals("") && response.optJSONObject("error") == null)))
                    return;

                JSONArray shows = response.getJSONArray("result");
                listItems.clear();
                for(int i = 0; i < shows.length(); ++i)
                {
                    JSONObject show = shows.getJSONObject(i);
                    namesId.put(show.optString("titleOriginal"),
                            show.optString("id"));
                    listItems.add(show.optString("titleOriginal"));
                }



                final Intent intentShow = new Intent(mainThis, Show.class);

                showsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final String item = adapter.getItem(i);

                        Thread showThread = new Thread(new Runnable() {
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
                        showThread.start();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
        httpThread.start();
        try {
            httpThread.join();
            adapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
