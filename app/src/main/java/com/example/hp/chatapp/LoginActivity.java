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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton,share;
    private EditText UserEmail,UserPassword;
    private TextView needNewAccountLink;
    FirebaseAuth auth;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Initialize();
        share=(Button)findViewById(R.id.share_button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,offlineActivity.class);
                startActivity(intent);
            }
        });
        auth=FirebaseAuth.getInstance();
        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();

            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });
    }

    private void AllowUserToLogin() {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        if(TextUtils.isEmpty(email))
            Toast.makeText(this,"please enter Email Address... ",Toast.LENGTH_SHORT).show();

        if(TextUtils.isEmpty(password))
            Toast.makeText(this,"please enter Password... ", Toast.LENGTH_SHORT).show();


        else{
            progressBar.setTitle("Sign in");
            progressBar.setMessage("Please wait....");
            progressBar.setCanceledOnTouchOutside(true);
            progressBar.show();

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()){
                      sendUserToMainActivity();
                      Toast.makeText(LoginActivity.this,"Logged in successfully",Toast.LENGTH_SHORT).show();
                      progressBar.dismiss();
                  }
                  else{
                      String message=task.getException().toString();
                      Toast.makeText(LoginActivity.this,"Error "+message,Toast.LENGTH_SHORT).show();
                      progressBar.dismiss();
                  }


                }
            });

        }
    }

    private void Initialize() {
        LoginButton=(Button)findViewById(R.id.login_button);
        UserEmail=(EditText)findViewById(R.id.login_email);
        UserPassword=(EditText)findViewById(R.id.login_password);
        needNewAccountLink=(TextView)findViewById(R.id.need_new_account);

        progressBar=new ProgressDialog(this);

    }


    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
    private void sendUserToRegisterActivity() {
        Intent Registerintent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(Registerintent);
    }
}
