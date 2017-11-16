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

public class EditAboutMe extends AppCompatActivity {

    //FireBase Declaration
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText address_text;
    private AutoCompleteTextView nationality_text;
    private Boolean complete_address = false;
    private Boolean complete_nationality = false;
    private ProgressDialog save_progressdialog;
    private FrameLayout touchInterceptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_about_me);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        address_text = (EditText) findViewById(R.id.text_box_salary);
        nationality_text = (AutoCompleteTextView)findViewById(R.id.text_box_work_location);
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
            mDatabase.child("User").child(uid).child("Profile").child("Address").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String profile_address = dataSnapshot.getValue().toString();
                        if (profile_address != null){
                            if (!profile_address.matches("-")){
                                address_text.setHint(profile_address);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //get Location from database
            mDatabase.child("User").child(uid).child("Profile").child("Nationality").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String profile_nationality = dataSnapshot.getValue().toString();
                        if (profile_nationality != null){
                            if (!profile_nationality.matches("-")){
                                nationality_text.setHint(profile_nationality);
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
                if (address_text.isFocused() || nationality_text.isFocused()){
                    Rect outRect = new Rect();
                    address_text.getGlobalVisibleRect(outRect);
                    nationality_text.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        address_text.clearFocus();
                        nationality_text.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });

        //set array adapter for location
        final ArrayAdapter<String> location_array = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("Application").child("Country").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String location_value = data.getValue(String.class);
                    location_array.add(location_value);
                }
                //set array adapter to auto completion text view
                nationality_text.setAdapter(location_array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //check if empty
        address_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (address_text.getText().toString().matches("")){
                        complete_address = false;
                    }
                    else{
                        complete_address = true;
                    }
                }
            }
        });

        //check if empty
        nationality_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (nationality_text.getText().toString().matches("")){
                        complete_nationality = false;
                    }
                    else {
                        complete_nationality = true;
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

    public void EditAboutOnClick(View v){
        save_progressdialog.setTitle("Loading");
        save_progressdialog.setMessage("Updating your information");
        save_progressdialog.setCanceledOnTouchOutside(false);
        save_progressdialog.show();

        //Store TextBox to String
        String address = address_text.getText().toString();
        String location = nationality_text.getText().toString();

        //Get current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (!complete_address && !complete_nationality) {
                save_progressdialog.hide();
                Toast.makeText(EditAboutMe.this, "Nothing changes.", Toast.LENGTH_SHORT).show();
            } else {

                if (complete_address){
                    //update qualification
                    mDatabase.child("User").child(uid).child("Profile").child("Address").setValue(address);
                }
                if (complete_nationality){
                    //update field of study
                    mDatabase.child("User").child(uid).child("Profile").child("Nationality").setValue(location);
                }
                save_progressdialog.dismiss();
                finish();
            }
        }
    }

}
