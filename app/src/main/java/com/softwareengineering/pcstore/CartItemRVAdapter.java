package com.softwareengineering.pcstore;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartItemRVAdapter extends RecyclerView.Adapter<CartItemRVAdapter.MyViewHolder> {


    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference2;
    Context c;
    FirebaseAuth mAuth;
    ArrayList<cartItem> list;

    public CartItemRVAdapter(Context context, ArrayList<cartItem> list, FirebaseAuth mAuth){
        c=context;
        this.list = list;
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Item");
        reference2=database.getReference("Cart");
        this.mAuth = mAuth;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(c).inflate(R.layout.cartitembox, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mCartItem = list.get(position);

        Query itemFind = reference.orderByChild("id").equalTo(holder.mCartItem.getId());

        itemFind.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Item item = dataSnapshot.getValue(Item.class);

                    Picasso.get().load(item.getProfile()).fit().centerCrop().into(holder.img);
                    holder.name.setText(item.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        String price = holder.mCartItem.getUnitPrice();
        int priceSpliter = price.indexOf("$");
        int totalPrice = (Integer.parseInt(price.substring(0, priceSpliter))) * Integer.parseInt(holder.mCartItem.getAmount());

        holder.price.setText(totalPrice +"$");
        holder.amount.setText(holder.mCartItem.getAmount());
        holder.removeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addItemToCart(holder.mCartItem);
            }
        });
    }

    public void addItemToCart(cartItem item){
        String mail;
        mail = mAuth.getCurrentUser().getEmail();
        System.out.println(mail);
        Query cart = reference2.orderByChild("email").equalTo(mail);
        cart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean checker = true;
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    cartItem cartItem1 = dataSnapshot.getValue(cartItem.class);
                    System.out.println(mail+"   "+cartItem1.getId());
                    if(cartItem1.getId().equals(item.getId())){
                        checker = false;
                        System.out.println("It goes inside Equal If");

                        System.out.println(mail+"   "+cartItem1.getId());
                        dataSnapshot.getRef().child("amount").setValue(Integer.toString(Integer.parseInt(cartItem1.getAmount())+1));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, price;
        EditText amount;
        ImageButton removeCart;
        cartItem mCartItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            amount = itemView.findViewById(R.id.productAmount);
            removeCart = itemView.findViewById(R.id.Remove_from_cart);
        }
    }
}
