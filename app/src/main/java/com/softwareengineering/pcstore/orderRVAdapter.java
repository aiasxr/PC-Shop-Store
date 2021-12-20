package com.softwareengineering.pcstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class orderRVAdapter extends RecyclerView.Adapter<orderRVAdapter.HolderOrder> {

    private Context context ;
    private ArrayList<Order> orderArrayList ;

    public orderRVAdapter(Context context, ArrayList<Order> orderArrayList, FirebaseAuth mAuth) {
        this.context = context;
        this.orderArrayList = orderArrayList;
    }

    @NonNull
    @Override
    public HolderOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order, parent, false);
        return new HolderOrder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrder holder, int position) {
        holder.morder = orderArrayList.get(position);
        Order order = orderArrayList.get(position) ;

        String i = orderArrayList.get(position).getID();
        String email = orderArrayList.get(position).getEmail();
        String p = orderArrayList.get(position).getTotal();
        String d = orderArrayList.get(position).getDate();

        //set
        holder.orderId.setText("Order Id: " + i);
        holder.orderBy.setText("Order By: " + email);
        holder.orderAmount.setText("Total: " + p );
        holder.orderDate.setText(d);
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    class HolderOrder extends RecyclerView.ViewHolder{

        //create views
        private TextView orderId , orderDate , orderBy , orderAmount ;
        Order morder ;

        public HolderOrder(@NonNull View itemView) {
            super(itemView);

            //init view
            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderBy = itemView.findViewById(R.id.orderBy);
            orderAmount = itemView.findViewById(R.id.orderAmount);

        }
    }
}
