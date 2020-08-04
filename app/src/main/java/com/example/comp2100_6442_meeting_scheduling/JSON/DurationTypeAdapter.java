package com.example.comp2100_6442_meeting_scheduling.JSON;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter {

    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        final Duration duration = (Duration) value;
        out.beginObject();
        out.name("durationString").value(duration.toString());
        out.endObject();

    }

    @Override
    public Duration read(final JsonReader in) throws IOException {

        in.beginObject();
        in.nextName();
        final Duration duration = Duration.parse(in.nextString());
        in.endObject();

        return duration;
    }



}
