package com.snaptiongame.snaptionapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.ui.wall.CreateGameFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author Cameron Geehr
 */

public class CreateGameActivity extends AppCompatActivity {

    public static final String IMAGE_FOLDER = "images/";
    public static final String GAMES_PATH_REF = "games";

    // Create a storage reference from our app
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private Uri imageUri;
    private boolean isPublic;
    private ArrayList<String> categories;
    private long endDate;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
                //Generate unique key for Games
                final String gameId = databaseReference.child(GAMES_PATH_REF).push().getKey();
                //Create a new game with a reference to the photo path
                final Game game = new Game(gameId, "1", IMAGE_FOLDER + gameId, new ArrayList<String>(),
                        categories, isPublic, 1000, 1000, "G");
                // Create a child reference
                // imagesRef now points to "images"
                StorageReference imagesRef = storageRef.child(IMAGE_FOLDER + key);

                if (imageUri != null) {
                    try {
                        byte[] data = getImageFromUri(imageUri);

                        UploadTask uploadTask = imagesRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                //Upload the child to the database
                                databaseReference.child(GAMES_PATH_REF).child(key).setValue(game);
                            }
                        });
                    } catch (Exception ex) {
                        Log.e(CreateGameFragment.class.getSimpleName(), "Problem uploading photo");
                    }
                }
            }
        });

        radioPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        radioPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        try {
            imageUri = data.getData();
            InputStream stream = getContentResolver().openInputStream(data.getData());
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            imageView.setImageBitmap(bitmap);
            imageView.setBackground(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
