package com.example.esanadmin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ImageRecycler extends RecyclerView.Adapter<ImageRecycler.ViewHolder> {

    private Context context;
    private ArrayList<String> bitmaplist = new ArrayList<>();
    private ArrayList<Item>  list = new ArrayList<Item>();
    private int pos;
    private String url = "http://35.234.10.58/";
    RequestManager mRequestManager;
    String num;
    public ImageRecycler(Context context, ArrayList<String> bitmaplist, ArrayList<Item> list) {
        this.context = context;
        this.bitmaplist = bitmaplist;
        this.list = list;
    }


    @NonNull
    @Override
    public ImageRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.image_list, parent, false);
        return new ImageRecycler.ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecycler.ViewHolder holder, int position) {
        Log.e("position", String.valueOf(position));
        //Log.e("pic",url+"cards/"+list.get(position));
        num = bitmaplist.get(position);
        Glide.with(context)
                .load(num)
                .diskCacheStrategy(DiskCacheStrategy.NONE )
                .skipMemoryCache(true)
                .centerCrop()
                .override(400,400)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return bitmaplist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_content);

        }
    }


}

