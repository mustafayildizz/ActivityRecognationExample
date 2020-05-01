package com.mgkhnyldz.activityrecognationexample;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
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

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        for (DetectedActivity activity : detectedActivities) {
         //   Log.e(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
            broadcastActivity(activity);
        }


    }

    private void broadcastActivity(DetectedActivity activity) {

        Intent intent = new Intent(MainActivity.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

//    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
//
//        for( DetectedActivity activity : probableActivities ) {
//            switch( activity.getType() ) {
//                case DetectedActivity.IN_VEHICLE: {
//                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
//                    Toast.makeText(getApplicationContext(), "in vehicle", Toast.LENGTH_LONG).show();
//                    break;
//                }
//                case DetectedActivity.ON_BICYCLE: {
//                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
//                    Toast.makeText(getApplicationContext(), "in vehicle", Toast.LENGTH_LONG).show();
//                    break;
//                }
//                case DetectedActivity.ON_FOOT: {
//                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
//                    Toast.makeText(getApplicationContext(), "in vehicle", Toast.LENGTH_LONG).show();
//                    break;
//                }
//                case DetectedActivity.RUNNING: {
//                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
//                    Toast.makeText(getApplicationContext(), "in vehicle", Toast.LENGTH_LONG).show();
//                    break;
//                }
//                case DetectedActivity.STILL: {
//                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
//                    Toast.makeText(getApplicationContext(), "in vehicle", Toast.LENGTH_LONG).show();
//                    break;
//                }
//                case DetectedActivity.TILTING: {
//                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
//                    Toast.makeText(getApplicationContext(), "in vehicle", Toast.LENGTH_LONG).show();
//                    break;
//                }
//                case DetectedActivity.WALKING: {
//                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
//                    Toast.makeText(getApplicationContext(), "in vehicle", Toast.LENGTH_LONG).show();
//                    if( activity.getConfidence() >= 75 ) {
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                        builder.setContentText( "Are you walking?" );
//                        builder.setSmallIcon( R.mipmap.ic_launcher );
//                        builder.setContentTitle( getString( R.string.app_name ) );
//                        NotificationManagerCompat.from(this).notify(0, builder.build());
//                    }
//                    break;
//                }
//                case DetectedActivity.UNKNOWN: {
//                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
//                    Toast.makeText(getApplicationContext(), "in vehicle", Toast.LENGTH_LONG).show();
//                    break;
//                }
//            }
//        }
//
//    }


}
