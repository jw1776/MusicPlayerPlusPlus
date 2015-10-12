package group1.musicplayer;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shawn on 10/11/2015.
 */

//This class will find any and all audio files along with their details, such as path
public class AudioSearcher {

    final String MEDIA_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/";
    private ArrayList<HashMap<String, String>> audioList = new ArrayList<HashMap<String, String>>();
    private String[] audioPattern = {".mp3", ".m4a", ".amr", ".wav", ".wma" };

    /**
     * Function to read all audio files and store the details in
     * ArrayList
     * */
    public ArrayList<HashMap<String, String>> getAudioList() {
        System.out.println("\n******************** The path is: " + MEDIA_PATH + "************************");
        if (MEDIA_PATH != null) {
            File home = new File(MEDIA_PATH);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    System.out.println("Abs path is: "  + file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addAudioToList(file);
                    }
                }
            }
        }
        return audioList;
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addAudioToList(file);
                    }
                }
            }
        }
    }

    private void addAudioToList(File audio) {

        for(int i = 0; i < audioPattern.length; i++) {
            //if the file contains any audio pattern add it to the list
            if (audio.getName().endsWith(audioPattern[i])) {
                HashMap<String, String> audioMap = new HashMap<String, String>();
                audioMap.put("audioTitle",
                        audio.getName().substring(0, (audio.getName().length() - 4)));
                audioMap.put("audioPath", audio.getPath());

                audioList.add(audioMap);
            }
        }
    }
}


//source : http://stackoverflow.com/questions/16080880/android-scanning-all-mp3-files-in-sd-card