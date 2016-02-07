package group1.musicplayer;

/**
 * Created by shawn on 1/4/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;

public class MetaData extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "song_meta_data.db";
    private static final int DATABASE_VERSION = 1;

    static final String ARTIST_TABLE = "artist_table";
    //the columns for the table
    static final String ARTIST_NAME_COLUMN = "artist_name";
    static final String ARTIST_UID_COLUMN = "artist_uid";
    static final String ARTIST_BIO_COLUMN = "artist_bio";
    static final String ARTIST_F_NAME_COLUMN = "artist_f_name";
    static final String ARTIST_L_NAME_COLUMN = "artist_l_name";
    static final Blob ARTIST_PIC = null;

    static final String ALBUM_TABLE = "album_table";
    //the columns for the table
    static final String ALBUM_NAME_COLUMN = "album_name";
    static final String ALBUM_UID_COLUMN = "album_uid";
    static final String ALBUM_RELEASE_DATE_COLUMN = "release_date";
    static final String GENRE_COLUMN = "genre";
    static final Blob ALBUM_COVER = null;

    static final String SONG_TABLE = "song_table";
    //the columns for the table
    static final String SONG_NAME_COLUMN = "song_name";
    static final String SONG_UID_COLUMN = "song_uid";
    static final String SONG_LENGTH_COLUMN = "song_length";


    public MetaData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {

        createArtistQuery(db);
        createAlbumQuery(db);
        createSongQuery(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ARTIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ALBUM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE);
        onCreate(db);
    }

    private void createArtistQuery(SQLiteDatabase db){

        String artist_query = "CREATE TABLE " + ARTIST_TABLE  + " IF NOT EXISTS (" +
                ARTIST_UID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ARTIST_NAME_COLUMN + " TEXT, " +
                ARTIST_BIO_COLUMN + " TEXT, " +
                ARTIST_F_NAME_COLUMN + " TEXT, " +
                ARTIST_L_NAME_COLUMN + " TEXT, " +
                //ARTIST_PIC + " BLOB " +
                //PLAYLIST_BYTESTREAM + " BLOB" +
                ");";

        db.execSQL(artist_query);
    }

    private void createAlbumQuery(SQLiteDatabase db){

        String album_query = "CREATE TABLE " + ALBUM_TABLE  + " IF NOT EXISTS (" +
                ALBUM_UID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ALBUM_NAME_COLUMN + " TEXT, " +
                ALBUM_RELEASE_DATE_COLUMN + " TEXT, " +
                GENRE_COLUMN + " TEXT, " +
                //ALBUM_COVER + " BLOB " +
                ");";

        db.execSQL(album_query);
    }

    private void createSongQuery(SQLiteDatabase db){

        String song_query = "CREATE TABLE " + SONG_TABLE  + " IF NOT EXISTS (" +
                SONG_UID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SONG_NAME_COLUMN + " TEXT, " +
                SONG_LENGTH_COLUMN + " TEXT, " +
                ");";

        db.execSQL(song_query);
    }
}
