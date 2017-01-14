package com.snaptiongame.snaptionapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallFragment extends Fragment {
    private static final int NUM_COLUMNS = 2;
    private Unbinder unbinder;

    @BindView(R.id.wall_list)
    protected RecyclerView wallListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        unbinder = ButterKnife.bind(this, view);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(NUM_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        wallListView.setLayoutManager(manager);
        wallListView.addItemDecoration(new WallGridItemDecorator(getResources().getDimensionPixelSize(R.dimen.wall_grid_item_spacing)));
        List<TempGame> items = new ArrayList<>();
        items.add(new TempGame("http://images.mentalfloss.com/sites/default/files/styles/insert_m" +
                "ain_wide_image/public/istock_000078603065_small.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://baby-animals.net/wp-content/gallery/Baby-pig-wallpapers/Baby-pig-wallpapers-2.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("https://cdn.daysoftheyear.com/wp-content/images/hedgehog-day1-e14" +
                "22787687319-804x382.jpg", "Hello, I am a cation of this photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://yesserver.space.swri.edu/yes2013/personal/emilyklotzbach/panda2.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://amazinganimalstories.com/wp-content/uploads/2013/10/cute-baby-animal-pictures-002-018.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://akns-images.eonline.com/eol_images/Entire_Site/2013229/rs_560x415-130329112005-1024.BabyPig1.mh.032913.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("https://lh6.ggpht.com/VPIhV2_K37sFk6bSQrlz-ndaUiZBXGPqUzAZvJ7VVAZ_nywvf-HP5kOoQ9PT9HzEng=h900", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://images.mentalfloss.com/sites/default/files/styles/insert_m" +
                "ain_wide_image/public/istock_000078603065_small.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://baby-animals.net/wp-content/gallery/Baby-pig-wallpapers/Baby-pig-wallpapers-2.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("https://cdn.daysoftheyear.com/wp-content/images/hedgehog-day1-e14" +
                "22787687319-804x382.jpg", "Hello, I am a cation of this photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://yesserver.space.swri.edu/yes2013/personal/emilyklotzbach/panda2.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://amazinganimalstories.com/wp-content/uploads/2013/10/cute-baby-animal-pictures-002-018.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://akns-images.eonline.com/eol_images/Entire_Site/2013229/rs_560x415-130329112005-1024.BabyPig1.mh.032913.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("https://lh6.ggpht.com/VPIhV2_K37sFk6bSQrlz-ndaUiZBXGPqUzAZvJ7VVAZ_nywvf-HP5kOoQ9PT9HzEng=h900", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://images.mentalfloss.com/sites/default/files/styles/insert_m" +
                "ain_wide_image/public/istock_000078603065_small.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://baby-animals.net/wp-content/gallery/Baby-pig-wallpapers/Baby-pig-wallpapers-2.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("https://cdn.daysoftheyear.com/wp-content/images/hedgehog-day1-e14" +
                "22787687319-804x382.jpg", "Hello, I am a cation of this photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://yesserver.space.swri.edu/yes2013/personal/emilyklotzbach/panda2.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://amazinganimalstories.com/wp-content/uploads/2013/10/cute-baby-animal-pictures-002-018.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://akns-images.eonline.com/eol_images/Entire_Site/2013229/rs_560x415-130329112005-1024.BabyPig1.mh.032913.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("https://lh6.ggpht.com/VPIhV2_K37sFk6bSQrlz-ndaUiZBXGPqUzAZvJ7VVAZ_nywvf-HP5kOoQ9PT9HzEng=h900", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://images.mentalfloss.com/sites/default/files/styles/insert_m" +
                "ain_wide_image/public/istock_000078603065_small.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://baby-animals.net/wp-content/gallery/Baby-pig-wallpapers/Baby-pig-wallpapers-2.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("https://cdn.daysoftheyear.com/wp-content/images/hedgehog-day1-e14" +
                "22787687319-804x382.jpg", "Hello, I am a cation of this photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://yesserver.space.swri.edu/yes2013/personal/emilyklotzbach/panda2.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://amazinganimalstories.com/wp-content/uploads/2013/10/cute-baby-animal-pictures-002-018.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("http://akns-images.eonline.com/eol_images/Entire_Site/2013229/rs_560x415-130329112005-1024.BabyPig1.mh.032913.jpg", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        items.add(new TempGame("https://lh6.ggpht.com/VPIhV2_K37sFk6bSQrlz-ndaUiZBXGPqUzAZvJ7VVAZ_nywvf-HP5kOoQ9PT9HzEng=h900", "Hello, I am a cation of t" +
                "his photo that needs a caption", "Brittany Berlanga"));
        wallListView.setAdapter(new WallViewAdapter(items));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class TempGame {
        public String gamePhoto;
        public String caption;
        public String captionerPhoto;
        public TempGame(String gamePhoto, String caption, String captionerPhoto) {
            this.gamePhoto = gamePhoto;
            this.caption = caption;
            this.captionerPhoto = captionerPhoto;
        }
    }
}
