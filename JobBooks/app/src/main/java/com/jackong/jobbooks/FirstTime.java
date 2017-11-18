package com.jackong.jobbooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirstTime extends AppCompatActivity {

    //Declare private variables
    private AutoCompleteTextView institute_text;
    private AutoCompleteTextView specialization_text;
    private AutoCompleteTextView location_text;
    private Button proceed_button;
    private ProgressBar proceed_progress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Boolean complete_check_institute;
    private Boolean complete_check_specialization;
    private Boolean complete_check_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_first_time);

        //Link variables to EditText id\
        specialization_text = (AutoCompleteTextView) findViewById(R.id.text_box_preferred_specializations);
        location_text = (AutoCompleteTextView) findViewById(R.id.text_box_work_location);
        institute_text = (AutoCompleteTextView) findViewById(R.id.text_box_institute);
        proceed_button = (Button)findViewById(R.id.btn_proceed_profile_setup);
        proceed_progress = (ProgressBar)findViewById(R.id.first_time_progress);

        //set Auth
        mAuth = FirebaseAuth.getInstance();
        //set database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //set array adapter for institute
        final ArrayAdapter<String> institute_array = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("Application").child("Institute University").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String institute_value = data.getValue(String.class);
                    institute_array.add(institute_value);
                }
                //set array adapter to auto completion text view
                institute_text.setAdapter(institute_array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set array adapter for specialization
        final ArrayAdapter<String> specialization_array = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("Application").child("Specializations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String specialization_value = data.getValue(String.class);
                    specialization_array.add(specialization_value);
                }
                //set array adapter to auto completion text view
                specialization_text.setAdapter(specialization_array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set array adapter for work location
        final ArrayAdapter<String> location_array = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("Application").child("Malaysia Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String location_value = data.getValue(String.class);
                    location_array.add(location_value);
                }
                //set array adapter to auto completion text view
                location_text.setAdapter(location_array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (institute_text.isFocused() || specialization_text.isFocused() || location_text.isFocused()){
                    Rect outRect = new Rect();
                    institute_text.getGlobalVisibleRect(outRect);
                    specialization_text.getGlobalVisibleRect(outRect);
                    location_text.getGlobalVisibleRect(outRect);

                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        institute_text.clearFocus();
                        specialization_text.clearFocus();
                        location_text.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });

        //Empty Text for Institute Edit Text
        //Check if user leave empty field
        institute_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    //set empty field
                    institute_text.setText("");
                    //set first name text color to black
                    institute_text.setTextColor(Color.parseColor("#000000"));
                    //set first name text style to default
                    institute_text.setTypeface(Typeface.DEFAULT);
                    institute_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                else {
                    if (institute_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Institute/University field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set text color to #777c7c
                        institute_text.setTextColor(Color.parseColor("#777c7c"));
                        //set text style to italic
                        institute_text.setTypeface(null, Typeface.ITALIC);
                        institute_text.setText("Institute/University");
                        complete_check_institute = false;
                    }
                    else {
                        complete_check_institute = true;
                    }
                }
            }
        });

        //Empty Text for Preferred Specialization Edit Text
        //Check if user leave empty field
        specialization_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    //set empty field
                    specialization_text.setText("");
                    //set first name text color to black
                    specialization_text.setTextColor(Color.parseColor("#000000"));
                    //set first name text style to default
                    specialization_text.setTypeface(Typeface.DEFAULT);
                    specialization_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                else {
                    if (specialization_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Institute/University field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set text color to #777c7c
                        specialization_text.setTextColor(Color.parseColor("#777c7c"));
                        //set text style to italic
                        specialization_text.setTypeface(null, Typeface.ITALIC);
                        specialization_text.setText("Preferred Specialization");
                        complete_check_specialization = false;
                    }
                    else {
                        complete_check_specialization = true;
                    }
                }
            }
        });

        //Empty Text for Preferred Specialization Edit Text
        //Check if user leave empty field
        location_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    //set empty field
                    location_text.setText("");
                    //set first name text color to black
                    location_text.setTextColor(Color.parseColor("#000000"));
                    //set first name text style to default
                    location_text.setTypeface(Typeface.DEFAULT);
                    location_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                else {
                    if (location_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Preferred Work Location field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set text color to #777c7c
                        location_text.setTextColor(Color.parseColor("#777c7c"));
                        //set text style to italic
                        location_text.setTypeface(null, Typeface.ITALIC);
                        location_text.setText("Preferred Work Location (Malaysia)");
                        complete_check_location = false;
                    }
                    else {
                        complete_check_location = true;
                    }
                }
            }
        });
    }

    //Continue to home page
    public void goToSearchOnClick(View v){
        proceed_button.setVisibility(View.GONE);
        proceed_progress.setVisibility(View.VISIBLE);

        String institute = institute_text.getText().toString();
        String specialization = specialization_text.getText().toString();
        String work_location = location_text.getText().toString();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            String uid = currentUser.getUid();
            if (!complete_check_institute | !complete_check_specialization |!complete_check_location){
                Toast.makeText(FirstTime.this, "Please complete the form.", Toast.LENGTH_SHORT).show();
                proceed_button.setVisibility(View.VISIBLE);
                proceed_progress.setVisibility(View.GONE);
            }
            else{
                //set first time to no
                mDatabase.child("User").child(uid).child("FirstTime").setValue("no");
                //update institute
                mDatabase.child("User").child(uid).child("Profile").child("Institute").setValue(institute);
                //update specialization
                mDatabase.child("User").child(uid).child("Profile").child("Preferred Specialization").setValue(specialization);
                //update work location
                mDatabase.child("User").child(uid).child("Profile").child("Preferred Work Location").setValue(work_location);
                Intent myIntent = new Intent(FirstTime.this, Search.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                FirstTime.this.startActivity(myIntent);
                finish();
            }

            //Toast.makeText(FirstTime.this, mDatabase.toString(),
            //       Toast.LENGTH_SHORT).show();
        }
    }

    //Disable Device Back Button
    @Override
    public void onBackPressed() {
        //do nothing
    }
}