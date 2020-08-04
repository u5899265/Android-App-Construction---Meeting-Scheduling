package com.example.comp2100_6442_meeting_scheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Write;

public class FirstRunActivity extends AppCompatActivity {

    User localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);
    }

    public void writeInfo(View view) {
        localUser = new User("Zheng Huang", "amoyhuangzheng@yahoo.com", 6134872);

        new Write(this, localUser);
        new Write(this, false);
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(intent);
        finish();
    }
}
