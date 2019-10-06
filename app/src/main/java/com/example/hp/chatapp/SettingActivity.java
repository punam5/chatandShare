package com.example.hp.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button UpdateAccountsSettings;
    private EditText userName,userStatus;
    private CircleImageView userProfileImage;
    private DatabaseReference rootref;
    private FirebaseAuth auth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();
        rootref=FirebaseDatabase.getInstance().getReference();
        initialize();
        userName.setVisibility(View.INVISIBLE);
        UpdateAccountsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        retriveUserInfo();
    }




    private void initialize() {
        UpdateAccountsSettings=(Button)findViewById(R.id.set_setting_button);
        userName=(EditText)findViewById(R.id.set_user_name);
        userStatus=(EditText)findViewById(R.id.set_profile_status);
        userProfileImage=(CircleImageView) findViewById(R.id.set_profile_image);


    }
    private void UpdateSettings() {
     String setUserName=userName.getText().toString();
        String setStatus=userStatus.getText().toString();
        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this,"Please write your user name..",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setStatus)){
            Toast.makeText(this,"Please write your user status..",Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,String>profileMap=new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUserName);
            profileMap.put("status",setStatus);
            rootref.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(SettingActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();


                            }
                            else
                                {
                                    String message=task.getException().toString();
                                    Toast.makeText(SettingActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                                }
                        }
                    });

        }

    }
    private void retriveUserInfo() {
        rootref.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")&&(dataSnapshot.hasChild("image")))){

                    String retriveName=dataSnapshot.child("name").getValue().toString();
                    String retriveStatus=dataSnapshot.child("status").getValue().toString();
                    String retriveImage=dataSnapshot.child("image").getValue().toString();
                    userName.setText(retriveName);
                    userStatus.setText(retriveStatus);

                }
                else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))){
                    String retriveName=dataSnapshot.child("name").getValue().toString();
                    String retriveStatus=dataSnapshot.child("status").getValue().toString();

                    userName.setText(retriveName);
                    userStatus.setText(retriveStatus);

                }
                else{
                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingActivity.this,"Please set up your profile first...",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(SettingActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}
