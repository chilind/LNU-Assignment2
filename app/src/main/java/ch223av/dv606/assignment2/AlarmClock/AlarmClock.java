package ch223av.dv606.assignment2.AlarmClock;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ch223av.dv606.assignment2.R;

public class AlarmClock extends AppCompatActivity{

    private static final int SET_ALARM_REQUEST = 1;
    private static final String TAG = AlarmClock.class.getSimpleName();
    private static final int UPDATE_FREQUENCY = 5000;

    private String alarmText;

    TextView mTimeTextView;
    TextView mAlarmTextView;
    Button mDeleteAlarmButton;

    Timer myTimer;
    private static Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_clock_main);
        mContext = getApplicationContext();

        mDeleteAlarmButton = (Button) findViewById(R.id.deleteAlarmButton);
        mAlarmTextView = (TextView) findViewById(R.id.alarmTextView);

        mDeleteAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlarmTextView.setText("No alarm set.");
                Intent intent = new Intent(getApplicationContext(), Alarm.class);
                PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
            }
        });

        RunAsync task = new RunAsync();
        myTimer = new Timer();
        myTimer.schedule(task, 0, UPDATE_FREQUENCY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_actionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_alarm:
                // User pressed the "Add country" button --> show the activity...
                startSetAlarmActivity();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        mAlarmTextView = (TextView) findViewById(R.id.alarmTextView);

        switch (requestCode) {
            case SET_ALARM_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    String hour = data.getStringExtra("hour");
                    String minute = data.getStringExtra("minute");

                    alarmText = "Alarm set to: " + hour + ":" + minute;
                    mAlarmTextView.setText(alarmText);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void startSetAlarmActivity() {
        //Display
        Intent intent = new Intent(this, SetAlarm.class);
        startActivityForResult(intent, SET_ALARM_REQUEST);
    }

    private class RunAsync extends TimerTask {
        @Override
        public void run() {
            new asyncOperations().execute();
        }

        private class asyncOperations extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... params) {
                Calendar c = Calendar.getInstance();
                int hours = c.get(Calendar.HOUR_OF_DAY);
                int minutes = c.get(Calendar.MINUTE);
                int seconds = c.get(Calendar.SECOND);
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }

            @Override
            protected void onPostExecute(String s) {
                mTimeTextView = (TextView) findViewById(R.id.timeTextView);
                mTimeTextView.setText(s);
            }
        }
    }
}
