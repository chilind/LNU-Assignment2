package ch223av.dv606.assignment2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ch223av.dv606.assignment2.MyCountriesCalendar.MyCountriesCalendar;

public class MainActivity extends AppCompatActivity {

    private Button mCountriesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();

        setContentView(R.layout.activity_main);

        mCountriesButton = (Button) findViewById(R.id.my_countries_button);
        mCountriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent countyInt = new Intent(getApplicationContext(), MyCountriesCalendar.class);
                startActivity(countyInt);
            }
        });
    }

    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "Asking for permissions");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
        }
    }
}
