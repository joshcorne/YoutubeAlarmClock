package uk.co.joshcorne.onlinealarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by Josh Corne on 25/4/17.
 * This is the launcher activity and also will receive the video.
 */

public class MainActivity extends AppCompatActivity
{
    AlarmManager alarmManager;
    Intent intent;
    PendingIntent pendingIntent;

    protected final static String VIDEO_TAG = "video";
    protected final static String HOUR_TAG = "hour";
    protected final static String MINUTE_TAG = "minute";
    protected final static String STATUS_TAG = "status";
    private final static String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.title));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = sp.edit();

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        Switch statusSwitch = (Switch) findViewById(R.id.statusSwitch);
        TextView videoTitle = (TextView) findViewById(R.id.textView4);

        String video = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        //Get alarm deets
        int hour = PreferenceManager.getDefaultSharedPreferences(this).getInt(HOUR_TAG, 0);
        int minute = PreferenceManager.getDefaultSharedPreferences(this).getInt(MINUTE_TAG, 0);
        boolean status = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(STATUS_TAG, false);
        String videoDetails = PreferenceManager.getDefaultSharedPreferences(this).getString(VIDEO_TAG, null);

        statusSwitch.setChecked(status);
        //Set up GUI
        if(videoDetails != null)
        {
            videoTitle.setText(videoDetails);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }
        else
        {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }

        //Create
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(video != null)
        {
            intent = new Intent(MainActivity.this, AlarmReceiver.class);
            intent.putExtra(VIDEO_TAG, video);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
            edit.putString(VIDEO_TAG, video);
            statusSwitch.setChecked(true);
            videoTitle.setText(video);
        }

        //Every change is saved
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
        {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
            {
                edit.putInt(HOUR_TAG, hourOfDay);
                edit.putInt(MINUTE_TAG, minute);

                edit.apply();
            }
        });

        //Status click handler
        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    setAlarm();
                }
                else
                {
                    disableAlarm();
                }
            }
        });

        edit.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAlarm()
    {
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

        //Get deets
        Calendar calendar = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
        }
        else
        {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        }

        //Set
        if(pendingIntent != null)
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else
        {
            //#Fail
            Toast.makeText(this, R.string.no_video_set_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void disableAlarm()
    {
        alarmManager.cancel(pendingIntent);
    }

    public void openYoutube(View view)
    {
        String video = PreferenceManager.getDefaultSharedPreferences(this).getString(VIDEO_TAG, null);
        if(video == null)
        {
            //Open youtube homepage if nothing set
            startActivity(getPackageManager().getLaunchIntentForPackage(YOUTUBE_PACKAGE_NAME));
        }
        else
        {
            //Open video if set
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video)));
        }
    }
}
