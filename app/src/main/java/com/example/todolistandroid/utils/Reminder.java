package com.example.todolistandroid.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.todolistandroid.AddTaskActivity.NOTIFICATION_CHANNEL_ID;

public class Reminder extends BroadcastReceiver {
    public static String TAG = "Reminder";
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION_CANCEL = "notification-cancel";
    public static String NOTIFICATION = "notification";
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE ) ;
        Notification notification = intent.getParcelableExtra( NOTIFICATION ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel) ;
        }
        int id = intent.getIntExtra(NOTIFICATION_ID , 0) ;
        assert notificationManager != null;
        boolean cancel = intent.getBooleanExtra(NOTIFICATION_CANCEL, false);
        if(cancel){
            notificationManager.cancel(TAG, id);
            Log.d(TAG, "Id: "+id);
        }else{
            notificationManager.notify(id , notification);
            Log.d(TAG, "Id: "+id);
        }
    }
}
