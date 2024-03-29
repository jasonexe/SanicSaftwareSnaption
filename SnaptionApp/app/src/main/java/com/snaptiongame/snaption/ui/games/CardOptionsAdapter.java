package com.snaptiongame.snaption.ui.games;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Card;

import java.util.List;

/**
 * Adapter that puts cards in their spots in the recycler view
 * Created by Jason Krein on 2/6/2017.
 */

public class CardOptionsAdapter extends RecyclerView.Adapter<CardOptionsViewHolder> {

    private List<Card> options;
    private GameActivity.CardToTextConverter converter;

    public CardOptionsAdapter(List<Card> options, GameActivity.CardToTextConverter converter) {
        this.options = options;
        this.converter = converter;
    }

    @Override
    public void onBindViewHolder(CardOptionsViewHolder holder, int position) {
        final Card cardAtPos = getCardAtPos(position);
        holder.possibleCardView.setText(cardAtPos
                .getCardText().replace("%s", "_____"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                converter.convertCard(cardAtPos);
            }
        });
        if(cardAtPos.getId().equals(GameActivity.REFRESH_STRING)) {
            holder.itemView.getBackground().setColorFilter(holder.possibleCardView.getResources()
                    .getColor(R.color.greenBackground),
                    PorterDuff.Mode.DARKEN);
            holder.possibleCardView.setTextColor(0xFFFFFFFF);
            holder.refreshIcon.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.getBackground().setColorFilter(holder.possibleCardView.getResources()
                    .getColor(android.R.color.white),
                    PorterDuff.Mode.DARKEN);
            holder.possibleCardView.setTextColor(holder.possibleCardView.getResources()
                    .getColor(R.color.colorText));
            holder.refreshIcon.setVisibility(View.GONE);
        }

        if(cardAtPos.getId().equals(GameActivity.BLANK_CARD)) {
            holder.possibleCardView.setText(holder.possibleCardView.getContext().getString(R.string.make_own_caption_prompt));
        }
    }

    @Override
    public CardOptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_option_layout, parent, false);
        return new CardOptionsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    private Card getCardAtPos(int position) {
        return options.get(position);
    }

    void replaceOptions(List<Card> cards) {
        options.clear();
        this.notifyItemRangeRemoved(0, cards.size());
        options.addAll(cards);
        this.notifyItemRangeInserted(0, options.size());
    }
}
