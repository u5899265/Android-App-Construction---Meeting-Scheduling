package com.example.comp2100_6442_meeting_scheduling;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ListAdapter;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Read;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.FetchByID;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.FetchConfirmed;
import com.example.comp2100_6442_meeting_scheduling.weekview.WeekViewEvent;
import com.google.gson.Gson;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CalendarActivity extends BaseActivity {
    private User owner;

    // Receive list of ID from notification;
    public List<UUID> meetingIDs = new LinkedList<>();
    //long[] indexTrack = new long[meetingIDs.size()]; //to store the first eventID of each meeting
    public final List<Integer> colors = new LinkedList<>();
    Map<Long, UUID> eventIDToMeetingID;
    List<Meeting> meetings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        String[] PERMISSIONS = {
                //android.Manifest.permission.READ_CONTACTS,
                //android.Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        };
        // Check the permission of the current activity to access calender
        if (ContextCompat.checkSelfPermission(this,
                Arrays.toString(PERMISSIONS)) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Arrays.toString(PERMISSIONS))) {
                //If user rejects the permission, this function is true;
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, PERMISSIONS, 0);
            }
        }


        // Get a reference for the week view in the layout.
        mWeekView = findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        this.owner = Read.getLocalUser(this);
        try {
            meetingIDs = new FetchConfirmed(this).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        // this will update the meetings field which includes the certain user

        meetings = new ArrayList<>();
        eventIDToMeetingID = new HashMap<>();
        //int count = 0; //to count number of meetings
        //long eventID = 0; //to assign an index id to each time slot of all meetings
        System.out.println(meetingIDs);

        //get list of meetings;
        //set map eventIDToMeetingID.
        long eventIndex = 0;
        for (UUID meetingID : meetingIDs) {
            Meeting meeting = null;
            try {
                meeting = new FetchByID(meetingID).execute().get();
                meetings.add(meeting);
                eventIDToMeetingID.put(eventIndex, meetingID);
                eventIndex += 1;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        // List of Event colors
        colors.add(R.color.event_color_01);
        colors.add(R.color.event_color_02);
        colors.add(R.color.event_color_03);
        colors.add(R.color.event_color_04);
    }


    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        //Toast.makeText(this, "newMonth: "+newMonth+"  newYear: "+newYear, Toast.LENGTH_SHORT).show();
        // Populate the week view with some events.
//        LocalDateTime startingTime1 = LocalDateTime.of(2020, 5, 26, 7, 20, 55);
//        LocalDateTime startingTime2 = LocalDateTime.of(2020, 5, 27, 8, 21, 56);
//        User owner = new User("Zheng Huang", "amoyhuangzheng@yahoo.com", 6134872);
//        User participant_1 = new User("Yuchen Zhang", "111222@gmail.com", 5115);
//        User participant_2 = new User("Yuchen Wang", "112233@gmail.com", 1551);
//        User participant_3 = new User("Yuchen Hu", "123456@gmail.com", 233);
//        List<User> participants = new LinkedList<>();
//        participants.add(participant_1);
//        participants.add(participant_2);
//        participants.add(participant_3);
//        LocalDateTime deadline = LocalDateTime.of(2020, 5, 29, 7, 23, 43);

        //Meeting meeting = fetch.excute.getMeeting(ID)
        //Meeting meeting = new Meeting(Duration.ofHours(1), "Meeting 1", "COMP6442", "the first meeting after 3 discussions", owner, participants, deadline, startingTime1, startingTime2);

        List<WeekViewEvent> eventToReturn = new LinkedList<>();

        int meetingIndex = 0; //actually seem as eventID
        for (Meeting meeting : meetings) {
                LocalDateTime time = meeting.getStartingTime();
                if (time.getYear() == newYear && time.getMonthValue() - 1 == newMonth) {
                    WeekViewEvent event = new WeekViewEvent(meetingIndex, meeting.getTitle(), toCalendar(time), toCalendar(time.plus(meeting.getDuration())));
                    event.setColor(ContextCompat.getColor(this, colors.get(meetingIndex % colors.size())));
                    eventToReturn.add(event);
                }
            meetingIndex += 1;
        }
        return eventToReturn;







//        List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
//        for (WeekViewEvent allEvent : events) {
//            if (eventMatches(allEvent, newYear, newMonth)) {
//                matchedEvents.add(allEvent);
//            }
//        }
//        return matchedEvents;
    }

    private Calendar toCalendar(LocalDateTime startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(startTime.getYear(), startTime.getMonthValue() - 1, startTime.getDayOfMonth(),
                startTime.getHour(), startTime.getMinute(), startTime.getSecond());
        return calendar;
    }

//            Calendar startTime = Calendar.getInstance();
//            startTime.set(Calendar.HOUR_OF_DAY, 3);
//            startTime.set(Calendar.MINUTE, 30);
//            startTime.set(Calendar.MONTH, newMonth-1);
//            startTime.set(Calendar.YEAR, newYear);
//            Calendar endTime = (Calendar) startTime.clone();
//            endTime.set(Calendar.HOUR_OF_DAY, 4);
//            endTime.set(Calendar.MINUTE, 30);
//            endTime.set(Calendar.MONTH, newMonth-1);
//            WeekViewEvent event = new WeekViewEvent(10, "TEST", startTime, endTime);
//            event.setColor(R.color.event_color_02);
//            events.add(event);

//    private boolean eventMatches(WeekViewEvent event, int year, int month) {
//        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
//    }

//    private Calendar toCalendar(LocalDateTime startTime, int newYear, int newMonth) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.clear();
//        calendar.set(newYear, newMonth-1, startTime.getDayOfMonth(),
//                startTime.getHour(), startTime.getMinute(), startTime.getSecond());
//        return calendar;
//    }

//    private WeekViewEvent toWeekViewEvent (Meeting meeting, long id, int newYear, int newMonth){
//        String title = meeting.getTitle();
//        Duration duration = meeting.getDuration();
//        LocalDateTime startTime = meeting.getStartingTime();
//        LocalDateTime endTime = startTime.plus(duration);
//        WeekViewEvent event = new WeekViewEvent(id, title, toCalendar(startTime, newYear, newMonth), toCalendar(endTime, newYear, newMonth));
//        return event;
//    }



    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        long eventID = event.getId();
        UUID id = eventIDToMeetingID.get(eventID);
        Meeting meeting = null;
        try {
            meeting = new FetchByID(id).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        //check if the owner is the event sponsor
        boolean sponsor = this.owner.equals(meeting.getOwner());
        if (sponsor){
            //pop-up window: display event info + back button + delete button
            DeleteActivity popUp = new DeleteActivity();
            popUp.showPopupWindow(meeting,id,this);
            //Toast.makeText(this, "selected " + event.getName(), Toast.LENGTH_SHORT).show();
        }
        else
        {//pop-up window: display event info + back button + delete button
            InfoActivity info = new InfoActivity();
            info.showInfoWindow(meeting,this);
         //Toast.makeText(this, "unselected " + event.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    public void clickAddNewMeeting (View v){
        Intent intent = new Intent(getApplicationContext(), CreateMeetingActivity.class);
        startActivity(intent);
    }

    public void clickMyMeetings (View v){
//        List<UUID> MyMeetings = Read.getMyMeetings(this);
//        ArrayAdapter<UUID> adapter = new ArrayAdapter(this,R.layout.activity_calender,MyMeetings);
//        ListView listView = findViewById(R.id.)
//        list.setAdapter(adapter);
        Intent intent = new Intent(this, MyMeetingsActivity.class);
        startActivity(intent);
    }



}
