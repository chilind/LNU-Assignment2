package ch223av.dv606.assignment2.AlarmClock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

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

				Intent returnIntent = new Intent();
				returnIntent.putExtra("hour", hour);
				returnIntent.putExtra("minute", minute);
				setResult(Activity.RESULT_OK, returnIntent);
				finish();
			}
		});

	}
}
