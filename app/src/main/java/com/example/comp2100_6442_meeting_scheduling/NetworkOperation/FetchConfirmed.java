package com.example.comp2100_6442_meeting_scheduling.NetworkOperation;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Read;
import com.example.comp2100_6442_meeting_scheduling.JSON.DurationTypeAdapter;
import com.example.comp2100_6442_meeting_scheduling.JSON.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FetchConfirmed extends AsyncTask<String, String, List<UUID>> {
    Activity activity;

    public FetchConfirmed(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected List<UUID> doInBackground(String... strings) {
        try {
            URL url = new URL("https://comp2100-lab8-task3.firebaseio.com/meetings.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200) {
                // 200 OK is the response normally used for successful requests in HTTP

                InputStream response = connection.getInputStream();

                // Parse data received
//                String res = "";
//                int data;
//
//                while ((data = response.read()) != -1) {
//                    res += (char) data;
//                }

                JsonReader reader = new JsonReader(new InputStreamReader(response, "UTF-8"));

//                Gson gson = new Gson();

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()); //nullSafe is not required
                gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
                Gson gson = gsonBuilder.create();

                final Type UUID_MEETING_MAP = new TypeToken<Map<UUID, Meeting>>() {}.getType();

                // parsing JSON
                Map<UUID, Meeting> meetings = gson.fromJson(reader, UUID_MEETING_MAP);

                // Close InputStream
                response.close();

//                Meetings meetings = gson.fromJson(res, Meetings.class);

                List<UUID> meetingIDs = new LinkedList<>();
                // Recording UUID and fetch when needed was our design, we decoded that being able to see the real time numbers would be more important
                // than saving a small fraction of time when the Internet is poor.

                for (Map.Entry<UUID, Meeting> entry : meetings.entrySet()) {
                    if (entry.getValue().getOwner().equals(Read.getLocalUser(activity))) { // use & ? will it improve structure/computational complexity/compiler decision? optimization?
                        if (entry.getValue().getTimeSlots().size() == 1) {
                            meetingIDs.add(entry.getKey());
                        }
                    }
                }
                return meetingIDs;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }
}
