package ch223av.dv606.assignment2.AlarmClock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ch223av.dv606.assignment2.R;

public class AlarmClock extends AppCompatActivity{

    private static final String TAG = AlarmClock.class.getSimpleName();
    private static final int UPDATE_FREQUENCY = 5000;

    TextView mTimeTextView;
    Timer myTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_clock_main);

        RunAsync task = new RunAsync();
        myTimer = new Timer();
        myTimer.schedule(task, 0, UPDATE_FREQUENCY);
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
