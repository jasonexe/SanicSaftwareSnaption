package com.snaptiongame.snaptionapp.ui.wall;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;

/**
 * The fragment where users can upload photos to start a game.
 *
 * @author Cameron Geehr
 */

public class UploadPhotoFragment extends Fragment {

    public static final String IMAGE_FOLDER = "images/";
    public static final String GAMES_PATH_REF = "games";

    // Create a storage reference from our app
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference dataReference;
    private Map<String, Caption> captions;

    @BindView(R.id.buttonSelect)
    protected Button buttonSelect;

    @BindView(R.id.buttonUpload)
    protected Button buttonUpload;

    @BindView(R.id.imageview)
    protected ImageView imageView;

    private Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
                final String key = databaseReference.child(GAMES_PATH_REF).push().getKey();
                //Create a new game with a reference to the photo path
                final Game game = new Game("1", "1", IMAGE_FOLDER + key,new ArrayList<String>(),
                        new ArrayList<String>(), true, 1000, 1000, "G");
                // Create a child reference
                // imagesRef now points to "images"
                StorageReference imagesRef = storageRef.child(IMAGE_FOLDER + key);

                if (imageUri != null) {
                    try {
                        Drawable drawable = imageView.getDrawable();
                        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

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
                        Log.e(UploadPhotoFragment.class.getSimpleName(), "Problem uploading photo");
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_photo, container, true);
    }

    /*@Override
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
*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            imageUri = data.getData();
            InputStream stream = getActivity().getContentResolver().openInputStream(data.getData()); //Not sure about this line
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            imageView.setImageBitmap(bitmap);
            imageView.setBackground(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
