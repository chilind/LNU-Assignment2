package ch223av.dv606.assignment2.AlarmClock;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ch223av.dv606.assignment2.R;

public class Alarm extends AppCompatActivity {
	private Button mStopButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "wakelock");
		wakeLock.acquire();

		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
		ringtone.play();

		mStopButton = (Button) findViewById(R.id.stop_alarm_button);
		mStopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("AlarmReceiver", "onReceive");
				ringtone.stop();
				wakeLock.release();
				finish();
			}
		});

		Toast.makeText(getApplicationContext(), "BEEP BEEP", Toast.LENGTH_LONG).show();
	}
}
