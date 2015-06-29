package com.swarmnyc.pup;

public class Consts
{
	// In GCM, the Sender ID is a project ID that you acquire from the API console
	public static final String PROJECT_NUMBER = "603805252086";

	public static final String EXTRA_MESSAGE = "message";

	public static final String GCM_NOTIFICATION    = "GCM Notification";
	public static final String GCM_DELETED_MESSAGE = "Deleted messages on server: ";
	public static final String GCM_INTENT_SERVICE  = "GcmIntentService";
	public static final String GCM_SEND_ERROR      = "Send error: ";
	public static final String GCM_RECEIVED        = "Received: ";

	public static final String KEY_LOBBIES    = "lobbies";
	public static final String KEY_MY_LOBBIES = "my_lobbies";
	public static final String KEY_FEEDBACK   = "feedback";
	public static final String KEY_SETTINGS   = "settings";
	public static final String KEY_LOBBY_ID   = "LobbyId";
	public static final String KEY_LOBBY_NAME = "LobbyName";
	public static final String KEY_LOBBY_IMAGE = "LobbyImage";
	public static final String KEY_FACEBOOK   = "Facebook";
	public static final String KEY_TWITTER    = "Twitter";
	public static final String KEY_REDDIT     = "Reddit";
	public static final String KEY_TUMBLR     = "Tumblr";

	public final static int PAGE_SIZE  = 20;

	public static int TOUCH_SLOP;
	public static int windowWidth;
	public static int windowHeight;

}