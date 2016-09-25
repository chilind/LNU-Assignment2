package ch223av.dv606.assignment2.AlarmClock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import ch223av.dv606.assignment2.R;

public class SetAlarm extends AppCompatActivity{

	TimePicker mTimePicker;
	Button mAddAlarmButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_add_alarm_activity);

		mTimePicker = (TimePicker) findViewById(R.id.timePicker);
		mTimePicker.setIs24HourView(true);

		mAddAlarmButton = (Button) findViewById(R.id.addAlarmButton);
		mAddAlarmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String hour = ""+mTimePicker.getCurrentHour();
				if (hour.length() < 2) {
					hour = "0" + hour;
				}
				String minute = ""+mTimePicker.getCurrentMinute();
				if (minute.length() < 2) {
					minute = "0" + minute;
				}

				// get a Calendar object with current time
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
				cal.set(Calendar.MINUTE, Integer.parseInt(minute));

				Intent intent = new Intent(getApplicationContext(), Alarm.class);
				intent.putExtra("hour", hour);
				intent.putExtra("minute", minute);

				PendingIntent sender = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
				Log.i("AlarmReceiver-setAlarm","Alarm set!");

				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}
}
