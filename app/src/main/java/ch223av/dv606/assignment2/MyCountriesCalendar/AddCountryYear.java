package ch223av.dv606.assignment2.MyCountriesCalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ch223av.dv606.assignment2.R;

public class AddCountryYear extends AppCompatActivity {

    public static final String TAG = AddCountryYear.class.getSimpleName();
    public static final String ADD_VISIT = "ADD_VISIT";

    private Button mSaveYearCountryButton;
    private EditText mYear;
    private EditText mCountry;
    private Visit mVisit;
    private String year = "";
    private String country = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country_year);

        mYear = (EditText) findViewById(R.id.yearEditText);
        mCountry = (EditText) findViewById(R.id.countryEditText);
        mSaveYearCountryButton = (Button) findViewById(R.id.saveCountryYearButton);


        View.OnClickListener saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {                //Compute button pressed
                mVisit = new Visit(Parcel.obtain());

                if(TextUtils.isDigitsOnly(mYear.getText().toString())
                        && !(TextUtils.isEmpty(mYear.getText().toString()))
                        && (mYear.getText().toString().length() <= 4)
                        && (mYear.getText().toString().length() > 1)){
                    year = mYear.getText().toString();
                }
                if(!TextUtils.isEmpty(mCountry.getText().toString())){
                    country = mCountry.getText().toString();
                }


                if(year != "" && country != ""){

                    CalendarProviderClient calendarImp = new CalendarProviderClientImp();
                    calendarImp.addNewEvent(Integer.parseInt(year),country);

                    mVisit.setCountry(country);
                    mVisit.setYear(Integer.parseInt(year));

                    Log.i(TAG,mVisit.getCountry());

                    Intent resultIntent = new Intent();
                    //Add extras to this intent.
                    resultIntent.putExtra(ADD_VISIT, mVisit);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
                else {
                    Log.d("Year: ", year);
                    Context context = getApplicationContext();
                    CharSequence text = context.getResources().getString(R.string.add_country_toast);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }



            }
        };
        mSaveYearCountryButton.setOnClickListener(saveListener);

    }
}

