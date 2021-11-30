package com.softwareengineering.pcstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class itemRVAdapter extends RecyclerView.Adapter<itemRVAdapter.MyViewHolder> {


    FirebaseDatabase database;
    DatabaseReference reference2;
    Context c;
    FirebaseAuth mAuth;
    ArrayList<Item> list;

    public itemRVAdapter(Context context, ArrayList<Item> list, FirebaseAuth mAuth){
        c=context;
        this.list = list;
        database=FirebaseDatabase.getInstance();
        reference2=database.getReference("Cart");
        this.mAuth = mAuth;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(c).inflate(R.layout.itembox, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mItem = list.get(position);
        Picasso.get().load(holder.mItem.getProfile()).fit().centerCrop().into(holder.img);

        holder.price.setText(list.get(position).getPrice());
        holder.name.setText(list.get(position).getName());
        holder.rating.setText(list.get(position).getRating());
        holder.addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToCart(holder.mItem);
            }
        });
    }

    public void addItemToCart(Item item){
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
                if (checker){
                    reference2.push().setValue(
                            new cartItem(
                                    item.getId(),
                                    item.getName(),
                                    mail,
                                    "1",
                                    item.getPrice()
                            )
                    );
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
        TextView name, price, rating;
        ImageButton addCart;
        Item mItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            rating = itemView.findViewById(R.id.productRating);
            addCart = itemView.findViewById(R.id.add_to_cart);
        }
    }
}
