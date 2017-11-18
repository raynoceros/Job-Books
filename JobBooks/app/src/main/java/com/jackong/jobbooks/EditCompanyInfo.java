package com.jackong.jobbooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditCompanyInfo extends AppCompatActivity {

    private EditText desc_et;
    private EditText address_et;
    private EditText contact_et;
    private FrameLayout touchInterceptor;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Boolean complete_check_description = false;
    private Boolean complete_check_address = false;
    private Boolean complete_check_contact = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_company_info);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        desc_et = (EditText)findViewById(R.id.text_box_description);
        address_et = (EditText)findViewById(R.id.text_box_address);
        contact_et = (EditText)findViewById(R.id.text_box_contact);
        touchInterceptor = (FrameLayout)findViewById(R.id.touchInterceptor);

        //set Auth
        mAuth = FirebaseAuth.getInstance();
        //set database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            //get Description from database
            mDatabase.child("User").child(uid).child("Profile").child("Description").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String company_des = dataSnapshot.getValue().toString();
                        if (!company_des.matches("-")){
                            desc_et.setHint(company_des);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //get Qualification from database
            mDatabase.child("User").child(uid).child("Profile").child("Address").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String company_address = dataSnapshot.getValue().toString();
                        if (company_address != null){
                            if (!company_address.matches("-")){
                                address_et.setHint(company_address);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //get Course from database
            mDatabase.child("User").child(uid).child("Profile").child("ContactNo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String company_contact = dataSnapshot.getValue().toString();
                        if (company_contact != null){
                            if (!company_contact.matches("-")){
                                contact_et.setHint(company_contact);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        touchInterceptor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (desc_et.isFocused() || address_et.isFocused() || contact_et.isFocused()){
                    Rect outRect = new Rect();
                    desc_et.getGlobalVisibleRect(outRect);
                    address_et.getGlobalVisibleRect(outRect);
                    contact_et.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        desc_et.clearFocus();
                        address_et.clearFocus();
                        contact_et.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });

        //check if empty
        desc_et.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if (desc_et.getText().toString().matches("")){
                        complete_check_description = false;
                    }
                    else {
                        complete_check_description = true;
                    }
                }
            }
        });

        //check if empty
        address_et.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (address_et.getText().toString().matches("")){
                        complete_check_address = false;
                    }
                    else{
                        complete_check_address = true;
                    }
                }
            }
        });

        //check if empty
        contact_et.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (contact_et.getText().toString().matches("")){
                        complete_check_contact = false;
                    }
                    else {
                        complete_check_contact = true;
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
            //end this activity
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void EditCompanyOnClick(View v){


        //TextBox to string store in String
        String desc = desc_et.getText().toString();
        String address = address_et.getText().toString();
        String ct = contact_et.getText().toString();

        //Get current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (!complete_check_address && !complete_check_description && !complete_check_contact) {
                Toast.makeText(EditCompanyInfo.this, "Nothing changes.", Toast.LENGTH_SHORT).show();
            } else {
                if (complete_check_description){
                    //update institute
                    mDatabase.child("User").child(uid).child("Profile").child("Description").setValue(desc);
                }
                if (complete_check_address){
                    //update qualification
                    mDatabase.child("User").child(uid).child("Profile").child("Address").setValue(address);

                }
                if (complete_check_contact){
                    //update field of study
                    mDatabase.child("User").child(uid).child("Profile").child("ContactNo").setValue(ct);
                }
                Toast.makeText(EditCompanyInfo.this, "Update Successful.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
