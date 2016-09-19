package ch223av.dv606.assignment2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ch223av.dv606.assignment2.MyCountriesCalendar.MyCountriesCalendar;

public class MainActivity extends AppCompatActivity {

    private Button mCountriesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
