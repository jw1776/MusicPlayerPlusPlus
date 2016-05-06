package group1.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Song implements Parcelable, Serializable {
    private long id;
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private String duration;


    public Song(long songID, String songTitle, String songArtist, String songAlbum, long songAlbumId, String songDuration){
        id=songID;
        title=songTitle;
        artist=songArtist;
        album=songAlbum;
        albumId=songAlbumId;
        duration=formatDuration(songDuration);
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

    public String getAlbum() { return album; }

    public long getAlbumId(){ return albumId; }

    public String getDuration() { return duration; }

    public String formatDuration(String songDuration) {

        if (songDuration != null) {
            int totalDuration = Integer.parseInt(songDuration); //in milliseconds
            totalDuration = totalDuration / 1000;                 //in seconds

            String newDuration = Integer.toString(totalDuration / 60);  //whole minutes

            if ((totalDuration % 60) < 10) {
                newDuration = newDuration + ":0" + Integer.toString(totalDuration % 60);
            }
            else {
                newDuration = newDuration + ":" + Integer.toString(totalDuration % 60);
            }
            return newDuration;
        }

        else {
            return "";
        }

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
