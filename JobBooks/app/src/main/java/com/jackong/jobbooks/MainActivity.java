package com.jackong.jobbooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            goToLogin();
        } else {
            final String uid = currentUser.getUid();
            mDatabase.child("User").child(uid).child("AccountType").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        String account_type = dataSnapshot.getValue().toString();

                        //check if is student
                        if (account_type.matches("Student")){
                            mDatabase.child("User").child(uid).child("FirstTime").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        String check_if_first_time = dataSnapshot.getValue().toString();
                                        if (check_if_first_time != null) {
                                            if (check_if_first_time.matches("yes")) {
                                                //go to first time
                                                goToFirstTime();
                                            } else {
                                                //go to student search
                                                goToStudentSearch();
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        //check if is company
                        else if (account_type.matches("Company")){
                            gotocompanyjob();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void goToLogin(){
        Intent startIntent = new Intent(MainActivity.this, Login.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        overridePendingTransition(0, R.anim.fadein);
        finish();
    }

    private void goToStudentSearch(){
        Intent startIntent = new Intent(MainActivity.this, Search.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        overridePendingTransition(0, R.anim.fadein);
        finish();
    }

    private void goToFirstTime(){
        Intent startIntent = new Intent(MainActivity.this, FirstTime.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        overridePendingTransition(0,0);
        finish();
    }

    private void gotocompanyjob(){
        Intent startIntent = new Intent(MainActivity.this, CompanyPost.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        overridePendingTransition(0,0);
        finish();
    }
}
