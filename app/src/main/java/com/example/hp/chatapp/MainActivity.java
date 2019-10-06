package com.example.hp.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private controller Controller;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        rootRef=FirebaseDatabase.getInstance().getReference();
        toolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat & Share");
        viewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
        Controller=new controller(getSupportFragmentManager());
        viewPager.setAdapter(Controller);

        tabLayout=(TabLayout)findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);





    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser==null){

            sendUserToLoginActivity();
        }
        else{
            verifyUserExistance();
        }
    }

    private void verifyUserExistance() {
        String currentUserId=auth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();

                }
                else{
                    sendUserToSettingActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.options_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId()==R.id.main_logout_option){
             auth.signOut();
             sendUserToLoginActivity();

         }

        if(item.getItemId()==R.id.main_setting_option){

             sendUserToSettingActivity();
        }
        if(item.getItemId()==R.id.main_create_group_option){
             RequestNewGroup();

        }

        if(item.getItemId()==R.id.main_find_friends_option){

         sendUserToFindFriendsActivity();

        }
      return  true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");
        final EditText groupNameField=new EditText(MainActivity.this);
        groupNameField.setHint("e.g Office Group");
        builder.setView(groupNameField);
       builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

               String groupName=groupNameField.getText().toString();
               if(TextUtils.isEmpty(groupName)){
                   Toast.makeText(MainActivity.this,"Please Enter Group Name...",Toast.LENGTH_SHORT).show();
               }
               else{

                   createNewGroup(groupName);
               }

           }
       });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel() ;
            }
        });
        builder.show();


    }

    private void createNewGroup(final String groupName) {
      rootRef.child("Groups").child(groupName).setValue("")
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          Toast.makeText(MainActivity.this,groupName+"  group is created successfully",Toast.LENGTH_SHORT).show();
                      }

                  }
              });

    }

    private void sendUserToSettingActivity() {
        Intent settingintent=new Intent(MainActivity.this,SettingActivity.class);
        settingintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingintent);
        finish();
    }
    private void sendUserToLoginActivity() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void sendUserToFindFriendsActivity() {
        Intent intent=new Intent(MainActivity.this,FindFriendActivity.class);

        startActivity(intent);

    }
}
