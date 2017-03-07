package com.snaptiongame.snaptionapp;

/**
 * Created by jason_000 on 3/7/2017.
 */

public class Constants {
    // Message used when creating a game from existing to indicate to the createGame activity
    // That is what is happening
    public static final String EXTRA_MESSAGE = "fromCurrentUri";
    // Another Intent label
    public static final String PHOTO_PATH = "currentPhotoPath";
    // Intent indicator that a game is being accessed
    public static final String GAME = "game";

    // BEGIN FIREBASE VARIABLES //
    // Path to root users in Firebase
    public static final String USER_PATH = "users/";
    // Path to root of games in Firebase
    public static final String GAMES_PATH = "games";
    // Path to user's friends list in Firebase
    public static final String FRIENDS_PATH = "users/%s/friends";
    // Path to a user's private games list in Firebase
    public static final String USER_PRIVATE_GAMES = "users/%s/privateGames";
    // Path to root of cards in Firebase
    public static final String CARDS_DIRECTORY = "cards";
    // Path to the list of players in a game in Firebase
    public static final String GAME_PLAYERS_PATH = "games/%s/players";
    // Name of the table in a user that contains their created games
    public static final String USERS_CREATED_GAMES =  "createdGames";
    // Name of the captions table in both user and game
    public static final String CAPTION_PATH = "captions";
    // Name of the images folder in storage
    public static final String IMAGE_PATH = "images";
    // Complete path to the votes that are contained in the user's Caption object
    public static final String USER_CAPTIONS_UPVOTES_PATH = "users/%s/captions/%s/votes";
    // Complete path to the votes that are contained in the game's Captions
    public static final String GAME_CAPTIONS_UPVOTES_PATH = "games/%s/captions/%s/votes";
    // Complete path to a specific private game a user has created
    public static final String USER_PRIVATE_GAMES_PATH = "users/%s/privateGames/%s";
    // The default card pack that everyone has. Used when retrieving cards
    public final static String DEFAULT_PACK = "InitialPack";
    // END FIREBASE VARIABLES //

    // Notification ID used in intents that have notifications
    public static final String NOTIFICATION_ID = "notificationId";
    public static final int CLIP_TO_OUTLINE_MIN_SDK = 21;
    public static final int DEEP_LINK_GOOGLE_ID = 1;
    public static final int NUM_CARDS_IN_HAND = 10;
    public static final int GOOGLE_LOGIN_RC = 13; //request code used for Google Login Intent
}
