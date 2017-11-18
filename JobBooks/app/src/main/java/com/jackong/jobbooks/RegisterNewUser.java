package com.jackong.jobbooks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterNewUser extends AppCompatActivity {

    //Declare private variables
    private EditText name_text;
    private EditText register_email_text;
    private EditText register_password_text;
    private EditText register_confirm_password_text;
    private int password_counter = 0;
    private int confirm_password_counter = 0;
    private FirebaseAuth mAuth;
    private boolean valid_format_name = false;
    private boolean valid_format_email = false;
    private boolean valid_format_password = false;
    private boolean valid_format_confirm_password = false;
    private DatabaseReference mDatabase;
    private ProgressBar register_progress;
    private Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register_new_user);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Link variables to EditText id
        name_text = (EditText) findViewById(R.id.text_box_register_name);
        register_email_text = (EditText) findViewById(R.id.text_box_register_email);
        register_password_text = (EditText) findViewById(R.id.text_box_register_password);
        register_confirm_password_text = (EditText) findViewById(R.id.text_box_register_confirmpassword);

        //Firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (register_email_text.isFocused() || name_text.isFocused() || register_password_text.isFocused() || register_confirm_password_text.isFocused()){
                    Rect outRect = new Rect();
                    name_text.getGlobalVisibleRect(outRect);
                    register_email_text.getGlobalVisibleRect(outRect);
                    register_password_text.getGlobalVisibleRect(outRect);
                    register_confirm_password_text.getGlobalVisibleRect(outRect);

                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        name_text.clearFocus();
                        register_email_text.clearFocus();
                        register_password_text.clearFocus();
                        register_confirm_password_text.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });


        //Empty Text for Name Edit Text
        //Check if user leave empty name field
        name_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (name_text.getText().toString().matches("Name")) {
                    //set empty field
                    name_text.setText("");
                    //set name text color to black
                    name_text.setTextColor(Color.parseColor("#000000"));
                    //set name text style to default
                    name_text.setTypeface(Typeface.DEFAULT);
                    name_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (!hasFocus) {
                    if (name_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Name field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set email text color to #777c7c
                        name_text.setTextColor(Color.parseColor("#777c7c"));
                        //set email text style to italic
                        name_text.setTypeface(null, Typeface.ITALIC);
                        name_text.setText("Name");
                    }
                    else{
                        valid_format_name = true;
                    }
                }
            }
        });


        //Empty Text for Email Edit Text is focused
        //Check if user enter wrong email format
        //Check if user leave empty email field
        register_email_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (register_email_text.getText().toString().matches("Email Address")){
                    //empty field
                    register_email_text.setText("");
                    //set email text color to black
                    register_email_text.setTextColor(Color.parseColor("#000000"));
                    //set email text style to default
                    register_email_text.setTypeface(Typeface.DEFAULT);
                    register_email_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                //email text field validation
                if (!hasFocus) {
                    if (register_email_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Email field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set email text color to #777c7c
                        register_email_text.setTextColor(Color.parseColor("#777c7c"));
                        //set email text style to italic
                        register_email_text.setTypeface(null, Typeface.ITALIC);
                        register_email_text.setText("Email Address");
                    } else if (!register_email_text.getText().toString().contains("@")) {
                        Snackbar.make(v, "Invalid email format!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else {
                        valid_format_email = true;
                    }
                }
            }
        });

        //Empty Password Edit Text field when is focus for the first time
        //Check if user leave empty field
        register_password_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((password_counter < 1) || (register_password_text.getText().toString().matches("Password"))){
                    //empty password field
                    register_password_text.setText("");
                    //set password text color to black
                    register_password_text.setTextColor(Color.parseColor("#000000"));
                    //set password text style to default
                    register_password_text.setTypeface(Typeface.DEFAULT);
                    //set password text inputType to PasswordInputType
                    register_password_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    register_password_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }

                if (password_counter > 0) {
                    if (!hasFocus) {
                        if (register_password_text.getText().toString().matches("")) {
                            Snackbar.make(v, "Password field cannot be empty!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //set password text color to #777c7c
                            register_password_text.setTextColor(Color.parseColor("#777c7c"));
                            //set password text style to italic
                            register_password_text.setTypeface(null, Typeface.ITALIC);
                            //set password text inputType to Text
                            register_password_text.setInputType(InputType.TYPE_CLASS_TEXT);
                            //set password text to Password
                            register_password_text.setText("Password");
                            password_counter = 0;
                        }
                        else {
                            valid_format_password = true;
                        }
                    }
                }
                password_counter++;
            }
        });

        //Empty Confirm Password Edit Text field when is focus for the first time
        //Check if user leave empty field
        register_confirm_password_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((confirm_password_counter < 1) || (register_confirm_password_text.getText().toString().matches("Confirm Password"))){
                    //empty password field
                    register_confirm_password_text.setText("");
                    //set password text color to black
                    register_confirm_password_text.setTextColor(Color.parseColor("#000000"));
                    //set password text style to default
                    register_confirm_password_text.setTypeface(Typeface.DEFAULT);
                    //set password text inputType to PasswordInputType
                    register_confirm_password_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    register_confirm_password_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }

                if (confirm_password_counter > 0) {
                    if (!hasFocus) {
                        if (register_confirm_password_text.getText().toString().matches("")) {
                            Snackbar.make(v, "Confirm Password field cannot be empty!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //set password text color to #777c7c
                            register_confirm_password_text.setTextColor(Color.parseColor("#777c7c"));
                            //set password text style to italic
                            register_confirm_password_text.setTypeface(null, Typeface.ITALIC);
                            //set password text inputType to Text
                            register_confirm_password_text.setInputType(InputType.TYPE_CLASS_TEXT);
                            //set password text to Password
                            register_confirm_password_text.setText("Confirm Password");
                            confirm_password_counter = -1;
                        }
                        else if (!register_confirm_password_text.getText().toString().matches(register_password_text.getText().toString())){
                            Snackbar.make(v, "Password not match!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else {
                            valid_format_confirm_password = true;
                        }
                    }
                }
                confirm_password_counter++;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

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

    //Button Open Forgot Password activity
    public void RegisterNewUserOnClick(View v) {
        register_progress = (ProgressBar)findViewById(R.id.register_progress);
        register_button = (Button)findViewById(R.id.btn_register);
        register_button.setVisibility(View.GONE);
        register_progress.setVisibility(View.VISIBLE);

        final String email = register_email_text.getText().toString();
        final String password = register_confirm_password_text.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();

                    //find time now
                    Date currentTime = Calendar.getInstance().getTime();
                    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
                    String time_register = df.format(currentTime);

                    //get name
                    String name;
                    name = name_text.getText().toString();
                    //get email
                    String email_address = email.toLowerCase();


                    //once register success, create the default database for user
                    //set user account type to Student
                    mDatabase.child("User").child(uid).child("AccountType").setValue("Student");
                    //set first time boolean to database
                    mDatabase.child("User").child(uid).child("FirstTime").setValue("yes");

                    //set user address
                    mDatabase.child("User").child(uid).child("Profile").child("Address").setValue("-");
                    //set user course
                    mDatabase.child("User").child(uid).child("Profile").child("Course").setValue("-");
                    //set user create account time to database
                    mDatabase.child("User").child(uid).child("Profile").child("DateCreated").setValue(time_register);
                    //set user email
                    mDatabase.child("User").child(uid).child("Profile").child("Email").setValue(email_address);
                    //set user expected salary
                    mDatabase.child("User").child(uid).child("Profile").child("Expected Salary").setValue("-");
                    //set user image
                    mDatabase.child("User").child(uid).child("Profile").child("Image").setValue("-");
                    //set user institute
                    mDatabase.child("User").child(uid).child("Profile").child("Institute").setValue("-");
                    //set user name to database
                    mDatabase.child("User").child(uid).child("Profile").child("Name").setValue(name);
                    //set user nationality
                    mDatabase.child("User").child(uid).child("Profile").child("Nationality").setValue("-");
                    //set user preferred specialization
                    mDatabase.child("User").child(uid).child("Profile").child("Preferred Specialization").setValue("-");
                    //set user preferred work location
                    mDatabase.child("User").child(uid).child("Profile").child("Preferred Work Location").setValue("-");
                    //set user qualification
                    mDatabase.child("User").child(uid).child("Profile").child("Qualification").setValue("-");

                    Toast.makeText(RegisterNewUser.this, "Register success.",
                            Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    if (task.getException()!= null){
                        String error_message;
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthWeakPasswordException e) {
                            error_message = "Invalid Password... Password requires more that 6 alphabets.";
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            error_message = "Invalid Email...";
                        } catch(FirebaseAuthUserCollisionException e) {
                            error_message = "This Email is already been used. Please try other email.";
                        } catch(FirebaseNetworkException e) {
                            error_message = "Network disconnected...Unable to register.";
                        } catch(Exception e) {
                            error_message = "Registration fail. Please try again later";
                        }
                        Toast.makeText(RegisterNewUser.this, ""+error_message, Toast.LENGTH_LONG).show();
                    }
                    register_button.setVisibility(View.VISIBLE);
                    register_progress.setVisibility(View.GONE);
                }
            }
        });
        //Intent myIntent = new Intent(Login.this, ForgotPassword.class);
        //myIntent.putExtra("key", email_text.getText().toString());
        //Login.this.startActivity(myIntent);

    }
    //Disable Device Back Button
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
