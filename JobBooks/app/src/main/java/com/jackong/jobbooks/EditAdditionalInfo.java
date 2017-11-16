package com.jackong.jobbooks;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class EditAdditionalInfo extends AppCompatActivity {

    //FireBase Declaration
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText expected_salary_text;
    private AutoCompleteTextView preferred_work_location_text;
    private Boolean complete_expected_salary = false;
    private Boolean complete_work_location = false;
    private ProgressDialog save_progressdialog;
    private FrameLayout touchInterceptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_additional_info);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        expected_salary_text = (EditText) findViewById(R.id.text_box_salary);
        preferred_work_location_text = (AutoCompleteTextView)findViewById(R.id.text_box_work_location);
        save_progressdialog = new ProgressDialog(this);
        touchInterceptor = (FrameLayout)findViewById(R.id.touchInterceptor);

        //set Auth
        mAuth = FirebaseAuth.getInstance();
        //set database
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //Get details
        //Get current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            //get Salary from database
            mDatabase.child("User").child(uid).child("Profile").child("Expected Salary").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String profile_salary = dataSnapshot.getValue().toString();
                        if (profile_salary != null){
                            if (!profile_salary.matches("-")){
                                DecimalFormat formatter = new DecimalFormat("#,###,###");
                                String formattedSalary = formatter.format(Integer.parseInt(profile_salary));
                                expected_salary_text.setHint("MYR" + formattedSalary);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //get Location from database
            mDatabase.child("User").child(uid).child("Profile").child("Preferred Work Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String profile_location = dataSnapshot.getValue().toString();
                        if (profile_location != null){
                            if (!profile_location.matches("-")){
                                preferred_work_location_text.setHint(profile_location);
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
                if (expected_salary_text.isFocused() || preferred_work_location_text.isFocused()){
                    Rect outRect = new Rect();
                    expected_salary_text.getGlobalVisibleRect(outRect);
                    preferred_work_location_text.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        expected_salary_text.clearFocus();
                        preferred_work_location_text.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });

        //set array adapter for field of study
        final ArrayAdapter<String> location_array = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("Application").child("Malaysia Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String location_value = data.getValue(String.class);
                    location_array.add(location_value);
                }
                //set array adapter to auto completion text view
                preferred_work_location_text.setAdapter(location_array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //check if empty
        expected_salary_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (expected_salary_text.getText().toString().matches("")){
                        complete_expected_salary = false;
                    }
                    else{
                        complete_expected_salary = true;
                    }
                }
            }
        });

        //check if empty
        preferred_work_location_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (preferred_work_location_text.getText().toString().matches("")){
                        complete_work_location = false;
                    }
                    else {
                        complete_work_location = true;
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

    public void EditAddOnClick(View v){
        save_progressdialog.setTitle("Loading");
        save_progressdialog.setMessage("Updating your information");
        save_progressdialog.setCanceledOnTouchOutside(false);
        save_progressdialog.show();

        //Store TextBox to String
        String salary = expected_salary_text.getText().toString();
        String location = preferred_work_location_text.getText().toString();

        //Get current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (!complete_expected_salary && !complete_work_location) {
                save_progressdialog.hide();
                Toast.makeText(EditAdditionalInfo.this, "Nothing changes.", Toast.LENGTH_SHORT).show();
            } else {

                if (complete_expected_salary){
                    //update qualification
                    mDatabase.child("User").child(uid).child("Profile").child("Expected Salary").setValue(salary);
                }
                if (complete_work_location){
                    //update field of study
                    mDatabase.child("User").child(uid).child("Profile").child("Preferred Work Location").setValue(location);
                }
                save_progressdialog.dismiss();
                finish();
            }
        }
    }

}
