package com.softwareengineering.pcstore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Additem extends AppCompatActivity {

    Button save ;
    private CircleImageView profilePhoto;
    private static final int pick_Image = 1;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        save = findViewById(R.id.save);
        // add Profile Photo
        profilePhoto = (CircleImageView) findViewById(R.id.profilePhoto);

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