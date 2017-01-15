package com.snaptiongame.snaptionapp;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallViewAdapter extends RecyclerView.Adapter<WallViewHolder> {
    private static final int CLIP_TO_OUTLINE_MIN_SDK = 21;
    protected List<TempGame> items;
    public WallViewAdapter(List<TempGame> items) {
        this.items = items;
    }
    @Override
    public WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_game_item, parent, false);
        return new WallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WallViewHolder holder, int position) {
        holder.captionText.setText(items.get(position).caption);
        holder.captionerText.setText(items.get(position).captionerPhoto);
        Glide.with(holder.photo.getContext()).load(items.get(position).gamePhoto).into(holder.photo);
        Glide.with(holder.captionPhoto.getContext()).load("http://i75.servimg.com/u/f75/11/25/80/77/210.jpg").into(holder.captionPhoto);
        if (Build.VERSION.SDK_INT >= CLIP_TO_OUTLINE_MIN_SDK) {
            // allows the image to be clipped with rounded edges
            holder.photo.setClipToOutline(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
