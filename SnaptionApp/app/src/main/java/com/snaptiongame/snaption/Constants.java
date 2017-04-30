package com.snaptiongame.snaption;

/**
 * A file containing constants that are going to be used in multiple classes.
 *
 * 1. Constants should never be or need to be concatenated. If you want to use a path just use
 *    "/%s/", where the %s is the id within that directory, and use String.format to fill in the ids
 * 2. If a constant is a path to a single instance, it should be "<DIRECTORY>_PATH"
 * 3. If a constant is to a list or map, it should be named "<DIRECTORY>S_PATH"
 * 4. If a constant is to a path within a directory, it should be <DIRECTORY>_<DIRECTORY>_PATH,
 *    nesting as deep as necessary, and depending on rules 2 and 3 to add an S if necessary
 * 5. If a constant falls outside of this category, ask the group or just do whatever makes sense
 *    to you, especially if it's something that will only be used in the class you're working on
 *
 * @author Jason Krein
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

    //ALL USER DATA PATHS for database redesign
    //Path to root user metadata directory in Firebase
    public static final String USERS_METADATA_PATH = "users/public/metadata";
    //Path to specific user metadata directory in Firebase
    public static final String USER_METADATA_PATH = "users/public/metadata/%s";
    //Path to user's public data like friends, public game captions, and created public games
    public static final String USER_PUBLIC_DATA_PATH = "users/public/data/%s";
    //Path to user's friend's list
    public static final String USER_FRIENDS_PATH = USER_PUBLIC_DATA_PATH + "/friends";
    //Path to user's public created games
    public static final String USER_PUBLIC_CREATED_GAMES_PATH = USER_PUBLIC_DATA_PATH + "/createdGames";
    //Path to user's public captions
    public static final String USER_PUBLIC_CAPTIONS_PATH = USER_PUBLIC_DATA_PATH + "/captions";
    //Path to user's data that is private
    public static final String USER_PRIVATE_DATA_PATH = "users/private/%s";
    //Path to user's joined games public and private
    public static final String USER_PRIVATE_JOINED_GAMES_PATH = USER_PRIVATE_DATA_PATH + "/joinedGames";
    //Path to user's joined games public and private
    public static final String USER_PRIVATE_JOINED_GAME_PATH = USER_PRIVATE_DATA_PATH + "/joinedGames/%s";
    //Path to user's private created games
    public static final String USER_PRIVATE_CREATED_GAMES_PATH = USER_PRIVATE_DATA_PATH + "/createdGames";
    //Path to user's private captions
    public static final String USER_PRIVATE_CAPTIONS_PATH = USER_PRIVATE_DATA_PATH + "/captions";

    //Key name of lowercase display name
    public static final String SEARCH_NAME = "searchName";
    //Key name for facebookId
    public static final String USER_FACEBOOK_ID = "facebookId";
    // Key name for the user's email
    public static final String EMAIL = "email";
    // Path to the user's notification ID
    public static final String USER_NOTIFICATION_PATH = USER_METADATA_PATH + "/notificationId";
    // Path to the user's isAndroid boolean
    public static final String USER_IS_ANDROID_PATH = USER_METADATA_PATH + "/isAndroid";
    // Path to user's displayName
    public static final String USER_DISPLAY_NAME_PATH = USER_METADATA_PATH + "/displayName";
    // Path to user's searchName
    public static final String USER_SEARCH_NAME_PATH = USER_METADATA_PATH + "/" + SEARCH_NAME;


    // Path to map of all games
    public static final String GAMES_PATH = "games/";
    // Path to root of games in Firebase
    public static final String GAME_PATH = "games/%s";
    // Path to root of cards in Firebase
    public static final String CARDS_DIRECTORY = "cards_%s/%s";
    // Path to the list of players in a game in Firebase
    public static final String GAME_PLAYERS_PATH = "games/%s/players";
    // Path to a player in a game
    public static final String GAME_PLAYER_PATH = "games/%s/players/%s";
    // Name of the table in a user that contains their created games
    public static final String USER_CREATED_GAME_PATH =  "users/%s/createdGames/%s";
    // Name of the images folder in storage
    public static final String STORAGE_IMAGE_PATH = "images/%s";
    // Path to a specific caption in a game
    public static final String GAME_CAPTION_PATH = "games/%s/captions/%s";
    // Path to the map of captions in a game
    public static final String GAME_CAPTIONS_PATH = "games/%s/captions/";
    // Complete path to a specific user that voted in a game's caption
    public static final String GAME_CAPTIONS_UPVOTE_PATH = "games/%s/captions/%s/votes/%s";
    // Path to all the upvotes in a game's caption
    public static final String GAME_CAPTIONS_UPVOTES_PATH = "games/%s/captions/%s/votes/";
    // Complete path to a specific user that voted a game
    public static final String GAME_UPVOTE_PATH = "games/%s/votes/%s";
    // Path to all upvotes that the game has
    public static final String GAME_UPVOTES_PATH = "games/%s/votes/";
    // The default card pack that everyone has. Used when retrieving cards
    public final static String DEFAULT_PACK = "InitialPack";
    // Path to the game's creation date
    public static final String CREATION_DATE = "creationDate";

    // END FIREBASE VARIABLES //

    // Notification ID used in intents that have notifications
    public static final int DEEP_LINK_GOOGLE_ID = 1;
    public static final int NUM_CARDS_IN_HAND = 10;
    public static final int GOOGLE_LOGIN_RC = 13; //request code used for Google Login Intent
    public static final String ASPECT_RATIO_KEY = "AspectRatio";
    public static final int MILLIS_PER_SECOND = 1000;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;
    public static final int COMPRESSION_VALUE = 80;
    public static final int MAX_IMAGE_UPLOAD_WIDTH = 1920;
    public static final int MAX_IMAGE_UPLOAD_HEIGHT = 1080;
    // constants for building the String for time remaining
    public static final String HOURS = " hour";
    public static final String MINUTES = " minute";
    public static final String DAYS = " day";
    public static final String REMAINING = " remaining";
    public static final String SINGULAR = "1 ";
}
