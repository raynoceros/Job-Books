package com.jackong.jobbooks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    private RelativeLayout education_container;
    private RelativeLayout language_container;
    private RelativeLayout additional_info_container;
    private RelativeLayout about_me_container;

    //FireBase Declaration
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mImageStorage;
    private FirebaseUser currentUser;


    private TextView name_profile;
    private TextView email_profile;
    private TextView date_profile;
    private TextView institute_profile;
    private TextView qualification_profile;
    private TextView course_profile;
    private TextView salary_profile;
    private TextView work_location_profile;
    private TextView address_profile;
    private TextView nationality_profile;
    private ListView language_listview;
    private ImageView profile_pic;

    public static final int GALLERY_PICK = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent myIntent;
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    myIntent = new Intent(Profile.this, Home.class);
                    Profile.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_search:

                    myIntent = new Intent(Profile.this, Search.class);
                    Profile.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;

                case R.id.navigation_notifications:

                    myIntent = new Intent(Profile.this, Notification.class);
                    Profile.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_chat:

                    myIntent = new Intent(Profile.this, Chat.class);
                    Profile.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_profile:
                    //do nothing

                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.navigation_profile);

        //Link variables to layout or Link variables
        education_container = (RelativeLayout)findViewById(R.id.profile_education);
        language_container = (RelativeLayout)findViewById(R.id.profile_language);
        additional_info_container = (RelativeLayout)findViewById(R.id.profile_additional);
        about_me_container = (RelativeLayout)findViewById(R.id.profile_about_me);
        profile_pic = (ImageView)findViewById(R.id.profile_picture);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        language_listview = (ListView)findViewById(R.id.language_listview);

        //Set Upload Image Button for Profile Pic
        profile_pic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        //Set Edit Button for Education
        education_container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent myIntent = new Intent (Profile.this, EditEducation.class);
                Profile.this.startActivity(myIntent);
            }
        });

        //Set Edit Button for Language
        language_container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent myIntent = new Intent (Profile.this, EditLanguage.class);
                Profile.this.startActivity(myIntent);
            }
        });

        //Set Edit Button for Additional Info
        additional_info_container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent myIntent = new Intent (Profile.this, EditAdditionalInfo.class);
                Profile.this.startActivity(myIntent);
            }
        });

        //Set Edit Button for About Me
        about_me_container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent myIntent = new Intent (Profile.this, EditAboutMe.class);
                Profile.this.startActivity(myIntent);
            }
        });


        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            String uid = currentUser.getUid();
            //update information
            Update_Info(uid);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading Image...");
            dialog.setMessage("Please wait while we upload your image...");
            dialog.show();

            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                currentUser = mAuth.getCurrentUser();
                if (currentUser!= null){
                    final String user_id = currentUser.getUid();
                    StorageReference filepath = mImageStorage.child("profile_images").child(user_id + getImageExt(resultUri));
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                final String download_uri = task.getResult().getDownloadUrl().toString();
                                mDatabase.child("User").child(user_id).child("Profile").child("Image").setValue(download_uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            dialog.dismiss();
                                            Toast.makeText(Profile.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(Profile.this, "Error uploading", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                dialog.dismiss();
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            mAuth.signOut();
            goToMain();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            goToMain();
        }
        else {
            String uid = currentUser.getUid();
            //update information
            Update_Info(uid);
        }
    }

    private void goToMain() {
        Intent startIntent = new Intent(Profile.this, Login.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        finish();
    }

    private void Update_Info(String uid){
        mDatabase.child("User").child(uid).child("Profile").child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_name = dataSnapshot.getValue().toString();
                    name_profile = (TextView)findViewById(R.id.profile_name);
                    if (name_profile != null){
                        name_profile.setText(profile_name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        email_profile = (TextView) findViewById(R.id.profile_email);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            email_profile.setText(currentUser.getEmail());
        }

        mDatabase.child("User").child(uid).child("Profile").child("DateCreated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_date_value = dataSnapshot.getValue().toString();
                    date_profile = (TextView) findViewById(R.id.profile_date_created);
                    if (date_profile != null){
                        date_profile.setText("Date Created: " + profile_date_value);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_image_link = dataSnapshot.getValue().toString();
                    if (profile_image_link != null){
                        Picasso.with(Profile.this).load(profile_image_link).into(profile_pic);
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Institute").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_institute = dataSnapshot.getValue().toString();
                    institute_profile = (TextView)findViewById(R.id.institute_name);
                    if (institute_profile != null){
                        institute_profile.setText(profile_institute);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Qualification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_qualification = dataSnapshot.getValue().toString();
                    qualification_profile = (TextView)findViewById(R.id.education_qualification);
                    if (qualification_profile != null){
                        qualification_profile.setText(profile_qualification);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Course").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_course = dataSnapshot.getValue().toString();
                    course_profile = (TextView)findViewById(R.id.education_field_of_study);
                    if (course_profile != null){
                        course_profile.setText(profile_course);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ArrayAdapter<String> language_list = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDatabase.child("User").child(uid).child("Profile").child("Language").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                language_list.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String languages = data.getValue(String.class);
                    if (languages != null){
                        language_list.add(languages);
                    }
                }
                language_listview.setAdapter(language_list);
                justifyListViewHeightBasedOnChildren(language_listview);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Expected Salary").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_salary = dataSnapshot.getValue().toString();
                    salary_profile = (TextView)findViewById(R.id.profile_salary);
                    if (profile_salary != null){
                        if (!profile_salary.matches("-")){
                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            String formattedSalary = formatter.format(Integer.parseInt(profile_salary));
                            salary_profile.setText("MYR " + formattedSalary);
                        }
                        else {
                            salary_profile.setText(profile_salary);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Preferred Work Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_work_location = dataSnapshot.getValue().toString();
                    work_location_profile = (TextView)findViewById(R.id.profile_preferred_location);
                    if (work_location_profile != null){
                        work_location_profile.setText(profile_work_location);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_address = dataSnapshot.getValue().toString();
                    address_profile = (TextView)findViewById(R.id.profile_address);
                    if (address_profile != null){
                        address_profile.setText(profile_address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Nationality").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_nationality = dataSnapshot.getValue().toString();
                    nationality_profile = (TextView)findViewById(R.id.profile_nationality);
                    if (nationality_profile != null){
                        nationality_profile.setText(profile_nationality);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    public String getImageExt(Uri uri){
        String uri_path = uri.toString();
        return uri_path.substring(uri_path.lastIndexOf("."));
    }
}
