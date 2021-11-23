package com.softwareengineering.pcstore;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
        holder.removeCart.setOnClickListener(v -> {
            list.remove(position);
            String mail;
            mail = mAuth.getCurrentUser().getEmail();
            System.out.println(mail);
            Query cart = reference2.orderByChild("email").equalTo(mail);
            cart.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        cartItem cartItem1 = dataSnapshot.getValue(cartItem.class);
                        if(cartItem1.getId().equals(holder.mCartItem.getId())){
                            dataSnapshot.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


        holder.amount.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String mail;
                    mail = mAuth.getCurrentUser().getEmail();

                    String newText = holder.amount.getText().toString();
                    Query cart = reference2.orderByChild("email").equalTo(mail);
                    list.remove(position);
                    cart.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                cartItem cartItem1 = appleSnapshot.getValue(cartItem.class);
                                if(cartItem1.getId().equals(holder.mCartItem.getId())) {
                                    appleSnapshot.getRef().child("amount").setValue(newText);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    return true;
                }
                return false;
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
