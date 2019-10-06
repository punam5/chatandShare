package com.example.hp.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.*;

public class RegisterActivity extends AppCompatActivity {
    private Button registerButton;
    private EditText UserEmail,UserPassword;
    private TextView AlreadyHaveAnAccount;
    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    private ProgressDialog progressBar;
    private Button share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Initialize();
        auth=FirebaseAuth.getInstance();
        rootRef=FirebaseDatabase.getInstance().getReference();
        share=(Button)findViewById(R.id.share_button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,offlineActivity.class);
                startActivity(intent);
            }
        });
        AlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             sendUserToLoginActivity();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        if(TextUtils.isEmpty(email))
            Toast.makeText(this,"please enter Email Address... ",Toast.LENGTH_SHORT).show();

        if(TextUtils.isEmpty(password))
           Toast.makeText(this,"please enter Password... ", Toast.LENGTH_SHORT).show();


        else{
            progressBar.setTitle("Creating new Account");
            progressBar.setMessage("Please wait....");
            progressBar.setCanceledOnTouchOutside(true);
            progressBar.show();

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String currentUserId=auth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentUserId).setValue("");
                        sendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this,"Account Created Successfully...",Toast.LENGTH_SHORT).show();

                        progressBar.dismiss();
                    }
                    else{
                        String message=task.getException().toString();
                        Toast.makeText(RegisterActivity.this,"Error "+message,Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();

                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void sendUserToLoginActivity() {
        Intent loginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }


    private void Initialize() {
        registerButton=(Button)findViewById(R.id.register_button);
        UserEmail=(EditText)findViewById(R.id.register_email);
        UserPassword=(EditText)findViewById(R.id.register_password);
        AlreadyHaveAnAccount=(TextView)findViewById(R.id.already_have_an_account);
        progressBar=new ProgressDialog(this);
    }
    private void sendUserToLoginrActivity() {
        Intent loginintent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginintent);
    }
}
