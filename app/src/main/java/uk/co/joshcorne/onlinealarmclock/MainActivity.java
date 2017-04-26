package uk.co.joshcorne.onlinealarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
    final static String VIDEO_TAG = "video";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Youtube Alarm Clock");

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        Switch statusSwitch = (Switch) findViewById(R.id.switch1);
        TextView videoTitle = (TextView) findViewById(R.id.textView4);
        String video = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        //Create
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(video != null)
        {
            intent = new Intent(MainActivity.this, AlarmReceiver.class);
            intent.putExtra(VIDEO_TAG, video);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        }

        //Get alarm deets
        int hour = PreferenceManager.getDefaultSharedPreferences(this).getInt("hour", 0);
        int minute = PreferenceManager.getDefaultSharedPreferences(this).getInt("minute", 0);
        boolean status = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("status", false);
        String videoDetails = PreferenceManager.getDefaultSharedPreferences(this).getString("video", video);

        //Set up GUI
        statusSwitch.setChecked(status);
        videoTitle.setText(videoDetails);
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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();

        Calendar calendar = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            edit.putInt("hour", timePicker.getHour());
            edit.putInt("minute", timePicker.getMinute());
        }
        else
        {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            edit.putInt("hour", timePicker.getCurrentHour());
            edit.putInt("minute", timePicker.getCurrentMinute());
        }

        if(pendingIntent != null)
        {
            edit.apply();
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else
        {
            Toast.makeText(this, "You haven't set a video.", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableAlarm()
    {
        alarmManager.cancel(pendingIntent);
    }
}
