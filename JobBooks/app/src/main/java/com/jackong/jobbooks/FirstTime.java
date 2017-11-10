package com.jackong.jobbooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class FirstTime extends AppCompatActivity {

    //Declare private variables
    private EditText first_name_text;
    private EditText last_name_text;
    private EditText institute_text;
    private EditText specialization_text;
    private EditText location_text;

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
        this.setContentView(R.layout.activity_first_time);

        //Link variables to EditText id
        first_name_text = (EditText) findViewById(R.id.text_box_first_name);
        last_name_text = (EditText) findViewById(R.id.text_box_last_name);
        institute_text = (EditText) findViewById(R.id.text_box_institute);
        specialization_text = (EditText) findViewById(R.id.text_box_preferred_specializations);
        location_text = (EditText) findViewById(R.id.text_box_preferred_locations);


        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (first_name_text.isFocused() || last_name_text.isFocused() || institute_text.isFocused() || specialization_text.isFocused() || location_text.isFocused()){
                    Rect outRect = new Rect();
                    first_name_text.getGlobalVisibleRect(outRect);
                    last_name_text.getGlobalVisibleRect(outRect);
                    institute_text.getGlobalVisibleRect(outRect);
                    specialization_text.getGlobalVisibleRect(outRect);
                    location_text.getGlobalVisibleRect(outRect);

                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        first_name_text.clearFocus();
                        last_name_text.clearFocus();
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

        //Empty Text for first name Edit Text
        //Check if user leave empty first name field
        first_name_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (first_name_text.getText().toString().matches("First Name")) {
                    //set empty field
                    first_name_text.setText("");
                    //set first name text color to white
                    first_name_text.setTextColor(Color.parseColor("#ffffff"));
                    //set first name text style to default
                    first_name_text.setTypeface(Typeface.DEFAULT);
                    first_name_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (!hasFocus) {
                    if (first_name_text.getText().toString().matches("")) {
                        Snackbar.make(v, "First Name field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set text color to #777c7c
                        first_name_text.setTextColor(Color.parseColor("#777c7c"));
                        //set text style to italic
                        first_name_text.setTypeface(null, Typeface.ITALIC);
                        first_name_text.setText("First Name");
                    }
                }
            }
        });

        //Empty Text for first name Edit Text
        //Check if user leave empty first name field
        last_name_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (last_name_text.getText().toString().matches("Last Name")) {
                    //set empty field
                    last_name_text.setText("");
                    //set first name text color to white
                    last_name_text.setTextColor(Color.parseColor("#ffffff"));
                    //set first name text style to default
                    last_name_text.setTypeface(Typeface.DEFAULT);
                    last_name_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (!hasFocus) {
                    if (last_name_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Last Name field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set text color to #777c7c
                        last_name_text.setTextColor(Color.parseColor("#777c7c"));
                        //set text style to italic
                        last_name_text.setTypeface(null, Typeface.ITALIC);
                        last_name_text.setText("Last Name");
                    }
                }
            }
        });

        //Empty Text for Institute Edit Text
        //Check if user leave empty field
        institute_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (institute_text.getText().toString().matches("Institute/University")) {
                    //set empty field
                    institute_text.setText("");
                    //set first name text color to white
                    institute_text.setTextColor(Color.parseColor("#ffffff"));
                    //set first name text style to default
                    institute_text.setTypeface(Typeface.DEFAULT);
                    institute_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (!hasFocus) {
                    if (institute_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Institute/University field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set text color to #777c7c
                        institute_text.setTextColor(Color.parseColor("#777c7c"));
                        //set text style to italic
                        institute_text.setTypeface(null, Typeface.ITALIC);
                        institute_text.setText("Institute/University");
                    }
                }
            }
        });

        //Empty Text for Preferred Specialization Edit Text
        //Check if user leave empty field
        specialization_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (specialization_text.getText().toString().matches("Preferred Specialization")) {
                    //set empty field
                    specialization_text.setText("");
                    //set first name text color to white
                    specialization_text.setTextColor(Color.parseColor("#ffffff"));
                    //set first name text style to default
                    specialization_text.setTypeface(Typeface.DEFAULT);
                    specialization_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (!hasFocus) {
                    if (specialization_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Institute/University field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set text color to #777c7c
                        specialization_text.setTextColor(Color.parseColor("#777c7c"));
                        //set text style to italic
                        specialization_text.setTypeface(null, Typeface.ITALIC);
                        specialization_text.setText("Preferred Specialization");
                    }
                }
            }
        });

        //Empty Text for Preferred Specialization Edit Text
        //Check if user leave empty field
        location_text.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (location_text.getText().toString().matches("Preferred Work Location")) {
                    //set empty field
                    location_text.setText("");
                    //set first name text color to white
                    location_text.setTextColor(Color.parseColor("#ffffff"));
                    //set first name text style to default
                    location_text.setTypeface(Typeface.DEFAULT);
                    location_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (!hasFocus) {
                    if (location_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Preferred Work Location field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set text color to #777c7c
                        location_text.setTextColor(Color.parseColor("#777c7c"));
                        //set text style to italic
                        location_text.setTypeface(null, Typeface.ITALIC);
                        location_text.setText("Preferred Work Location");
                    }
                }
            }
        });
    }

    //Continue to home page
    public void GoToHomeOnClick(View v){
        Intent myIntent = new Intent(FirstTime.this, Home.class);
        FirstTime.this.startActivity(myIntent);
    }

    //Disable Device Back Button
    @Override
    public void onBackPressed() {
        //do nothing
    }
}