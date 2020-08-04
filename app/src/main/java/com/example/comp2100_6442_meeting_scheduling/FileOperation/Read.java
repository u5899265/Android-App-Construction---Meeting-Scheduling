package com.example.comp2100_6442_meeting_scheduling.FileOperation;

import android.app.Activity;

import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Read {
    static File file;

    public static User getLocalUser(Activity activity) {
        file = new File(activity.getApplicationContext().getFilesDir(), "owner.json");

        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            return gson.fromJson(reader, User.class);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static List<UUID> getMyMeetings(Activity activity) {
        file = new File(activity.getApplicationContext().getFilesDir(), "myMeetings.json");
        Gson gson = new Gson();
        final Type UUID_LIST_TYPE = new TypeToken<List<UUID>>() {}.getType();
        List<UUID> result = new LinkedList<>();
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            result = gson.fromJson(reader, UUID_LIST_TYPE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (result == null) {
                result = new LinkedList<>();
            }
        }
        return result;
    }

    public static List<UUID> getMeetingIDs(Activity activity) {
        file = new File(activity.getApplicationContext().getFilesDir(), "uuidFromEmail.json");
        Gson gson = new Gson();
        final Type UUID_LIST_TYPE = new TypeToken<List<UUID>>() {}.getType();
        List<UUID> result = new LinkedList<>();
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            result = gson.fromJson(reader, UUID_LIST_TYPE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (result == null) {
                result = new LinkedList<>();
            }
        }
        return result;
    }

    public static boolean checkFirstRun(Activity activity) {
        file = new File(activity.getApplicationContext().getFilesDir(), "firstRun.ini");
        try {
            String content = new Scanner(file).next();
            System.out.println(content);
            if (content.equals("true")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
}
