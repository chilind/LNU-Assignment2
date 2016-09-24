package ch223av.dv606.assignment2.MP3Player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ch223av.dv606.assignment2.R;

public class PlayService extends Service {

    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    NotificationCompat.Builder mBuilder;
    public static final String TAG = "PlayService";
    private boolean isRunning = false;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        isRunning = true;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        final String action = intent == null ? null : intent.getAction();
        Log.i(TAG,"service action:" + action);


        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setTicker("ticker")
                .setAutoCancel(true);


        Intent notificationIntent = new Intent(this, MP3Player.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder.setContentIntent(pi);
        Notification notification = mBuilder.build();

        //mBuilder.setAutoCancel(true);
        startForeground(1, notification);

        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }
}

