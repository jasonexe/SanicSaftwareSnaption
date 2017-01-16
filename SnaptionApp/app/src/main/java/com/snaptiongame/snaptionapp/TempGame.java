package com.snaptiongame.snaptionapp;

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.testobjects.TestCaption;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by brittanyberlanga on 1/15/17.
 */

public class TempGame {
    public String gamePhoto;
    public Caption caption;
    public String captionerPhoto;
    public TempGame(String gamePhoto, Caption caption, String captionerPhoto) {
        this.gamePhoto = gamePhoto;
        this.caption = caption;
        this.captionerPhoto = captionerPhoto;
    }

    // Used to get game photos since I'm lazy
    public TempGame(String gamePhoto, String caption, String captionerPhoto) {
        this.gamePhoto = gamePhoto;
        this.captionerPhoto = captionerPhoto;
    }

    public static List<TempGame> getMockData() {
        List<String> gamePhotos = new ArrayList<String>();


        List<TempGame> items = new ArrayList<>();
        List<TempGame> newerTempGame = new ArrayList<>(); // Uses Captions instead of Strings
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

        for (TempGame oldFormat: items) {
            Random rand = new Random();
            TempGame newGame;
            if(rand.nextInt(2) == 1) {
                newGame = new TempGame(oldFormat.gamePhoto, TestCaption.getSingleInputCaption(), oldFormat.captionerPhoto);
            } else {
                newGame = new TempGame(oldFormat.gamePhoto, TestCaption.getDoubleInputCaption(), oldFormat.captionerPhoto);
            }

            newerTempGame.add(newGame);
        }

        return newerTempGame;
    }
}