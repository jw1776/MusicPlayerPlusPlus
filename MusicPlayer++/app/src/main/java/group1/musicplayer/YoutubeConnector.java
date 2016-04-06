package group1.musicplayer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shawn on 3/26/2016.
 */
public class YoutubeConnector {

    private YouTube youtube;
    private YouTube.Search.List query;
    private Context context;

    public static final String KEY
            = "AIzaSyArtdg4l3BJs0o1RQmAGUbMRNiLrJLsE44";//browser key

    public YoutubeConnector(Context context) {

        this.context = context;
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {}
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try{
            query = youtube.search().list("id,snippet");
            query.setKey(KEY);
            query.setType("video");
            query.setFields("items(kind,id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url),nextPageToken,pageInfo,prevPageToken");

           // System.out.printf("******INFO ABOUT QUERY: Size: %d\n ", query.size());
        }catch(IOException e){
            Log.d("YC", "Could not initialize: " + e);
            System.out.println("query is null***********");
        }
    }

    public List<VideoItem> findVideos(String keywords){

        query.setQ(keywords);
        try{
           // System.out.println("0***********");
            SearchListResponse response = query.execute();
          //  System.out.println("1***********");

            List<SearchResult> results = response.getItems();
            //System.out.println("2***********");

            List<VideoItem> items = new ArrayList<VideoItem>();
           // System.out.println("3***********");

            for(SearchResult result:results){

                VideoItem item = new VideoItem();
                //  System.out.println("4***********");

                //set the title, thumbnail, id (url) for the video items which are shown on the results page
                item.setTitle(result.getSnippet().getTitle());
                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getId().getVideoId());

                items.add(item);
            }
//            if(items == null){
//                System.out.println("items is null***********");
//            }else{
//                System.out.println(items.size() + " items is NOT null***********");
//            }
            return items;
        }
        catch (GoogleJsonResponseException e) {
            Toast.makeText(context, "There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage(), Toast.LENGTH_LONG).show();
            return null;
        } catch (IOException e){
            Toast.makeText(context, "I/O error: " + e, Toast.LENGTH_SHORT).show();
            return null;
        }catch(Exception e){
            Toast.makeText(context, "ERROR:  " + e, Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}