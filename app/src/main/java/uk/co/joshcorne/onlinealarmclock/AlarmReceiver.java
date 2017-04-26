package uk.co.joshcorne.onlinealarmclock;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Josh Corne on 25/4/17.
 * This is what will be called when the alarm goes off.
 * @author Josh Corne
 */

public class AlarmReceiver extends WakefulBroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Open video
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra(MainActivity.VIDEO_TAG)));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
