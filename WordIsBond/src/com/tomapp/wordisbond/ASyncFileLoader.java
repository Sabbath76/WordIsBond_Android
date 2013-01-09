package com.tomapp.wordisbond;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.tomapp.wordisbond.RSSFeed.EFeedType;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

public class ASyncFileLoader extends AsyncTask<Void, Void, RSSFeed>
{
	public final int BUFFER_SIZE = 2024;
	public boolean mNewer 	= false;
	public boolean mItems 	= false;
	public boolean mFeatures = false;
	public RSSFeed mOriginalFeed;
	public EFeedType mFeedType = EFeedType.FEED_TABLET;
	
	public ASyncFileLoader(RSSFeed originalFeed, boolean loadNewer, boolean loadItems, boolean loadFeatures, EFeedType feedType)
	{
		super();

		mOriginalFeed = originalFeed;
		mNewer 		= loadNewer;
		mItems 		= loadItems;
		mFeatures 	= loadFeatures;
		mFeedType   = feedType;
	}

	@Override
 	protected RSSFeed doInBackground(Void... params) 
	{
        RSSFeed feed = new RSSFeed();
        feed.SetFeedType(mFeedType);
        if (mOriginalFeed.getType() == mFeedType)
        {
	        feed.setupFileInfo(mOriginalFeed);
        }
        if (mItems)
        {
	        feed.loadData(true, mNewer);
        }
        if (mFeatures)
        {
	        feed.loadData(false, mNewer);
        }
        if ((feed.getItemCount() == 0) && (feed.getFeatureCount() == 0))
        {
        	feed = null;
        }
        
        return feed;
/*
		String filename = params[0];
		
        String packageName = ItemListActivity.main.getPackageName();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/" + packageName + "/files/";
        String state = Environment.getExternalStorageState();
        final String fname = filename;
        if (Environment.MEDIA_MOUNTED.equals(state)) 
        {
            try 
            {
            	File pathPattern = new File(path);
            	FilenameFilter filter = new FilenameFilter() 
            	{
            		public boolean accept(File directory, String fileName) 
            		{
            		    return fileName.contains("_") && fileName.startsWith(fname);
            		}
            	};
            	File[] fileList = pathPattern.listFiles(filter);
                boolean exists = (new File(path)).exists();
                if (fileList.length == 0) 
                {
                	return null;
                }
                // Open input stream
//                FileInputStream fIn = new FileInputStream(path + filename);
                FileInputStream fIn = new FileInputStream(fileList[fileList.length-1]);
                BufferedInputStream bis = new BufferedInputStream(fIn, BUFFER_SIZE); 
                ObjectInputStream ois = new ObjectInputStream(bis);

                RSSFeed feed = new RSSFeed();
                feed.LoadItems(ois);
                fIn.close();
                
                return feed;
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        return null;
*/	}
	
    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(RSSFeed feed) 
    {
    	ItemListActivity.main.OnNewItems(feed, mItems, mFeatures);
    }
	
}
