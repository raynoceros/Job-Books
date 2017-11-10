package com.jackong.jobbooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class RegisterNewUser extends AppCompatActivity {

    //Declare private variables
    private EditText username_text;
    private EditText register_email_text;
    private EditText register_password_text;
    private EditText register_confirm_password_text;
    private int password_counter = 0;
    private int confirm_password_counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Make status bar transparent
        //Set background image
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundResource(R.mipmap.fancyblackxhdpi);
        this.setContentView(R.layout.activity_register_new_user);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Link variables to EditText id
        username_text = (EditText) findViewById(R.id.text_box_register_username);
        register_email_text = (EditText) findViewById(R.id.text_box_register_email);
        register_password_text = (EditText) findViewById(R.id.text_box_register_password);
        register_confirm_password_text = (EditText) findViewById(R.id.text_box_register_confirmpassword);

        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (register_email_text.isFocused() || username_text.isFocused() || register_password_text.isFocused() || register_confirm_password_text.isFocused()){
                    Rect outRect = new Rect();
                    username_text.getGlobalVisibleRect(outRect);
                    register_email_text.getGlobalVisibleRect(outRect);
                    register_password_text.getGlobalVisibleRect(outRect);
                    register_confirm_password_text.getGlobalVisibleRect(outRect);

                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        username_text.clearFocus();
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


        //Empty Text for Username Edit Text
        //Check if user leave empty username field
        username_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (username_text.getText().toString().matches("Username")) {
                    //set empty field
                    username_text.setText("");
                    //set username text color to white
                    username_text.setTextColor(Color.parseColor("#ffffff"));
                    //set username text style to default
                    username_text.setTypeface(Typeface.DEFAULT);
                    username_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (!hasFocus) {
                    if (username_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Username field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set email text color to #777c7c
                        username_text.setTextColor(Color.parseColor("#777c7c"));
                        //set email text style to italic
                        username_text.setTypeface(null, Typeface.ITALIC);
                        username_text.setText("Username");
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
                    //set email text color to white
                    register_email_text.setTextColor(Color.parseColor("#ffffff"));
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
                    //set password text color to white
                    register_password_text.setTextColor(Color.parseColor("#ffffff"));
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
                    //set password text color to white
                    register_confirm_password_text.setTextColor(Color.parseColor("#ffffff"));
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
                            confirm_password_counter = 0;
                        }
                        else if (!register_confirm_password_text.getText().toString().matches(register_password_text.getText().toString())){
                            Snackbar.make(v, "Password not match!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }
                confirm_password_counter++;
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

    //Button Open Forgot Password activity
    public void RegisterNewUserOnClick(View v) {
        //Intent myIntent = new Intent(MainActivity.this, ForgotPassword.class);
        //myIntent.putExtra("key", email_text.getText().toString());
        //MainActivity.this.startActivity(myIntent);
    }
    //Disable Device Back Button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
