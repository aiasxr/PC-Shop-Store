package com.softwareengineering.pcstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends AppCompatActivity {

    CircleImageView dp;
    Uri imgURI;

    Button back, post;
    EditText firstName, lastName, gender, phoneNo, aboutMe;
    FirebaseDatabase database;
    DatabaseReference reference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Intent data=getIntent();
        String id=data.getStringExtra("id");
        String email=data.getStringExtra("email");


        firstName = findViewById(R.id.FirstName);
        lastName = findViewById(R.id.LastName);
        gender = findViewById(R.id.Gender);
        dp = findViewById(R.id.dp);
        post = findViewById(R.id.post);
        phoneNo = findViewById(R.id.phoneNo);
        aboutMe = findViewById(R.id.AboutMe);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("ProfilesTable");


        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(CreateProfileActivity.this)
                        .galleryOnly()
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)*/
                        //.saveDir(new File(getFilesDir(), "ImagePicker"))
                        .start();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgURI!=null){
                    StorageReference st= FirebaseStorage.getInstance().getReference();
                    st=st.child("images/"+id+".jpg");
                    st.putFile(imgURI)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task= taskSnapshot.getStorage().getDownloadUrl();

                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String dp=uri.toString();
                                            String name = firstName.getText().toString() +" "+ lastName.getText().toString();
                                            reference.push().setValue(
                                                    new Profile(id
                                                            ,email
                                                            ,name
                                                            ,gender.getText().toString()
                                                            ,phoneNo.getText().toString()
                                                            ,aboutMe.getText().toString()
                                                            ,dp));

                                            Intent intent=new Intent(CreateProfileActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CreateProfileActivity.this,"Image uploading failed at 1st",Toast.LENGTH_LONG).show();
                                            Log.e("exception",e.getMessage());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateProfileActivity.this,"Image uploading failed at 2nd",Toast.LENGTH_LONG).show();
                            Log.e("exception",e.getMessage());
                        }
                    });
                }
                else{
                    Toast.makeText(CreateProfileActivity.this,"Image not loaded",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    protected void login() {
        startActivity(new Intent(CreateProfileActivity.this,LoginActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imgURI = data.getData();
        dp.setImageURI(imgURI);
    }
}