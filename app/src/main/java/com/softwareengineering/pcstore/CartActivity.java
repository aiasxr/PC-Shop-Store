package com.softwareengineering.pcstore;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference2;
    DatabaseReference reference3;
    DatabaseReference reference4;
    RecyclerView rv;
    FirebaseAuth mAuth;
    CartItemRVAdapter adapter;
    ArrayList<cartItem> cartItems;
    CircleImageView profilePic;
    AppCompatButton checkout;
    String eMail;
    String fullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rv = findViewById(R.id.rv);
        cartItems = new ArrayList<cartItem>();
        profilePic = findViewById(R.id.profilepic);


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        mAuth = FirebaseAuth.getInstance();
        eMail = mAuth.getCurrentUser().getEmail();
        database = FirebaseDatabase.getInstance("https://pc-store-4e657-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = database.getReference("Cart");
        reference2 = database.getReference("ProfilesTable");
        reference3 = database.getReference("Order");
        reference4 = database.getReference("OrderList");
        checkout = findViewById(R.id.Checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CartActivity.this, mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                String emailBody = createOrderCheckout(cartItems);
                try {
                    GMailSender sender = new GMailSender("pcstore666@gmail.com", "PCstoreiswack");
                    sender.sendMail("This is Subject",
                            emailBody,
                            "pcstore666@gmail.com",
                            eMail);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        });

        Query getImg = reference2.orderByChild("email").equalTo(eMail);
        getImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                    Profile profile = appleSnapshot.getValue(Profile.class);
                    fullName = profile.getName();
                    System.out.println(profile.getEmail());
                    Picasso.get().load(profile.getDp()).into(profilePic);
                }
                if (snapshot.exists()) {
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
                if (item.getEmail().equals(eMail)) {
                    cartItems.add(item);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                cartItem item = snapshot.getValue(cartItem.class);
                if (item.getEmail().equals(eMail)) {
                    cartItems.add(item);
                    adapter.notifyDataSetChanged();
                }
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

    final int orderIDLength = 12;

    public String createOrderCheckout(ArrayList<cartItem> cartItems) {
        int totalSumPrice = 0;
        String orderID = getAlphaNumericString(orderIDLength) + eMail.substring(0, 5);

        StringBuilder emailBody = new StringBuilder("<h3>Thank you for shopping at PC Store</h3>\n" +
                "        <h4>Hello " + fullName + ", You recently made a purchase at PC Store and here are details of the order:</h4>\n" +
                "        <table style=\"border-collapse:collapse;border-spacing:0\" class=\"tg\">\n" +
                "        <thead>\n" +
                "            <tr>\n" +
                "            <th style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;font-weight:normal;overflow:hidden;padding:12px 5px;position:-webkit-sticky;position:sticky;text-align:left;top:-1px;vertical-align:top;will-change:transform;word-break:normal\" class=\"tg-j1i3\" colspan=\"11\">Name</th>\n" +
                "            <th style=\"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;font-weight:normal;overflow:hidden;padding:12px 5px;position:-webkit-sticky;position:sticky;text-align:left;top:-1px;vertical-align:top;will-change:transform;word-break:normal\" class=\"tg-ul38\" colspan=\"4\">Price</th>\n" +
                "            </tr>\n" +
                "        </thead>\n"+"<tbody>\n");


        for (cartItem item : cartItems) {
            String price = item.getUnitPrice();
            int priceSpliter = price.indexOf("$");
            int totalPrice = (Integer.parseInt(price.substring(0, priceSpliter))) * Integer.parseInt(item.getAmount());
            totalSumPrice += totalPrice;
            //OrderList(String orderID, String itemID, String count, String price)
            emailBody.append("            <tr>\n" + "            <td style=\"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;overflow:hidden;padding:12px 5px;text-align:left;vertical-align:top;word-break:normal\" class=\"tg-0lax\" colspan=\"11\">").append(item.getItemName()).append("</td>\n").append("            <td style=\"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;overflow:hidden;padding:12px 5px;text-align:left;vertical-align:top;word-break:normal\" class=\"tg-0lax\" colspan=\"4\">").append(totalPrice).append("$</td>\n").append("            </tr>\n");

            reference4.push().setValue(
                    new OrderList(
                            orderID,
                            item.getId(),
                            item.getAmount(),
                            totalPrice + "$"
                    )
            );
        }



        emailBody.append("            <tr>\n" + "            <td style=\"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;overflow:hidden;padding:12px 5px;text-align:left;vertical-align:top;word-break:normal\" class=\"tg-0lax\" colspan=\"11\">" + "Total" + "</td>\n" + "            <td style=\"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;overflow:hidden;padding:12px 5px;text-align:left;vertical-align:top;word-break:normal\" class=\"tg-0lax\" colspan=\"4\">").append(totalSumPrice).append("$</td>\n").append("            </tr>\n").append("</tbody>\n").append("</table>").append("<h4>Please consider shoping from us in future as well.</h4>");

        String date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        reference3.push().setValue(
                new Order(
                        orderID,
                        eMail,
                        date1,
                        totalSumPrice + "$"
                )
        );

        adapter.deleteCart(eMail);

        return emailBody.toString();
    }


    // to generate Random Order ID
    static String getAlphaNumericString(int n) {

        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString
                = new String(array, StandardCharsets.UTF_8);

        // Create a StringBuffer to store the result
        StringBuilder r = new StringBuilder();

        // remove all spacial char
        String AlphaNumericString
                = randomString
                .replaceAll("[^A-Za-z0-9]", "");

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < AlphaNumericString.length(); k++) {

            if (Character.isLetter(AlphaNumericString.charAt(k))
                    && (n > 0)
                    || Character.isDigit(AlphaNumericString.charAt(k))
                    && (n > 0)) {

                r.append(AlphaNumericString.charAt(k));
                n--;
            }
        }
        return r.toString();
    }
}