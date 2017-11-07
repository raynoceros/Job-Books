package com.jackong.jobbooks;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class RegisterNewUser extends AppCompatActivity {

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
}
