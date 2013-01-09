package com.tomapp.wordisbond;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.tomapp.wordisbond.RSSFeed.EFeedType;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class WordIsBoneService extends Service implements Runnable
{
    protected boolean mRunning;
    private static final String TAG = "RSSReaderService";
    private final IBinder mBinder = new LocalBinder();
//    private RSSFeed mFeed;
    private Messenger mMessenger;
    
    final Messenger mMessengerIncoming = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.


//    public final String RSSFEEDOFCHOICE = "http://feeds.feedburner.com/thewordisbond?format=xml";
//	public final String RSSFEEDOFCHOICE = "http://www.thewordisbond.com/feed/mobile/?format=xml";
	public final String RSSFEEDOFCHOICE = "http://www.thewordisbond.com/feed/tablet/?format=xml";
	//public final String RSSFEEDOFCHOICE = "http://www.engadget.com/rss.xml";
	
//	public final String FILENAME = "feed_historyMobile";
	public final String FILENAME = "feed_historyTablet";
//	public final String FILENAME = "feed_history7";
	public final int BUFFER_SIZE = 2024;

	private SAXParser  mParser;
	private XMLReader  mXMLReader;
	private RSSHandler mRssHandler;
	private RSSFeed	   mRssFeed;

	public void OnMessage(int message)
	{
        String packageName = getPackageName();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/" + packageName + "/files/";
        
		switch (message)
		{
	        case 0:
	        {
	        	File from = new File(path,FILENAME);
	        	File to = new File(path,"bak_"+FILENAME);
	        	
	        	from.renameTo(to);
	
	            break;
	        }
	        case 1:
	        {
	        	File from = new File(path,"bak_"+FILENAME);
	        	File to = new File(path,FILENAME);
	        	
	        	from.renameTo(to);
	
	            break;
	        }
		}
		
	}
    class IncomingHandler extends Handler 
    { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) 
        {
            String packageName = getPackageName();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/" + packageName + "/files/";

            switch (msg.what) 
            {
            case 0:
            {
            	File from = new File(path,FILENAME);
            	File to = new File(path,"bak_"+FILENAME);
            	
            	from.renameTo(to);

                break;
            }
            case 1:
            {
            	File from = new File(path,"bak_"+FILENAME);
            	File to = new File(path,FILENAME);
            	
            	from.renameTo(to);

                break;
            }
            default:
                super.handleMessage(msg);
            }
        }
    }

    
    public class LocalBinder extends Binder 
    {
    	WordIsBoneService getService() 
        {
            return WordIsBoneService.this;
        }
    }

    @Override
    public void onCreate()
    {
            Log.d(TAG, "onCreate");

            Thread t = new Thread(null, this, "WordIsBond_Service");
            t.start();
            
            mRunning = true;

    }
    
    
    public void onStart(int startId, Bundle args)
    {
            Log.d(TAG, "onStart(" + startId + ")");
            
    }
    
    @Override
    public void onDestroy()
    {
            /* TODO: Do something? */
            Log.d(TAG, "onDestroy");
    }
    
    public void run()
    {
            Log.d(TAG, "Doing some work, look at me!");

    // Normally we would do some work here...  for our sample, we will
    // just sleep for 4 seconds.
//    long endTime = System.currentTimeMillis() + 4*1000;
        SetupFeed();

        int lastFeedSize = 0;
        if (loadFile(FILENAME, mRssFeed))
        {
    		Message msg = Message.obtain();
    		lastFeedSize = mRssFeed.getItemCount();
    	    msg.arg1 = lastFeedSize;
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
        
	    while (true)//System.currentTimeMillis() < endTime) 
	    {
//	        synchronized (mBinder) 
	        {
	            try 
	            {
	            	updateFeed(RSSFEEDOFCHOICE);
	            	
	            	if (mRssFeed != null)
	            	{
	            		int numNewItems = mRssFeed.getItemCount() - lastFeedSize;
            			lastFeedSize = mRssFeed.getItemCount();
	            		boolean isNew = (numNewItems != 0);

	            		if (isNew)
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

	            			NotificationManager notificationManager = (NotificationManager) 
	            					  getSystemService(NOTIFICATION_SERVICE); 
	            			
	            			int icon = R.drawable.logo;
	            			CharSequence tickerText = "New RSS Feed Recieved";
	            			long when = System.currentTimeMillis();

	            			Notification notification = new Notification(icon, tickerText, when); 
	            			
	            			Context context = getApplicationContext();
	            			CharSequence contentTitle = "Word Is Bond";
	            			CharSequence contentText = "New RSS Feed Received - " + numNewItems + " new posts";
//	            			for (int i=0; i<numNewItems; i++)
//	            			{
//	            				contentText = contentText + "\n" + mRssFeed.getItem(i).getTitle();
//	            			}
//	            			Toast.makeText(this, contentText, 2000).show();
	            			
	            			Intent notificationIntent = new Intent(this, ItemListActivity.class);
	            			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

	            			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	            			
	            			final int HELLO_ID = 1;

	            			notificationManager.notify(HELLO_ID, notification);
	            			
		            		saveFile(FILENAME, mRssFeed);

	            		}
	            		
	            		//--- Now save out feed history
//	            		String string = "hello world!";
	            		

//	            		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//	            		mFeed.SaveTo(fos);
//	            		fos.close();
	            	}
	
	            	android.os.SystemClock.sleep(30 * 1000);
	            } 
	            catch (Exception e) 
	            {
	            }
	        }
	    }
    
//	    Log.d(TAG, "Finished, sigh...");
    
            /* Done synchronizing, stop our service.  We will be called up again
             * at our next scheduled interval... */
 //           this.stopSelf();
            
 //           mRunning = false;
    }

	public boolean loadFile(String filename, RSSFeed feed) 
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
                	return false;
                }
                // Open input stream
                FileInputStream fIn = new FileInputStream(path + filename);
                BufferedInputStream bis = new BufferedInputStream(fIn, BUFFER_SIZE); 
                ObjectInputStream ois = new ObjectInputStream(bis);

                feed.LoadFrom(ois);
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
                // Open output stream
                FileOutputStream fOut = new FileOutputStream(path + filename);
                BufferedOutputStream bos = new BufferedOutputStream(fOut, BUFFER_SIZE);   
                ObjectOutputStream oos = new ObjectOutputStream(bos);

                // write integers as separated ascii's
                feed.SaveTo(oos);
