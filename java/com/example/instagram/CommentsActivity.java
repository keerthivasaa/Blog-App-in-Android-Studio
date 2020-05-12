package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity
{
    private ImageButton postCommentButton;
    private EditText CommentInputText;
    private RecyclerView CommentsList;

    private DatabaseReference UsersRef,PostsRef;
    private FirebaseAuth mAuth;

    private String Post_Key, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("comments");

        CommentsList = (RecyclerView) findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        CommentInputText = (EditText) findViewById(R.id.comment_input);
        postCommentButton = (ImageButton) findViewById(R.id.comment_post_button);

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    private DataSnapshot dataSnapshot;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        this.dataSnapshot = dataSnapshot;
                        if (dataSnapshot.exists())
                        {
                            String userName = dataSnapshot.child("username").getValue().toString();

                            ValidateComment(userName);

                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>
                (
                        Comments.class,
                        R.layout.all_comments_layout,
                        CommentsViewHolder.class,
                        PostsRef

        ) {
            @Override
            protected void populateViewHolder(CommentsViewHolder commentsViewHolder, Comments comments, int i)
            {
                commentsViewHolder.setUsername(comments.getUsername());
                commentsViewHolder.setComment(comments.getComment());
                commentsViewHolder.setDate(comments.getDate());
                commentsViewHolder.setTime(comments.getTime());
            }
        };

        CommentsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public CommentsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username)
        {
            TextView myUserName = (TextView) mView.findViewById(R.id.comment_username);
            myUserName.setText("@"+username+" ");
        }
        public void setComment(String comment)
        {
            TextView myComment = (TextView) mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }
        public void setDate(String date)
        {
            TextView myDate = (TextView) mView.findViewById(R.id.comment_date);
            myDate.setText(date);
        }
        public void setTime(String time)
        {
            TextView myTime = (TextView) mView.findViewById(R.id.comment_time);
            myTime.setText(time);
        }
    }

    private void ValidateComment(String userName)
    {
        String commentText = CommentInputText.getText().toString();

        if (TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "please write text to comment...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordTime.getTime());

            final String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid",current_user_id);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time",saveCurrentTime);
            commentsMap.put("username",userName);

            PostsRef.child(RandomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(CommentsActivity.this,"you have commented successfully",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(CommentsActivity.this,"Error Occurred, try again...",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
