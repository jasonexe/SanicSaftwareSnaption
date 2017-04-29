package com.snaptiongame.snaption.ui.games;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by brittanyberlanga on 4/24/17.
 */

public class PhotoZoomActivity extends AppCompatActivity {

    public static final String PHOTO_PATH = "photo_path";
    public static final String TRANSITION_NAME = "transition_name";

    @BindView(R.id.zoom_photo_view)
    protected PhotoView zoomPhotoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_zoom);
        ButterKnife.bind(this);
        String transitionName = getIntent().getStringExtra(TRANSITION_NAME);
        ViewCompat.setTransitionName(zoomPhotoView, transitionName);
        String imagePath = getIntent().getStringExtra(PHOTO_PATH);
        if (imagePath != null) {
            FirebaseResourceManager.loadImageIntoView(imagePath, zoomPhotoView);
        }
    }

    @OnClick (R.id.zoom_photo_view)
    public void onClickPhoto() {
        onBackPressed();
    }
}
