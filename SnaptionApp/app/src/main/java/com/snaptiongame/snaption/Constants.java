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
    // Path to root users map in Firebase
    public static final String USERS_PATH = "users";
    // Path to a specific user in Firebase
    public static final String USER_PATH = "users/%s";
    // Path to map of all games
    public static final String GAMES_PATH = "games/";
    // Path to root of games in Firebase
    public static final String GAME_PATH = "games/%s";
    // Path to user's friends list in Firebase
    public static final String FRIENDS_PATH = "users/%s/friends";
    // Path to a user's private games list in Firebase
    public static final String USER_PRIVATE_GAMES = "users/%s/privateGames";
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
    // Path to a specific caption in a user
    public static final String USER_CAPTION_PATH = "users/%s/captions/%s";
    // Path to a specific caption in a game
    public static final String GAME_CAPTION_PATH = "games/%s/captions/%s";
    // Path to the map of captions in a game
    public static final String GAME_CAPTIONS_PATH = "games/%s/captions/";
    // Complete path to a specific user that voted
    public static final String USER_CAPTIONS_UPVOTE_PATH = "users/%s/captions/%s/votes/%s";
    // Complete path to a specific user that voted in a game's caption
    public static final String GAME_CAPTIONS_UPVOTE_PATH = "games/%s/captions/%s/votes/%s";
    // Path to all the upvotes in a game's caption
    public static final String GAME_CAPTIONS_UPVOTES_PATH = "games/%s/captions/%s/votes/";
    // Complete path to a specific user that voted a game
    public static final String GAME_UPVOTE_PATH = "games/%s/votes/%s";
    // Path to all upvotes that the game has
    public static final String GAME_UPVOTES_PATH = "games/%s/votes/";
    // Complete path to a specific private game a user has created
    public static final String USER_PRIVATE_GAMES_PATH = "users/%s/privateGames/%s";
    // Path to the user's notification ID
    public static final String USER_NOTIFICATION_PATH = "users/%s/notificationId";
    // Path to the user's isAndroid boolean
    public static final String USER_IS_ANDROID_PATH = "users/%s/isAndroid";
    // The default card pack that everyone has. Used when retrieving cards
    public final static String DEFAULT_PACK = "InitialPack";
    // Path to the user's lowercase display name
    public static final String USER_NAME = "lowercaseDisplayName";
    // Path to the game's creation date
    public static final String CREATION_DATE = "creationDate";
    // Path to the user's email
    public static final String EMAIL = "email";
    // END FIREBASE VARIABLES //

    // Notification ID used in intents that have notifications
    public static final int DEEP_LINK_GOOGLE_ID = 1;
    public static final int NUM_CARDS_IN_HAND = 10;
    public static final int GOOGLE_LOGIN_RC = 13; //request code used for Google Login Intent
    public static final String ASPECT_RATIO_KEY = "AspectRatio";
    public static final int MILLIS_PER_SECOND = 1000;
    public static final int COMPRESSION_VALUE = 80;
    public static final int MAX_IMAGE_UPLOAD_WIDTH = 1280;
    public static final int MAX_IMAGE_UPLOAD_HEIGHT = 1280;

}
