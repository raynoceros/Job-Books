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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddPost extends AppCompatActivity {

    //FireBase Declaration
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText title;
    private EditText slot;
    private EditText description;
    private EditText salary;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        spinner = (Spinner) findViewById(R.id.specialization_list);
        title = (EditText)findViewById(R.id.job_title_et);
        slot = (EditText)findViewById(R.id.job_slot_et);
        description = (EditText)findViewById(R.id.job_description_et);
        salary = (EditText)findViewById(R.id.job_salary_et);

        list = new ArrayList<String>();

        //Load database to spinner
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
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Enable Virtual Back Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            //end this activity
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void AddPostClick(View v){
        //set Auth
        mAuth = FirebaseAuth.getInstance();
        //set database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Date currentTime = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time_sent = sdf.format(currentTime);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            String uid = currentUser.getUid();
            String timemill = Long.toString(System.currentTimeMillis());
            String str_title = title.getText().toString();
            String str_slot = slot.getText().toString();
            String str_desc = description.getText().toString();
            String str_salary = salary.getText().toString();
            String str_job_type = spinner.getSelectedItem().toString();
            String job_type_directory = str_job_type.replaceAll("/", "_");

            if (str_title.matches("") || str_slot.matches("") || str_desc.matches("") || str_salary.matches("")){
                Toast.makeText(getApplicationContext(), "Cannot create job post, some field is empty", Toast.LENGTH_SHORT).show();
            }
            else{
                //Add to database
                mDatabase.child("Vacancy").child("TypeOfJob").child(job_type_directory).child(uid).child(timemill).child("JobTitle").setValue(str_title);
                mDatabase.child("Vacancy").child("TypeOfJob").child(job_type_directory).child(uid).child(timemill).child("JobSlot").setValue(str_slot);
                mDatabase.child("Vacancy").child("TypeOfJob").child(job_type_directory).child(uid).child(timemill).child("JobDesc").setValue(str_desc);
                mDatabase.child("Vacancy").child("TypeOfJob").child(job_type_directory).child(uid).child(timemill).child("JobSalary").setValue(str_salary);
                mDatabase.child("Vacancy").child("TypeOfJob").child(job_type_directory).child(uid).child(timemill).child("PostTime").setValue(time_sent);
                mDatabase.child("Vacancy").child("TypeOfJob").child(job_type_directory).child(uid).child(timemill).child("JobSpec").setValue(str_job_type);
                mDatabase.child("Vacancy").child("TypeOfJob").child(job_type_directory).child(uid).child(timemill).child("JobID").setValue(uid+timemill);
                mDatabase.child("Vacancy").child("TypeOfJob").child(job_type_directory).child(uid).child(timemill).child("JobCompanyID").setValue(uid);
                Toast.makeText(AddPost.this, "Add success.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else {
            Toast.makeText(AddPost.this, "Can't get Authentication.", Toast.LENGTH_SHORT).show();
        }




    }
}
