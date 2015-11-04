package group1.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LukeJr on 9/26/2015.
 */
public class Song implements Parcelable {
    private long id;
    private String title;
    private String artist;

    public Song(long songID, String songTitle, String songArtist){
        id=songID;
        title=songTitle;
        artist=songArtist;
    }
    public Song(Parcel in){
        String [] data = new String[3];
        in.readStringArray(data);
        this.id = Long.parseLong(data [0]);
        this.title = data[1];
        this.artist = data[2];
    }

    public String toString(){
        return artist + " " + title;
    }

    public long getID(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{String.valueOf(this.id),this.title,this.artist});
    }
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>(){
        @Override
        public Song createFromParcel(Parcel source){
            return new Song(source);
        }
        @Override
        public Song[] newArray(int size){
            return new Song[size];
        }
    };
}
