package com.mgkhnyldz.activityrecognationexample;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetect extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ActivityDetect(String name) {
        super(name);
    }

    public ActivityDetect() {
        super("myservice");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                broadcastActivity(event);
            }


        }
    }

    private void broadcastActivity(ActivityTransitionEvent activity) {

        Intent intent = new Intent(MainActivity.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getActivityType());
        intent.putExtra("transition", activity.getTransitionType());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


}



