package com.jackong.jobbooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;



//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    //Declare private variables
    //private Button mSendData;
    private EditText email_text;
    private EditText pw_text;
    private Button showpw;
    private int password_counter = 0;


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
        this.setContentView(R.layout.activity_main);

        //Link variable to Edit Text id
        email_text = (EditText) findViewById(R.id.text_box_login_email);
        pw_text = (EditText) findViewById(R.id.text_box_login_pw);

        //Change Text for Email Edit Text is focused
        //Check if user enter wrong email format
        //Check if user leave empty email field
        email_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (email_text.getText().toString().matches("Email Address")){
                    //empty field
                    email_text.setText("");
                    //set email text color to white
                    email_text.setTextColor(Color.parseColor("#ffffff"));
                    //set email text style to default
                    email_text.setTypeface(Typeface.DEFAULT);
                    email_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                //email text field validation
                if (!hasFocus) {
                    if (email_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Email field cannot be empty!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        //set email text color to #777c7c
                        email_text.setTextColor(Color.parseColor("#777c7c"));
                        //set email text style to italic
                        email_text.setTypeface(null, Typeface.ITALIC);
                        email_text.setText("Email Address");
                    } else if (!email_text.getText().toString().contains("@")) {
                        Snackbar.make(v, "Invalid email format!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }

            }
        });

        //Empty Password Edit Text field when is focus for the first time
        //Check if user leave empty email field
        pw_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (password_counter < 1){
                    //empty password field
                    pw_text.setText("");
                    //set password text color to white
                    pw_text.setTextColor(Color.parseColor("#ffffff"));
                    //set password text style to default
                    pw_text.setTypeface(Typeface.DEFAULT);
                    //set password text inputType to PasswordInputType
                    pw_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pw_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                    showpw.setVisibility(View.VISIBLE);
                }

                if (password_counter > 0) {
                    if (!hasFocus) {
                        if (pw_text.getText().toString().matches("")) {
                            Snackbar.make(v, "Password field cannot be empty!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //set password text color to #777c7c
                            pw_text.setTextColor(Color.parseColor("#777c7c"));
                            //set password text style to italic
                            pw_text.setTypeface(null, Typeface.ITALIC);
                            //set password text inputType to Text
                            pw_text.setInputType(InputType.TYPE_CLASS_TEXT);
                            //set password text to Password
                            pw_text.setText("Password");
                            password_counter = 0;
                            showpw.setVisibility(View.GONE);
                        }
                    }
                }
                password_counter++;
            }
        });

        // On Touch Listener for Show Password Button
        showpw = (Button) findViewById(R.id.btn_show_pw);
        showpw.setOnClickListener(new View.OnClickListener(){
            boolean reveal_pw = false;
            @Override
            public void onClick(View view) {
                if (!reveal_pw){
                    pw_text.setInputType(InputType.TYPE_CLASS_TEXT);
                    reveal_pw = true;
                }
                else{
                    pw_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    reveal_pw = false;
                }
                if (pw_text.isFocused()){
                    String new_text = pw_text.getText().toString();
                    pw_text.setText("");
                    pw_text.append(new_text);
                }
            }
        });

        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (email_text.isFocused() || pw_text.isFocused()){
                    Rect outRect = new Rect();
                    email_text.getGlobalVisibleRect(outRect);
                    pw_text.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        email_text.clearFocus();
                        pw_text.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });
    }


    //Button Open Forgot Password activity
    public void forgotPasswordOnClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, ForgotPassword.class);
        myIntent.putExtra("key", email_text.getText().toString());
        MainActivity.this.startActivity(myIntent);

    }

    //Button Login
    public void LoginOnClick(View v){
        Intent myIntent = new Intent(MainActivity.this, FirstTime.class);
        MainActivity.this.startActivity(myIntent);
    }

    //Button Open Register activity
    public void RegisterOnClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, RegisterNewUser.class);
        MainActivity.this.startActivity(myIntent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //        mSendData = (Button) findViewById(R.id.addString);
//        mSendData.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view){
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("message");
//
//                myRef.setValue("Hello, World!");
//            }
//        });
}



