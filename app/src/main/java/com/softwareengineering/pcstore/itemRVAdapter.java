package com.softwareengineering.pcstore;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class itemRVAdapter extends RecyclerView.Adapter<itemRVAdapter.MyViewHolder> implements Filterable {


    FirebaseDatabase database;
    DatabaseReference reference2;
    Context c;
    FirebaseAuth mAuth;
    ArrayList<Item> list,filterList;
    private FilterProduct filter ;

    public itemRVAdapter(Context context, ArrayList<Item> list, FirebaseAuth mAuth){
        c=context;
        this.list = list;
        database=FirebaseDatabase.getInstance();
        reference2=database.getReference("Cart");
        this.mAuth = mAuth;
        this.filterList = list ;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDetail(holder.mItem);
            }
        });
    }

    public void productDetail(Item item){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(c) ;
        View view = LayoutInflater.from(c).inflate(R.layout.bs_product_detail , null) ;

        bottomSheetDialog.setContentView(view);

        //initialize views of bottom sheet !
        ImageView backButton = view.findViewById(R.id.backButton) ;
        ImageView reviewButton = view.findViewById(R.id.reviewButton) ;
        ImageView deleteButton = view.findViewById(R.id.deleteButton) ;
        ImageView editButton = view.findViewById(R.id.editButton);
        ImageView product_icon = view.findViewById(R.id.product_icon) ;
        TextView product_title = view.findViewById(R.id.product_title) ;
        TextView product_description = view.findViewById(R.id.product_description) ;
        TextView product_price = view.findViewById(R.id.product_price) ;

        //get data
        String id = item.getId() ;
        String uid = item.getId();
        String name = item.getName();
        String brand = item.getBrand();
        String rating = item.getRating();
        String profile = item.getProfile();
        String price = item.getPrice();

        //set data
        product_title.setText(name);
        product_description.setText(brand);
        product_price.setText(price);

        //set pic
        Picasso.get().load(item.getProfile()).fit().centerCrop().into(product_icon);

        bottomSheetDialog.show();

        // review button clicked
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( c , addProductReview.class) ;
                intent.putExtra("ProductId" , id) ;
                c.startActivity(intent);
            }
        });

        // edit button clicked
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( c , editItem.class) ;
                intent.putExtra("ProductId" , id) ;
                c.startActivity(intent);
            }
        });

        //delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting up alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(c) ;
                builder.setTitle("Delete")
                        .setMessage("Are You Sure You Want To Delete "+ name + "?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               //deleted
                               deleteProduct(id) ;
                               bottomSheetDialog.dismiss();

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //don't delete
                                dialog.dismiss();
                            }
                        })
                        .show() ;
            }
        });

        //back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();

            }
        });

    }

    private void deleteProduct(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query q = reference1.child("Item").orderByChild("id").equalTo(id) ;

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot deleteSnapshot: snapshot.getChildren()) {
                    deleteSnapshot.getRef().removeValue();
                }
                Toast.makeText(c , "Product Deleted" + id , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e( TAG , "OnCancelled" , error.toException());
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

    @Override
    public Filter getFilter() {

        if (filter==null){
            filter = new FilterProduct(this,filterList) ;
        }
        return filter;
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
