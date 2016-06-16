package group1.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/* Created by Art on 3/28/2016.*/

public class SearchLyrics extends Activity {

    private WebView webView;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lyrics);

        URL = getIntent().getStringExtra("url");

        loadWebView();
    }

    // http://stackoverflow.com/questions/7305089/how-to-load-external-webpage-inside-webview Reference
    private void loadWebView(){
        webView  = new WebView(this);

        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        webView.getSettings().setBuiltInZoomControls(true); //Set Zoom in/Zoom out
        webView.getSettings().setDisplayZoomControls(false);

        final Activity activity = this;

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl(URL);
        setContentView(webView);
    }
}
