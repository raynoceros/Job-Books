package com.jackong.jobbooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    //Declare private variables
    //private Button mSendData;
    private EditText email_text;
    private EditText pw_text;
    private Button showpw;
    private int password_counter = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressBar login_progress;
    private Button login_button;
    private Button register_button;
    private FrameLayout touchInterceptor;
    private ProgressDialog login_progressdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_login);

        //firebase auth
        mAuth = FirebaseAuth.getInstance();
        //firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Link variable to Edit Text id
        email_text = (EditText) findViewById(R.id.text_box_login_email);
        pw_text = (EditText) findViewById(R.id.text_box_login_pw);
        showpw = (Button) findViewById(R.id.btn_show_pw);
        touchInterceptor = (FrameLayout) findViewById(R.id.touchInterceptor);
        login_progressdialog = new ProgressDialog(this);

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
                            password_counter = -1;
                            showpw.setVisibility(View.GONE);
                        }
                    }
                }
                password_counter++;
            }
        });

        // On Touch Listener for Show Password Button
        showpw.setOnClickListener(new View.OnClickListener(){
            boolean reveal_pw = false;
            @Override
            public void onClick(View view) {
                if (!reveal_pw){
                    pw_text.setInputType(InputType.TYPE_CLASS_TEXT);
                    reveal_pw = true;
                    showpw.setBackgroundResource(R.drawable.ic_show_pw_off);
                }
                else{
                    pw_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    reveal_pw = false;
                    showpw.setBackgroundResource(R.drawable.ic_show_pw);
                }
                if (pw_text.isFocused()){
                    String new_text = pw_text.getText().toString();
                    pw_text.setText("");
                    pw_text.append(new_text);
                }
            }
        });

        //Touch Interceptor function allow EditText to lose focus when touch anywhere else
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
        Intent myIntent = new Intent(Login.this, ForgotPassword.class);
        Login.this.startActivity(myIntent);

    }

    //Button Login
    public void LoginOnClick(View v){

        String final_email = email_text.getText().toString();
        String final_password = pw_text.getText().toString();

        login_progressdialog.setTitle("Signing In");
        login_progressdialog.setMessage("Please wait while we check your credentials");
        login_progressdialog.setCanceledOnTouchOutside(false);
        login_progressdialog.show();

        mAuth.signInWithEmailAndPassword(final_email, final_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        mDatabase.child("User").child(uid).child("FirstTime").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String check_if_first_time = dataSnapshot.getValue().toString();
                                    if (check_if_first_time != null) {
                                        if (check_if_first_time.matches("yes")) {
                                            goToFirstTime();
                                        } else {
                                            goToHome();
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    // If sign in fails, display a message to the user.
                    login_progressdialog.hide();
                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Button Open Register activity
    public void RegisterOnClick(View v) {
        login_progressdialog.dismiss();
        Intent myIntent = new Intent(Login.this, RegisterNewUser.class);
        Login.this.startActivity(myIntent);

    }

    private void goToFirstTime(){
        login_progressdialog.dismiss();
        Intent myIntent = new Intent(Login.this, FirstTime.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Login.this.startActivity(myIntent);
        overridePendingTransition(0,0);
        finish();
    }

    private void goToHome(){
        login_progressdialog.dismiss();
        Intent myIntent = new Intent(Login.this, Home.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Login.this.startActivity(myIntent);
        overridePendingTransition(0,R.anim.fadein);
        finish();
    }
}



