package group1.musicplayer;

/**
 * Created by LukeJr on 5/8/2016.
 */
public interface ServiceCallbacks { //Allows MusicService to invoke MainActivity methods
    void notifySongTab();
    void notifyAlbumTab();
    void notifyArtistTab();
    void notifyPlaylistTab();
}
