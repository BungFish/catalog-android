package com.slogup.catalog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.slogup.catalog.CommonHelper;
import com.slogup.catalog.R;
import com.slogup.catalog.models.Product;

import java.util.ArrayList;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private ArrayList<Product> data;
    private Context mContext;
    private ClickListener clickListener;
    private int mSelectedCard;

    public ProductRecyclerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.data = new ArrayList<>();
        this.mContext = context;
    }


    public void setData(ArrayList<Product> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.product_thumbnail, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Product product = data.get(position);
        Glide.with(mContext).load(CommonHelper.urlFormatter(product, 0)).into(holder.imageView);

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.itemClick(v, getPosition());
            }
        }
    }

    public interface ClickListener {

        void itemClick(View view, int position);
    }

    public Product getItem(int position) {
        return data.get(position);
    }

    public int getmSelectedCard() {
        return mSelectedCard;
    }

    public void setmSelectedCard(int mSelectedCard) {
        this.mSelectedCard = mSelectedCard;
    }
}
