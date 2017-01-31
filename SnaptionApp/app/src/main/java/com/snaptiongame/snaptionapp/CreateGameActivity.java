package com.snaptiongame.snaptionapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.Uploader;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author Cameron Geehr
 */

public class CreateGameActivity extends AppCompatActivity {

    private static final int DATE_DIALOG_ID = 999;
    private static final String MATURE = "mature";
    private static final String PG = "PG";

    // Create a storage reference from our app
    private Uploader uploader;

    private Uri imageUri;
    private ArrayList<String> categories;
    private String maturityRating;
    private boolean isPublic;
    private long endDate;
    private Calendar calendar;
    private int year, month, day;

    private boolean alreadyExisting; //True if user is creating this from an exisitng game
    private String existingPhotoPath;

    @BindView(R.id.add_photo_layout)
    protected RelativeLayout addPhotoLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);
        alreadyExisting = false;

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(WallViewAdapter.EXTRA_MESSAGE);
        if(uri != null) {
            alreadyExisting = true;
            existingPhotoPath = intent.getStringExtra(WallViewAdapter.PHOTO_PATH);
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

        showDate();

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data = null;

                if (imageUri == null) {
                    Toast.makeText(CreateGameActivity.this, "You must pick an image.",
                            Toast.LENGTH_LONG).show();
                }
                else if (!radioPrivate.isChecked() && !radioPublic.isChecked()) {
                    Toast.makeText(CreateGameActivity.this,
                            "You must choose whether the game is public or private.",
                            Toast.LENGTH_LONG).show();
                }
                else if (!radioAdult.isChecked() && !radioEveryone.isChecked()) {
                    Toast.makeText(CreateGameActivity.this,
                            "You must choose who the game is appropriate for.",
                            Toast.LENGTH_LONG).show();
                }
                else if (calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
                    Toast.makeText(CreateGameActivity.this, "You must select a day in the future.",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    categories = getCategoriesFromText(categoryInput.getText().toString());
                    endDate = calendar.getTimeInMillis();
                    //Generate unique key for Games

                    String gameId = uploader.getNewGameKey();
                    if(!alreadyExisting) {
                        data = getImageFromUri(imageUri);
                        Game game = new Game(gameId, FirebaseResourceManager.getUserId(), gameId + ".jpg",
                                new ArrayList<String>(), categories, isPublic, endDate, maturityRating);
                        uploader.addGame(game, data, new UploaderDialog());
                    } else {
                        // If the photo does exist, addGame but without the data
                        Game game = new Game(gameId, FirebaseResourceManager.getUserId(), existingPhotoPath,
                                new ArrayList<String>(), categories, isPublic, endDate, maturityRating);
                        uploader.addGame(game);
                        backToMain();
                    }
                }
            }
        });

        radioPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPublic = false;
            }
        });

        radioPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPublic = true;
            }
        });

        radioEveryone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maturityRating = PG;
            }
        });

        radioAdult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maturityRating = MATURE;
            }
        });
    }

    @OnClick(R.id.add_photo_layout)
    public void onClickAddPhoto() {
        //Gets the content from the imageview
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose Image from..."), 8);
    }

    @OnClick(R.id.text_date)
    public void onClickEndDate() {
        setDate();
    }

    class UploaderDialog implements  FirebaseUploader.UploadDialogInterface {
        int progressDivisor = 1000; // This converts from bytes to whatever units you want.
        // IE 1000 = display with kilobytes

        ProgressDialog loadingDialog = new ProgressDialog(CreateGameActivity.this);
        @Override
        public void onStartUpload(long maxBytes) {
            loadingDialog.setIndeterminate(false);
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            loadingDialog.setProgress(0);
            loadingDialog.setProgressNumberFormat("%1dKB/%2dKB");
            loadingDialog.setMessage("Uploading photo");
            loadingDialog.setMax((int) maxBytes/progressDivisor);
            //Display progress dialog
            loadingDialog.show();
        }

        @Override
        public void onUploadProgress(long bytes) {
            loadingDialog.setProgress((int) bytes/progressDivisor);
        }

        @Override
        public void onUploadDone() {
            loadingDialog.dismiss();
            backToMain();
        }
    }

    private void backToMain() {
        onBackPressed();
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
        alreadyExisting = false;

        try {
            imageUri = data.getData();
            setImageFromUrl(imageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setImageFromUrl(Uri uri) {
        Glide.with(CreateGameActivity.this).load(uri).into(imageView);
        addPhotoLayout.setBackground(null);
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

    private DatePickerDialog.OnDateSetListener myDateListener = new
        DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            year = arg1;
            month = arg2;
            day = arg3;

            calendar.set(year, month, day);
            showDate();
        }
    };

    public void setDate() {
        new DatePickerDialog(this, myDateListener, year, month, day).show();
    }

    private void showDate() {
        dateView.setText(new SimpleDateFormat("MM/dd/yy").format(calendar.getTime()));
    }
}
