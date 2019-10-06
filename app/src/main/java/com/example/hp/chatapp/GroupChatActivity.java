package com.example.hp.chatapp;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private ImageButton sendMessageButton;
    private DatabaseReference userRef,groupNameRef,groupMessageKeyRef;
    private EditText userMessageInput;
    private ScrollView scrollView;
    private FirebaseAuth auth;
    private TextView displayTextMessages;
    private String currentGroupName,currentUserId,currentUserName,currentDate,currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this,currentGroupName,Toast.LENGTH_SHORT).show();
        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        InitializeFields();
        getUserInfo();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageToDatabase();
                userMessageInput.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    displayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void InitializeFields() {
        toolbar=(Toolbar)findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMessageButton=(ImageButton)findViewById(R.id.send_message_button);
        userMessageInput=(EditText)findViewById(R.id.input_group_message);
        displayTextMessages=(TextView)findViewById(R.id.group_chat_text_display);
        scrollView=(ScrollView)findViewById(R.id.my_scroll_view);

    } private void getUserInfo() {
          userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if(dataSnapshot.exists()){
                      currentUserName=dataSnapshot.child("name").getValue().toString();
                  }

              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });

    }
    private void saveMessageToDatabase() {
        String message=userMessageInput.getText().toString();
        String messageKey=groupNameRef.push().getKey();
        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this,"Please write message..",Toast.LENGTH_SHORT).show();

        }
        else{
            Calendar calendarForDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd, yyyy");
            currentDate=currentDateFormat.format(calendarForDate.getTime());

            Calendar calendarForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calendarForTime.getTime());
            HashMap<String,Object> groupMessageKey=new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);
            groupMessageKeyRef=groupNameRef.child(messageKey);
            HashMap<String,Object>messageInfoMap=new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);

        }
    }

    private void displayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String)((DataSnapshot)iterator.next()).getValue();
            displayTextMessages.append(chatName+": \n"+chatMessage+"\n"+chatTime+"    "+chatDate+"\n\n\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            Object object = dataSnapshot.getValue(Object.class);
            String json = new Gson().toJson(object);
           // Toast.makeText(GroupChatActivity.this,json,Toast.LENGTH_LONG).show();


        }
    }
}
