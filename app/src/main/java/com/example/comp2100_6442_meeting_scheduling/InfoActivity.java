package com.example.comp2100_6442_meeting_scheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.Delete;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class InfoActivity extends AppCompatActivity {

//PopupWindow display method

    public void showInfoWindow(final Meeting meeting,  final CalendarActivity calendarView)  {

        final View view = calendarView.getCurrentFocus();

        String title = meeting.getTitle();
        User owner = meeting.getOwner();
        List<User> participants = meeting.getParticipants();
        String description = meeting.getDescription();

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_info, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        TextView meeting_title = popupView.findViewById(R.id.meeting_title);
        meeting_title.setText(title);
        TextView meeting_owner = popupView.findViewById(R.id.meeting_owner);
        meeting_owner.setText(owner.getName());
        TextView meeting_participants = popupView.findViewById(R.id.meeting_participants);

        //transfer list of users into a string of names
        String participants_name ="";
        for (int i=0; i< participants.size(); i++){
            participants_name += participants.get(i).getName();
            if (i<participants.size()-1){
                participants_name += ", ";
            }
        }
        meeting_participants.setText(participants_name);
        TextView meeting_description = popupView.findViewById(R.id.meeting_description);
        meeting_description.setText(description);


        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
