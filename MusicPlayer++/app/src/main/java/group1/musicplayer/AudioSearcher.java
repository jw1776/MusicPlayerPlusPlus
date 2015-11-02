package group1.musicplayer;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private ArrayList<String> audioTitleList = new ArrayList<String>();

    private Context context;
    private ProgressBar spinner;

//
//    AudioSearcher(Context c ){
//        context = c;
//       // spinner = (ProgressBar)findViewById(R.id.loading_icon);
//       // spinner.setVisibility(View.VISIBLE);
//    }

    public ArrayList<String> getAudioTitleList(){
        return audioTitleList;
    }

    public ArrayList<HashMap<String, String>> getAudioList(){
        return audioList;
    }

    /**
     * Function to read all audio files and store the details in
     * ArrayList
     * */
    public void createAudioList() {
        System.out.println("\n******************** The path is: " + MEDIA_PATH + "************************");
        if (MEDIA_PATH != null) {
            File home = new File(MEDIA_PATH);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
//                Toast.makeText(context,
//                    "Loading audio file that are not in the main player...",
//                    Toast.LENGTH_LONG).show();
                for (File file : listFiles) {

                    //     System.out.println("Abs path is: "  + file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addAudioToList(file);
                    }
                }
            }
        }
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
                audioTitleList.add(audio.getName().substring(0, (audio.getName().length() - 4)));
                audioList.add(audioMap);
            }
        }
    }
}
//source : http://stackoverflow.com/questions/16080880/android-scanning-all-mp3-files-in-sd-card