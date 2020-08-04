package com.example.comp2100_6442_meeting_scheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Read;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyMeetingsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private List<UUID> myMeetings;
    private MeetingAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myMeetings = Read.getMyMeetings(this);
//        for (int i = 0; i < myMeetings.size(); i++) {
//            UUID uuid = myMeetings.get(i);
//        }
        adapter = new MeetingAdapter(this, myMeetings);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MeetingAdapter.onItemClickListener() {
            @Override
            public void onclickListener(int position) {
                Intent intent = new Intent(getApplicationContext(), ConfirmMeetingActivity.class);
                String uuid = myMeetings.get(position).toString();
                System.out.println(uuid);
                intent.putExtra("id", uuid);
                System.out.println("This is the uuid " + myMeetings.get(position).toString());
                startActivity(intent);


//                myMeetings.get(position).setChosen(!myMeetings.get(position).isChosen());
//                adapter.notifyItemChanged(position);
            }
        });
//        findViewById(R.id.bt_choose).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                List selectUser = new ArrayList();
////                for (int i = 0; i < myMeetings.size(); i++) {
////                    if (myMeetings.get(i).isChosen()){
////                        selectUser.add(myMeetings.get(i));
////                    }
////                }
////                Intent intent = new Intent();
////
////
////                // Convert List of User to Json
////                Gson gson = new Gson();
////                String usersJson = gson.toJson(selectUser);
////                System.out.println(usersJson);
////
////
////                intent.putExtra("users", usersJson);
////                setResult(RESULT_OK, intent);
////                finish();
//            }
//        });
    }
}
