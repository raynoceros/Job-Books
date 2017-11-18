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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CompanyPost extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private ListView post_list;
    private String title;
    private String desc;
    private String spec;
    private String jobid;
    private String time;
    ArrayList<JobPostList> jobPostLists;
    private CompanyPostCustomAdapter companyPostCustomAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent myIntent;
            switch (item.getItemId()) {
                case R.id.navigation_company_chat:

                    myIntent = new Intent(CompanyPost.this, CompanyChat.class);
                    CompanyPost.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);
                    return true;

                case R.id.navigation_company_post:
                    //do nothing
                    return true;

                case R.id.navigation_company_profile:
                    myIntent = new Intent(CompanyPost.this, CompanyProfile.class);
                    CompanyPost.this.startActivity(myIntent);
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
        setContentView(R.layout.activity_company_post);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_company);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.navigation_company_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        post_list = (ListView) findViewById(R.id.jobpost_lv);


        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            final String uid = currentUser.getUid();
            mDatabase.child("Vacancy").child("TypeOfJob").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    jobPostLists = new ArrayList<>();
                    for (DataSnapshot types : dataSnapshot.getChildren()){
                        if (types!=null){
                            for (DataSnapshot post : types.child(uid).getChildren()){
                                desc = post.child("JobDesc").getValue(String.class);
                                title = post.child("JobTitle").getValue(String.class);
                                spec = post.child("JobSpec").getValue(String.class);
                                jobid = post.child("JobID").getValue(String.class);
                                time = post.child("PostTime").getValue(String.class);
                                if (desc!= null){
                                    if (desc.length() > 25){
                                        desc = desc.substring(0,21);
                                        desc = desc + "...";
                                    }
                                }
                                jobPostLists.add(new JobPostList(spec, title, desc, jobid, time));
                                companyPostCustomAdapter = new CompanyPostCustomAdapter( jobPostLists, getApplicationContext());
                                post_list.setAdapter(companyPostCustomAdapter);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        post_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                JobPostList job = jobPostLists.get(position);
                //Toast.makeText(getApplicationContext(), job.getJob_id(), Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(CompanyPost.this, ViewJobCompany.class);
                myIntent.putExtra("target_job_id", job.getJob_id());
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CompanyPost.this.startActivity(myIntent);
                overridePendingTransition(0, R.anim.fadein);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    public void AddPostOnClick(View v){
        Intent myIntent = new Intent(CompanyPost.this, AddPost.class);
        CompanyPost.this.startActivity(myIntent);
    }

}
