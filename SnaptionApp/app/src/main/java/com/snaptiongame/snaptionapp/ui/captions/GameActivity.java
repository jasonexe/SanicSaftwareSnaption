package com.snaptiongame.snaptionapp.ui.captions;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {
    List<Card> allCards;
    private final static String INITIAL_PACK = "InitialPack";
    private final FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();

    private Game game;
    private String photoPath;

    @BindView(R.id.image_view)
    protected ImageView imageView;

    @BindView(R.id.picker_photo)
    protected ImageView pickerPhoto;

    @BindView(R.id.picker_name)
    protected TextView pickerName;

    @BindView(R.id.flag_photo)
    protected ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        game = (Game)getIntent().getSerializableExtra(WallViewAdapter.GAME); //Obtaining data
        photoPath = game.getImagePath();
        FirebaseResourceManager.loadImageIntoView(photoPath, imageView);

        String userPath = FirebaseResourceManager.getUserPath(game.getPicker());
        firebaseResourceManager.retrieveSingleNoUpdates(userPath, new ResourceListener<User>() {
            @Override
            public void onData(User user) {
                pickerName.setText(user.getDisplayName());
                FirebaseResourceManager.loadImageIntoView(user.getImagePath(), pickerPhoto);
            }

            @Override
            public Class getDataType() {
                return User.class;
            }
        });

//        setSupportActionBar(toolbar);
        populateCards();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            // This will pop up the caption options
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void showCardSelectPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();

    }

    private void populateCards() {
        FirebaseResourceManager.loadCardsFromPack(INITIAL_PACK, new ResourceListener<List<Card>>() {
            @Override
            public void onData(List<Card> data) {
                allCards = data;
            }

            @Override
            public Class getDataType() {
                return null; // Not used.
            }
        });
    }

}
