package com.softwareengineering.pcstore;

import static android.content.ContentValues.TAG;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class editItem extends AppCompatActivity {

    String ProductId ;

    Button save ;
    private CircleImageView profilePhoto;
    private static final int pick_Image = 1;
    Uri imageUri;

    EditText name,brand,price,rating ;
    private FirebaseAuth fb ;
    FirebaseDatabase database ;
    DatabaseReference reference ;

    String Timestamp = ""+System.currentTimeMillis() ;
    String n,b,p,r,i,pp ;
    private String[] storagePermissions ;
    private static final int STORAGE_REQUEST_CODE=300 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        //getting intent values
        ProductId = getIntent().getStringExtra("ProductId") ;

        //ui
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

        // here we want to load the previous details added so we can edit afterwards
        //loadProductDetails() ;
        tryAgainToLoadProductDetails(ProductId);

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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data

                n = name.getText().toString() ;
                b = brand.getText().toString() ;
                p = price.getText().toString() ;
                r = rating.getText().toString() ;
                i = fb.getUid() ;
                pp = profilePhoto.toString();

                //validate
                if(TextUtils.isEmpty(n)){
                    Toast.makeText(editItem.this , "Product title required" , Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(b)){
                    Toast.makeText(editItem.this , "Brand name required" , Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(p)){
                    Toast.makeText(editItem.this , "Price is required" , Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(r)){
                    Toast.makeText(editItem.this , "Rating is required" , Toast.LENGTH_SHORT).show();
                }

                //if image is not updated
                if(imageUri == null){
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query q = reference1.child("Item").orderByChild("id").equalTo(ProductId) ;

                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot deleteSnapshot: snapshot.getChildren()) {
                                deleteSnapshot.child("name").getRef().setValue(n) ;
                                deleteSnapshot.child("brand").getRef().setValue(b) ;
                                deleteSnapshot.child("price").getRef().setValue(p) ;
                                deleteSnapshot.child("rating").getRef().setValue(r) ;
                                Picasso.get().load(pp).placeholder(R.drawable.ic_outline_camera_alt_24).into(profilePhoto);

                            }
                            Toast.makeText( editItem.this , "Product Data Updated" , Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e( TAG , "OnCancelled" , error.toException());
                        }
                    });

                    Toast.makeText(editItem.this,"Product Updated",Toast.LENGTH_LONG).show();

                    //clear data
                    name.setText("");
                    brand.setText("");
                    rating.setText("");
                    price.setText("");
                    profilePhoto.setImageResource(R.drawable.ic_outline_camera_alt_24);
                    imageUri=null;
                }

                // if image is updated
                else{
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

                                            //updation wali science
                                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                                            Query q = reference1.child("Item").orderByChild("id").equalTo(ProductId) ;

                                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot deleteSnapshot: snapshot.getChildren()) {
                                                        deleteSnapshot.child("name").getRef().setValue(n) ;
                                                        deleteSnapshot.child("brand").getRef().setValue(b) ;
                                                        deleteSnapshot.child("price").getRef().setValue(p) ;
                                                        deleteSnapshot.child("rating").getRef().setValue(r) ;
                                                        deleteSnapshot.child("profile").getRef().setValue(dp) ;

                                                    }
                                                    Toast.makeText( editItem.this , "Product Data Updated" , Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e( TAG , "OnCancelled" , error.toException());
                                                }
                                            });

                                            Toast.makeText(editItem.this,"Product Updated",Toast.LENGTH_LONG).show();

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
                                            Toast.makeText(editItem.this,"Image uploading failed at 1st",Toast.LENGTH_LONG).show();
                                            Log.e("exception",e.getMessage());
                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(editItem.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
    }

    public void tryAgainToLoadProductDetails(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query q = reference1.child("Item").orderByChild("id").equalTo(id) ;

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot deleteSnapshot: snapshot.getChildren()) {
                    String n = deleteSnapshot.child("name").getValue().toString() ;
                    String b = deleteSnapshot.child("brand").getValue().toString() ;
                    String p = deleteSnapshot.child("price").getValue().toString() ;
                    String r = deleteSnapshot.child("rating").getValue().toString() ;
                    String pro = deleteSnapshot.child("profile").getValue().toString() ;

                    //set data to views
                    name.setText(n);
                    brand.setText(b);
                    price.setText(p);
                    rating.setText(r);
                    Picasso.get().load(pro).placeholder(R.drawable.ic_outline_camera_alt_24).into(profilePhoto);
                }
                Toast.makeText( editItem.this , "Product Data Loaded" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e( TAG , "OnCancelled" , error.toException());
            }
        });
    }

    public void loadProductDetails(){
        DatabaseReference r = FirebaseDatabase.getInstance().getReference("Item");
        r.child(ProductId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String i = ""+snapshot.child("id").getValue() ;
                        String n = ""+snapshot.child("name").getValue() ;
                        String p = ""+snapshot.child("price").getValue() ;
                        String b = ""+snapshot.child("brand").getValue() ;
                        String r = ""+snapshot.child("rating").getValue() ;
                        String p1 = ""+snapshot.child("profile").getValue() ;

                        Toast.makeText(editItem.this , ""+ProductId , Toast.LENGTH_SHORT).show();

                        //set data to views
                        name.setText(n);
                        brand.setText(b);
                        price.setText(p);
                        rating.setText(r);
                        Picasso.get().load(p1).placeholder(R.drawable.ic_outline_camera_alt_24).into(profilePhoto);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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