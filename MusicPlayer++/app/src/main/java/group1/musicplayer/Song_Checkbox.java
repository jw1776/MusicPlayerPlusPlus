package group1.musicplayer;
/**
 * Created by Jack on 2/12/2016.
 */
public class Song_Checkbox {

    private long id;
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private boolean selected;

    public Song_Checkbox(long songID, String songTitle, String songArtist, String songAlbum, long songAlbumId){
        id=songID;
        title=songTitle;
        artist=songArtist;
        album=songAlbum;
        albumId=songAlbumId;
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

    public long getAlbumId(){
        return albumId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
