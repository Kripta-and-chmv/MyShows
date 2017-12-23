package com.example.myfirstapp;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class Show extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        try {
            JSONObject show = new JSONObject(message).optJSONObject("result");

            ImageView bindImage = (ImageView)findViewById(R.id.showLogo);
            DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(bindImage);
            downloadTask.execute(show.optString("image"));

            TextView title = findViewById(R.id.showTitle);
            title.setText(show.optString("titleOriginal"));
            TextView description = findViewById(R.id.showDescription);
            description.setText(Html.fromHtml(show.optString("description")));
            TextView totalSeasons = findViewById(R.id.showTotalSeasons);
            totalSeasons.setText(show.optString("totalSeasons"));
            TextView status = findViewById(R.id.showStatus);
            status.setText(show.optString("status"));
            TextView country = findViewById(R.id.showCountry);
            country.setText(show.optString("country"));
            TextView started = findViewById(R.id.showStarted);
            started.setText(show.optString("started"));
            TextView ended = findViewById(R.id.showEnded);
            TextView endedText = findViewById(R.id.txtEnded);
            ended.setText("");
            if(!show.optString("ended").equals("null"))
            {
                ended.setText(show.optString("ended"));
            }
            else {
                endedText.setText("Next Episode: ");
                ended.setText(nextEpisodDate(show.optJSONArray("episodes")));
            }

            if(endedText.getText().toString().startsWith("Ended") || ended.getText().toString().endsWith("Unknown"))
            {
                Button addEvent = findViewById(R.id.button3);
                addEvent.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String nextEpisodDate(JSONArray episodes) throws JSONException {
        try {
            for(int i = 0; i < episodes.length(); ++i) {
                JSONObject episode = episodes.getJSONObject(i);
                String dateStr = episode.optString("airDate");
                if(dateStr.equals("null"))
                    continue;
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(dateStr.split("T")[0]);
                Date currentTime = Calendar.getInstance().getTime();
                DateFormat newFormat = new SimpleDateFormat("MMM/dd/yyyy");

                if(date.after(currentTime))
                    return newFormat.format(date);
                else
                    return "Unknown";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    public void setEvent(View view)
    {
        TextView ended = findViewById(R.id.showEnded);
        TextView title = findViewById(R.id.txtTitle);
        String date = ended.getText().toString().split(": ")[1];

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Integer.parseInt(date.split("-")[0]),
                      Integer.parseInt(date.split("-")[1]),
                      Integer.parseInt(date.split("-")[2]),
                 15, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(Integer.parseInt(date.split("-")[0]),
                Integer.parseInt(date.split("-")[1]),
                Integer.parseInt(date.split("-")[2]),
                16, 30);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.CALENDAR_ID, 3)
                .putExtra(CalendarContract.Events.TITLE, "New episode of " + title.getText().toString().split(": ")[1]);
        startActivity(intent);
    }
}
