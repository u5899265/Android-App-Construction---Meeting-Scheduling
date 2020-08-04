package com.example.comp2100_6442_meeting_scheduling.NetworkOperation;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.comp2100_6442_meeting_scheduling.BaseActivity;
import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Read;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Write;
import com.example.comp2100_6442_meeting_scheduling.JSON.DurationTypeAdapter;
import com.example.comp2100_6442_meeting_scheduling.JSON.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Delete extends AsyncTask<String, String, Boolean> {
    Activity activity;
    UUID uuid;

    public Delete(Activity activity, UUID uuid) {
        this.activity = activity;
        this.uuid = uuid;
    }

    // must delete in both local and remote
    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            URL url = new URL("https://comp2100-lab8-task3.firebaseio.com/meetings/" + uuid.toString() + ".json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");

            System.out.println(connection.getResponseCode());

            List<UUID> myMeetings = Read.getMyMeetings(activity);
            if (myMeetings.contains(uuid)) {
                myMeetings.remove(uuid);
                new Write(activity, myMeetings);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Occurred in Deletion");
        } finally {
            // should close in case of exception
        }
        return false;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
