package com.example.comp2100_6442_meeting_scheduling.Data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    String name;
    String email;
    int ID;
    boolean isChosen;

    public User(String name, String email, int ID) {
        this.name = name;
        this.email = email;
        this.ID = ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return ID == user.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return "User " + name + ", " + email + ", ID: " + ID;
    }

    public int getID() {
        return ID;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public static List<User> contacts(){
        List list = new ArrayList<>();
        list.add(new User("Allen","Allen1999@gmail.com", 1));
        list.add(new User("Jones","1208@gmail.com", 2));
        list.add(new User("kommer","1208@gmail.com", 3));
        list.add(new User("Tom","Tom1998@gmail.com", 4));
        list.add(new User("Adam","Adam128@gmail.com", 5));
        list.add(new User("Alex","Alex2020@gmail.com", 6));
        list.add(new User("Tomkkj","1208@gmail.com", 7));
        list.add(new User("qinzhi","qinzhi123@gmail.com", 8));
        list.add(new User("BLount","xianghongj@gmail.com", 9));
        list.add(new User("Cloud","Cloud@gmail.com", 10));
        list.add(new User("Rain","Rain@gmail.com", 11));
        return list;
    }
}
