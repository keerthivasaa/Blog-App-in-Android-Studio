package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    DatabaseReference profileUserRef, PostsRef;
    FirebaseAuth mAuth;
    String currentUserId;
    private Button myPosts;
    private int countPosts = 0;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        userName = (TextView) findViewById(R.id.my_username);
        userProfName = (TextView) findViewById(R.id.my_profile_full_name);
        userStatus = (TextView) findViewById(R.id.my_profile_status);
        userCountry = (TextView) findViewById(R.id.my_country);
        userGender = (TextView) findViewById(R.id.my_gender);
        userRelation = (TextView) findViewById(R.id.my_relationship_status);
        userDOB = (TextView) findViewById(R.id.my_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.my_profile_pic);

        myPosts = (Button) findViewById(R.id.my_post_button);
        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToMyPostsActivity();
            }
        });

        PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId + "\uf8ff" )
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            countPosts = (int) dataSnapshot.getChildrenCount();
                            myPosts.setText(countPosts + "  Posts" );
                        }
                        else
                        {
                            myPosts.setText("0 Posts");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        profileUserRef.addValueEventListener(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String myProfileImage = Objects.requireNonNull(dataSnapshot.child("profileimage").getValue()).toString();
                    String myUserName = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                    String myProfileName = Objects.requireNonNull(dataSnapshot.child("fullname").getValue()).toString();
                    String myProfileStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    String myDOB = Objects.requireNonNull(dataSnapshot.child("dob").getValue()).toString();
                    String myCountry = Objects.requireNonNull(dataSnapshot.child("country").getValue()).toString();
                    String myGender = Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString();
                    String myRelationStatus = Objects.requireNonNull(dataSnapshot.child("relationshipstatus").getValue()).toString();

                    Picasso.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText("@"+ myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("DOB:"+ myDOB);
                    userCountry.setText("Country:"+ myCountry);
                    userGender.setText("Gender:"+ myGender);
                    userRelation.setText("Relationship:"+ myRelationStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToMyPostsActivity()
    {
        Intent myPostsIntent = new Intent(ProfileActivity.this, MyPostsActivity.class);
        startActivity(myPostsIntent);
    }
}
