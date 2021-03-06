package ch223av.dv606.assignment2.MyCountriesCalendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.CalendarContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class CalendarProviderClientImp extends AppCompatActivity implements CalendarProviderClient {

    private SimpleCursorAdapter mAdapter;
    private Visit mVisit;

    SharedPreferences sharedpreferences = MyCountriesCalendar.getContext().getSharedPreferences(Settings.MyPREFERENCES, Context.MODE_PRIVATE);

    @Override
    public long getMyCountriesCalendarId() {
        Context context = MyCountriesCalendar.getContext();
        ContentResolver cr = context.getContentResolver();
        Uri calendarUri = CALENDARS_LIST_URI;
        Cursor cursor = cr.query(calendarUri, CALENDARS_LIST_PROJECTION, CALENDARS_LIST_SELECTION, CALENDARS_LIST_SELECTION_ARGS, null);

        if (cursor.moveToFirst()) {
            long a = cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX);
            cursor.close();

            return a;
        } else {
            //create calendar
            ContentValues v = new ContentValues();
            v.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_TITLE);
            v.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            v.put(CalendarContract.Calendars.NAME, CALENDAR_TITLE);
            v.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_TITLE);
            v.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.GREEN);
            v.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
            v.put(CalendarContract.Calendars.OWNER_ACCOUNT, ACCOUNT_TITLE);

            Uri creationUri = asSyncAdapter(CALENDARS_LIST_URI, ACCOUNT_TITLE, CalendarContract.Calendars.CAL_ACCESS_OWNER + "");
            Uri calendarData = context.getContentResolver().insert(creationUri, v);
            Log.i("getMyCountriesCalId", "Calendar created");

            cursor.close();

            return Long.parseLong(calendarData.getLastPathSegment());
        }
    }

    @Override
    public void addNewEvent(int year, String country) {

        long startMillis = CalendarUtils.getEventStart(year);
        long endMillis = CalendarUtils.getEventEnd(year);
        String timezone = CalendarUtils.getTimeZoneId();

        if (getLoaderManager() == null) {
            Log.i("addNewEvent", "Creating loaderManager");
            getLoaderManager().initLoader(LOADER_MANAGER_ID, null, this);
        } else {
            Log.i("addNewEvent", "LoaderManager already existing");
            //Log.i("getLoaderManager",getLoaderManager().toString());
        }

        Context context = MyCountriesCalendar.getContext();
        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(CALENDARS_LIST_URI, CALENDARS_LIST_PROJECTION, CALENDARS_LIST_SELECTION, CALENDARS_LIST_SELECTION_ARGS, null);
        if (cursor.moveToFirst()) {
            final String[] calNames = new String[cursor.getCount()];
            final int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }
            //add event
            ContentValues v = new ContentValues();
            v.put(CalendarContract.Events.CALENDAR_ID, calIds[0] + "");
            v.put(CalendarContract.Events.DTSTART, startMillis + "");
            v.put(CalendarContract.Events.DTEND, endMillis + "");
            v.put(CalendarContract.Events.TITLE, country);
            v.put(CalendarContract.Events.EVENT_TIMEZONE, timezone + "");

            Uri calendarData = cr.insert(CalendarContract.Events.CONTENT_URI, v);

            // get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(calendarData.getLastPathSegment());
            Log.i("addNewEvent - eventID",eventID+"");

            getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);

        } else{
            Log.i("addNewEvent","No calendar found.");
        }
        cursor.close();
    }

    @Override
    public void updateEvent(int eventId, int year, String country) {
        long startMillis = CalendarUtils.getEventStart(year);
        long endMillis = CalendarUtils.getEventEnd(year);
        Context context = MyCountriesCalendar.getContext();

        ContentValues v = new ContentValues();
        v.put(CalendarContract.Events.TITLE, country);
        v.put(CalendarContract.Events.DTSTART, startMillis+"");
        v.put(CalendarContract.Events.DTEND, endMillis+"");

        //Get correct event id.
        ContentResolver cr = context.getContentResolver();
        String[] eventsId = {CalendarContract.Events._ID};

        String orderPrefs = sharedpreferences.getString(Settings.ORDERPREFS, null);
        Cursor cursor = cr.query(EVENTS_LIST_URI,eventsId,"CALENDAR_ID="+(int)getMyCountriesCalendarId(),null, orderPrefs);
        cursor.moveToPosition(eventId);

        Uri updateUri = ContentUris.withAppendedId(EVENTS_LIST_URI, cursor.getInt(0));
        context.getContentResolver().update(updateUri, v, null, null);
        Log.i("updateEvent", "Event updated: " + cursor.getInt(0));

        getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);
        cursor.close();
    }

    @Override
    public void deleteEvent(int eventId) {
        Context context = MyCountriesCalendar.getContext();
        ContentResolver cr = context.getContentResolver();
        String[] eventsId = {CalendarContract.Events._ID};

        String orderPrefs = sharedpreferences.getString(Settings.ORDERPREFS, null);
        Cursor cursor = cr.query(EVENTS_LIST_URI,eventsId,"CALENDAR_ID="+(int)getMyCountriesCalendarId(),null, orderPrefs);

        cursor.moveToPosition(eventId);
        Uri deleteUri = ContentUris.withAppendedId(EVENTS_LIST_URI, cursor.getInt(0));
        int event = context.getContentResolver().delete(deleteUri, null, null);
        Log.i("deleteEvent", "Event deleted: " + cursor.getInt(0));

        getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);
        cursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(MyCountriesCalendar.getContext(),
                CalendarContract.Events.CONTENT_EXCEPTION_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("onLoadFinished",data.toString());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }

    public Visit[] getCalendarVisits(){
        String[] eventsArr = {
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART
        };

        Context context = MyCountriesCalendar.getContext();
        ContentResolver cr = context.getContentResolver();

        String orderPrefs = sharedpreferences.getString(Settings.ORDERPREFS, null);
        Cursor cursor = cr.query(EVENTS_LIST_URI,eventsArr,"CALENDAR_ID="+(int)getMyCountriesCalendarId(),null, orderPrefs);
        Visit[] mVisitList = new Visit[cursor.getCount()];

        if (cursor.moveToFirst()) {
            for (int i = 0; i < mVisitList.length; i++) {
                mVisit = new Visit(Parcel.obtain());
                mVisit.setCountry(cursor.getString(0));
                mVisit.setYear(CalendarUtils.getEventYear(Long.parseLong(cursor.getString(1))));
                mVisitList[i] = mVisit;
                cursor.moveToNext();
            }
        }
        cursor.close();
        return mVisitList;
    }
}
