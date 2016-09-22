package ch223av.dv606.assignment2.MyCountriesCalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ch223av.dv606.assignment2.R;
import ch223av.dv606.assignment2.MyCountriesCalendar.adapters.VisitAdapter;

public class MyCountriesCalendar extends AppCompatActivity{
    static final int SET_COUNTRY_REQUEST = 1;
    static final int SET_VISIT_REQUEST = 2;
    static final int SET_SETTINGS_REQUEST = 3;

    public static String ascDesc = "dtstart ASC";

    private Visit mVisit;
    private Visit mEditVisit;
    private ArrayList<Visit> mCountries = new ArrayList<Visit>();
    private VisitAdapter adapter;
    private VisitAdapter adapter2;
    private ListView mListView;
    private static Context mContext;
    private int editID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countries_main);

        //Calendar
        mContext = getApplicationContext();

        adapter = new VisitAdapter(this, R.layout.country_year_item, mCountries);
        mListView = (ListView) findViewById(R.id.list);
        try {
            mListView.setAdapter(adapter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        registerForContextMenu(mListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Edit"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            functionEdit(info.position);
        }
        else if(item.getTitle()=="Delete"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            functionDelete(info.position);
        }
        else { return false; }
        return true;
    }

    public void functionEdit(int id) {
        Log.i("functionEdit - id", id + "");
        editID = id;

        Intent intent2 = new Intent(this, EditVisit.class);
        startActivityForResult(intent2, SET_VISIT_REQUEST);
    }

    public void functionDelete(int id) {
        Log.i("functionDelete - id",id+"");
        CalendarProviderClient test = new CalendarProviderClientImp();
        test.deleteEvent(id);
        onResume();
    }


    @Override
    protected void onResume() {
        super.onResume();

        adapter2 = new VisitAdapter(this, R.layout.country_year_item, mCountries);
        mListView = (ListView) findViewById(R.id.list);
        try {
            mListView.setAdapter(adapter2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        CalendarProviderClient test = new CalendarProviderClientImp();

        Visit[] events = test.getCalendarVisits();
        mCountries.clear();

        for(int i=0; i<events.length; i++){
            mCountries.add(events[i]);
        }

        adapter2.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addCountry:
                // User pressed the "Add country" button --> show the activity...
                startAddVisitActivity();
                return true;
            /*
            case R.id.action_sort:
                startSortActivity();
                return true;
            */

            case R.id.action_settings:
                startSettingsActivity();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void startAddVisitActivity() {
        //Display
        Intent intent = new Intent(this, AddVisit.class);
        startActivityForResult(intent, SET_COUNTRY_REQUEST);
    }

    public void startSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivityForResult(intent, SET_SETTINGS_REQUEST);
    }

    private void startSortActivity() {
        Context context = getApplicationContext();
        int toastDuration = Toast.LENGTH_SHORT;
        if(ascDesc == "dtstart ASC"){
            ascDesc = "dtstart DESC";
            CharSequence text = "Sorting descending order.";
            (Toast.makeText(context, text, toastDuration)).show();
        }
        else{
            ascDesc = "dtstart ASC";
            CharSequence text = "Sorting ascending order.";
            (Toast.makeText(context, text, toastDuration)).show();
        }
        onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == SET_COUNTRY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mVisit = data.getParcelableExtra(AddVisit.ADD_VISIT);
                //mCountries.add(mVisit);
                //adapter.notifyDataSetChanged();
            }
        }else if(requestCode == SET_VISIT_REQUEST) {
            mEditVisit = data.getParcelableExtra(EditVisit.EDIT_VISIT);

            String country = mEditVisit.getCountry();
            int year = mEditVisit.getYear();

            CalendarProviderClient test = new CalendarProviderClientImp();
            test.updateEvent(editID, year, country);
        }else if(requestCode == SET_SETTINGS_REQUEST) {
            Log.i("Settings","exited");
        }
    }
    public static Context getContext() {
        return mContext;
    }
}
