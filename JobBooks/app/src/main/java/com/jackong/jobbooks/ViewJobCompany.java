package com.jackong.jobbooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewJobCompany extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView job_title;
    private TextView job_spec;
    private TextView job_post_time;
    private TextView job_desc;
    private TextView job_salary;
    private TextView job_slot;
    private Button delete_button;

    private String targetJobId;
    private String j_title;
    private String j_spec;
    private String j_post_time;
    private String j_desc;
    private String j_salary;
    private String j_slot;
    private String key_1;
    private String key_2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job_company);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        job_title = (TextView)findViewById(R.id.job_title);
        job_spec = (TextView)findViewById(R.id.job_spec);
        job_post_time = (TextView)findViewById(R.id.job_post_time);
        job_desc = (TextView)findViewById(R.id.job_desc);
        job_salary = (TextView)findViewById(R.id.job_salary_range);
        job_slot = (TextView)findViewById(R.id.job_slot);
        delete_button = (Button)findViewById(R.id.delete_job_btn);

        Intent intent = getIntent();
        targetJobId = intent.getStringExtra("target_job_id");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!= null){
            final String uid = currentUser.getUid();
            mDatabase.child("Vacancy").child("TypeOfJob").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot type : dataSnapshot.getChildren()){
                        if (type!=null){
                            for (DataSnapshot post : type.child(uid).getChildren()){
                                String jobid = post.child("JobID").getValue(String.class);
                                if (jobid != null ){
                                    if (jobid.matches(targetJobId)){
                                        j_title = post.child("JobTitle").getValue(String.class);
                                        j_spec = post.child("JobSpec").getValue(String.class);
                                        j_post_time = post.child("PostTime").getValue(String.class);
                                        j_desc = post.child("JobDesc").getValue(String.class);
                                        j_salary = post.child("JobSalary").getValue(String.class);
                                        j_slot = post.child("JobSlot").getValue(String.class);

                                        job_title.setText(j_title);
                                        job_spec.setText(j_spec);
                                        job_post_time.setText(j_post_time);
                                        job_desc.setText(j_desc);
                                        job_salary.setText(j_salary);
                                        job_slot.setText(j_slot);

                                        key_1 = type.getKey();
                                        key_2 = post.getKey();

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

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser!= null){
                    String uid = currentUser.getUid();
                    mDatabase.child("Vacancy").child("TypeOfJob").child(key_1).child(uid).child(key_2).removeValue();
                    Toast.makeText(getApplicationContext(), "Delete Successful",Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(ViewJobCompany.this, CompanyPost.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    overridePendingTransition(0, R.anim.fadein);
                    ViewJobCompany.this.startActivity(myIntent);
                    finish();
                }
            }
        });


    }


    //Enable Virtual Back Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            Intent myIntent = new Intent(ViewJobCompany.this, CompanyPost.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            overridePendingTransition(0, R.anim.fadein);
            ViewJobCompany.this.startActivity(myIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
