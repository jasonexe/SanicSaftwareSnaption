package com.snaptiongame.snaption.ui.wall;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The adapter for Games to be displayed in the wall's recyclerview.
 * Handles creating a new game, viewing a game, and upvoting games.
 *
 * @author Brittany Berlanga
 * @author Cameron Geehr
 */

public class WallViewAdapter extends RecyclerView.Adapter<WallViewHolder> {

    private List<GameMetadata> items;
    private Map<Integer, WallViewHolder> itemNumToHolder;

    public WallViewAdapter(List<GameMetadata> items) {
        this.items = items;
        itemNumToHolder = new HashMap<>();
    }

    @Override
    public WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WallViewHolder holder = WallViewHolder.newInstance(parent);
        holder.gamePhoto.setMaxImageHeight(parent.getContext().getResources()
                .getDimension(R.dimen.max_wall_image_height));
        return holder;
    }

    @Override
    public void onBindViewHolder(final WallViewHolder holder, int position) {
        final GameMetadata game = items.get(position);
        itemNumToHolder.put(position, holder);
        holder.populate(game);
    }

    @Override
    public void onViewRecycled(WallViewHolder vh) {
        vh.clear();
        int position = vh.getAdapterPosition();
        itemNumToHolder.remove(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Adds a list of games.
     *
     * @param newGames a list of games to add
     */
    public void addItems(List<GameMetadata> newGames) {
        int startPos = items.size();
        items.addAll(newGames);
        this.notifyItemRangeChanged(startPos, newGames.size());
    }

    public void gameChanged(int changedIndex, Map<String, Integer> newUpvotes) {
        GameMetadata newGame = items.get(changedIndex);
        Map<String, Integer> oldUpvotes = newGame.getUpvotes();
        newGame.setUpvotes(newUpvotes);
        WallViewHolder holder = itemNumToHolder.get(changedIndex);

        if(holder != null) {
            if (newUpvotes == null) {
                holder.setUpvoteView(0, false, false);
            } else {
                String userId = FirebaseUserResourceManager.getUserId();
                boolean hasUpvoted = newUpvotes.containsKey(userId);
                boolean hasUpvotedPrior = oldUpvotes != null && oldUpvotes.containsKey(userId);
                holder.setUpvoteView(newUpvotes.size(), hasUpvoted, hasUpvoted
                        && !hasUpvotedPrior);
            }
        }
    }

    public void clearItems() {
        items.clear();
        itemNumToHolder.clear();
        notifyDataSetChanged();
    }
}
