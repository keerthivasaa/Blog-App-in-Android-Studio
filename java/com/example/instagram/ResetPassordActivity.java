package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassordActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button ResetPasswordEmailButton;
    private EditText ResetEmailInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_passord);

        mAuth = FirebaseAuth.getInstance();

        mToolbar=(Toolbar) findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        ResetPasswordEmailButton = (Button) findViewById(R.id.send_email);
        ResetEmailInput = (EditText) findViewById(R.id.reset_email);

        ResetPasswordEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = ResetEmailInput.getText().toString();

                if (TextUtils.isEmpty(userEmail))
                {
                    Toast.makeText(ResetPassordActivity.this,"Please write your valid address...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(ResetPassordActivity.this,"Please check your email Account, If you want to Reset your Password",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassordActivity.this,LoginActivity.class));
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPassordActivity.this,"Error Occurred" + message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
