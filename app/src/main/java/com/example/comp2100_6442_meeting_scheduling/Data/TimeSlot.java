package com.example.comp2100_6442_meeting_scheduling.Data;

import java.time.LocalDateTime;

public class TimeSlot {
    int availableCount;
    LocalDateTime startingTime;

    TimeSlot(LocalDateTime startingTime) {
        this.availableCount = 0;
        this.startingTime = startingTime;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }
}
