package ch223av.dv606.assignment2.MyCountriesCalendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class CalendarProviderClientImp extends AppCompatActivity implements CalendarProviderClient {
    /*
    String displayName = "";
    String accountName = "";
    String ownerName = "";
    long calendarID = 0;
    */
    SimpleCursorAdapter mAdapter;


    @Override
    public long getMyCountriesCalendarId() {
        Cursor cursor;
        Context context = MyCountriesCalendar.getContext();
        ContentResolver cr = context.getContentResolver();
        Uri calendarUri = CALENDARS_LIST_URI;
        cursor = cr.query(calendarUri, CALENDARS_LIST_PROJECTION, CALENDARS_LIST_SELECTION, CALENDARS_LIST_SELECTION_ARGS, null);

        if (cursor.moveToFirst()) {
            Log.d("ID", cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX) + "");
            Log.d("Name", cursor.getString(PROJ_CALENDARS_LIST_NAME_INDEX));
            return cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX);
        } else {
            //create calendar
            Log.d("Creating", "Calendar");

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
            Log.d("Calendar", "created");
            return Long.parseLong(calendarData.getLastPathSegment());
        }
    }


    @Override
    /*
    Implement the addNewEvent() method. It should be invoked from your code after parsing and validating
    user input values for year and country title (in Assignment 1, you probably simply added a new item
    to a list at this point). The idea is that you use ContentResolver.insert() to create a new calendar
    event. You should use the country title as the event name, and transform the year value into event
    start & end timestamps by invoking corresponding methods from CalendarUtils provided in the starter kit.
    At this point, you should be able to see the new events added to the calendar with the stock Android
    calendar application (events will be created on January 1st of the respective years).
    */
    public void addNewEvent(int year, String country) {

        long startMillis = CalendarUtils.getEventStart(year);
        long endMillis = CalendarUtils.getEventEnd(year);
        String timezone = CalendarUtils.getTimeZoneId();

        Context context = MyCountriesCalendar.getContext();
        ContentResolver cr = context.getContentResolver();

        //Cursor cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{ "_id", "calendar_displayName" }, null, null, null);
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
            v.put(CalendarContract.Events.CALENDAR_ID, calIds[0]+"");
            v.put(CalendarContract.Events.DTSTART, startMillis + "");
            v.put(CalendarContract.Events.DTEND, endMillis + "");
            v.put(CalendarContract.Events.TITLE, country);
            v.put(CalendarContract.Events.EVENT_TIMEZONE, timezone + "");

            //Uri creationUri = asSyncAdapter(CALENDARS_LIST_URI, ACCOUNT_TITLE, CalendarContract.Calendars.CAL_ACCESS_OWNER + "");
            //Uri calendarData = context.getContentResolver().insert(creationUri, v);
            Uri calendarData = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, v);

            // get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(calendarData.getLastPathSegment());
            //Log.d("eventID",eventID+"");

        } else{
            Log.d("Warning2","No calendar found.");
        }
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

        Uri updateUri = ContentUris.withAppendedId(EVENTS_LIST_URI, eventId);
        int rows = context.getContentResolver().update(updateUri, v, null, null);
        Log.i("updateEvent", "Rows updated: " + rows);

        getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);
    }

    @Override
    public void deleteEvent(int eventId) {
        Context context = MyCountriesCalendar.getContext();

        Uri deleteUri = ContentUris.withAppendedId(EVENTS_LIST_URI, eventId);
        context.getContentResolver().delete(deleteUri, null, null);

        getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);
        return null;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("onLoadFinished",data.toString());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }


    static Uri asSyncAdapterNewCalendar(Uri uri,
                                        String account,
                                        String accountType,
                                        String name,
                                        int color,
                                        String access_level) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType)
                .appendQueryParameter(CalendarContract.Calendars.NAME, name)
                .appendQueryParameter(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, name)
                .appendQueryParameter(CalendarContract.Calendars.CALENDAR_COLOR, color+"")
                .appendQueryParameter(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, access_level)
                .appendQueryParameter(CalendarContract.Calendars.OWNER_ACCOUNT, account).build();
    }


    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

}
