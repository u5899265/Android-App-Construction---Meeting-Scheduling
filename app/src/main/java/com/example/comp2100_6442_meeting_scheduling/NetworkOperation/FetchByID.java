package com.example.comp2100_6442_meeting_scheduling.NetworkOperation;

import android.os.AsyncTask;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.JSON.DurationTypeAdapter;
import com.example.comp2100_6442_meeting_scheduling.JSON.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class FetchByID extends AsyncTask<String, String, Meeting> {
    UUID uuid;

    public FetchByID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    protected Meeting doInBackground(String... strings) {
        try {
            URL url = new URL("https://comp2100-lab8-task3.firebaseio.com/meetings/" + uuid.toString() + ".json");
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

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()); //nullSafe is not required
                gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
                Gson gson = gsonBuilder.create();

                // parsing JSON
                Meeting meeting = gson.fromJson(reader, Meeting.class);

                // Close InputStream
                response.close();

                // when using this line, the parsing can be place after the resource is closed, preventing any exception from skipping the close()
//                Meeting meeting = gson.fromJson(res, Meeting.class);

//                System.out.println("Data read from server: " + res);

                return meeting;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
