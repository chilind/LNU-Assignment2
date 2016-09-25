package ch223av.dv606.assignment2.AlarmClock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import java.util.Calendar;

public class AlarmReceiver extends AppCompatActivity {
	AlarmManager am;
	PendingIntent sender;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("AlarmReceiver", "onReceive");
		AlarmClock.play(this);

	}
	/*
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AlarmReceiver", "onReceive");
		AlarmClock.play(context);
	}*/


	public void setAlarm(String hour, String minute) {
		Log.i("hour",hour);
		Log.i("minute",minute);
		// get a Calendar object with current time
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		cal.set(Calendar.MINUTE, Integer.parseInt(minute));

		Intent intent = new Intent(AlarmClock.getAppContext(), AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(AlarmClock.getAppContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) AlarmClock.getAppContext().getSystemService(AlarmClock.getAppContext().ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		Log.i("AlarmReceiver-setAlarm","Alarm set!");
	}

	public void cancelAlarm(){
		if(am != null && sender != null){
			am.cancel(sender);
		}
	}
}

