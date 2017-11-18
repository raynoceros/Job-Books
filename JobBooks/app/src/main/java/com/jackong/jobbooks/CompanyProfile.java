package com.jackong.jobbooks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class CompanyProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mImageStorage;
    private FirebaseUser currentUser;

    private ImageView profile_pic;
    private TextView profile_name;
    private TextView profile_email;
    private TextView profile_description;
    private TextView profile_address;
    private TextView profile_contact_number;
    private Button profile_info_edit_button;

    private RelativeLayout company_info_container;

    public static final int GALLERY_PICK = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent myIntent;
            switch (item.getItemId()) {
                case R.id.navigation_company_chat:

                    myIntent = new Intent(CompanyProfile.this, CompanyChat.class);
                    CompanyProfile.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_company_post:

                    myIntent = new Intent(CompanyProfile.this, CompanyPost.class);
                    CompanyProfile.this.startActivity(myIntent);
                    overridePendingTransition(0,0);
                    finishActivity(0);

                    return true;
                case R.id.navigation_company_profile:
                    //do nothing

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_company);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.navigation_company_profile);

        //Link variables
        profile_name = (TextView)findViewById(R.id.profile_name);
        profile_email = (TextView)findViewById(R.id.profile_email);
        profile_description = (TextView)findViewById(R.id.company_description);
        profile_address = (TextView)findViewById(R.id.company_address);
        profile_contact_number = (TextView)findViewById(R.id.company_contact);
        profile_info_edit_button = (Button)findViewById(R.id.profile_edit_button);
        profile_pic = (ImageView)findViewById(R.id.profile_picture);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        company_info_container = (RelativeLayout)findViewById(R.id.profile_company_container);

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

        //Set Edit Button
        profile_info_edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CompanyProfile.this);
                final EditText profile_name_edit = new EditText(CompanyProfile.this);
                profile_name_edit.setHint(profile_name.getText().toString());
                builder.setTitle("Change Name");
                builder.setView(profile_name_edit);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (profile_name_edit.getText().toString().matches("")){
                            Toast.makeText(getApplicationContext(), "Nothing changes.", Toast.LENGTH_SHORT).show();
                        }
                        else if (profile_name_edit.getText().toString().matches(profile_name_edit.getHint().toString())){
                            Toast.makeText(getApplicationContext(), "Nothing changes.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String currentUser = mAuth.getUid();
                            String tv_name = profile_name_edit.getText().toString();
                            if (currentUser !=null) {
                                mDatabase.child("User").child(currentUser).child("Profile").child("Name").setValue(tv_name);
                                Toast.makeText(getApplicationContext(), "Update successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
        });

        //Set Edit Button For Info
        company_info_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent (CompanyProfile.this, EditCompanyInfo.class);
                CompanyProfile.this.startActivity(myIntent);
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
                                            Toast.makeText(CompanyProfile.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(CompanyProfile.this, "Error uploading", Toast.LENGTH_SHORT).show();
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

    private void goToMain() {
        Intent startIntent = new Intent(CompanyProfile.this, Login.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    public String getImageExt(Uri uri){
        String uri_path = uri.toString();
        return uri_path.substring(uri_path.lastIndexOf("."));
    }

    private void Update_Info(String uid) {


        mDatabase.child("User").child(uid).child("Profile").child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String name = dataSnapshot.getValue().toString();
                    profile_name = (TextView)findViewById(R.id.profile_name);
                    if (name != null){
                        profile_name.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profile_email = (TextView) findViewById(R.id.profile_email);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            profile_email.setText(currentUser.getEmail());
        }

        mDatabase.child("User").child(uid).child("Profile").child("Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String profile_image_link = dataSnapshot.getValue().toString();
                    if (profile_image_link != null){
                        if (!profile_image_link.matches("-")) {
                            Picasso.with(CompanyProfile.this).load(profile_image_link).into(profile_pic);
                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("Description").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String desc = dataSnapshot.getValue().toString();
                    if (desc != null){
                        profile_description.setText(desc);
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
                    String address = dataSnapshot.getValue().toString();
                    if (address != null){
                        profile_address.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("User").child(uid).child("Profile").child("ContactNo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String contact = dataSnapshot.getValue().toString();
                    if (contact != null){
                        profile_contact_number.setText(contact);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
