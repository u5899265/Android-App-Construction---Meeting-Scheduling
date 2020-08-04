package com.example.comp2100_6442_meeting_scheduling;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.Data.TimeSlot;
import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Read;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.Delete;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.FetchByID;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.FetchConfirmed;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.Submit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    NotificationManagerCompat notificationManager;
    private static final String TAG = "MainActivity";
    boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // decide whether to open the first run activity
        if (Read.checkFirstRun(this) == true) {
            Intent intent = new Intent(getApplicationContext(), FirstRunActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);
        // remote notification channel:
        // 1. Nominee: new meeting invitation, click to open a calendar view
        // 2. Organiser: automatic notification from server at deadline, click to open a calendar view
        // 3. Nominee: new meeting confirmation, click?
        createNotificationChannel("WeMeetRemote", "New Meeting Invitation",
                "User needs to respond to a new meeting.");
        // local notification channel
        createNotificationChannel("WeMeetLocal", "Meeting Reminder",
                "User needs to prepare for upcoming meeting.");
    }

    public void createNotificationChannel(String id, String name, String description) {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(View view) { // parameter may depend on the source, definitely not View
        int notificationID = NotificationID.getID();
        boolean remote = true; // how to determine local or remote notification?
        String channelID = remote ? "WeMeetRemote" : "WeMeetLocal";


        Intent notificationIntent = new Intent(this, MainActivity.class); // need to change to the target activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (remote) {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info) // change to app logo
                    .setContentTitle("New Meeting!")
                    .setContentText("<organizer> just invited you to attend a new meeting. Choose your preferred time now.") // need update
                    .setChannelId(channelID)
                    .setNumber(5) // the number of notifications shown when long press the app
                    .setContentIntent(pendingIntent);
            notificationManager.notify(notificationID, notification.build());
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info) // change to app logo
                    .setContentTitle("Upcoming meeting")
                    .setContentText("The meeting called by <organizer> is starting in <how long>.") // need updates
                    .setChannelId(channelID)
                    .setNumber(5) // the number of notifications shown when long press the app
                    .setContentIntent(pendingIntent);
            notificationManager.notify(notificationID, notification.build());
        }
    } // need to delete the notification after pendingIntent is activated

    public void openCalender(View view){
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        startActivity(intent);
    }

    public void openSelection(View view){
        Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
        startActivity(intent);
    }



    public void startCreation(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateMeetingActivity.class);
        startActivity(intent);
    }

//    public void goToMeet(View view) {
    public void goToFirstRun(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateMeetingActivity.class);
        startActivity(intent);
    }

    public void testRead(View view) {
        User user = Read.getLocalUser(this);
        boolean firstRun = Read.checkFirstRun(this);
        System.out.println(user.toString() + ", firstRun is " + firstRun);
//        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void testFetch(View view) {
        for (UUID i : Read.getMyMeetings(this)) {
            System.out.println(i.toString());
            try {
                Meeting meeting = new FetchByID(i).execute().get(); // get and assigning is required to make the code in this method sequential
                System.out.println(meeting.getTitle());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void testUpdate(View view) {
        for (UUID i : Read.getMyMeetings(this)) {
            update(i, 0, true);
        }
    }

    private Boolean update(UUID uuid, int timeSlotIndex, boolean add) {
        try {
            Meeting meeting = new FetchByID(uuid).execute().get();

            TimeSlot timeSlot = meeting.getTimeSlots().get(timeSlotIndex);
            if (add) {
                timeSlot.setAvailableCount(timeSlot.getAvailableCount() + 1);
                // The availableCount for this particular timeSlot +1
            } else {
                timeSlot.setAvailableCount(timeSlot.getAvailableCount() - 1);
                // -1
            }
//                System.out.println(meeting.getTimeSlots().get(0).getAvailableCount());
            // confirm the meeting has been changed

            String result = new Submit(this, uuid, meeting).execute().get();
            Boolean success = ! result.equals("Exception Occurred in Submit");
            return success;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void testDelete(View view) {
        for (UUID i : Read.getMyMeetings(this)) {
            System.out.println(i.toString());
            try {
                Boolean success = new Delete(this, i).execute().get(); // get and assigning is required to make the code in this method sequential
                System.out.println(success);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void testFetchConfirmed(View view) {
        try {
            List<UUID> list = new FetchConfirmed(this).execute().get();
            for (UUID i : list) {
                System.out.println(i.toString());
                Meeting meeting = new FetchByID(i).execute().get();
                System.out.println(meeting.getTitle());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testOverwriteTimeSlots(View view) {
        try {
            UUID uuid = Read.getMyMeetings(this).get(0);
            System.out.println(uuid.toString());
            Meeting meeting = new FetchByID(uuid).execute().get();
            System.out.println(meeting.getTitle());
            List<TimeSlot> temp = new ArrayList<>();
            temp.add(meeting.getTimeSlots().get(1)); // 1 is the timeSlot to keep
            meeting.setTimeSlots(temp);
            String result = new Submit(this, uuid, meeting).execute().get();
            Boolean success = ! result.equals("Exception Occurred in Submit");
            System.out.println(success);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
