package com.jackong.jobbooks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private ListView post_list;
    private Button search_button;

    private String selected_text;
    private String selected_text_without_slash;
    private String title;
    private String desc;
    private String spec;
    private String jobid;
    private String time;

    ArrayList<JobPostList> jobPostLists;
    private CompanyPostCustomAdapter companyPostCustomAdapter;

    private Spinner search_box;
    private ArrayAdapter<String> adapter;
    private List<String> list;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent myIntent;
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    //do nothing

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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        post_list = (ListView) findViewById(R.id.search_listview);
        search_box = (Spinner)findViewById(R.id.search_spinner);
        search_button = (Button)findViewById(R.id.search_btn);

        //Load database to spinner
        list = new ArrayList<String>();
        mDatabase.child("Application").child("Specializations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String spec = data.getValue(String.class);
                    list.add(spec);
                }
                adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                search_box.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_list.setAdapter(null);
                selected_text = search_box.getSelectedItem().toString();
                selected_text_without_slash = selected_text.replaceAll("/","_");

                if (selected_text != null){
                    mDatabase.child("Vacancy").child("TypeOfJob").child(selected_text_without_slash).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            jobPostLists = new ArrayList<>();
                            for (DataSnapshot company : dataSnapshot.getChildren()){
                                if (company!=null){
                                    for (DataSnapshot post : company.getChildren()){
                                        String job_type = post.child("JobSpec").getValue(String.class);
                                        if (job_type!=null){
                                            if (selected_text.matches(job_type)){
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
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        post_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                JobPostList job = jobPostLists.get(position);
                //Toast.makeText(getApplicationContext(), job.getJob_id(), Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(Search.this, ViewJob.class);
                myIntent.putExtra("target_job_id", job.getJob_id());
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Search.this.startActivity(myIntent);
                overridePendingTransition(0, R.anim.fadein);
                finish();
            }
        });
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
