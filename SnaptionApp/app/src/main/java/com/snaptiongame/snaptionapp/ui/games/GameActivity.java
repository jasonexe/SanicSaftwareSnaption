package com.snaptiongame.snaptionapp.ui.games;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    @BindView(R.id.flag_icon)
    protected ImageView flag;

    @BindView(R.id.number_captions)
    protected TextView numberCaptions;

    @BindView(R.id.text_date)
    protected TextView endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        game = (Game)getIntent().getSerializableExtra(WallViewAdapter.GAME); //Obtaining data
        photoPath = game.getImagePath();
        FirebaseResourceManager.loadImageIntoView(photoPath, imageView);

        numberCaptions.setText(Integer.toString(game.getCaptions().size()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(game.getEndDate());
        endDate.setText(new SimpleDateFormat("MM/dd/yy").format(calendar.getTime()));

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
