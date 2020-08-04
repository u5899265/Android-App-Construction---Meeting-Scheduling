package com.example.comp2100_6442_meeting_scheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.Data.TimeSlot;
import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.FetchByID;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.Submit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ConfirmMeetingActivity extends AppCompatActivity {
    Meeting meeting;
    UUID uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_meeting);

        String uuidString = getIntent().getStringExtra("id"); // receive UUID.toString() from previous activity
        uuid = UUID.fromString(uuidString); // convert string back to UUID
        meeting = fetch(uuid); // access the meeting instance from UUID

        TextView choice1 = findViewById(R.id.choice1);
        TextView choice2 = findViewById(R.id.choice2);
        TextView choice3 = findViewById(R.id.choice3);
        LocalDateTime ldt1 = meeting.getTimeSlots().get(0).getStartingTime();
        LocalDateTime ldt2 = meeting.getTimeSlots().get(1).getStartingTime();
        LocalDateTime ldt3 = meeting.getTimeSlots().get(2).getStartingTime();
        int count1 = meeting.getTimeSlots().get(0).getAvailableCount();
        int count2 = meeting.getTimeSlots().get(1).getAvailableCount();
        int count3 = meeting.getTimeSlots().get(2).getAvailableCount();

        choice1.setText("Slot 1: " + ldt1.toString() + "." + count1 + "people chose this slot.");
        choice2.setText("Slot 2: " + ldt2.toString() + "." + count2 + "people chose this slot.");
        choice3.setText("Slot 3: " + ldt3.toString() + "." + count3 + "people chose this slot.");
    }

    public void submitChoice1(View view) {
        //delete unselected Timeslots
        List<TimeSlot> timeSlots = new LinkedList<>();
        timeSlots.add(meeting.getTimeSlots().get(0));
        meeting.setTimeSlots(timeSlots);
        // submit edited meeting
        submitAndFinish(uuid);
    }

    public void submitChoice2(View view) {
        //delete unselected Timeslots
        List<TimeSlot> timeSlots = new LinkedList<>();
        timeSlots.add(meeting.getTimeSlots().get(1));
        meeting.setTimeSlots(timeSlots);
        // submit edited meeting
        submitAndFinish(uuid);
    }

    public void submitChoice3(View view) {
        //delete unselected Timeslots
        List<TimeSlot> timeSlots = new LinkedList<>();
        timeSlots.add(meeting.getTimeSlots().get(2));
        meeting.setTimeSlots(timeSlots);
        // submit edited meeting
        submitAndFinish(uuid);
    }

    public Meeting fetch(UUID uuid) {
        try {
            return new FetchByID(uuid).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void submitAndFinish(UUID uuid) {
        try {
            new Submit(this, uuid, meeting).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        sendEmail();
        finish();
    }

    public void sendEmail() {
        // prepare addresses
        String ad= "";
        List<User> users = meeting.getParticipants();
        for (User user : users) {
            ad = ad + user.getEmail() + ",";
        }
        String[] addresses = ad.split(",");
        String subject = meeting.getTitle() + " confirmation.";
        String content = meeting.getTitle() + " scheduled by " + meeting.getOwner().getName() +
                " is confirmed at " + meeting.getStartingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        composeEmail(addresses, subject, content);
    }

    // adapt from https://developer.android.com/guide/components/intents-common#java
    public void composeEmail(String[] addresses, String subject, String content) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
