package com.snaptiongame.snaption.ui.new_game;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.models.Person;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brittanyberlanga on 2/24/17.
 */

public class AddedPersonAdapter extends RecyclerView.Adapter<GameFriendViewHolder> {
    private List<Person> persons;

    public AddedPersonAdapter(List<Person> persons) {
        this.persons = persons;
    }
    @Override
    public GameFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return GameFriendViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(GameFriendViewHolder holder, int position) {
        final Person person = persons.get(position);
        holder.name.setText(person.getDisplayName());
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(person);
            }
        });
        FirebaseResourceManager.loadImageIntoView(person.getImagePath(), holder.photo);
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public void addItem(Person person) {
        if (!persons.contains(person)) {
            persons.add(person);
            notifyItemInserted(persons.size() - 1);
        }
    }

    public void removeItem(Person person) {
        int ndx = persons.indexOf(person);
        if (ndx >= 0) {
            persons.remove(ndx);
            notifyItemRemoved(ndx);
        }
    }

    public List<Person> getPersons() {
        return new ArrayList<>(persons);
    }
}
