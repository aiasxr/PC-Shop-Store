package com.softwareengineering.pcstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Additem extends AppCompatActivity {

    Button save ;
    private CircleImageView profilePhoto;
    private static final int pick_Image = 1;
    Uri imageUri;

    EditText name,brand,price,rating ;
    private FirebaseAuth fb ;
    FirebaseDatabase database ;
    DatabaseReference reference ;

    String Timestamp = ""+System.currentTimeMillis() ;
    String n,b,p,r,i ;
    private String[] storagePermissions ;

    private static final int STORAGE_REQUEST_CODE=300 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        save = findViewById(R.id.save);
        name = findViewById(R.id.name) ;
        brand = findViewById(R.id.brand) ;
        price = findViewById(R.id.price) ;
        rating = findViewById(R.id.rating) ;
        // add Profile Photo
        profilePhoto = (CircleImageView) findViewById(R.id.profilePhoto);

        fb = FirebaseAuth.getInstance() ;
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Item");


        //permissions 
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE} ;

        // Old Method of Uploading Picture
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // for Uploading Image from Gallery

                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_OPEN_DOCUMENT);

                startActivityForResult(Intent.createChooser(gallery, "select Picture"), pick_Image);
            }
        });

        //listener for save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data

                n = name.getText().toString() ;
                b = brand.getText().toString() ;
                p = price.getText().toString() ;
                r = rating.getText().toString() ;
                i = fb.getUid() ;

                //validate
                if(TextUtils.isEmpty(n)){
                    Toast.makeText(Additem.this , "Product title required" , Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(b)){
                    Toast.makeText(Additem.this , "Brand name required" , Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(p)){
                    Toast.makeText(Additem.this , "Price is required" , Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(r)){
                    Toast.makeText(Additem.this , "Rating is required" , Toast.LENGTH_SHORT).show();
                }

                //add to firebase
                // upload image to storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference=storageReference.child("ProductImage/"+Timestamp+".jpg");

                storageReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //get url of uploaded image
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl() ;

                                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadImageUri = uriTask.getResult() ;
                                        String dp=downloadImageUri.toString();

                                        reference.push().setValue(
                                                new Item(i
                                                        ,n
                                                        ,p
                                                        ,b
                                                        ,r
                                                        ,dp
                                                ));

                                        Toast.makeText(Additem.this,"Product Added",Toast.LENGTH_LONG).show();

                                        //clear data
                                        name.setText("");
                                        brand.setText("");
                                        rating.setText("");
                                        price.setText("");
                                        profilePhoto.setImageResource(R.drawable.ic_outline_camera_alt_24);
                                        imageUri=null;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Additem.this,"Image uploading failed at 1st",Toast.LENGTH_LONG).show();
                                        Log.e("exception",e.getMessage());
                                    }
                                });

                                /////////////////////////////////

//                                Uri downloadImageUri = uriTask.getResult() ;
//
//                                if(uriTask.isSuccessful()){
//                                    // url received
//                                    HashMap<String,Object> hashMap = new HashMap<>() ;
//
//                                    //put data now
//                                    hashMap.put("productId", ""+ Timestamp) ;
//                                    hashMap.put("productTitle", ""+n) ;
//                                    hashMap.put("productBrand", ""+b) ;
//                                    hashMap.put("productPrice", ""+p) ;
//                                    hashMap.put("productRating",""+r);
//                                    hashMap.put("productProfile", downloadImageUri );
//                                    hashMap.put("timeStamp",""+ Timestamp);
//                                    hashMap.put("uid",""+fb.getUid());
//
//                                    //add to db now
//                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Item");
//                                    reference=reference.child("ProductImage/"+".jpg");
//                                    reference.setValue(hashMap)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    Toast.makeText(Additem.this , "Product Added" , Toast.LENGTH_SHORT).show();
//
//                                                    //clear data
//                                                    name.setText("");
//                                                    brand.setText("");
//                                                    rating.setText("");
//                                                    price.setText("");
//                                                    profilePhoto.setImageResource(R.drawable.ic_outline_camera_alt_24);
//                                                    imageUri=null;
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(Additem.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Additem.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

    }

    private boolean checkStoragePermissions(){
        boolean res = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED) ;

        return res ;
    }

    private void requestStoragePermissions(){
        ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE);
    }

    //handle permissions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED ;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == pick_Image && resultCode == RESULT_OK){

            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profilePhoto.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}