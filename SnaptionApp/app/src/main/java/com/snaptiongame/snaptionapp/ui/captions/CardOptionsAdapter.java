package com.snaptiongame.snaptionapp.ui.captions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.ui.profile.ProfileGameViewHolder;

import java.util.List;

/**
 * Created by jason_000 on 2/6/2017.
 */

public class CardOptionsAdapter extends RecyclerView.Adapter<CardOptionsViewHolder> {

    private List<Card> options;
    private GameActivity.CardToTextConverter converter;

    public CardOptionsAdapter(List<Card> options, GameActivity.CardToTextConverter converter) {
        this.options = options;
        this.converter = converter;
    }

    @Override
    public void onBindViewHolder(CardOptionsViewHolder holder, final int position) {
        holder.possibleCardView.setText(getCardAtPos(position)
                .getCardText().replace("%s", "_____"));
        holder.possibleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                converter.convertCard(getCardAtPos(position));
            }
        });
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

    public Card getCardAtPos(int position) {
        return options.get(position);
    }

    public void setOptions(List<Card> options) {
        this.options = options;
        this.notifyDataSetChanged();
    }
}
