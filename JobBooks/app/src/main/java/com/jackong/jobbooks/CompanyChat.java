package com.jackong.jobbooks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CompanyChat extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private ListView chat_listview;
    ArrayList<ChatListModel> chatListModels;
    String username;
    String image_link;
    String last_message;
    String last_msg_time;
    String targetUserID;
    private CustomAdapter adapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent myIntent;
            switch (item.getItemId()) {
                case R.id.navigation_company_chat:
                    //do nothing
                    return true;

                case R.id.navigation_company_post:

                    myIntent = new Intent(CompanyChat.this, CompanyPost.class);
                    CompanyChat.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);
                    return true;

                case R.id.navigation_company_profile:
                    myIntent = new Intent(CompanyChat.this, CompanyProfile.class);
                    CompanyChat.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_chat);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_company);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.navigation_company_chat);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        chat_listview = (ListView)findViewById(R.id.companychat_listview);
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            final String current_user_uid = currentUser.getUid();
            mDatabase.child("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatListModels = new ArrayList<>();
                    for (DataSnapshot allPPL : dataSnapshot.child(current_user_uid).child("ListOfChat").getChildren()){
                        if (allPPL!=null){
                            targetUserID = allPPL.getValue(String.class);
                            if (targetUserID!=null){
                                username = dataSnapshot.child(targetUserID).child("Profile").child("Name").getValue(String.class);
                                image_link = dataSnapshot.child(targetUserID).child("Profile").child("Image").getValue(String.class);
                                if (image_link != null) {
                                    if (image_link.matches("-")) {
                                        image_link = "https://firebasestorage.googleapis.com/v0/b/job-books-7f74b.appspot.com/o/profile_images%2Fprofiledummy.jpg?alt=media&token=cdcf2561-6728-479e-9aaa-61b8d2e7d370";
                                    }
                                }
                                for (DataSnapshot data : dataSnapshot.child(current_user_uid).child("Messages").child(targetUserID).getChildren()){
                                    if (data!=null){
                                        last_message = data.child("Message").getValue(String.class);
                                        last_msg_time = data.child("Time").getValue(String.class);
                                        if (last_message!= null){
                                            if (last_message.length() > 30){
                                                last_message = last_message.substring(0,26);
                                                last_message = last_message + "...";
                                            }
                                        }
                                    }
                                }
                                chatListModels.add(new ChatListModel(targetUserID, username, last_message, last_msg_time, image_link));
                                adapter = new CustomAdapter(chatListModels, getApplicationContext());
                                chat_listview.setAdapter(adapter);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        chat_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ChatListModel chatListModel = chatListModels.get(position);
                //Toast.makeText(getApplicationContext(), chatListModel.getUser_id(), Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(CompanyChat.this, ChatWindow.class);
                myIntent.putExtra("target_user_id", chatListModel.getUser_id());
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CompanyChat.this.startActivity(myIntent);
                overridePendingTransition(0, R.anim.fadein);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

    }


}