//                fOut.write((Integer.valueOf(content).toString() + " ").getBytes());
//                fOut.write((Integer.valueOf(content).toString() + " ").getBytes());
                // Close output stream
                oos.flush();
                fOut.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }

	
	@Override
	public IBinder onBind(Intent intent) 
	{
	    Bundle extras = intent.getExtras();
	    if (extras != null) 
	    {
	    	mMessenger = (Messenger) extras.get("MESSENGER");
	    }
		return mBinder;
	}
	
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
	        mRssHandler = new RSSHandler(0, EFeedType.FEED_TABLET);
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


/*           // create the factory
           SAXParserFactory factory = SAXParserFactory.newInstance();
           // create a parser
           SAXParser parser = factory.newSAXParser();

           // create the reader (scanner)
           XMLReader xmlreader = parser.getXMLReader();
           // instantiate our handler
           RSSHandler theRssHandler = new RSSHandler();
           // assign our handler
           xmlreader.setContentHandler(theRssHandler);
           // get our data via the url class
           InputSource is = new InputSource(url.openStream());

           // perform the synchronous parse           
           xmlreader.parse(is);
           // get the results - should be a fully populated RSSFeed instance, or null on error
           return theRssHandler.getFeed();
*/    	}
    	catch (Exception ee)
    	{
    		// if we have a problem, simply return null
    	}
    }


}
