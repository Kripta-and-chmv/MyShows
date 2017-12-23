package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        final Intent intent = new Intent(this,  DisplayMessageActivity.class);

        Thread httpThread = new Thread(new Runnable() {
            public void run() {
            EditText login = (EditText) findViewById(R.id.usernameText);
            JSONObject data = new JSONObject();

            try {
                JSONObject params = new JSONObject();
                params.put("login", login.getText());
                data.put("jsonrpc", "2.0");
                data.put("method", "profile.Get");
                data.put("id", 1);
                data.put("params", params);

                String mybla = Requests.sendJSON(Requests.urlMyShows, data);
                JSONObject response = new JSONObject();
                response = new JSONObject(mybla);
                if(!mybla.equals("") && response.optJSONObject("error") == null)
                {
                    intent.putExtra(EXTRA_MESSAGE, mybla);
                    startActivity(intent);
                }
                else {
                    login.setText("Error");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
        httpThread.start();
    }


}
