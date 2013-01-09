package com.tomapp.wordisbond;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

class bitmapDownloader extends AsyncTask<String, Void, Bitmap> {
    private String mURL;
    private final WeakReference<RSSItem> mRSSItem;

    public bitmapDownloader(RSSItem rssItem) {
        mRSSItem = new WeakReference<RSSItem>(rssItem);
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
         // params comes from the execute() call: params[0] is the url.
         return downloadBitmap(params[0]);
    }

    Bitmap downloadBitmap(String URL)
	{
    	Bitmap ret = null;
		URL url = null;
		try
		{
			url = new URL(URL);
		}
		catch(MalformedURLException e)
        {

            e.printStackTrace();
        }
		if (url != null)
		{
			try
			{
				ret = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			}
			catch(IOException e)
	        {
	
	            e.printStackTrace();
	        }
		}
		
		return ret;
	}


    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (mRSSItem != null) 
        {
//        	mRSSItem.get().setBitmap(bitmap);
        }
    }
}