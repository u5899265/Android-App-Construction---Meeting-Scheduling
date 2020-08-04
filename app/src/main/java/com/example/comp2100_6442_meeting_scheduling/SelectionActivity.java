package com.example.comp2100_6442_meeting_scheduling;

import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Read;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Write;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.FetchByID;
import com.example.comp2100_6442_meeting_scheduling.weekview.WeekViewEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SelectionActivity extends BaseActivity {

    // Receive UUID of existing Meetings on the server one by one from notification;
    // The meetings are preserved as long as you don't click the confirm button

    // List of Event colors
    public final List<Integer> colors = new LinkedList<>();

    Map<Long, UUID> eventIDToMeetingID;
    Map<Long, Boolean> selected; // A dictionary of whether a certain timeslot(event) has been selected
    Map<Long, Integer> eventIDToTimeSlotIndex;

    List<Meeting> meetings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
//        fetch(this).execute();// this will update the meetings field

        // Get a reference for the week view in the layout.
        mWeekView = findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // get meeting id from deeplink in email
        Intent intent = getIntent();
        Uri data = intent.getData(); // get data from link
        String uuidString = data.getQueryParameter("uuid"); // extract string of UUID
        UUID uuid = UUID.fromString(uuidString); // convert string back to UUID

        //append meeting id to local file
        new Write(this, uuid);
        meetingIDs.addAll(Read.getMeetingIDs(this));
        meetings = new ArrayList<>();
        eventIDToMeetingID = new HashMap<>();
        selected = new HashMap<>();
        eventIDToTimeSlotIndex = new HashMap<>();

        long eventIndex = 0;
        int meetingIndex = 0;
        for (UUID meetingID : meetingIDs) {
            Meeting meeting = null;
            try {
                meeting = new FetchByID(meetingID).execute().get();
                int timeSlotIndex = 0;
                for (LocalDateTime time : meeting.getStartingTimes()) {
                    eventIDToMeetingID.put(eventIndex, meetingID);
                    selected.put(eventIndex, false);
                    eventIDToTimeSlotIndex.put(eventIndex, timeSlotIndex);
                    eventIndex += 1;
                    timeSlotIndex += 1;
                }
                meetings.add(meeting);
                meetingIndex += 1;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
//            indexTrack[meetingIndex] = selected_length;
//            meetingIndex += 1;
        }

        colors.add(R.color.event_color_01);
        colors.add(R.color.event_color_02);
        colors.add(R.color.event_color_03);
        colors.add(R.color.event_color_04);
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        //Toast.makeText(this, "newMonth: "+newMonth+"  newYear: "+newYear, Toast.LENGTH_SHORT).show();
        // Populate the week view with some events.

        List<WeekViewEvent> eventToReturn = new LinkedList<>();

        int meetingIndex = 0;
        int totalTimeSlotIndex = 0;
        for (Meeting meeting : meetings) {
            int timeSlotIndex = 0;
            for (LocalDateTime time : meeting.getStartingTimes()) {
                if (time.getYear() == newYear && time.getMonthValue() - 1 == newMonth) {
                    WeekViewEvent event = new WeekViewEvent(totalTimeSlotIndex, meeting.getTitle(), toCalendar(time), toCalendar(time.plus(meeting.getDuration())));
                    event.setColor(ContextCompat.getColor(this, colors.get(meetingIndex % colors.size())));
                    eventToReturn.add(event);
                }
                timeSlotIndex += 1;
                totalTimeSlotIndex += 1;
            }
//            totalTimeSlotIndex += timeSlotIndex;
            meetingIndex += 1;
        }
        return eventToReturn;
    }

    private Calendar toCalendar(LocalDateTime startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(startTime.getYear(), startTime.getMonthValue() - 1, startTime.getDayOfMonth(),
                startTime.getHour(), startTime.getMinute(), startTime.getSecond());
        return calendar;
    }


    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        long eventID = event.getId();
        UUID MeetingID = eventIDToMeetingID.get(eventID);
        System.out.println(MeetingID.toString());
        int timeSlotIndex = eventIDToTimeSlotIndex.get(eventID);
        System.out.println(timeSlotIndex);
        boolean isSelected = selected.get(eventID);
        System.out.println(isSelected);
        update(MeetingID, timeSlotIndex, isSelected);
        if (selected.get(event.getId())){
            //Toast.makeText(this, "selected " + event.getName(), Toast.LENGTH_SHORT).show();
            event.setColor(ContextCompat.getColor(this, colors.get(meetingIDs.indexOf(eventIDToMeetingID.get(event.getId())) % colors.size())));
            selected.put(event.getId(), false);
        } else {
            //Toast.makeText(this, "unselected " + event.getName(), Toast.LENGTH_SHORT).show();
            event.setColor(ContextCompat.getColor(this, R.color.event_select_color));
            selected.put(event.getId(), true);
        }
        mWeekView.invalidate();
    }

    @Override
    public void confirmClick(View view){
        //update uuidList by removing meetings with selected time slots
        for(Map.Entry<Long, Boolean> entry : selected.entrySet()){
            if (entry.getValue() == Boolean.TRUE){
                meetingIDs.remove(eventIDToMeetingID.get(entry.getKey()));
            }
        }
        new Write(this,meetingIDs,true);
        //back to Calender
//        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
//        startActivity(intent);
        finish();
    }
}
