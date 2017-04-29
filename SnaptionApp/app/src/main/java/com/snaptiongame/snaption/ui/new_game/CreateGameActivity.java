package com.snaptiongame.snaption.ui.new_game;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.Person;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.servercalls.FirebaseReporter;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.utilities.BitmapConverter;
import com.snaptiongame.snaption.utilities.ViewUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.snaptiongame.snaption.Constants.MILLIS_PER_SECOND;


/**
 * @author Cameron Geehr
 */

public class CreateGameActivity extends AppCompatActivity {
    private static final int FRIENDS_LIST_MIN_HEIGHT = 0;
    private static final int FRIENDS_LIST_MAX_HEIGHT = 250;
    private static final long FRIENDS_ANIMATION_DURATION = 400;
    private static final int DEFAULT_DAYS_AHEAD = 5;

    // Create a storage reference from our app
    private Uploader uploader;

    private Uri imageUri;
    private Map<String, Integer> categories;
    private boolean isPublic;
    private long endDate;
    private Calendar calendar;
    private int year, month, day;

    private boolean alreadyExisting; //True if user is creating this from an exisitng game
    private String existingPhotoPath;
    private PersonAdapter friendsListAdapter;
    private AddedPersonAdapter gameFriendsAdapter;
    private PersonAdapter.AddListener addListener = new PersonAdapter.AddListener() {
        @Override
        public void onPersonSelected(Person person) {
            // when a person is selected, add them to the gameFriendsAdapter
            gameFriendsAdapter.addItem(person);
        }
    };

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

    @BindView(R.id.category_input)
    protected EditText categoryInput;

    @BindView(R.id.text_date)
    protected TextView dateView;

    @BindView(R.id.add_friends_view)
    protected FrameLayout addFriendsView;

    @BindView(R.id.friends_loading)
    protected ProgressBar friendProgressBar;

    @BindView(R.id.friends_list)
    protected RecyclerView friendsList;

    @BindView(R.id.no_friends)
    protected TextView noFriendsView;

