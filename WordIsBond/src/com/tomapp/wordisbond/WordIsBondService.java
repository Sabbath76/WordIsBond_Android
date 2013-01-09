package com.tomapp.wordisbond;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.tomapp.wordisbond.RSSFeed.EFeedType;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class WordIsBondService extends IntentService
{
	protected boolean mRunning;
	protected boolean mForceUpdate = false;
    private static final String TAG = "RSSReaderService";

//    public final String RSSFEEDOFCHOICE = "http://feeds.feedburner.com/thewordisbond?format=xml";
///	public final String RSSFEEDOFCHOICE = "http://www.thewordisbond.com/feed/mobile/?format=xml";
//	public final String RSSFEEDOFCHOICE = "http://www.thewordisbond.com/feed/tablet/?format=xml";
	//public final String RSSFEEDOFCHOICE = "http://www.engadget.com/rss.xml";
	
//	public final int SPLIT_SIZE    = 20;
	public final int FEED_FEATURES = 5;
//	public final String FILENAME = "feed_historyMobile";
///	public final String FILENAME = "feed_historyMobC";
//	public static final String FILENAME = "feed_historyTabletAB";
//	public final int BUFFER_SIZE = 2024;
	
	public EFeedType mFeedType = EFeedType.FEED_TABLET;
	
//	private int mNumFiles = 0;

    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";

	private SAXParser  mParser;
	private XMLReader  mXMLReader;
	private RSSHandler mRssHandler;
	private RSSFeed	   mRssFeed;
	
	public static final String[] TESTXMLS =
		{
		"thewordisbond.xml",
		"thewordisbond (1).xml",
		"thewordisbond (2).xml",
		"thewordisbond (3).xml",
		"thewordisbond (4).xml",
		"thewordisbond (5).xml"
		};

	/*
    private Messenger mMessenger;
    private final IBinder mBinder = new LocalBinder();

	private List<RSSServiceListener> mListeners = new ArrayList<RSSServiceListener>();

    private RemoteService.Stub myRemoteServiceStub = new RemoteService.Stub() 
    {
		public void TriggerParse() throws RemoteException 
		{
		}
		
		public RSSFeedData GetLatestResult()
		{
			return mRssFeed.extractData();
		}

		public void RegisterListener(RSSServiceListener listener) throws RemoteException 
		{
			synchronized (mListeners) 
			{
				mListeners.add(listener);
			}
			mForceUpdate = true;
//			OnUpdateFeed();
		}

		public void UnregisterListener(RSSServiceListener listener) throws RemoteException 
		{
			synchronized (mListeners) 
			{
				mListeners.remove(listener);
			}
		}
    };
      
    public class LocalBinder extends Binder 
    {
    	WordIsBondService getService() 
        {
            return WordIsBondService.this;
        }
    }
*/
    public WordIsBondService() 
    {
		super("WordIsBondService");
	}
    
    @Override
    public void onStart(Intent intent, int startId) 
    {
       super.onStart(intent, startId);
    }
    
	@Override
	protected void onHandleIntent(Intent intent) 
	{
//        String msg = intent.getStringExtra(PARAM_IN_MSG);
        
        ResultReceiver rr = (ResultReceiver) intent.getParcelableExtra("resultreceiver");
        int newFeed = intent.getIntExtra("feedType", EFeedType.FEED_TABLET.ordinal());
        mFeedType = EFeedType.values()[newFeed];
        
//        Debug.waitForDebugger();
        
        OnUpdateFeed(rr);
	}
	
/*	@Override
	public IBinder onBind(Intent intent) 
	{
        Log.d(getClass().getSimpleName(), "onBind()");
        return myRemoteServiceStub;
//	    Bundle extras = intent.getExtras();
//	    if (extras != null) 
//	    {
//	    	mMessenger = (Messenger) extras.get("MESSENGER");
//	    	
//	    	mForceUpdate = !extras.getBoolean("ReTrigger");
//	    }
//		return mBinder;
	}
*/	
	public void OnUpdateFeed(ResultReceiver rr)
	{
		Toast.makeText(this, "OnUpdateFeed", Toast.LENGTH_SHORT).show();

        SetupFeed();

        mRssFeed.loadData(true, true);
        mRssFeed.loadData(false, true);
    	int lastItemCount = mRssFeed.getItemCount();
    	int lastFeatureCount = mRssFeed.getFeatureCount();
		
        try
        {
        	updateFeed(mRssFeed.getURL());
        	
        	if (mRssFeed != null)
        	{
        		int numNewItems = mRssFeed.getItemCount() - lastItemCount;
        		int numNewFeatures = mRssFeed.getFeatureCount() - lastFeatureCount;
        		boolean hasNewItems = (numNewItems != 0);
        		boolean hasNewFeatures = (numNewFeatures != 0);

        		if (hasNewItems || hasNewFeatures || mForceUpdate)
        		{
        			mForceUpdate = false;

/*    				synchronized (mListeners) 
    				{
    					for (RSSServiceListener listener : mListeners) 
    					{
    						try 
    						{
    							listener.onNewFeed(mRssFeed.getPubDate());
    						} 
    						catch (RemoteException e) 
    						{
    							Log.w(TAG, "Failed to notify listener " + listener, e);
    						}
    					}
    				}
    				        			
    				if (mMessenger != null)
    				{
	            		Message msg = Message.obtain();
	            	    msg.arg1 = numNewItems;
	            	    msg.obj = mRssFeed;
	            	    
	            	    try 
	            	    {
	            	        mMessenger.send(msg);
	            	    } 
	            	    catch (android.os.RemoteException e1) 
	            	    {
	            	      Log.w(getClass().getName(), "Exception sending message", e1);
	            	    }
    				}
*/
            	    if (hasNewItems || hasNewFeatures)
            	    {
	        			NotificationManager notificationManager = (NotificationManager) 
	        					  getSystemService(NOTIFICATION_SERVICE); 
	        			
	        			int icon = R.drawable.logo;
	        			CharSequence tickerText = "New RSS Feed Recieved";
	        			long when = System.currentTimeMillis();
	
	        			Notification notification = new Notification(icon, tickerText, when); 
	        			
	        			Context context = getApplicationContext();
	        			CharSequence contentTitle = "Word Is Bond";
	        			CharSequence contentText = "New RSS Feed Received - " + numNewItems + " new posts";
	        			if (numNewFeatures > 0)
	        			{
	        				String newFeatures = " " + numNewFeatures + " new features";
	        				contentText = contentText + newFeatures;
	        			}
	//        			for (int i=0; i<numNewItems; i++)
	//        			{
	//        				contentText = contentText + "\n" + mRssFeed.getItem(i).getTitle();
	//        			}
	        			Toast.makeText(this, contentText, Toast.LENGTH_SHORT).show();
	        			
	        			Intent notificationIntent = new Intent(this, ItemListActivity.class);
	        			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	
	        			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	        			
	        			final int HELLO_ID = 1;
	
	        			notificationManager.notify(HELLO_ID, notification);

	        			if (hasNewItems)
	        			{
	        				mRssFeed.saveFile(true);
	        			}
	        			if (hasNewFeatures)
	        			{
	        				mRssFeed.saveFile(false);
	        			}
            	    }
        		}
        	}
        } 
        catch (Exception e) 
        {
        }
        
		if (rr != null)
		{
			Bundle resultData = new Bundle();
			
//			RSSFeedData feedData = mRssFeed.extractData();
//			resultData.putParcelable("feed", feedData);
			
			rr.send(1, resultData);
		}
	}

/*
 	public boolean loadFile(String filename, RSSFeed feed) 
    {
        String packageName = this.getPackageName();
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
            		    return fileName.startsWith(fname);
            		}
            	};
            	File[] fileList = pathPattern.listFiles(filter);
                boolean exists = (new File(path)).exists();
                mNumFiles = fileList.length;
                if (fileList.length == 0) 
                {
                	return false;
                }
                // Open input stream
//                FileInputStream fIn = new FileInputStream(path + filename);
                File file = fileList[fileList.length-1];
                FileInputStream fIn = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fIn, BUFFER_SIZE); 
                ObjectInputStream ois = new ObjectInputStream(bis);

                if (file.getName().contains("_"))
                {
                	feed.LoadItems(ois);
                }
                else
                {
                	feed.LoadFrom(ois);
                }
                fIn.close();
                
                return true;
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        return false;
    }


    public void saveFile(String filename, RSSFeed feed) 
    {
        String packageName = this.getPackageName();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/" + packageName + "/files/";
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) 
        {
            try 
            {
                boolean exists = (new File(path)).exists();
                if (!exists) 
                {
                    new File(path).mkdirs();
                }
                int numItems = feed.getItemCount();
                int numRecords = Math.max(numItems / SPLIT_SIZE, 1);
                int currentItem = numItems;
                for (int i=0; i<numRecords; i++)
                {
	                // Open output stream
                	String newFilename = String.format("%s_%03d", path + filename, mNumFiles);
	                FileOutputStream fOut = new FileOutputStream(newFilename);
	                BufferedOutputStream bos = new BufferedOutputStream(fOut, BUFFER_SIZE);   
	                ObjectOutputStream oos = new ObjectOutputStream(bos);
	
	                // write integers as separated ascii's
	                int from = (i==(numRecords-1)) ? 0 : currentItem - SPLIT_SIZE;
	                feed.SaveItems(oos, from, currentItem);
	
	                // Close output stream
	                oos.flush();
	                fOut.close();
	                
	                currentItem -= SPLIT_SIZE;
	                mNumFiles++;
                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
*/
	private void SetupFeed()
	{
    	try
    	{
	        // create the factory
	        SAXParserFactory factory = SAXParserFactory.newInstance();
	        // create a parser
	        mParser = factory.newSAXParser();
	
	        // create the reader (scanner)
	        mXMLReader = mParser.getXMLReader();
	        // instantiate our handler
	        mRssHandler = new RSSHandler(FEED_FEATURES, mFeedType);
	        mRssHandler.startDocument();
	        // assign our handler
	        mXMLReader.setContentHandler(mRssHandler);
	        
	        mRssFeed = mRssHandler.getFeed();
    	}
    	catch (Exception ee)
    	{
    	}		
	}
	
    private void updateFeed(String urlToRssFeed)
    {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
    	
    	try
    	{
    		
        	if (mRssFeed.getType() == EFeedType.FEED_TEST)
        	{
        		AssetManager mgr = getAssets();
        		for (String filename : TESTXMLS)
        		{
            		InputStream istream = mgr.open(filename);
            		InputSource is = new InputSource(istream);
                    try
                    {
                    	mXMLReader.parse(is);
                    }
                    catch (BreakParsingException e)
                    {
                 	   //--- is fine
                    }        			
        		}
        	}
        	else
        	{
	    		// setup the url
        		URL url = new URL(urlToRssFeed);
        		InputSource is = new InputSource(url.openStream());

                try
                {
     	           // perform the synchronous parse           
     	           mXMLReader.parse(is);
                }
                catch (BreakParsingException e)
                {
             	   //--- is fine
                }
}
    	}
    	catch (Exception ee)
    	{
    		// if we have a problem, simply return null
    	}
    }

}
