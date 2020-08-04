package com.example.comp2100_6442_meeting_scheduling;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.example.comp2100_6442_meeting_scheduling.FileOperation.Read;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.Submit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateMeetingActivity extends AppCompatActivity {

    private TextView tvOrg;
    private TextView tvAttend;
    private TextView tvDeadline;
    private TextView tvDuration;
//    List<User_Skye> orgUsers = new ArrayList();
//    List<User_Skye> attendUsers = new ArrayList();
    private RecyclerView recyclerStart;
    private TimeAdapter adapter;
    private List<LocalDateTime> startTimeList = new ArrayList<>();
//    private List<User_Skye> selectOrg;
    List<User> attendants = new LinkedList<>();
    private EditText etTitle;
    private EditText etTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
        tvOrg = findViewById(R.id.tv_org);
        tvAttend = findViewById(R.id.tv_attend);
        etTitle = findViewById(R.id.et_title);
        etTheme = findViewById(R.id.et_theme);
        tvDuration = findViewById(R.id.tv_duration);
        tvDeadline = findViewById(R.id.tv_deadline);
        recyclerStart = findViewById(R.id.recycler);
        recyclerStart.setLayoutManager(new LinearLayoutManager(this));
        startTimeList.add(LocalDateTime.now());
        adapter = new TimeAdapter(this, startTimeList);
        recyclerStart.setAdapter(adapter);
        findViewById(R.id.bt_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeList.add(LocalDateTime.now());
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setOnItemClickListener(new TimeAdapter.onItemClickListener() {
            @Override
            public void onclickListener(int position) {
                selectStartDate(position);
            }
        });
        findViewById(R.id.ll_org).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                selectOrg();
                showOrganizer();
            }

        });
        findViewById(R.id.ll_attend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectAttend();
                }
            });
        findViewById(R.id.ll_duration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDuration();
            }
        });
        findViewById(R.id.ll_deadline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeadLine();
            }
        });
        findViewById(R.id.test_first_run_and_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

    }

    private void submit() {
//        if (orgUsers==null||orgUsers.size()==0){
//            Toast.makeText(this,"Please select Organizer",Toast.LENGTH_SHORT).show();
//            return;
//        }
        String title = etTitle.getText().toString();
        String theme = etTheme.getText().toString();


        String durationString = tvDuration.getText().toString();



        String deadlineString = tvDeadline.getText().toString();
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this,"Please enter a title",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(theme)){
            Toast.makeText(this,"Please enter a theme",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(durationString)){
            Toast.makeText(this,"Please select Meeting duration",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(deadlineString)){
            Toast.makeText(this,"Please select deadline",Toast.LENGTH_SHORT).show();
            return;
        }
        if (startTimeList==null||startTimeList.size()==0){
            Toast.makeText(this,"Please select  start time",Toast.LENGTH_SHORT).show();
            return;
        }
        if (attendants ==null|| attendants.size()==0){
            Toast.makeText(this,"Please select Attend Users",Toast.LENGTH_SHORT).show();
            return;
        }
        //create new object to pass parameter
//        Meeting meet = new Meeting();
//        meet.orgUsers = orgUsers;
//        meet.title = title;
//        meet.theme = theme;
//        meet.duration = durationString;
//        meet.deadline = deadline;
//        meet.startTimeList = startTimeList;
//        meet.attendUsers = attendUsers;

        User organizer = Read.getLocalUser(this);
        LocalDateTime deadline = LocalDateTime.parse(deadlineString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
//        for (String startingTime : startTimeList)

        // TODO
        String tag = "Example Tag";

        Duration duration = Duration.parse("PT" + durationString);

        Meeting meeting = new Meeting(duration, title, tag, theme, organizer, attendants, deadline, startTimeList.toArray(new LocalDateTime[startTimeList.size()]));
        // duration, title, tag, description, owner, participant, deadline, startingTimes

//        Intent intent = new Intent(this, SubmitMeetingActivity.class);
//        intent.putExtra("data", meet);//pass value
//        startActivity(intent);
        try {
            String result = new Submit(this, meeting).execute().get();
            Boolean success = ! result.equals("Exception Occurred in Submit");
            if (success) {
                finish();
                sendEmail(result, meeting);
            } else {
                Toast.makeText(this,"Submission unsuccessful, please try again later. ",Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void sendEmail(String uuid, Meeting meeting) {
        // prepare addresses
        String ad= "";
        List<User> users = meeting.getParticipants();
        for (User user : users) {
            ad = ad + user.getEmail() + ",";
        }
        String[] addresses = ad.split(",");
        // prepare email subject
        String subject = "New meeting invitation from " + meeting.getOwner().getName() + ": " + meeting.getTitle();

        composeEmail(addresses, subject, UUID.fromString(uuid));
    }

    // adapt from https://developer.android.com/guide/components/intents-common#java
    public void composeEmail(String[] addresses, String subject, UUID uuid) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "http://comp2100_6442_meeting_scheduling.example.com/SelectionActivity/?uuid="+ uuid.toString()); // need fromString() to change back to UUID instance
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void selectStartDate(final int position) {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                startTimeList.set(position, LocalDateTime.parse(getTime(date), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                adapter.notifyDataSetChanged();
            }
        }).setType(new boolean[]{true, true, true, true, true, false}).build();
        pvTime.show();
    }
    private void selectDeadLine() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tvDeadline.setText(getTime(date));
            }
        }).setType(new boolean[]{true, true, true, true, true, false}).build();
        pvTime.show();
    }

    private String getTime(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    private void selectDuration() {
        if (durationList.size() == 0){
            initDuration();
        }
        final String[] toBeStored = durationList.toArray(new String[durationList.size()]);
        new AlertDialog.Builder(this)
                .setTitle("please select duration")
                .setSingleChoiceItems(toBeStored, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvDuration.setText(toBeStored[i]);
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    private void selectAttend() {
        Intent intent = new Intent(this,UserListActivity.class);

        Gson gson = new Gson();
        String usersJson = gson.toJson(attendants);
        intent.putExtra("users", usersJson);
        startActivityForResult(intent,101);
    }

    private void showOrganizer() {
        tvOrg.append(Read.getLocalUser(this).getName());
    }

//    private void selectOrg() {
//        Intent intent = new Intent(this,UserListActivity.class);
//        intent.putExtra("users", (Serializable) orgUsers);
//        startActivityForResult(intent,100);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==100&&resultCode==RESULT_OK){
//            selectOrg = (List<User>) data.getSerializableExtra("users");
//            orgUsers.clear();
//            tvOrg.setText("");
//            if (selectOrg!=null&&selectOrg.size()>0){
//                orgUsers.addAll(selectOrg);
//                for (int i = 0; i < orgUsers.size(); i++) {
//                    tvOrg.append(orgUsers.get(i).getName());
//                    if (i<orgUsers.size()-1){
//                        tvOrg.append(",");
//                    }
//                }
//            }
//        }
        if (requestCode == 101 && resultCode == RESULT_OK) {
            Gson gson = new Gson();
            final Type USER_LIST_TYPE = new TypeToken<List<User>>() {}.getType();
            String userJsonInIntent = data.getStringExtra("users");
//            System.out.println(userJsonInIntent);
            List<User> selectedUsers = gson.fromJson(userJsonInIntent, USER_LIST_TYPE);
//            System.out.println(selectedUsers == null);
            attendants.clear();
            tvAttend.setText("");
            if (selectedUsers != null && selectedUsers.size() > 0){
                attendants.addAll(selectedUsers);
                for (int i = 0; i < attendants.size(); i++) {
                    tvAttend.append(attendants.get(i).getName());
                    if (i < attendants.size() - 1){
                        tvAttend.append(",");
                    }
                }
            }
        }
    }


    private ArrayList<String> durationList = new ArrayList<>();
    private void initDuration(){
        for (int i = 0; i < 20; i++) {
//            int minutes = (15*(i+1));
//            if (minutes / 60 < 1) {
//                durationList.add(minutes +"minutes");
//            } else {
//                if (minutes / 60 == 1) {
//                    durationList.add("1 hour and" + minutes % 60 +"minutes");
//                } else {
//                    durationList.add(minutes / 60 + "hours and" + minutes % 60 +"minutes");
//                }
//            }
            Duration duration = Duration.ofMinutes(15 * (i + 1));
            durationList.add(duration.toString().substring(2));
        }
    }
}
