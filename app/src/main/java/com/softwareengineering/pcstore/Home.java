package com.softwareengineering.pcstore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Home extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference2;
    itemRVAdapter adapter;
    RecyclerView rv;
    FirebaseAuth mAuth;
    ArrayList<Item> itemList;
    CircleImageView profilePic;
    ImageView cart;
    ImageView add_product1;
    TextView productTab , orderTab , searchProduct;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rv = findViewById(R.id.rv);
        itemList = new ArrayList<Item>();
        profilePic = findViewById(R.id.profilepic);
        cart = findViewById(R.id.cart) ;
        add_product1 = findViewById(R.id.add_product1) ;
        productTab = findViewById(R.id.productTab) ;
        orderTab = findViewById(R.id.orderTab) ;
        searchProduct = findViewById(R.id.searchProduct) ;

        productTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHome=new Intent(Home.this,Home.class);
                startActivity(toHome);
            }
        });

        orderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapter.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHome=new Intent(Home.this,CartActivity.class);
                startActivity(toHome);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://pc-store-4e657-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference=database.getReference("Item");
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
                Item item = snapshot.getValue(Item.class);
                itemList.add(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Item item = snapshot.getValue(Item.class);
                //itemList.add(item);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Item item = snapshot.getValue(Item.class);
                itemList.remove(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add_product1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddProduct=new Intent(Home.this, Additem.class);
                startActivity(toAddProduct);
            }
        });


        adapter = new itemRVAdapter(this, itemList, mAuth);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);

        Toast.makeText(this, "It goes in here"+adapter.getItemCount(),Toast.LENGTH_SHORT).show();
    }
}