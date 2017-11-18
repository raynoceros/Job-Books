package com.jackong.jobbooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewJob extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView job_title;
    private TextView job_spec;
    private TextView job_post_time;
    private TextView job_desc;
    private TextView job_salary;
    private TextView job_slot;
    private Button message_btn;
    private TextView company_name;
    private TextView company_email;
    private TextView company_contact;
    private TextView company_address;
    private TextView company_desc;
    private ImageView company_image;

    private String targetJobId;
    private String j_title;
    private String j_spec;
    private String j_post_time;
    private String j_desc;
    private String j_salary;
    private String j_slot;

    private String c_name;
    private String c_email;
    private String c_contact;
    private String c_address;
    private String c_desc;
    private String c_image_link;

    private String company_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);

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

        company_name = (TextView)findViewById(R.id.company_name);
        company_email = (TextView)findViewById(R.id.company_email);
        company_contact = (TextView)findViewById(R.id.company_contact);
        company_address = (TextView)findViewById(R.id.company_address);
        company_desc = (TextView)findViewById(R.id.company_desc);
        company_image = (ImageView)findViewById(R.id.commpany_iv);

        message_btn = (Button)findViewById(R.id.message_company);

        Intent intent = getIntent();
        targetJobId = intent.getStringExtra("target_job_id");

        mDatabase.child("Vacancy").child("TypeOfJob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot type : dataSnapshot.getChildren()){
                    if (type!=null){
                        for (DataSnapshot company : type.getChildren()){
                            if (company!=null){
                                for (DataSnapshot post : company.getChildren()){

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

                                            company_id = company.getKey();
                                            //Toast.makeText(getApplicationContext(), company_id,Toast.LENGTH_LONG).show();

                                            if (company_id!=null){

                                                mDatabase.child("User").child(company_id).child("Profile").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        c_name = dataSnapshot.child("Name").getValue(String.class);
                                                        c_email = dataSnapshot.child("Email").getValue(String.class);
                                                        c_contact = dataSnapshot.child("ContactNo").getValue(String.class);
                                                        c_address = dataSnapshot.child("Address").getValue(String.class);
                                                        c_desc = dataSnapshot.child("Description").getValue(String.class);
                                                        c_image_link = dataSnapshot.child("Image").getValue(String.class);

                                                        company_name.setText(c_name);
                                                        company_email.setText(c_email);
                                                        company_contact.setText(c_contact);
                                                        company_address.setText(c_address);
                                                        company_desc.setText(c_desc);
                                                        Picasso.with(ViewJob.this).load(c_image_link).into(company_image);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }
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


        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (company_id!=null){
                    Intent myIntent = new Intent(ViewJob.this, ChatWindow.class);
                    myIntent.putExtra("target_user_id", company_id);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ViewJob.this.startActivity(myIntent);
                    overridePendingTransition(0, R.anim.fadein);
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
            Intent myIntent = new Intent(ViewJob.this, Search.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            overridePendingTransition(0, R.anim.fadein);
            ViewJob.this.startActivity(myIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
