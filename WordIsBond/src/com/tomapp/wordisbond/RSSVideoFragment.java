package com.tomapp.wordisbond;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class RSSVideoFragment  extends Fragment 
{
	public RSSItem mItem;
	private TextView mTitle = null;
	private VideoView mVideoView = null;
	private WebView mWebView = null;
	private TextView mTextView = null;
	
	public void SetItem(RSSItem item)
	{

		mItem = item;
		if (mTitle != null)
		{
	        mTitle.setText(item.getMediaSource());
		}
		if ((mVideoView != null))
		{
//	        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/DCIM/Camera/VID_20120817_193917.3pg";
	        String url = "http://www.law.duke.edu/cspd/contest/finalists/viewentry.php?file=docandyou";
		        
	//        mVideoView.setVideoPath(path);
	        mVideoView.setVideoURI(Uri.parse(url));
	       mVideoView.setMediaController(new MediaController(ItemListActivity.main));
	       mVideoView.requestFocus();
	       mVideoView.start();
		}
		if (mWebView != null)
		{
//	        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/DCIM/Camera/VID_20120817_193917.3pg";
//	        String url = "http://www.law.duke.edu/cspd/contest/finalists/viewentry.php?file=docandyou";
	
//			String urlEnd = "?player_id=player&autoplay=1&title=0&byline=0&portrait=0&api=1&maxheight=480&maxwidth=800";
			String url = item.getMediaSource();
/*			int widthIdx = url.indexOf("width");
			if (widthIdx >= 0)
			{
				Display display = ItemListActivity.main.getWindowManager().getDefaultDisplay();
				int quoteStart = url.indexOf("\"", widthIdx);
				int quoteStart = url.indexOf("\"", widthIdx);
			}*/
			if (!url.contains("youtube"))
			{
				mWebView.loadUrl(url);//+urlEnd);
			}
			else
			{
		        mWebView.loadData(url, "text/html", null);
			}
//	       mWebView.loadUrl("http://www.thewordisbond.com");
		}
		if (mTextView != null)
		{
			mTextView.setText(Html.fromHtml(item.getDescription()));
		}
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
        
        if (ItemListActivity.main.mCurrentAudio != null)
        {
        	SetItem(ItemListActivity.main.mCurrentAudio);
        }

  //      setListAdapter(mAdapter);
//        ListView itemlist = (ListView) getView().findViewById(R.id.itemlist);
        
//        itemlist.setAdapter(mAdapter);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	// The last two arguments ensure LayoutParams are inflated
        // properly.

//    	int resID = R.layout.videoplayback;
    	int resID = R.layout.videofrag;
    	
    	View rootView = inflater.inflate(
                resID, container, false);

//    	rootView.setRotation(90);
    	
//    	mTitle = (TextView)rootView.findViewById(R.id.title);
    	mVideoView = (VideoView)rootView.findViewById(R.id.videoView);
    	mWebView = (WebView)rootView.findViewById(R.id.webView);
    	mTextView = (TextView)rootView.findViewById(R.id.description);
    	
    	if (mWebView != null)
    	{
//	        mWebView.getSettings().setLoadWithOverviewMode(true);
//	        mWebView.getSettings().setUseWideViewPort(true);
//	        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

	        mWebView.getSettings().setJavaScriptEnabled(true);
	        mWebView.getSettings().setAppCacheEnabled(true);
	        mWebView.getSettings().setDomStorageEnabled(true);
	        mWebView.setWebChromeClient(new WebChromeClient());
 //	        mWebView.getSettings().setAllowContentAccess(false);
	        mWebView.getSettings().setPluginState(PluginState.ON);
	        
	        if (ItemListActivity.main.isFullScreen())
	        {
//		        mWebView.getSettings().setJavaScriptEnabled(true);
		        mWebView.getSettings().setLoadWithOverviewMode(true);
		        mWebView.getSettings().setUseWideViewPort(true);
		        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		        mWebView.setScrollbarFadingEnabled(false);
	        }
	        
//	        mWebView.getSettings().setLoadWithOverviewMode(true);
//	        mWebView.getSettings().setUseWideViewPort(true);
//	        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

/*            mWebView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.766.0 Safari/534.36");
            mWebView.setWebViewClient(new WebViewClient() {
            	@Override
            	public boolean shouldOverrideUrlLoading(WebView view, String url)
            	{
            		return false;
            	}
            });
*/            
	        // how plugin is enabled change in API 8
//          mWebView.getSettings().setPluginState(PluginState.ON);
    	}

    	if (mItem != null)
    	{
	        SetItem(mItem);
    	}
        
        return rootView;
    }

}
