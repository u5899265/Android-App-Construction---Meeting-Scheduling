package com.example.comp2100_6442_meeting_scheduling.FileOperation;

import android.app.Activity;

import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Write {
    File file;

    public Write(Activity activity, User localUser) {
        this.file = new File(activity.getApplicationContext().getFilesDir(), "owner.json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveToJSONFile(file, localUser);
    }

    public Write(Activity activity, List<UUID> myMeetings) {
        this.file = new File(activity.getApplicationContext().getFilesDir(), "myMeetings.json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveToJSONFile(file, myMeetings);
    }

    //append a single id into "uuidFromEmail.json"
    public Write(Activity activity, UUID uuidFromEmail) {
        this.file = new File(activity.getApplicationContext().getFilesDir(), "uuidFromEmail.json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<UUID> IDlist = Read.getMeetingIDs(activity);
        if (! IDlist.contains(uuidFromEmail)) {
            IDlist.add(uuidFromEmail);
        }
        System.out.println("Before" + IDlist);
        saveToJSONFile(file, IDlist);
    }

    public Write(Activity activity, List<UUID> myMeetings, boolean isRetain) {
        this.file = new File(activity.getApplicationContext().getFilesDir(), "uuidFromEmail.json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveToJSONFile(file, myMeetings);
    }

    public Write(Activity activity, boolean firstRun) {
        this.file = new File(activity.getApplicationContext().getFilesDir(), "firstRun.ini"); // could use local preference in the future
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(file)) {
//            System.out.println(String.valueOf(firstRun));
            writer.write(String.valueOf(firstRun));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveToJSONFile(File file, Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
