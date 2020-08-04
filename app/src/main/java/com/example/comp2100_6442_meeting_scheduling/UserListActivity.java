package com.example.comp2100_6442_meeting_scheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private List<User> localContacts;
    private UserAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        localContacts = User.contacts();
        Gson gson = new Gson();
        final Type USER_LIST_TYPE = new TypeToken<List<User>>() {}.getType();
//        String userJsonInIntent
        List<User> selectOrg = gson.fromJson(getIntent().getStringExtra("users"), USER_LIST_TYPE);
        for (int i = 0; i < localContacts.size(); i++) {
            User data = localContacts.get(i);
            for (int j = 0; j < selectOrg.size(); j++) {
                if (data.getID() == (selectOrg.get(j).getID())){
                    data.setChosen(selectOrg.get(j).isChosen());
                }
            }
        }
        adapter = new UserAdapter(this, localContacts);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new UserAdapter.onItemClickListener() {
            @Override
            public void onclickListener(int position) {
                localContacts.get(position).setChosen(!localContacts.get(position).isChosen());
                adapter.notifyItemChanged(position);
            }
        });
        findViewById(R.id.bt_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List selectUser = new ArrayList();
                for (int i = 0; i < localContacts.size(); i++) {
                    if (localContacts.get(i).isChosen()){
                        selectUser.add(localContacts.get(i));
                    }
                }
                Intent intent = new Intent();


                // Convert List of User to Json
                Gson gson = new Gson();
                String usersJson = gson.toJson(selectUser);
                System.out.println(usersJson);


                intent.putExtra("users", usersJson);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
