package com.tomapp.wordisbond;

import com.tomapp.wordisbond.RSSFeed.EFeedType;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.VideoView;

public class ItemListActivity extends FragmentActivity
        implements OnItemClickListener, OnItemSelectedListener, OnTouchListener
{

	//public final String RSSFEEDOFCHOICE = "http://www.ibm.com/developerworks/views/rss/customrssatom.jsp?zone_by=XML&zone_by=Java&zone_by=Rational&zone_by=Linux&zone_by=Open+source&zone_by=WebSphere&type_by=Tutorials&search_by=&day=1&month=06&year=2007&max_entries=20&feed_by=rss&isGUI=true&Submit.x=48&Submit.y=14";
	//public final String RSSFEEDOFCHOICE = "http://www.engadget.com/rss.xml";
//	public final String RSSFEEDOFCHOICE = "http://feeds.feedburner.com/thewordisbond?format=xml";
//	public final String RSSFEEDOFCHOICE = "http://www.thewordisbond.com/feed/mobile/?format=xml";
	//public final String RSSFEEDOFCHOICE = "http://feeds.feedburner.com/thewordisbond?format=xml";

    SwipePagerAdapter mSwipePagerAdapter;
    RSSArrayAdapter mArrayAdapter;
    FeatureAdapter mFeatureAdapter;
    ViewPager mViewPager;
	public RSSFeed mFeed = null;
	public int mSelectedItem = 0;
	public boolean mIsInstalled = false;
	public boolean mAutoPlaying = false;
	public boolean mIsLandscape = false;
	public boolean mFullscreen = true;
	public int mAutoplayItem = 0;
	public RSSFeed.EFeedType mFeedType = EFeedType.FEED_TABLET;
	
	public ImageManager mImageManager = null;
	
	private MediaPlayer mMediaPlayer = null;
	private View mRootView = null;
	
	public RSSItem mCurrentAudio = null;

	public static ItemListActivity main = null;
	
	public VideoView mVideoView = null;
	
    public ImageDownloader imageDownloader = null;

	
	public boolean isFullScreen()
	{
		return mFullscreen;
	}
    
	public void StartMainApp()
	{
     	mIsInstalled = true;
     	
     	setContentView(R.layout.switchview);
     	
     	mRootView = findViewById(R.layout.switchview);
    	
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSwipePagerAdapter);
      	mViewPager.setCurrentItem(1, true);
      	
      	mArrayAdapter.UpdateFeed(mFeed);
      	
        mArrayAdapter.notifyDataSetChanged();
        mFeatureAdapter.forceRefresh();
        mSwipePagerAdapter.notifyDataSetChanged();
	}

	public void UpdateMainApp()
	{
      	mArrayAdapter.UpdateFeed(mFeed);
      	
        mArrayAdapter.notifyDataSetChanged();
        mFeatureAdapter.forceRefresh();
        mSwipePagerAdapter.notifyDataSetChanged();
	}
	
	class SavedState
	{
		public RSSFeed mFeed		= null;
		public RSSItem mMediaItem	= null;
		public MediaPlayer mMediaPlayer = null;
		public ImageManager mImageManager = null;
		public boolean mIsPlaying = false;
		public int mSelectedItem;
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() 
	{
		final SavedState data = new SavedState();
		data.mFeed = mFeed;
		data.mMediaItem = mCurrentAudio;
		data.mMediaPlayer = mMediaPlayer;
		data.mImageManager = mImageManager;
		data.mIsPlaying = mAutoPlaying;
		data.mSelectedItem = mSelectedItem;

	    return data;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	super.onCreateOptionsMenu(menu);
    	
      	menu.add(0,0,0, "Remove RSS File");
    	menu.add(0,1,1, "Restore RSS File");

    	return true;
    }
    
    private void sendMessageToService(int intvaluetosend) 
    {
        if (mIsBound) 
        {
        	mBoundService.OnMessage(intvaluetosend);
        }
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) 
        {
        case 0:
        	sendMessageToService(0);
            return true;
        case 1:
        	sendMessageToService(0);
            return true;
        }
        return false;
    }

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageDownloader = new ImageDownloader(this);
        
        AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent msgAlarmIntent = new Intent(this, WIBAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, msgAlarmIntent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, 1000 * 60 * 60, pi);       
        
		SavedState data = (SavedState)getLastCustomNonConfigurationInstance();
     	if (data == null)
     	{
            mFeed = new RSSFeed();
            mImageManager = new ImageManager(this);
     	}
     	else
     	{
     		mFeed = data.mFeed;
     		mCurrentAudio = data.mMediaItem;
     		mAutoPlaying = data.mIsPlaying;
    		mSelectedItem = data.mSelectedItem;
            mImageManager = data.mImageManager;
            mFeedType = mFeed.getType();
     	}
     	
        mSwipePagerAdapter = new SwipePagerAdapter(getSupportFragmentManager());
        mArrayAdapter = new RSSArrayAdapter(this, mFeed.getAllItems());
        mFeatureAdapter = new FeatureAdapter(this, mFeed);
        mSwipePagerAdapter.Initialise(mFeed, mArrayAdapter, mFeatureAdapter);

        mFullscreen = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        boolean customTitleSupported = false;
     	if (mFullscreen)
     	{
     		requestWindowFeature(Window.FEATURE_NO_TITLE);
     	}
     	else
     	{
         	customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
     	}

     	if (data == null)
     	{
	     	setContentView(R.layout.loadingscreen);

	        ImageView imageView = (ImageView) findViewById(R.id.spinner);
	        if (imageView != null)
	        {
				AnimationDrawable ani = (AnimationDrawable)imageView.getDrawable();
				ani.start();	
	        }
     	}
     	else
     	{
    		mIsInstalled = true;
     		StartMainApp();
     		mSwipePagerAdapter.refreshFragments(getSupportFragmentManager(), mViewPager);
	        mMediaPlayer = data.mMediaPlayer;
	        
			int itemIdx = mArrayAdapter.getType(mSelectedItem).itemIdx;

	        mSwipePagerAdapter.SetSelectedItem(itemIdx);
     	}

        if ( customTitleSupported ) 
        {
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        	ImageButton topPlay = (ImageButton)getWindow().findViewById(R.id.topPlay);
        	if (topPlay != null)
        	{
        		topPlay.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) 
					{
						// TODO Auto-generated method stub
						onToggleAutoPlay();
					}
				});
        	}
        	ImageButton topFF = (ImageButton)getWindow().findViewById(R.id.topFastForwards);
        	if (topFF != null)
        	{
        		topFF.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) 
					{
						onNextAutoPlay();
					}
				});
        	}
        	ImageButton topRW = (ImageButton)getWindow().findViewById(R.id.topRewind);
        	if (topRW != null)
        	{
        		topRW.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) 
					{
						onPrevAutoPlay();
					}
				});
        	}
        	ImageButton topOptions = (ImageButton)getWindow().findViewById(R.id.options);
        	if (topOptions != null)
        	{
        		topOptions.setOnClickListener(new View.OnClickListener() 
        		{					
					public void onClick(View v) 
					{
						onOptions(v);
					}
				});
        	}
        }
        
        doBindService();

        main = this;

        if (mMediaPlayer == null)
        {
	        mMediaPlayer = new MediaPlayer();
	        mMediaPlayer.setOnErrorListener(new OnErrorListener()
	        {
	        	public boolean onError(MediaPlayer mp, int what, int extra)
	        	{
	                ItemListActivity.main.OnMediaError();
	        		return false;
	        	}
	        }
	        );
	        mMediaPlayer.setOnPreparedListener(new OnPreparedListener() 
	        {
	            public void onPrepared(MediaPlayer mp) {
	            	ItemListActivity.main.OnPrepared();
	            }
	        });
	        mMediaPlayer.setOnCompletionListener(new OnCompletionListener()
	        {
	        	public void onCompletion(MediaPlayer mp)
	        	{
	        		onPlaybackComplete();
		        }
	        });
        }
    }    
   
	protected void onOptions(View v) 
	{
        final PopupMenu popupMenu = new PopupMenu(this, v);

        int itemID = 0;
		switch (mFeedType) 
		{
		case FEED_TABLET:
			itemID = 0;
			break;
		case FEED_MOBILE:
			itemID = 1;
			break;
		case FEED_JAZZ_MAIN:
			itemID = 2;
			break;
		case FEED_TEST:
			itemID = 3;
			break;
		}

        
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, popupMenu.getMenu());
        popupMenu.getMenu().getItem(itemID).setChecked(true);
        
        popupMenu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() 
                {
            public boolean onMenuItemClick(MenuItem item) 
            {
            	EFeedType newFeedType = EFeedType.FEED_TOTAL;
                switch (item.getItemId()) 
                {
                    case R.id.tablet:
                    	newFeedType = EFeedType.FEED_TABLET;
                        break;
                    case R.id.mobile:
                    	newFeedType = EFeedType.FEED_MOBILE;
                        break;
                    case R.id.jazz:
                    	newFeedType = EFeedType.FEED_JAZZ_MAIN;
                        break;
                    case R.id.test:
                    	newFeedType = EFeedType.FEED_TEST;
                        break;
                }
                
                if ((newFeedType != EFeedType.FEED_TOTAL)
                	&& (newFeedType != mFeedType))
                {
                	mFeedType = newFeedType;
                	Intent intent = new Intent(WordIsBondService.class.getName());
                	intent.putExtra("feedType", mFeedType.ordinal());
                	intent.putExtra("resultreceiver", mResultReceiver);
                	startService(intent);
                	
                }
                return true;
            }
        });
        
        popupMenu.show();
	}

	public void onPlaybackComplete()
	{
    	if (mAutoPlaying)
    	{
    		onNextAutoPlay();
    	}
    	else
    	{
			PlayMedia(mCurrentAudio);
    	}
	}
	
	public void onNextAutoPlay()
	{
    	if (mAutoPlaying)
    	{
	    	int totalItems = mFeed.getItemCount();
	    	int nextAudioItem = -1;
	    	for(int i=mAutoplayItem+1; i<totalItems; i++)
	    	{
	    		if (mFeed.getItem(i).GetMediaType() == EMediaType.Media_Audio)
	    		{
	    			nextAudioItem = i;
	    			break;
	    		}
	    	}
	    	if (nextAudioItem == -1)
	    	{
		    	for(int i=0; i<mAutoplayItem+1; i++)
		    	{
		    		if (mFeed.getItem(i).GetMediaType() == EMediaType.Media_Audio)
		    		{
		    			nextAudioItem = i;
		    			break;
		    		}
		    	}
	    	}
	    	
	    	if (nextAudioItem != -1)
	    	{
				PlayMedia(mFeed.getItem(nextAudioItem));
				mAutoplayItem = nextAudioItem;
	    	}
    	}
	}

	public void onPrevAutoPlay()
	{
    	if (mAutoPlaying)
    	{
	    	int totalItems = mFeed.getItemCount();
	    	int nextAudioItem = -1;
	    	for(int i=mAutoplayItem-1; i>=0; i--)
	    	{
	    		if (mFeed.getItem(i).GetMediaType() == EMediaType.Media_Audio)
	    		{
	    			nextAudioItem = i;
	    			break;
	    		}
	    	}
	    	if (nextAudioItem == -1)
	    	{
		    	for(int i=totalItems-1; i>=0; i--)
		    	{
		    		if (mFeed.getItem(i).GetMediaType() == EMediaType.Media_Audio)
		    		{
		    			nextAudioItem = i;
		    			break;
		    		}
		    	}
	    	}
	    	
	    	if (nextAudioItem != -1)
	    	{
				PlayMedia(mFeed.getItem(nextAudioItem));
				mAutoplayItem = nextAudioItem;
	    	}
    	}
	}

    public void onToggleAutoPlay()
    {
		mAutoPlaying = !mAutoPlaying;
    	if (mAutoPlaying)
    	{ 
    		if(mCurrentAudio == null)
    		{
		    	int totalItems = mFeed.getItemCount();
		    	for(int i=0; i<totalItems; i++)
		    	{
		    		if (mFeed.getItem(i).GetMediaType() == EMediaType.Media_Audio)
		    		{
		    			PlayMedia(mFeed.getItem(i));
		    			mAutoplayItem = i;
		    			break;
		    		}
		    	}
    		}
    		else
    		{
    			PlayMedia(mCurrentAudio);
    		}
    	}
    	else
    	{
			StopMusic();
    	}
    }
    
    public void onFeatureSelect(int position)
    {
		mSelectedItem = position;
      	mSwipePagerAdapter.SetSelectedFeature(position);
      	mViewPager.setCurrentItem(2, true);
    }

    public void onItemClick(AdapterView parent, View v, int position, long id)
    {
//    	Log.i(tag,"item clicked! [" + mFeed.getItem(position).getTitle() + "]");

      	RSSArrayAdapter adapter = (RSSArrayAdapter)parent.getAdapter();
      	adapter.OnSelect(position);

		int itemIdx = mArrayAdapter.getType(position).itemIdx;
      	mSwipePagerAdapter.SetSelectedItem(itemIdx);
		mSelectedItem = position;
    }

	public void onItemSelected(AdapterView parent, View v, int position, long id) 
	{
		int itemIdx = mArrayAdapter.getType(position).itemIdx;
		mSwipePagerAdapter.SetSelectedItem(itemIdx);
		mSelectedItem = position;
	}
	
	public void setSelected(int position)
	{
		if (position != mSelectedItem)
		{
			if (position > 0)
			{
				int itemIdx = mArrayAdapter.getType(position).itemIdx;
				mSwipePagerAdapter.SetSelectedItem(itemIdx);
			}
			mSelectedItem = position;
		}
	}
	
	public int getSelected()
	{
		return mSelectedItem;
	}
	
	public void onNothingSelected(AdapterView<?> arg0) 
	{
		// TODO Auto-generated method stub
		
	}   

	public boolean onTouch(View v, MotionEvent event) 
    {
        return true;
    }

    public void OnPrepared()
    {
    	if (mCurrentAudio != null)
    	{
	    	mMediaPlayer.start();
	    	mCurrentAudio.SetMediaState(EMediaState.Playing);
	    	mCurrentAudio.UpdateMediaButton();
	    	mArrayAdapter.notifyDataSetChanged();
    	}
    }

    public void OnMediaError()
    {
    	if (mCurrentAudio != null)
    	{
	    	mCurrentAudio.SetMediaState(EMediaState.None);
	    	mCurrentAudio.UpdateMediaButton();
	    	mArrayAdapter.notifyDataSetChanged();
	    	mCurrentAudio = null;
    	}
    }

    public void StopMusic()
    {
    	if (mCurrentAudio != null)
    	{
    		mCurrentAudio.SetMediaState(EMediaState.None);
    		mCurrentAudio.UpdateMediaButton();
    		mArrayAdapter.notifyDataSetChanged();
    	}
    	mMediaPlayer.stop();
    	mMediaPlayer.reset();
		mAutoPlaying = false;
    }
    
    public boolean ToggleMedia(RSSItem item)
    {    	
    	if ((mCurrentAudio == item) && mAutoPlaying)
    	{
    		StopMusic();
    	}
    	else
    	{
	    	mAutoPlaying = PlayMedia(item);
	    	if (mAutoPlaying)
	    	{
		    	mAutoplayItem = mFeed.findItemIndex(item);
	    	}
    	}
    	
    	return mAutoPlaying;
    }
    
    public boolean PlayMedia(RSSItem item)
    {
    	if ((mCurrentAudio != null) && (mCurrentAudio.GetMediaState() != EMediaState.None))
    	{
    		mCurrentAudio.SetMediaState(EMediaState.None);
    		mCurrentAudio.UpdateMediaButton();
    		mArrayAdapter.notifyDataSetChanged();
        	mMediaPlayer.stop();
        	mMediaPlayer.reset();
    	}
    	
/*    	if (mCurrentAudio == item)
    	{
    		mCurrentAudio = null;
    		return false;
    	}
    	else
*/    	{
	    	String mediaSource = item.getMediaSource();
	    	if (item.GetMediaType() == EMediaType.Media_Video)
	    	{
//		    	mCurrentAudio.SetMediaState(EMediaState.Playing);
	    		mSwipePagerAdapter.mVideoFragment.SetItem(item);
	          	mViewPager.setCurrentItem(0, true);
	          	
		    	return false;
	    	}
	    	else
	    	{
		    	mCurrentAudio = item;
		    	
	            Toast.makeText(ItemListActivity.this, mCurrentAudio.getTitle(), Toast.LENGTH_SHORT).show();

		    	mCurrentAudio.SetMediaState(EMediaState.Streaming);
	    		mCurrentAudio.UpdateMediaButton();
	    		mArrayAdapter.notifyDataSetChanged();
	    		
		    	return PlayMusic(mediaSource);
	    	}
    	}
    }
    
    public boolean PlayVideo(String source)
    {
		try
		{
//	        String url = "http://50.30.41.45/yt/f/df/wi_piti_piti_reg_78066.3gp";
	        String url = "http://www.law.duke.edu/cspd/contest/finalists/viewentry.php?file=docandyou";
			Uri uri = Uri.parse(url);

			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 

			View popView = inflater.inflate(R.layout.videoplayback, null, false);
			final PopupWindow popup = new PopupWindow(popView,100,100, true);
	        popup.setContentView(popView);
	        VideoView popVideo = (VideoView)popView.findViewById(R.id.videoView);
	        popVideo.setZOrderOnTop(true);
	        popup.setOutsideTouchable(false);
	        
	        mVideoView = popVideo;

			Button quitVid = (Button) popView.findViewById(R.id.close);
			quitVid.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					popup.dismiss();
				}
			});


