package com.tomapp.wordisbond;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.VideoView;

public class RSSItemView extends Fragment 
{
	public RSSItem mItem;
	private TextView mTitle = null;
	private WebView mWebView = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
        
        mItem = null;
    }
    
	public void SetItem(RSSItem item)
	{
		mItem = item;
		if (mTitle != null)
		{
	        mTitle.setText(mItem.getTitle());
		}
		if (mWebView != null)
		{
	        final String html = "<body>" + mItem.getEncodedData() + "</body>";
	        mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
		}	
/*		if ((mVideoView != null))
		{
	        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/DCIM/Camera/VID_20120817_193917.3pg";
	        String url = "http://www.law.duke.edu/cspd/contest/finalists/viewentry.php?file=docandyou";

//	        mVideoView.setVideoPath(path);
	        mVideoView.setVideoURI(Uri.parse(url));
	       mVideoView.setMediaController(new MediaController(ItemListActivity.main));
	       mVideoView.requestFocus();
	       mVideoView.start();
		}
*/	}
	
    
    @Override
    public void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", 3);
    }
			
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) 
    {
        // The last two arguments ensure LayoutParams are inflated
        // properly.

//    	mItem = null;
    
//    	setRetainInstance(true);

/*    	if (false)
    	{
	    	View rootView = inflater.inflate(
	                R.layout.videoplayback, container, false);

	    	mVideoView = (VideoView)rootView.findViewById(R.id.videoView);

//	        SetItem(mItem);
	        
	        return rootView;
    	}
    	else
*/    	{
	    	View rootView = inflater.inflate(
	                R.layout.showdescription, container, false);
	    	
	        mTitle = (TextView) rootView.findViewById(R.id.label);
	
	//        if (image != null)
	//        {
	//			ImageView imageView = (ImageView) findViewById(R.id.image);
	//			imageView.setImageBitmap(image);
	//        }
	
	        mWebView = (WebView) rootView.findViewById(R.id.webview);
	        
	        mWebView.getSettings().setJavaScriptEnabled(true);
	        mWebView.getSettings().setAppCacheEnabled(true);
	        mWebView.getSettings().setDomStorageEnabled(true);
	        mWebView.getSettings().setPluginState(PluginState.ON);
// 	        mWebView.getSettings().setPluginState(PluginState.ON_DEMAND);

	        mWebView.getSettings().setLoadWithOverviewMode(true);
//	        mWebView.getSettings().setUseWideViewPort(true);
	        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	
	        if (mItem != null)
	        {
	           SetItem(mItem);
	        }
	//        Bundle args = getArguments();
	//        ((TextView) rootView.findViewById(R.id.label)).setText(
	//                Integer.toString(args.getInt("test")));
	        return rootView;
    	}
    }
    
}
