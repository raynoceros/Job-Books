package com.jackong.jobbooks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class ForgotPassword extends AppCompatActivity {

    //Declare private variables
    private EditText forgot_email_text;
    private Button reset_pw;
    private ProgressBar reset_progress;


    //OnCreate Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_forgot_password);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
        FrameLayout touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (forgot_email_text.isFocused()){
                    Rect outRect = new Rect();
                    forgot_email_text.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) view.getX(), (int) view.getY())){
                        forgot_email_text.clearFocus();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });


        //Link variable to Edit Text id
        forgot_email_text = (EditText) findViewById(R.id.text_box_forgot_email);

        //Empty Text for Email Edit Text is focused
        //Check if user enter wrong email format
        //Check if user leave empty email field
        forgot_email_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (forgot_email_text.getText().toString().matches("Email Address")) {
                    //set empty field
                    forgot_email_text.setText("");
                    //set text color to black
                    forgot_email_text.setTextColor(Color.parseColor("#000000"));
                    //set email text style to default
                    forgot_email_text.setTypeface(Typeface.DEFAULT);
                    forgot_email_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                if (!hasFocus) {
                    if (forgot_email_text.getText().toString().matches("")) {
                        Snackbar.make(v, "Email field cannot be empty!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //set email text style to italic
                        forgot_email_text.setTypeface(null, Typeface.ITALIC);
                        //set text color to 777c7c
                        forgot_email_text.setTextColor(Color.parseColor("#777c7c"));
                        forgot_email_text.setText("Email Address");
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

    //Reset Password
    public void ResetPasswordOnClick(View v){
        reset_pw = (Button)findViewById(R.id.btn_send_reset_email);
        reset_progress = (ProgressBar)findViewById(R.id.reset_progress);
        reset_progress.setVisibility(View.VISIBLE);
        reset_pw.setVisibility(View.INVISIBLE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = forgot_email_text.getText().toString();
        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Email sent.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            String error_message;
                            if (task.getException() != null){
                                try {
                                    throw task.getException();
                                } catch(FirebaseNetworkException e) {
                                    error_message = "Network disconnected...Unable to connect.";
                                } catch(Exception e) {
                                    error_message = "The email you entered is not found.";
                                }
                                Toast.makeText(ForgotPassword.this, error_message, Toast.LENGTH_LONG).show();
                                reset_progress.setVisibility(View.INVISIBLE);
                                reset_pw.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                });

    }

    //Disable Device Back Button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
