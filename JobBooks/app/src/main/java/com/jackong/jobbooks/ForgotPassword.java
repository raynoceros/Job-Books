package com.jackong.jobbooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPassword extends AppCompatActivity {

    //Declare private variables
    private EditText forgot_email_text;


    //OnCreate Method
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
        this.setContentView(R.layout.activity_forgot_password);

        //Get Info from previous activity
        //Intent intent = getIntent();
        //String email_info = intent.getStringExtra("key");
        //email = (TextView) findViewById(R.id.email_info);
        //email.setText(email_info);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        final FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (forgot_email_text.isFocused()) {
                        Rect outRect = new Rect();
                        forgot_email_text.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            forgot_email_text.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                        }
                    }
                }
                return false;
            }
        });


        //Link variable to Edit Text id
        forgot_email_text = (EditText) findViewById(R.id.text_box_forgot_email);

        //Empty Text for Email Edit Text is focused
        //Check if user enter wrong email format
        //Check if user leave empty email field
        forgot_email_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            int forgot_email_counter = 0;
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (forgot_email_counter < 1){
                    //set email text color to white
                    forgot_email_text.setTextColor(Color.parseColor("#ffffff"));
                    //set email text style to default
                    forgot_email_text.setTypeface(Typeface.DEFAULT);
                    forgot_email_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (forgot_email_counter > 0){
                    //email text field validation
                    if (!hasFocus) {
                        if (forgot_email_text.getText().toString().matches("")) {
                            Toast.makeText(getApplication(), "Email field cannot be empty!",
                                    Toast.LENGTH_LONG).show();
                        } else if (!forgot_email_text.getText().toString().contains("@")) {
                            Toast.makeText(getApplication(), "Invalid email!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else{
                    //force empty text
                    if (forgot_email_text.getText().toString().matches("Email Address")) {
                        forgot_email_text.setText("");
                    }
                }
                forgot_email_counter++;
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

    //Disable Device Back Button
    @Override
    public void onBackPressed() {

    }
}