    @BindView(R.id.game_friends)
    protected RecyclerView gameFriendsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);
        alreadyExisting = false;

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(Constants.EXTRA_MESSAGE);
        if(uri != null) {
            alreadyExisting = true;
            existingPhotoPath = intent.getStringExtra(Constants.PHOTO_PATH);
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
        calendar.add(Calendar.DATE, DEFAULT_DAYS_AHEAD);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(calendar);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonUpload.setClickable(false);
                boolean shouldUploadBeClickable = true;
                if (imageUri == null) {
                    Toast.makeText(CreateGameActivity.this, "You must pick an image.",
                            Toast.LENGTH_LONG).show();
                }
                else if (!radioPrivate.isChecked() && !radioPublic.isChecked()) {
                    Toast.makeText(CreateGameActivity.this,
                            "You must choose whether the game is public or private.",
                            Toast.LENGTH_LONG).show();
                }
                else if (calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
                    Toast.makeText(CreateGameActivity.this, "You must select a day in the future.",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    shouldUploadBeClickable = false;
                    if(!alreadyExisting) {
                        try {
                            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(imageUri, "r");
                            new ImageCompressTask().execute(fd);
                        } catch (Exception e) {
                            Toast.makeText(CreateGameActivity.this, "Error, file not found",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        uploadGame(null);
                    }
                }
                buttonUpload.setClickable(shouldUploadBeClickable);
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

        setupFriendsViews();
    }

    // Set data = to null if game already exists
    private void uploadGame(byte[] data) {
        Map<String, Integer> friends = new HashMap<>();
        List<Person> addedFriends =  gameFriendsAdapter.getPersons();
        for (Person friend : addedFriends) {
            // TODO if/when inviting is also supported, handle when a Friend is added
            friends.put(friend.getId(), 1);
        }
        categories = getCategoriesFromText(categoryInput.getText().toString());
        endDate = calendar.getTimeInMillis() / MILLIS_PER_SECOND;
        //Generate unique key for Games

        String gameId = uploader.getNewGameKey();
        // Data should always be null if the game already exists. If the game doesn't exist (and is
        // being pulled from the user's device) then data should be populated.
        if(!alreadyExisting && data != null) {
            try {
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(imageUri, "r");
                UploaderDialog dialog = new UploaderDialog();
                double aspectRatio = BitmapConverter.getFileDescriptorAspectRatio(pfd);
                Game game = new Game(gameId, FirebaseResourceManager.getUserId(), gameId + ".jpg",
                        friends, categories, isPublic, endDate, aspectRatio);
                uploader.addGame(game, data, aspectRatio, dialog);
            } catch (Exception e) {
                Toast.makeText(CreateGameActivity.this, "Error, file not found",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            // If the photo does exist, addGame but without the data
            // TODO figure out a better way to do this... will have to pull the game to get aspect ratio probs
            Game game = new Game(gameId, FirebaseResourceManager.getUserId(), existingPhotoPath,
                                friends, categories, isPublic, endDate, 1);
            uploader.addGame(game);
            backToMain();
        }
    }

    private ProgressDialog showConvertProgress() {
        ProgressDialog convertDialog = new ProgressDialog(CreateGameActivity.this);
        convertDialog.setIndeterminate(true);
        convertDialog.setMessage(getString(R.string.converting));
        convertDialog.setCanceledOnTouchOutside(false);
        //Display progress dialog
        convertDialog.show();
        return convertDialog;
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

    @OnClick(R.id.add_friends)
    public void onClickAddFriends() {
        ViewUtilities.expandCollapseView(addFriendsView, FRIENDS_LIST_MIN_HEIGHT,
                FRIENDS_LIST_MAX_HEIGHT, FRIENDS_ANIMATION_DURATION);
        displayFriends();
    }

    private void setupFriendsViews() {
        friendsList.setNestedScrollingEnabled(true);
        // TODO support both adding and inviting to the game
        friendsListAdapter = new PersonAdapter(new ArrayList<Person>(), addListener);
        friendsList.setAdapter(friendsListAdapter);
        gameFriendsAdapter = new AddedPersonAdapter(new ArrayList<Person>());
        gameFriendsView.setAdapter(gameFriendsAdapter);
    }

    private void displayFriends() {
        if (friendsListAdapter.getItemCount() == 0) {
            loadFriends();
        }
    }

    private void loadFriends() {
        String userPath = FirebaseResourceManager.getUserPath();
        if (userPath != null) {
            FirebaseResourceManager.retrieveSingleNoUpdates(userPath, new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    if (user != null && user.getFriends() != null) {
                        // load each friend
                        FirebaseResourceManager.loadUsers(user.getFriends(), new ResourceListener<User>() {
                            @Override
                            public void onData(User user) {
                                if (user != null) {
                                    if (friendsListAdapter.getItemCount() == 0) {
                                        showFriends();
                                    }
                                    friendsListAdapter.addSingleItem(user);
                                }
                            }

                            @Override
                            public Class getDataType() {
                                return User.class;
                            }
                        });
                    }
                    else {
                        showNoFriends();
                    }
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    private void showFriends() {
        friendProgressBar.setVisibility(View.GONE);
        friendsList.setVisibility(View.VISIBLE);
    }

    private void showNoFriends() {
        friendProgressBar.setVisibility(View.GONE);
        noFriendsView.setVisibility(View.VISIBLE);
    }

    private class UploaderDialog implements  FirebaseUploader.UploadDialogInterface {
        int progressDivisor = 1000; // This converts from bytes to whatever units you want.
        boolean started = false;
        // IE 1000 = display with kilobytes

        ProgressDialog loadingDialog = new ProgressDialog(CreateGameActivity.this);
        @Override
        public void onStartUpload(long maxBytes) {
            if(!started) {
                started = true;
                loadingDialog.setIndeterminate(false);
                loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.setProgress(0);
                loadingDialog.setProgressNumberFormat("%1dKB/%2dKB");
                loadingDialog.setMessage("Uploading photo");
                loadingDialog.setMax((int) maxBytes/progressDivisor);
                //Display progress dialog
                loadingDialog.show();
            }
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
            FirebaseReporter.reportException(e, "Couldn't read user's photo data");
            e.printStackTrace();
        }
    }


    private void setImageFromUrl(Uri uri) {
        Glide.with(CreateGameActivity.this).load(uri).into(imageView);
        addPhotoLayout.setBackground(null);
    }

    /**
     * Takes a string of text, separates each word by comma, and removes any repeated words.
     *
     * @param text - The text to parse with comma delimiters
     * @return A list of strings not containing repeats or empty strings
     */
    private Map<String, Integer> getCategoriesFromText(String text) {
        Map<String, Integer> categories = new HashMap<>();
        text = text.toLowerCase();
        String[] list = text.split(",");

        for (String input : list) {
            String potentialCategory = input.trim();
            if (!TextUtils.isEmpty(potentialCategory)) {
                categories.put(potentialCategory, 1);
            }
        }
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
            showDate(calendar);
        }
    };

    /**
     * Displays the datepicker dialog to allow the user to input the date.
     */
    public void setDate() {
        new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)).show();
    }
    
    /**
     * Displays the date in the textview.
     *
     * @param calendar The calendar to take the date from
     */
    private void showDate(Calendar calendar) {
        //TODO have configurable for spanish dates based on locale
        dateView.setText(new SimpleDateFormat("MM/dd/yy").format(calendar.getTime()));
    }

    private class ImageCompressTask extends AsyncTask<ParcelFileDescriptor, Integer, byte[]> {
        ProgressDialog convertingDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            convertingDialog = showConvertProgress();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            convertingDialog.dismiss();
            uploadGame(bytes);

        }

        @Override
        protected byte[] doInBackground(ParcelFileDescriptor... pfds) {
            return BitmapConverter.decodeSampledBitmapFromStream(pfds[0], Constants.MAX_IMAGE_UPLOAD_WIDTH, Constants.MAX_IMAGE_UPLOAD_HEIGHT);
        }
    }

}
