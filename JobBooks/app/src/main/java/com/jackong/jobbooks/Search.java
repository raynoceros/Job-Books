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

public class Search extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent myIntent;
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    myIntent = new Intent(Search.this, Home.class);
                    Search.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_search:
                    //do nothing

                    return true;

                case R.id.navigation_notifications:

                    myIntent = new Intent(Search.this, Notification.class);
                    Search.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_chat:

                    myIntent = new Intent(Search.this, Chat.class);
                    Search.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_profile:

                    myIntent = new Intent(Search.this, Profile.class);
                    Search.this.startActivity(myIntent);
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
        setContentView(R.layout.activity_search);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.navigation_search);

        //Link variables
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        Intent startIntent = new Intent(Search.this, MainActivity.class);
        startActivity(startIntent);
        finish();
    }
}