//			VideoView video = (VideoView)findViewById(R.id.VideoView01);
//			Log.d(tag , urlVideo);
//	        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/media/videos/The Moomins (en26) 01 Spring in Moominvalley.avi";
	        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/DCIM/Camera/VID_20120817_193917.3pg";

//	        popVideo.setVideoPath(path);
	        popVideo.setVideoURI(uri);
			MediaController mc = new MediaController(this);
			popVideo.setMediaController(mc);
			popVideo.requestFocus();
			popVideo.start();
			mc.show();

			View rootView = this.mViewPager.findViewById(R.id.pager);
	        popup.showAtLocation(rootView, Gravity.BOTTOM, 10, 10);
	        popup.update(50, 50, 300, 80);

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
//			Log.i("RSSReader","characters[" + e.printStackTrace() + "]");
		}

		return false;
    }
    
    @Override
    protected void onPause()
    {
        Log.v("MediaVideo", "onPause");
        super.onPause();
        if (mVideoView != null)
        {
	        this.mVideoView.pause();
	        this.mVideoView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume()
    {
        Log.v("MediaVideo", "onResume");
        super.onResume();
        if (mVideoView != null)
        {
	        this.mVideoView.resume();
        }
    }
      
    public boolean PlayMusic(String source)
    {
		try
		{
			Uri uri = Uri.parse(source);

			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource(this, uri);
			mMediaPlayer.prepareAsync();
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
//			Log.i("RSSReader","characters[" + e.printStackTrace() + "]");
		}
		
		return false;
    }
    
    public void updateFullscreenStatus(boolean bUseFullscreen)
    {   
    	if (mFullscreen != bUseFullscreen)
    	{
    		mFullscreen = bUseFullscreen;
	       if(bUseFullscreen)
	       {
	            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	        }
	        else
	        {
	            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        }
	
	        mRootView.requestLayout();
    	}
    }
    
    private WordIsBoneService mBoundService;
    boolean mIsBound = false;
    Messenger mMessenger = null;
    Messenger mService = null;

    private ServiceConnection mConnection = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.

///        	mBoundService = ((WordIsBoneService.LocalBinder)service).getService();

//            mService = new Messenger(service);

            // Tell the user about this for our demo.
            Toast.makeText(ItemListActivity.this, "Local Service Connected",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(ItemListActivity.this, "Local Service Disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private static Handler handler = new Handler() 
    {
        public void handleMessage(Message message) 
        {
        	RSSFeed rssFeed = (RSSFeed)message.obj;
        	int numNewItems = message.arg1;
        	if (main.mIsInstalled == false)
        	{
        		main.mIsInstalled = true;
        		main.mFeed.MergeFeed(rssFeed, rssFeed.getItemCount());
//        		main.mFeed.UpdateFeatures("Audio");
        		main.StartMainApp();
        	}
        	else
        	{
        		main.mFeed.MergeFeed(rssFeed, numNewItems);
//        		main.mFeed.UpdateFeatures("Audio");
        		main.UpdateMainApp();
        	}
        	
/*        	int features = main.mFeed.getFeatureCount();
        	for (int i=0; i<features; i++)
        	{
        		main.mFeed.getFeature(i).loadImage();
        	}
*/	        	
//			Object path = message.obj;
//			if (message.arg1 == RESULT_OK && path != null) 
//			{
//				Toast.makeText(ItemListActivity.this, "Downloaded" + path.toString(), Toast.LENGTH_LONG).show();
//			} 
//			else 
//			{
//			    Toast.makeText(ItemListActivity.this, "Download failed.", Toast.LENGTH_LONG).show();
//			}

        };
      };
      

/*      class RemoteServiceConnection implements ServiceConnection 
      {
          public void onServiceConnected(ComponentName className, IBinder boundService ) 
          {
        	  remoteService = RemoteService.Stub.asInterface((IBinder)boundService);
        	  try 
        	  {
				remoteService.RegisterListener(mServiceListener);
        	  } 
        	  catch (RemoteException e) 
        	  {
				e.printStackTrace();
        	  }
              Log.d( getClass().getSimpleName(), "onServiceConnected()" );
          }

          public void onServiceDisconnected(ComponentName className) 
          {
        	  if (remoteService != null)
    		  {
        		  try 
        		  {
					remoteService.UnregisterListener(mServiceListener);
        		  } 
        		  catch (RemoteException e) 
        		  {
					e.printStackTrace();
        		  }
    		  }
        	  remoteService = null;

              Log.d( getClass().getSimpleName(), "onServiceDisconnected" );
          }
    };
    */
/*	private RSSServiceListener.Stub mServiceListener = new RSSServiceListener.Stub() 
	{
		public void onNewFeed(String pubDate) throws RemoteException 
		{
			if ((mFeed.getPubDate().compareToIgnoreCase(pubDate) == 0)
				&& (remoteService != null))
			{
				RSSFeedData feedData = remoteService.GetLatestResult();
	        	if (main.mIsInstalled == false)
	        	{
	        		main.mIsInstalled = true;
	        		main.mFeed.MergeFeed(feedData);
	        		main.StartMainApp();
	        	}
	        	else
	        	{
	        		main.mFeed.MergeFeed(feedData);
	        		main.UpdateMainApp();
	        	}
			}
		}
	};
*/	
///    RemoteService remoteService;
///    RemoteServiceConnection remoteServiceConnnection;
      
      public void OnNewItems(RSSFeed feed, boolean items, boolean features)
      {
    	mFeed.SetFeedType(mFeedType);
    	if (items)
    	{
	    	mFeed.UpdateItems(feed);
    	}
    	if (features)
    	{
	    	mFeed.UpdateFeatures(feed);
    	}

      	if (mIsInstalled == false)
      	{
      		mIsInstalled = true;
      		StartMainApp();
      	}
      	else
      	{
      		UpdateMainApp();
      	}
      }
    
	  private class NewFeedHandler implements Runnable 
	  {
		  RSSFeedData mFeedData;
		  
		  public NewFeedHandler(RSSFeedData feedData)
		  {
			  mFeedData = feedData;
		  }
		  
		  public void run()
		  {
      		mFeed.MergeFeed(mFeedData);
        	if (mIsInstalled == false)
        	{
        		mIsInstalled = true;
        		StartMainApp();
        	}
        	else
        	{
        		UpdateMainApp();
        	}
		  }
	  }

	 public ASyncFileLoader mFileLoader;
    private ResultReceiver mResultReceiver = new ResultReceiver(null)
    {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) 
        {
        	mFileLoader = new ASyncFileLoader(mFeed, true, true, true, mFeedType);
        	mFileLoader.execute();
 /*       	
        	
        	resultData.setClassLoader(RSSFeedData.class.getClassLoader());
        	RSSFeedData feedData = (RSSFeedData)resultData.getParcelable("feed");
        	
        	if (feedData != null)
        	{
        		NewFeedHandler newFeedHandler = new NewFeedHandler(feedData);
        		runOnUiThread(newFeedHandler);
        	}
*/        }
    };

    void doBindService() 
    {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
    	
    	
///    	remoteServiceConnnection = new RemoteServiceConnection();
//    	Intent intent = new Intent(ItemListActivity.this, WordIsBondService.class);
    	Intent intent = new Intent(WordIsBondService.class.getName());
    	intent.putExtra("FeedType", mFeedType.ordinal());
    	intent.putExtra("resultreceiver", mResultReceiver);
    	startService(intent);
///        bindService(intent, remoteServiceConnnection, 0);

    	
/*
     	Intent intent = new Intent(ItemListActivity.this, WordIsBondService.class);
        mMessenger = new Messenger(handler);
        intent.putExtra("MESSENGER", mMessenger);
        intent.putExtra("Retrigger", mFeed.getItemCount() > 0);
        intent.setData(Uri.parse(RSSFEEDOFCHOICE));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
*/
/*
    	private void bindService() {
    	     if(conn == null) {
    	        conn = new RemoteServiceConnection();
    	        Intent i = new Intent();
    	        i.setClassName("com.collabera.labs.sai", "com.collabera.labs.sai.RemoteService");
    	        bindService(i, conn, Context.BIND_AUTO_CREATE);
    	        updateServiceStatus();
    	        Log.d( getClass().getSimpleName(), "bindService()" );
    	     } else {
    	       Toast.makeText(RemoteServiceClient.this, "Cannot bind - service already bound", Toast.LENGTH_SHORT).show();
    	     }
    	}
 */
        mIsBound = true;
    }

    void doUnbindService() 
    {
/*        if (mIsBound) 
        {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
*/    }

    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
//        doUnbindService();
    }

	public void loadEarilerItems() 
	{
    	mFileLoader = new ASyncFileLoader(mFeed, false, true, false, mFeedType);
    	mFileLoader.execute();
	}

	public void loadLaterItems() 
	{
    	mFileLoader = new ASyncFileLoader(mFeed, true, true, false, mFeedType);
    	mFileLoader.execute();
	}

}
