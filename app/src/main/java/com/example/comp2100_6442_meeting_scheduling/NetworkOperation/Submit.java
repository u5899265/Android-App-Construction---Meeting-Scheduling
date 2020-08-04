package com.example.comp2100_6442_meeting_scheduling.NetworkOperation;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Read;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Write;
import com.example.comp2100_6442_meeting_scheduling.JSON.DurationTypeAdapter;
import com.example.comp2100_6442_meeting_scheduling.JSON.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// This submits a single meeting to the server
public class Submit extends AsyncTask<String, String, String> {
    Activity activity;
    Meeting meeting;
//    URL url;
    UUID uuid = null;

    // The activity calling this constructor need to have a showSubmissionStatus method, takes one boolean argument
    // ↑ EDIT: on calling this_class.execute().get(), you will get a Boolean, which you may use in your own implementation of show submission status
    public Submit(Activity activity, Meeting meeting) {
//        this.activity = (CreateMeetingActivity) activity;
        this.activity = activity;
        this.meeting = meeting;
    }

    public Submit(Activity activity, UUID uuid, Meeting meeting) {
        this.activity = activity;
        this.uuid = uuid;
        this.meeting = meeting;
//        this.url = new URL("https://comp2100-lab8-task3.firebaseio.com/meetings.json");
        // Generally I would prefer passing the exception up so they can be handled together
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL("https://comp2100-lab8-task3.firebaseio.com/meetings.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PATCH");
            OutputStream out = connection.getOutputStream();

            // Call gson package and create class instances after all exceptions should have been triggered
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()); //nullSafe is not required
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
            Gson gson = gsonBuilder.create();
            Map<UUID, Meeting> meetings = new HashMap<>();
            if (uuid == null) {
                uuid = UUID.randomUUID();
            }
            meetings.put(uuid, meeting);

//            String key = "{\"" + uuid.toString() + "\":";
//            String value = gson.toJson(meeting) + "}";
//
//            System.out.println(value);
//            out.write(key.getBytes());
//            out.write(value.getBytes());
            System.out.println(gson.toJson(meetings));
            out.write(gson.toJson(meetings).getBytes());

            out.close();
            System.out.println(connection.getResponseCode());

            List<UUID> myMeetings = Read.getMyMeetings(activity);
            if (! myMeetings.contains(uuid)) {
                myMeetings.add(uuid);
                new Write(activity, myMeetings);
            }

            return uuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception Occurred in Submit";
        } finally {
            // should close in case of exception
        }
    }
}
