package ch223av.dv606.assignment2.MyCountriesCalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ch223av.dv606.assignment2.R;

public class Settings extends AppCompatActivity{

    public static final String ORDERPREFS = "orderPrefs";
    public static final String SIZEPREFS = "SIZEPREFS";
    public static final String COLORPREFS = "COLORPREFS";
    public static final String MyPREFERENCES = "MyPrefs";

    private static final String TAG = Settings.class.getSimpleName();
    public SharedPreferences sharedpreferences;

    private String dtstartAscDesc = "dtstart ASC";
    private String titleAscDesc = "title ASC";
    private String orderBy = "dtstart ASC, title ASC";

    RadioGroup rgYear;
    RadioGroup rgTitle;
    SeekBar mTextSizeSeekBar;
    TextView mChangeTextSize;
    Switch mBgSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rgTitle = (RadioGroup) findViewById(R.id.radioGroupTitle);
        rgYear = (RadioGroup) findViewById(R.id.radioGroupYears);
        mTextSizeSeekBar = (SeekBar) findViewById(R.id.textSizeSeekBar);
        mChangeTextSize = (TextView) findViewById(R.id.changeTextSize);
        mBgSwitch = (Switch) findViewById(R.id.bgColorSwitch);

        sharedpreferences = MyCountriesCalendar.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        getWindow().getDecorView().setBackgroundColor(sharedpreferences.getInt(COLORPREFS,getResources().getColor(R.color.colorBackground)));
        mChangeTextSize.setTextSize(sharedpreferences.getInt(SIZEPREFS,24));

        rgYear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioButton1) {
                    dtstartAscDesc = "dtstart ASC";
                }
                if (checkedId == R.id.radioButton2) {
                    dtstartAscDesc = "dtstart DESC";
                }
                orderBy = dtstartAscDesc + ", " + titleAscDesc;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(ORDERPREFS, orderBy);
                editor.apply();
            }
        });

        rgTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton3) {
                    titleAscDesc = "title ASC";
                }
                if (checkedId == R.id.radioButton4) {
                    titleAscDesc = "title DESC";
                }
                orderBy = dtstartAscDesc + ", " + titleAscDesc;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(ORDERPREFS, orderBy);
                editor.apply();
            }
        });

        mTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Minimum text size 8sp
                progress = progress + 8;
                mChangeTextSize.setTextSize(progress);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(SIZEPREFS, progress);
                editor.apply();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mBgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(COLORPREFS, Color.BLACK);
                    editor.apply();
                    getWindow().getDecorView().setBackgroundColor(sharedpreferences.getInt(COLORPREFS,0));
                }
                else{
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(COLORPREFS, getResources().getColor(R.color.colorBackground));
                    editor.apply();
                    getWindow().getDecorView().setBackgroundColor(sharedpreferences.getInt(COLORPREFS,0));
                }
            }
        });
    }

}
