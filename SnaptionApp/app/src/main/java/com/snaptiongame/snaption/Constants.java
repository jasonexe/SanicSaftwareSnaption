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
    // Intent indicator for an image aspect ratio used when creating a game from an existing game
    public static final String ASPECT_RATIO = "aspectRatio";
    // Intent indicator that a game is being accessed
    public static final String GAME = "game";
    // Intent indicator that a picker name is given for notification
    public static final String PICKER = "pickerName";
    // Intent indicator that the next strings are fields
    public static final String FIELDS = "fields";

    // BEGIN FIREBASE VARIABLES //

    //ALL USER DATA PATHS for database redesign
    //Path to user profile photos in Firebase Storage
    public static final String USER_PROFILE_PHOTO_PATH = "ProfilePictures/%s";
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
    public static final String USER_PRIVATE_DATA_PATH = "users/private/data/%s";
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
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";


    // Path to root of cards in Firebase
    public static final String CARDS_DIRECTORY = "cards_%s/%s";
    // Name of the images folder in storage
    public static final String STORAGE_IMAGE_PATH = "images/%s";
    // The default card pack that everyone has. Used when retrieving cards
    public final static String DEFAULT_PACK = "InitialPack";
    // Path to the game's creation date
    public static final String CREATION_DATE = "creationDate";

    // END FIREBASE VARIABLES //

    //ALL GAME DATA PATHS for database redesign
    //Path to metadata with configurable access
    public static final String GAMES_METADATA_PATH = "games/%s/metadata";
    //Path to specific game's metadata with configurable access
    public static final String GAME_METADATA_PATH = "games/%s/metadata/%s";
    //Path to topCaption of a game with configurable access
    public static final String GAME_TOPCAPTION_PATH = "games/%s/metadata/%s/topCaption";
    //Path to data with configurable access
    public static final String GAMES_DATA_PATH = "games/%s/data";
    //Path to specific game's metadata with configurable access
    public static final String GAME_DATA_PATH = "games/%s/data/%s";
    //Path to the game's players with configurable access
    public static final String GAME_DATA_PLAYERS_PATH = "games/%s/data/%s/players";
    //Path to specific game's captions with configurable access
    public static final String GAME_DATA_CAPTIONS_PATH = "games/%s/data/%s/captions";
    //Path to game's specific caption with configurable access
    public static final String GAME_DATA_CAPTION_PATH = "games/%s/data/%s/captions/%s";
    //Path to game's player list with configurable access
    public static final String GAME_DATA_PLAYER_PATH = "games/%s/data/%s/players/%s";
    //PUBLIC
    //Path to public root game metadata directory in Firebase
    public static final String GAMES_PUBLIC_METADATA_PATH = "games/public/metadata";
    //Path to specific public game metadata directory in Firebase
    public static final String GAME_PUBLIC_METADATA_PATH = "games/public/metadata/%s";
    //Path to specific public game map of upvotes
    public static final String GAME_PUBLIC_METADATA_UPVOTES_PATH = "games/public/metadata/%s/upvotes";
    //Path to specific public game's specific upvote
    public static final String GAME_PUBLIC_METADATA_UPVOTE_PATH = "games/public/metadata/%s/upvotes/%s";
    //Path to specific public game map of tags
    public static final String GAME_PUBLIC_METADATA_TAGS_PATH = "games/public/metadata/%s/tags";
    //Path to public root game data directory
    public static final String GAMES_PUBLIC_DATA_PATH = "games/public/data";
    //Path to games's public data like the captions and people in the game
    public static final String GAME_PUBLIC_DATA_PATH = "games/public/data/%s";
    //Path to the public game's captions
    public static final String GAME_PUBLIC_DATA_CAPTIONS_PATH = "games/public/data/%s/captions";
    //Path to the public game's specific caption
    public static final String GAME_PUBLIC_DATA_CAPTION_PATH = "games/public/data/%s/captions/%s";
    //Path to the public game's specific caption's card
    public static final String GAME_PUBLIC_DATA_CAPTION_CARD_PATH = "games/public/data/%s/captions/%s/card";
    //Path to the public game's specific caption's map of upvotes
    public static final String GAME_PUBLIC_DATA_CAPTION_UPVOTES_PATH = "games/public/data/%s/captions/%s/upvotes";
    //Path to the public game's specific caption's upvote
    public static final String GAME_PUBLIC_DATA_CAPTION_UPVOTE_PATH = "games/public/data/%s/captions/%s/upvotes/%s";
    //Path to the public game's players
    public static final String GAME_PUBLIC_DATA_PLAYERS_PATH = "games/public/data/%s/players";
    //Path to the public game's specific player
    public static final String GAME_PUBLIC_DATA_PLAYER_PATH = "games/public/data/%s/players/%s";
    //PRIVATE
    //Path to private root game metadata directory in Firebase
    public static final String GAMES_PRIVATE_METADATA_PATH = "games/private/metadata";
    //Path to specific private game metadata directory in Firebase
    public static final String GAME_PRIVATE_METADATA_PATH = "games/private/metadata/%s";
    //Path to specific private game map of upvotes
    public static final String GAME_PRIVATE_METADATA_UPVOTES_PATH = "games/private/metadata/%s/upvotes";
    //Path to specific private game's specific upvote
    public static final String GAME_PRIVATE_METADATA_UPVOTE_PATH = "games/private/metadata/%s/upvotes/%s";
    //Path to specific private game map of tags
    public static final String GAME_PRIVATE_METADATA_TAGS_PATH = "games/private/metadata/%s/tags";
    //Path to private root game data directory
    public static final String GAMES_PRIVATE_DATA_PATH = "games/private/data";
    //Path to games's private data like the captions and people in the game
    public static final String GAME_PRIVATE_DATA_PATH = "games/private/data/%s";
    //Path to the private game's captions
    public static final String GAME_PRIVATE_DATA_CAPTIONS_PATH = "games/private/data/%s/captions";
    //Path to the private game's specific caption
    public static final String GAME_PRIVATE_DATA_CAPTION_PATH = "games/private/data/%s/captions/%s";
    //Path to the private game's specific caption's card
    public static final String GAME_PRIVATE_DATA_CAPTION_CARD_PATH = "games/private/data/%s/captions/%s/card";
    //Path to the private game's specific caption's map of upvotes
    public static final String GAME_PRIVATE_DATA_CAPTION_UPVOTES_PATH = "games/private/data/%s/captions/%s/upvotes";
    //Path to the private game's specific caption's upvote
    public static final String GAME_PRIVATE_DATA_CAPTION_UPVOTE_PATH = "games/private/data/%s/captions/%s/upvotes/%s";
    //Path to the private game's players
    public static final String GAME_PRIVATE_DATA_PLAYERS_PATH = "games/private/data/%s/players";
    //Path to the private game's specific player
    public static final String GAME_PRIVATE_DATA_PLAYER_PATH = "games/private/data/%s/players/%s";

    //ALL TAG DATA PATHS for database redesign
    //Path to the base tags directory
    public static final String TAGS_PATH = "tags";
    //Path to a specific tag name's map of gameIds
    public static final String TAG_GAMES_PATH = "tags/%s";
    //Path to a tag name's specific gameId
    public static final String TAG_GAME_PATH = "tags/%s/%s";

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
    public static final int MIN_IMAGE_UPLOAD_HEIGHT = 20;
    public static final int MIN_IMAGE_UPLOAD_WIDTH = 20;

    // Shared preferences keys
    public static final String SHOW_CREATE_EXISTING_GAME_PREF = "show_create_existing_game_pref";
}
