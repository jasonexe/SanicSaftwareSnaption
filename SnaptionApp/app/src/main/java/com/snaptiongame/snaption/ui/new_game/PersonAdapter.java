package com.snaptiongame.snaption.ui.new_game;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.models.Person;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.ui.friends.PersonViewHolder;

import java.util.List;

/**
 * Created by brittanyberlanga on 2/24/17.
 */

public class PersonAdapter extends RecyclerView.Adapter<PersonViewHolder> {
    private List<Person> persons;
    private AddListener addListener;

    interface AddListener {
        void onPersonSelected(Person person);
    }

    public PersonAdapter(List<Person> persons, AddListener addListener) {
        this.persons = persons;
        this.addListener = addListener;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PersonViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, int position) {
        final Person person = persons.get(position);
        holder.name.setText(person.getDisplayName());
        holder.email.setText(person.getEmail());
        holder.email.setVisibility(TextUtils.isEmpty(person.getEmail()) ? View.GONE : View.VISIBLE);
        // set the image
        if (person.getImagePath() != null) {
            FirebaseResourceManager.loadImageIntoView(persons.get(position).getImagePath(), holder.photo);
        }
        else {
            FirebaseResourceManager.loadSmallFbPhotoIntoImageView(person.getFacebookId(), holder.photo);
        }
        // set click listener
        if (addListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addListener.onPersonSelected(person);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public void addSingleItem(Person person) {
        persons.add(person);
        notifyItemInserted(persons.size() - 1);
    }
}
