package ch223av.dv606.assignment2.MP3Player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ch223av.dv606.assignment2.R;

public class Mp3Player extends AppCompatActivity{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_main);
	}
}
