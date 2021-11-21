package com.softwareengineering.pcstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference2;
    itemRVAdapter adapter;
    RecyclerView rv;
    FirebaseAuth mAuth;
    ArrayList<Item> itemList;
    CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rv = findViewById(R.id.rv);
        itemList = new ArrayList<Item>();
        profilePic = findViewById(R.id.profilepic);

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://pc-store-4e657-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference=database.getReference("Item");
        reference2=database.getReference("ProfilesTable");

        Query getImg = reference2.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
        getImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                    /*String dp = appleSnapshot.getRef().child("dp").getKey();
                    String mail = appleSnapshot.getRef().child("email");
                    System.out.println(mail);
                    Picasso.get().load(dp).centerCrop().into(profilePic);*/
                    Profile profile = appleSnapshot.getValue(Profile.class);
                    System.out.println(profile.getEmail());
                    Picasso.get().load(profile.getDp()).into(profilePic);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Item item = snapshot.getValue(Item.class);
                itemList.add(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Item item = snapshot.getValue(Item.class);
                itemList.add(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        adapter = new itemRVAdapter(this, itemList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);

        Toast.makeText(this, "It goes in here"+adapter.getItemCount(),Toast.LENGTH_SHORT).show();
    }
}