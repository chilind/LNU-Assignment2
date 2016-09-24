package ch223av.dv606.assignment2.MP3Player;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ch223av.dv606.assignment2.R;
/**
 * A simple MP3 player skeleton for 2DV606 Assignment 2.
 *
 * Created by Oleksandr Shpak in 2013.
 * Ported to Android Studio by Kostiantyn Kucher in 2015.
 * Last modified by Kostiantyn Kucher on 04/04/2016.
 */
public class MP3Player extends AppCompatActivity {

    private static final String TAG = MP3Player.class.getSimpleName();

    public static final MediaPlayer mediaPlayer = new MediaPlayer();
    public static final String PLAYLISTPROGRESS = "PLAYLISTPROGRESS";
    public static final String MyPREFERENCES = "MyPrefs";

    public SharedPreferences sharedpreferences;
    NotificationCompat.Builder mBuilder;

    Button mPreviousButton;
    Button mNextButton;
    Button mPlayButton;

    int songProgress;
    int playlistProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize the layout
        setContentView(R.layout.activity_mp3_player);

        // Initialize the list of songs
        final ListView playlistListView = (ListView) findViewById(R.id.playlist_list_view);
        final ArrayList<Song> songs = songList();

        mPreviousButton = (Button) findViewById(R.id.prevButton);
        mNextButton = (Button) findViewById(R.id.nextButton);
        mPlayButton = (Button) findViewById(R.id.playButton);

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    songProgress = mediaPlayer.getCurrentPosition();
                } else {
                    mediaPlayer.seekTo(songProgress);
                    mediaPlayer.start();
                    songProgress = 0;
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Songs size: " +songs.size());

                playlistProgress = sharedpreferences.getInt(PLAYLISTPROGRESS,0) + 1;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(PLAYLISTPROGRESS, playlistProgress);
                editor.apply();

                //Ensure there is another song in playlist
                if(songs.size() > playlistProgress ) {
                    play(songs.get(playlistProgress)); // set the song to play
                }
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playlistProgress = sharedpreferences.getInt(PLAYLISTPROGRESS,0) - 1;
                Log.i("playlistprog", playlistProgress+"");

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(PLAYLISTPROGRESS, playlistProgress);
                editor.apply();

                play(songs.get(playlistProgress));
                Log.i(TAG,playlistProgress+"");
            }
        });

        Log.i(TAG,"Starting service");
        Intent svc = new Intent(this, PlayService.class);
        this.startService(svc);


        playlistListView.setAdapter(new PlayListAdapter(this, songs));
        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long arg3)
            {
                Log.i(TAG,"Position: " + pos);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(PLAYLISTPROGRESS, pos);
                editor.apply();

                play(songs.get(pos));
            }
        });
    }

    private class PlayListAdapter extends ArrayAdapter<Song>
    {
        public PlayListAdapter(Context context, ArrayList<Song> objects)
        {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View row, ViewGroup parent)
        {
            Song data = getItem(position);

            row = getLayoutInflater().inflate(R.layout.layout_row, parent, false);

            TextView name = (TextView) row.findViewById(R.id.label);
            name.setText(String.valueOf(data));
            row.setTag(data);

            return row;
        }
    }

    /**
     * Checks the state of media storage. True if mounted;
     * @return
     */
    private boolean isStorageAvailable()
    {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Reads song list from media storage.
     * @return
     */
    private ArrayList<Song> songList()
    {
        ArrayList<Song> songs = new ArrayList<Song>();

        if(!isStorageAvailable()) // Check for media storage
        {
            Toast.makeText(this, R.string.nosd, Toast.LENGTH_SHORT).show();
            return songs;
        }

        Cursor music = getContentResolver().query( // using content resolver to read music from media storage
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.IS_MUSIC + " > 0 ",
                null, null
        );

        if (music.getCount() > 0)
        {
            music.moveToFirst();
            Song prev = null;
            do
            {
                Song song = new Song(music.getString(0), music.getString(1), music.getString(2), music.getString(3));

                if (prev != null) // play the songs in a playlist, if possible
                    prev.setNext(song);

                prev = song;
                songs.add(song);
            }
            while(music.moveToNext());

            prev.setNext(songs.get(0)); // play in loop
        }
        music.close();

        return songs;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent svc2 = new Intent(this, PlayService.class);
        this.stopService(svc2);

    }

    /**
     * Uses mediaPlayer to play the selected song.
     * The sequence of media player operations is crucial for it to work.
     * @param song
     */
    private void play(final Song song)
    {
        if(song == null) return;

        try
        {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop(); // stop the current song

            mediaPlayer.reset(); // reset the resource of player
            mediaPlayer.setDataSource(this, Uri.parse(song.getPath())); // set the song to play
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION); // select the audio stream
            mediaPlayer.prepare(); // prepare the resource
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() // handle the completion
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    play(song.getNext());
                }
            });
            mediaPlayer.start(); // play!
        }
        catch(Exception e)
        {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void stop(){
        try
        {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop(); // stop the current song

        }
        catch(Exception e)
        {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mp3_player, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_player:
                stop();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
