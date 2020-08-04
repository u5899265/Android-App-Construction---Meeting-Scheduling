package com.example.comp2100_6442_meeting_scheduling.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

// Stores a Meeting, whether it's pending response, pending confirmation or finalised.
public class Meeting {
    Duration duration;
    List<TimeSlot> timeSlots = new LinkedList<>(); // methods were provided to access the starting time for different stages
    String title;
    String tag; // group the meeting belongs to
    String description;
    User owner;
    List<User> participants;
    LocalDateTime deadline;

    public User getOwner() {
        return owner;
    }

    public Meeting(Duration duration, String title, String tag, String description, User owner,
                   List<User> participants, LocalDateTime deadline, LocalDateTime... times) {
        this.duration = duration;
        for (LocalDateTime time : times) {
            this.timeSlots.add(new TimeSlot(time));
        }
        this.title = title;
        this.tag = tag;
        this.description = description;
        this.owner = owner;
        this.participants = participants;
        this.deadline = deadline;
    }

    public Duration getDuration() {
        return duration;
    }

    public List<LocalDateTime> getStartingTimes() {
        List<LocalDateTime> startingTimes = new LinkedList<>();
        for (TimeSlot ts : timeSlots) {
            startingTimes.add(ts.startingTime);
        }
        return startingTimes;
    }

    public LocalDateTime getStartingTime() {
        return timeSlots.get(0).startingTime;
    }
    
    public String getTitle() {
        return title;
    }

    public String getDescription(){
        return description;
    }

    public List<User> getParticipants(){
        return participants;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getTag() {
        return tag;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }
}
