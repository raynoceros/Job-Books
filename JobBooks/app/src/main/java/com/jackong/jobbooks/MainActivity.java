package com.jackong.jobbooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;



//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    // Declare private variables
    //private Button mSendData;
    private EditText email_text;
    private EditText pw_text;
    private Button showpw;



    @SuppressLint("ClickableViewAccessibility")
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

        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        final FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (email_text.isFocused() || pw_text.isFocused()) {
                        Rect outRect = new Rect();
                        email_text.getGlobalVisibleRect(outRect);
                        pw_text.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            email_text.clearFocus();
                            pw_text.clearFocus();
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


        //Change Text for Email Edit Text is focused
        //Check if user enter wrong email format
        //Check if user leave empty email field
        email_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (email_text.getText().toString().matches("Email Address")) {
                    email_text.setText("");
                }
                if (!hasFocus) {
                    if (email_text.getText().toString().matches("")) {
                        Toast.makeText(getApplication(), "Email field cannot be empty!",
                                Toast.LENGTH_LONG).show();
                    } else if (!email_text.getText().toString().contains("@")) {
                        Toast.makeText(getApplication(), "Invalid email!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Empty Password Edit Text field when is focus for the first time
        //Check if user leave empty email field
        pw_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            int count = 0;

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (count > 0) {
                    if (!hasFocus) {
                        if (pw_text.getText().toString().matches("")) {
                            Toast.makeText(getApplication(), "Password field cannot be empty!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    pw_text.setText("");
                    count++;
                }
            }
        });

        // On Touch Listener for Show Password Button
        showpw = (Button) findViewById(R.id.btn_show_pw);
        showpw.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pw_text.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        pw_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                if (pw_text.isFocused()) {
                    String new_text = pw_text.getText().toString();
                    pw_text.setText("");
                    pw_text.append(new_text);
                }
                return true;
            }
        });
    }


    //Button Open Forgot Password activity
    public void forgotPasswordOnClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, ForgotPassword.class);
        myIntent.putExtra("key", email_text.getText().toString());
        MainActivity.this.startActivity(myIntent);

    }

    //Button Open Register activity
    public void RegisterOnClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, RegisterNewUser.class);
        MainActivity.this.startActivity(myIntent);

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



