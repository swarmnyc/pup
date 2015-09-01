package com.swarmnyc.pup.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.module.models.Lobby;
import com.swarmnyc.pup.module.service.ServiceCallback;
import com.swarmnyc.pup.ui.activities.LobbyActivity;
import com.swarmnyc.pup.ui.activities.MainActivity;

public class GcmIntentService extends IntentService {
    private static final String TAG = GcmIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super(Consts.GCM_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String type = extras.getString("type");
        String message = extras.getString("message");
        Log.d(TAG, "type=" + type + ", Message=" + message);
        switch (type) {
            case "LobbyStart":
                processLobbyStartNotification(extras.getString("lobbyId"), message);
                break;
        }


        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void processLobbyStartNotification(String lobbyId, final String message) {
        PuPApplication.getInstance().getModule().provideLobbyService().getLobby(lobbyId, new ServiceCallback<Lobby>() {
            @Override
            public void success(Lobby lobby) {
                if ( lobby.isAliveUser(User.current.getId())){
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent resultIntent = new Intent(GcmIntentService.this, LobbyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString( Consts.KEY_LOBBY_ID, lobby.getId() );
                    bundle.putString( Consts.KEY_LOBBY_NAME, lobby.getName() );
                    bundle.putString( Consts.KEY_LOBBY_IMAGE, lobby.getPictureUrl() );
                    resultIntent.putExtras(bundle);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(GcmIntentService.this);
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GcmIntentService.this)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.pup_lg)
                            .setContentTitle(getResources().getString(R.string.title_notification))
                            .setContentText(message)
                            .setContentIntent(contentIntent);

                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
            }
        });


    }
}