package com.example.comp2100_6442_meeting_scheduling.JSON;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter {

    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        final LocalDateTime time = (LocalDateTime) value;
        out.beginObject();
        out.name("timeString").value(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        out.endObject();

    }

    @Override
    public LocalDateTime read(final JsonReader in) throws IOException {

        in.beginObject();
        in.nextName();
        final LocalDateTime time = LocalDateTime.parse(in.nextString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        in.endObject();

        return time;
    }



}
