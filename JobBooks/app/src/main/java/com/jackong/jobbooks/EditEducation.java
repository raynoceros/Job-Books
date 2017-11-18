package com.jackong.jobbooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditEducation extends AppCompatActivity {

    //FireBase Declaration
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private AutoCompleteTextView institute_text;
    private AutoCompleteTextView qualification_text;
    private AutoCompleteTextView field_of_study_text;
    private Boolean complete_check_institute = false;
    private Boolean complete_check_qualification = false;
    private Boolean complete_check_field_of_study = false;
    private FrameLayout touchInterceptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_education);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        institute_text = (AutoCompleteTextView)findViewById(R.id.text_box_institute);
        qualification_text = (AutoCompleteTextView)findViewById(R.id.text_box_qualification);
        field_of_study_text = (AutoCompleteTextView)findViewById(R.id.text_box_field_of_study);
        touchInterceptor = (FrameLayout)findViewById(R.id.touchInterceptor);

        //set Auth
        mAuth = FirebaseAuth.getInstance();
        //set database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            //get Institute from database
            mDatabase.child("User").child(uid).child("Profile").child("Institute").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String profile_institute = dataSnapshot.getValue().toString();
                        if (profile_institute != null){
                            institute_text.setHint(profile_institute);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //get Qualification from database
            mDatabase.child("User").child(uid).child("Profile").child("Qualification").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String profile_qualification = dataSnapshot.getValue().toString();
                        if (profile_qualification != null){
                            if (!profile_qualification.matches("-")){
                                qualification_text.setHint(profile_qualification);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //get Course from database
            mDatabase.child("User").child(uid).child("Profile").child("Course").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String profile_course = dataSnapshot.getValue().toString();
                        if (profile_course != null){
                            if (!profile_course.matches("-")){
                                field_of_study_text.setHint(profile_course);
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
                if (institute_text.isFocused() || qualification_text.isFocused() || field_of_study_text.isFocused()){
                    Rect outRect = new Rect();
                    institute_text.getGlobalVisibleRect(outRect);
                    qualification_text.getGlobalVisibleRect(outRect);
                    field_of_study_text.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        institute_text.clearFocus();
                        qualification_text.clearFocus();
                        field_of_study_text.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });

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

        //set array adapter for qualification
        final ArrayAdapter<String> qualification_array = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("Application").child("Qualification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String qualification_value = data.getValue(String.class);
                    qualification_array.add(qualification_value);
                }
                //set array adapter to auto completion text view
                qualification_text.setAdapter(qualification_array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set array adapter for field of study
        final ArrayAdapter<String> field_of_study_array = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("Application").child("Field Of Studies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String field_of_study_value = data.getValue(String.class);
                    field_of_study_array.add(field_of_study_value);
                }
                //set array adapter to auto completion text view
                field_of_study_text.setAdapter(field_of_study_array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //check if empty
        institute_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if (institute_text.getText().toString().matches("")){
                        complete_check_institute = false;
                    }
                    else {
                        complete_check_institute = true;
                    }
                }
            }
        });

        //check if empty
        qualification_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (qualification_text.getText().toString().matches("")){
                        complete_check_qualification = false;
                    }
                    else{
                        complete_check_qualification = true;
                    }
                }
            }
        });

        //check if empty
        field_of_study_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (field_of_study_text.getText().toString().matches("")){
                        complete_check_field_of_study = false;
                    }
                    else {
                        complete_check_field_of_study = true;
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

    public void EditEduOnClick(View v){
        final ProgressDialog save_progressdialog;
        save_progressdialog = new ProgressDialog(this);
        save_progressdialog.setTitle("Loading");
        save_progressdialog.setMessage("Updating your information");
        save_progressdialog.setCanceledOnTouchOutside(false);
        save_progressdialog.show();

        //TextBox to string store in String
        String institute = institute_text.getText().toString();
        String qualification = qualification_text.getText().toString();
        String field_of_study = field_of_study_text.getText().toString();

        //Get current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (!complete_check_institute && !complete_check_qualification && !complete_check_field_of_study) {
                save_progressdialog.hide();
                Toast.makeText(EditEducation.this, "Nothing changes.", Toast.LENGTH_SHORT).show();
            } else {
                if (complete_check_institute){
                    //update institute
                    mDatabase.child("User").child(uid).child("Profile").child("Institute").setValue(institute);
                }
                if (complete_check_qualification){
                    //update qualification
                    mDatabase.child("User").child(uid).child("Profile").child("Qualification").setValue(qualification);

                }
                if (complete_check_field_of_study){
                    //update field of study
                    mDatabase.child("User").child(uid).child("Profile").child("Course").setValue(field_of_study);
                }
                save_progressdialog.dismiss();
                finish();
            }
        }
    }
}
