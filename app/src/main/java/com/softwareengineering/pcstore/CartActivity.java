package com.softwareengineering.pcstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class CartActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference2;
    RecyclerView rv;
    FirebaseAuth mAuth;
    CartItemRVAdapter adapter;
    ArrayList<cartItem> cartItems;
    CircleImageView profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rv = findViewById(R.id.rv);
        cartItems = new ArrayList<cartItem>();
        profilePic = findViewById(R.id.profilepic);

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://pc-store-4e657-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference=database.getReference("Cart");
        reference2=database.getReference("ProfilesTable");

        Query getImg = reference2.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
        getImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                    Profile profile = appleSnapshot.getValue(Profile.class);
                    System.out.println(profile.getEmail());
                    Picasso.get().load(profile.getDp()).into(profilePic);
                }
                if(snapshot.exists()){
                    System.out.println("exist works");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                cartItem item = snapshot.getValue(cartItem.class);
                cartItems.add(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                cartItem item = snapshot.getValue(cartItem.class);
                cartItems.add(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        adapter = new CartItemRVAdapter(this, cartItems, mAuth);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
    }
}