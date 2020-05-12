package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    private Button SendFriendReqButton, CancelFriendReqButton;
    private DatabaseReference profileUserRef, FriendRequestRef, FriendsRef, UserRef, PostsRef;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserId, saveCurrentDate, CURRENT_STATE;
    private String currentUserId;
    private Button myPosts;
    private int countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        IntializeFields();

        UserRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            private DataSnapshot dataSnapshot;

            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                this.dataSnapshot = dataSnapshot;
                if(dataSnapshot.exists())
                {
                    String myProfileImage = Objects.requireNonNull(dataSnapshot.child("profileimage").getValue()).toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.with(PersonProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

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
    private void IntializeFields()
    {
        userName = (TextView) findViewById(R.id.person_username);
        userProfName = (TextView) findViewById(R.id.person_profile_full_name);
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userCountry = (TextView) findViewById(R.id.person_country);
        userGender = (TextView) findViewById(R.id.person_gender);
        userRelation = (TextView) findViewById(R.id.person_relationship_status);
        userDOB = (TextView) findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic);

    }

    private void SendUserToMyPostsActivity()
    {
        Intent myPostsIntent = new Intent(PersonProfileActivity.this, UsersPostActivity.class);
        startActivity(myPostsIntent);
    }
}
