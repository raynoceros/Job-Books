package com.jackong.jobbooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatWindow extends AppCompatActivity {

    private ListView listView;
    private Button sendButton;
    ArrayList<ChatListViewItem> chatListViewItems;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private String current_user_id;
    String time;
    String message;
    int message_type;
    String targetUserId;
    private ChatWindowCustomAdapter adapter;
    private String target_name;
    private EditText chatbox;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listView = (ListView) findViewById(R.id.chatlog_listview);
        chatbox = (EditText) findViewById(R.id.message_box);
        sendButton = (Button) findViewById(R.id.send_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        targetUserId = intent.getStringExtra("target_user_id");
        mDatabase.child("User").child(targetUserId).child("Profile").child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                target_name = dataSnapshot.getValue(String.class);
                getSupportActionBar().setTitle(target_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            current_user_id = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("User").child(current_user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    accountType = dataSnapshot.child("AccountType").getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mDatabase.child("User").child(current_user_id).child("Messages").child(targetUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatListViewItems = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        if (data != null){
                            time = data.child("Time").getValue(String.class);
                            message = data.child("Message").getValue(String.class);
                            String type = data.child("Receiver").getValue(String.class);
                            if (type != null){
                                if (type.matches(targetUserId)){
                                    message_type = 1;
                                }
                                else {
                                    message_type = 0;
                                }
                            }
                        }
                        chatListViewItems.add(new ChatListViewItem(time, message, message_type));
                        adapter = new ChatWindowCustomAdapter(chatListViewItems, message_type,  getApplicationContext());
                        listView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_user_id = currentUser.getUid();
                Date currentTime = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String time_sent = sdf.format(currentTime);
                String message_to_send = chatbox.getText().toString();
                String time_now = Long.toString(System.currentTimeMillis());
                if (!message_to_send.matches("")){

                    //add database to self message
                    mDatabase.child("User").child(current_user_id).child("ListOfChat").child(targetUserId).setValue(targetUserId);
                    mDatabase.child("User").child(current_user_id).child("Messages").child(targetUserId).child(time_now).child("Message").setValue(message_to_send);
                    mDatabase.child("User").child(current_user_id).child("Messages").child(targetUserId).child(time_now).child("Time").setValue(time_sent);
                    mDatabase.child("User").child(current_user_id).child("Messages").child(targetUserId).child(time_now).child("Sender").setValue(current_user_id);
                    mDatabase.child("User").child(current_user_id).child("Messages").child(targetUserId).child(time_now).child("Receiver").setValue(targetUserId);

                    //add database to target message
                    mDatabase.child("User").child(targetUserId).child("ListOfChat").child(current_user_id).setValue(current_user_id);
                    mDatabase.child("User").child(targetUserId).child("Messages").child(current_user_id).child(time_now).child("Message").setValue(message_to_send);
                    mDatabase.child("User").child(targetUserId).child("Messages").child(current_user_id).child(time_now).child("Time").setValue(time_sent);
                    mDatabase.child("User").child(targetUserId).child("Messages").child(current_user_id).child(time_now).child("Sender").setValue(current_user_id);
                    mDatabase.child("User").child(targetUserId).child("Messages").child(current_user_id).child(time_now).child("Receiver").setValue(targetUserId);

                    //empty chat box
                    chatbox.setText("");
                }
            }
        });

        chatbox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){

                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (chatbox.isFocused()){
                    Rect outRect = new Rect();
                    chatbox.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        chatbox.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });
    }
    //Enable Virtual Back Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            if (accountType != null){
                if (accountType.matches("Company")){
                    Intent myIntent = new Intent(ChatWindow.this, CompanyChat.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    overridePendingTransition(0, R.anim.fadein);
                    ChatWindow.this.startActivity(myIntent);
                    finish();
                }
                else {
                    Intent myIntent = new Intent(ChatWindow.this, Chat.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ChatWindow.this.startActivity(myIntent);
                    overridePendingTransition(0, R.anim.fadein);
                    finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (accountType != null){
            if (accountType.matches("Company")){
                Intent myIntent = new Intent(ChatWindow.this, CompanyChat.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(0, R.anim.fadein);
                ChatWindow.this.startActivity(myIntent);
                finish();
            }
            else {
                Intent myIntent = new Intent(ChatWindow.this, Chat.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ChatWindow.this.startActivity(myIntent);
                overridePendingTransition(0, R.anim.fadein);
                finish();
            }
        }
    }
}
