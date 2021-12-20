package com.softwareengineering.pcstore;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class addProductReview extends AppCompatActivity {

    String ProductId ;

    Button save ;
    private CircleImageView profilePhoto;
    private static final int pick_Image = 1;
    Uri imageUri;
    TextView itemName ;

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
        setContentView(R.layout.activity_add_product_review);

        //getting intent values
        ProductId = getIntent().getStringExtra("ProductId") ;

        //init
        save = findViewById(R.id.save);
        name = findViewById(R.id.name) ;
        rating = findViewById(R.id.rating) ;
        itemName = findViewById(R.id.itemName) ;
        // add Profile Photo
        profilePhoto = (CircleImageView) findViewById(R.id.profilePhoto);

        fb = FirebaseAuth.getInstance() ;
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Review");

        //permissions
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE} ;

        //set previous picture
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query q = reference1.child("Item").orderByChild("id").equalTo(ProductId) ;

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot deleteSnapshot: snapshot.getChildren()) {
                    String n = deleteSnapshot.child("name").getValue().toString() ;
                    String pro = deleteSnapshot.child("profile").getValue().toString() ;

                    //set data to views
                    itemName.setText(n);
                    Picasso.get().load(pro).placeholder(R.drawable.ic_outline_camera_alt_24).into(profilePhoto);
                }
                Toast.makeText( addProductReview.this , "Product Data Loaded" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e( TAG , "OnCancelled" , error.toException());
            }
        });

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
                r = rating.getText().toString() ;
                i = fb.getUid() ;

                //validate
                if(TextUtils.isEmpty(n)){
                    Toast.makeText(addProductReview.this , "Product review required" , Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(r)){
                    Toast.makeText(addProductReview.this , "Rating is required" , Toast.LENGTH_SHORT).show();
                }

                //adding review
                reference.push().setValue(
                        new Review(i
                                ,n
                                ,r
                                ,ProductId
                        ));

                Toast.makeText(addProductReview.this,"Review Added",Toast.LENGTH_LONG).show();

                //clear data
                name.setText("");
                rating.setText("");
                profilePhoto.setImageResource(R.drawable.ic_outline_camera_alt_24);

            }
        });

    }
}