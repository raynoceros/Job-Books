package com.jackong.jobbooks;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditLanguage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ListView language_listview;
    private Button add_button;
    private String user_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_language);

        //Add back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Set up firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Link variables to layout
        language_listview = (ListView)findViewById(R.id.language_listview);
        add_button = (Button)findViewById(R.id.add_new_button);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            user_uid = currentUser.getUid();
            final ArrayAdapter<String> language_list = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            mDatabase.child("User").child(user_uid).child("Profile").child("Language").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    language_list.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        String languages = data.getValue(String.class);
                        if (languages != null){
                            if (!languages.matches("-")) {
                                //means there is nothing
                                language_list.add(languages);
                            }
                        }
                    }
                    language_listview.setAdapter(language_list);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(EditLanguage.this, "Authentication Error.", Toast.LENGTH_SHORT).show();
            finish();
        }
        //list view on click listener
        language_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String selectedString = language_listview.getItemAtPosition(position).toString();
                //set Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EditLanguage.this);
                builder.setTitle("Delete Selected");
                builder.setMessage("Are you sure you want to delete " + selectedString + "?");
                // Set up the buttons
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (user_uid != null){
                            mDatabase.child("User").child(user_uid).child("Profile").child("Language").child(selectedString).removeValue();
                        }
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });
        //add button on click listener
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EditLanguage.this);
                builder.setTitle("Add Language");
                // Set up the input
                final AutoCompleteTextView language_text = new AutoCompleteTextView(EditLanguage.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                language_text.setInputType(InputType.TYPE_CLASS_TEXT);
                //set array adapter for location
                final ArrayAdapter<String> language_array = new ArrayAdapter<String>(EditLanguage.this, android.R.layout.simple_list_item_1);
                mDatabase.child("Application").child("Language").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            String language_value = data.getValue(String.class);
                            language_array.add(language_value);
                        }
                        //set array adapter to auto completion text view
                        language_text.setAdapter(language_array);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                builder.setView(language_text);
                // Set up the buttons
                builder.setNegativeButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String language_tobeadd = language_text.getText().toString();
                        if (user_uid != null){
                            mDatabase.child("User").child(user_uid).child("Profile").child("Language").child(language_tobeadd).setValue(language_tobeadd);
                        }
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
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
}
