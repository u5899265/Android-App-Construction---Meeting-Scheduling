package com.example.comp2100_6442_meeting_scheduling;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * From: https://stackoverflow.com/questions/25713157/generate-int-unique-id-as-android-notification-id
 * The answer by Ted Hopp
 */
public class NotificationID {
    private final static AtomicInteger id = new AtomicInteger();
    public static int getID() {
        return id.incrementAndGet();
    }
}
