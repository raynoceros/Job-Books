package com.jackong.jobbooks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Notification extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent myIntent;
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    myIntent = new Intent(Notification.this, Home.class);
                    Notification.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_search:

                    myIntent = new Intent(Notification.this, Search.class);
                    Notification.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;

                case R.id.navigation_notifications:
                    //do nothing

                    return true;
                case R.id.navigation_chat:

                    myIntent = new Intent(Notification.this, Chat.class);
                    Notification.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_profile:

                    myIntent = new Intent(Notification.this, Profile.class);
                    Notification.this.startActivity(myIntent);
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
        setContentView(R.layout.activity_notification);

        //Link variables
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.navigation_notifications);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            goToMain();
        }
    }

    private void goToMain() {
        Intent startIntent = new Intent(Notification.this, Login.class);
        startActivity(startIntent);
        finish();
    }
}


