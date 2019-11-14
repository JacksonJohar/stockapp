package com.johar.Johar_Stocks;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import java.text.DecimalFormat;
import java.util.*;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.LayoutInflater;


public class StockAdapter extends RecyclerView.Adapter <
        StockAdapter.MyViewHolder > {
private List <StockBuilder>
        stockList;
private OnItemClickListener mListener;
private OnLongItemClickListener mlListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnLongItemClickListener {
        void onLongItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public void setOnLongItemClickListener(OnLongItemClickListener listener) {
        mlListener = listener;
    }
public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title, content, price, priceChange, percent;
    private Context context;

    public MyViewHolder(View view) {
        super(view);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mlListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mlListener.onLongItemClick(position);
                        //deleteItem(view, position);
                    }
                }
                return true;
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            }
        });
        title = (TextView) view.findViewById(R.id.title);
        content = (TextView) view.findViewById(R.id.name);
        price = (TextView) view.findViewById(R.id.price);
        priceChange = (TextView) view.findViewById(R.id.priceChange);
        percent = (TextView) view.findViewById(R.id.percent);

    }
}

    public StockAdapter(List <StockBuilder> stockList) {
        this.stockList = stockList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DecimalFormat df = new DecimalFormat("0.00");
        StockBuilder note = stockList.get(position);
        holder.title.setText(note.getTitle());
        String contOut;
        if (note.getContent().length() > 21){
            contOut = note.getContent().substring(0,20);
        } else {
            contOut = note.getContent();
        }
        Float pr = note.getPrice();
        Float pc = note.getChange();
        Float perc = note.getPercent();
        holder.content.setText(note.getContent());
        holder.price.setText(df.format(pr));
        holder.priceChange.setText(df.format(pc));
        holder.percent.setText("(" + df.format(perc) + "%)");

        if (note.getChange() < 0) {
            holder.title.setTextColor(Color.RED);
            holder.content.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.priceChange.setTextColor(Color.RED);
            holder.percent.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public void updateList(List<StockBuilder> data) {
        stockList = data;
        notifyDataSetChanged();
    }

    public void deleteItem(View v, int position) {
        MyViewHolder holder =(MyViewHolder)v.getTag();
        stockList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, stockList.size());
        holder.itemView.setVisibility(View.GONE);
    }

}
