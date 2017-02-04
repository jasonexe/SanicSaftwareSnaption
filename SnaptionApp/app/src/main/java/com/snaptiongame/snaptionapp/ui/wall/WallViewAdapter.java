package com.snaptiongame.snaptionapp.ui.wall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaptionapp.CreateGameActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

import java.util.List;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallViewAdapter extends RecyclerView.Adapter<WallViewHolder> {

    public static final String EXTRA_MESSAGE = "fromCurrentUri";
    public static final String PHOTO_PATH = "currentPhotoPath";
    public static final int CLIP_TO_OUTLINE_MIN_SDK = 21;
    private List<Game> items;

    public WallViewAdapter(List<Game> items) {
        this.items = items;
    }

    @Override
    public WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_game_item, parent, false);
        return new WallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WallViewHolder holder, int position) {
        Game game = items.get(position);
        holder.captionText.setText(game.getTopCaption() != null ?
                game.getTopCaption().retrieveCaptionText() :
                holder.captionerText.getContext().getResources().getString(R.string.caption_filler));
        FirebaseResourceManager.loadImageIntoView(game.getImagePath(), holder.photo);

        // distinguish between complete and incomplete games
        if (game.getIsOpen()) {
            holder.captionText.setTypeface(Typeface.create(holder.captionText.getTypeface(),
                    Typeface.NORMAL), Typeface.NORMAL);
        }
        else {
            holder.captionText.setTypeface(holder.captionText.getTypeface(), Typeface.BOLD);
        }
        if (Build.VERSION.SDK_INT >= CLIP_TO_OUTLINE_MIN_SDK) {
            // allows the image to be clipped with rounded edges
            holder.photo.setClipToOutline(true);
        }

        final String imagePath = game.getImagePath();
        // TODO add a confirmation dialog that they want to create the new game??
        holder.createFromExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If they want to create a game from this one, start the create game intent
                // with this game's image path
                FirebaseResourceManager.getImageURI(imagePath, new ResourceListener<Uri>() {
                    @Override
                    public void onData(Uri data) {
                        Context buttonContext = holder.createFromExisting.getContext();
                        Intent createGameIntent = new Intent(buttonContext, CreateGameActivity.class);
                        createGameIntent.putExtra(EXTRA_MESSAGE, data);
                        createGameIntent.putExtra(PHOTO_PATH, imagePath);
                        buttonContext.startActivity(createGameIntent);
                    }

                    @Override
                    public Class getDataType() {
                        return Uri.class;
                    }
                });
            }
        });

        // TODO add the actual captioner name and photo
        holder.captionerText.setText("First Last");
        Glide.with(holder.captionPhoto.getContext()).load("http://i75.servimg.com/u/f75/11/25/80/77/210.jpg").into(holder.captionPhoto);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<Game> newGames) {
        int startPos = items.size();
        items.addAll(newGames);
        this.notifyItemRangeChanged(startPos, newGames.size());
    }
}
