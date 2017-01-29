package com.snaptiongame.snaptionapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.Uploader;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.bitmap;
import static android.R.attr.data;


/**
 * @author Cameron Geehr
 */

public class CreateGameActivity extends AppCompatActivity {

    public static final String IMAGE_FOLDER = "images/";
    public static final String GAMES_PATH_REF = "games";
    public static final int DATE_DIALOG_ID = 999;

    // Create a storage reference from our app
    private Uploader uploader;

    private Uri imageUri;
    private ArrayList<String> categories;
    private String maturityRating;
    private boolean isPublic;
    private long endDate;
    private Calendar calendar;
    private int year, month, day;

    @BindView(R.id.buttonSelect)
    protected Button buttonSelect;

    @BindView(R.id.buttonUpload)
    protected Button buttonUpload;

    @BindView(R.id.imageview)
    protected ImageView imageView;

    @BindView(R.id.radio_private)
    protected RadioButton radioPrivate;

    @BindView(R.id.radio_public)
    protected RadioButton radioPublic;

    @BindView(R.id.radio_everyone)
    protected RadioButton radioEveryone;

    @BindView(R.id.radio_adult)
    protected RadioButton radioAdult;

    @BindView(R.id.category_input)
    protected EditText categoryInput;

    @BindView(R.id.text_date)
    protected TextView dateView;

    @BindView(R.id.button_set_date)
    protected Button buttonSetDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(WallViewAdapter.EXTRA_MESSAGE);
        if(uri != null) {
            imageUri = uri;
            setImageFromUrl(uri);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        uploader = new FirebaseUploader();
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(year, month + 1, day);

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose Image from..."), 8);
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data = null;
                //Generate unique key for Games
                final String gameId = uploader.getNewGameKey();

                if (imageUri == null) {
                    //Notification to say "You must pick an image"
                }
                else if (!radioPrivate.isChecked() && !radioPublic.isChecked()) {
                    //Notification to say "You must choose whether the game is public or private"
                }
                else if (!radioAdult.isChecked() && !radioEveryone.isChecked()) {
                    //Notification to say "You must choose who the game is appropriate for"
                }
                else {
                    data = getImageFromUri(imageUri);
                    categories = getCategoriesFromText(categoryInput.getText().toString());
                    for (String i : categories) {
                        System.out.println(i);
                    }
                    final Game game = new Game(gameId, "1", IMAGE_FOLDER + gameId,
                            new ArrayList<String>(), categories, isPublic, endDate, maturityRating);

                    //uploader.addGame(data, game); //Questions about interface parameter
                }
            }
        });

        radioPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Private");
                isPublic = false;
            }
        });

        radioPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Public");
                isPublic = true;
            }
        });

        radioEveryone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Is radioEveryone checked? " + radioEveryone.isChecked());
                System.out.println("Is radioEveryone checked? " + radioAdult.isChecked());
                maturityRating = "E";
            }
        });

        radioAdult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Adult");
                maturityRating = "M";
            }
        });

        buttonSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(view);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return true;
    }

    // Sets the image in the imageview
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        try {
            InputStream stream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            imageView.setImageBitmap(bitmap);
            imageView.setBackground(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setImageFromUrl(Uri uri) {
        Glide.with(CreateGameActivity.this).load(uri).into(imageView);
        imageView.setBackground(null);
    }

    /**
     * Takes the image from a uri and converts it into a byte array for uploading.
     *
     * @param imageUri - The location of the image in the device
     * @return A byte array containing the data from the image
     */
    private byte[] getImageFromUri(Uri imageUri) {
        byte[] data = null;

        try {
            InputStream stream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
            baos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Takes a string of text, separates each word by comma, and removes any repeated words.
     *
     * @param text - The text to parse with comma delimiters
     * @return A list of strings not containing repeats or empty strings
     */
    private ArrayList<String> getCategoriesFromText(String text) {
        text = text.toLowerCase();

        String[] list = text.split(",");
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].trim();
        }
        //Converts the array to a set and removes duplicate elements and converts back to a list
        ArrayList<String> categories = new ArrayList(new HashSet(Arrays.asList(list)));
        categories.remove("");

        return categories;
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(DATE_DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
        DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int year, int month, int day) {

            calendar.set(year, month, day);
            showDate(year, month + 1, day);

        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year));
    }
}
