package group1.musicplayer;

/**
 * Created by Jack on 11/1/2015.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "mpp_data.db";

    public static final String TABLE_PLAYLISTS = "playlists"; //name of the table storing information for calendar events
    public static final String PLAYLIST_ID = "_id"; //The following are columns in the table
    public static final String PLAYLIST_TITLE = "title";
    public static final String PLAYLIST_BYTESTREAM = "bytestream";
    public static final String PLAYLIST_ISDEFAULT ="isdefault";


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query_playlists = "CREATE TABLE " + TABLE_PLAYLISTS + "(" +
                PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLAYLIST_TITLE + " TEXT, " +
                PLAYLIST_BYTESTREAM + " BLOB, " +
                PLAYLIST_ISDEFAULT + " INTEGER" +
                ");";

        db.execSQL(query_playlists);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        onCreate(db);
    }

    public void addPlaylist(Playlist playlist, boolean isDefault){ //Add a new playlist row to the database
        ContentValues values = new ContentValues();

        values.put(PLAYLIST_TITLE, playlist.getTitle()); //fill column with value
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); //CONVERT PLAYLIST TO BYTE ARRAY
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(playlist);
            byte[] yourBytes = bos.toByteArray();
            values.put(PLAYLIST_BYTESTREAM, yourBytes); //fill column with value

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }                                                    //CONVERT PLAYLIST TO BYTE ARRAY

        if (isDefault) { //if this is a default playlist
            values.put(PLAYLIST_ISDEFAULT, 1); //1 represents true
        }
        else {
            values.put(PLAYLIST_ISDEFAULT, 0); //0 represents false
        }

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PLAYLISTS, null, values);
        db.close();
    }


    public void deletePlaylist(String title){ //Delete an playlist row in the database. (Need to update to use _id if possible)
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYLISTS + " WHERE " + PLAYLIST_TITLE + "=\"" + title + "\";");
    }

    public void deleteDefaultPlaylists() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYLISTS + " WHERE " + PLAYLIST_ISDEFAULT + "=" + 1);
    }

    public boolean databaseExists(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
    public ArrayList<Playlist> pullPlaylists (){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLISTS + " WHERE 1";
        //Cursor points to a location in the results
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst(); // move to the first row

        ArrayList<Playlist> allPlaylistsArray = new ArrayList<Playlist>();

        while(!c.isAfterLast()){ //While there are still more entries (while the cursor is not after the last entry)
            //Loops through all rows in the table

            int pId = c.getInt(c.getColumnIndex("_id"));
            byte[] thebytes = c.getBlob(c.getColumnIndex("bytestream"));

            ByteArrayInputStream bis = new ByteArrayInputStream(thebytes); //CONVERT RAW BYTES TO PLAYLIST OBJECT
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                Object o = in.readObject();
                Playlist p = (Playlist) o; //typecast into playlist from raw bytes
                p.setId(pId);
                allPlaylistsArray.add(p); //add playlist to the array of all playlists

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bis.close();
                } catch (IOException ex) {
                    // ignore close exception
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }
            }                                                       //CONVERT RAW BYTES TO PLAYLIST OBJECT

            c.moveToNext();
        }//end while
        db.close();

        return allPlaylistsArray;
    }// end of pullEvents method

}
