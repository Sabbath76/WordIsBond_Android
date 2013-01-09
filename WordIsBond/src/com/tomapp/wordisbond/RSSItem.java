package com.tomapp.wordisbond;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

enum EMediaType
{
	Media_Audio,
	Media_Video,
	Media_Text
};
enum EMediaState
{
	None,
	Streaming,
	Playing
};

public class RSSItem //implements Runnable
{
	private String _title = null;
	private String _description = null;
	private String _fullDescription = null;
	private String _link = null;
	private String _category = null;
	private String _pubdate = null;
	public String mShortPubDate = null;
	private String _imageURL = null;
	private String _webViewURL = null;
	private String _encodedData = null;
	private String _mediaSource = null;
	private Date _dateTime = null; 
//	private Bitmap _image = null;
	private EMediaState _state = EMediaState.None;
	private EMediaType _mediaType = EMediaType.Media_Text;
	private ImageButton _mediaButton = null;
	public SViewHolder viewHolder = null;
	private boolean mIsFeature = false;
	private int 	mRef = 0;
	private BitSet mCatagories = new BitSet();
	
	RSSItem()
	{
	}
	public RSSItem(RSSItemData rssItemData) 
	{
		setTitle(rssItemData.mTitle);
		setImage(rssItemData.mImageURL);
		setDescription(rssItemData.mDescription);
		setEncodedData(rssItemData.mHtmlDescription);
		setDateTime(rssItemData.mDateTime);
		setWebview(rssItemData.mWebViewURL);
		SetCatagories(rssItemData.mCatagories);
		SetupMedia();
	}
	void setTitle(String title)
	{
		_title = title;
	}
	void setImage(String imageURL)
	{
		_imageURL = imageURL;
	}
	
	public void SetCatagory(int catagoryID)
	{
		mCatagories.set(catagoryID);
	}
	public BitSet GetCatagories()
	{
		return mCatagories;
	}
	public void SetCatagories(BitSet catagories)
	{
		mCatagories = catagories;
	}
	public boolean MatchesCatagory(int catagoryID)
	{
		return mCatagories.get(catagoryID);
	}
	
	public void addRef()
	{
		mRef++;
	}

	public void release()
	{
		mRef--;
		if (mRef == 0)
		{
			//-- Free up the image
//			_image = null;
			viewHolder = null;
		}
	}

/*    public void run()
    {
    	doLoadImage();
    }
*/
/*	void loadImage()
	{
		if ((_imageURL != null) && (_image == null) && !mIsStreamingImage)
		{
			bitmapDownloader task = new bitmapDownloader(this);
			task.execute(_imageURL);
//            Thread t = new Thread(null, this, "RSSImage_Streaming");
//            t.start();
            mIsStreamingImage = true;
		}
	}

	void doLoadImage()
	{
		URL url = null;
		try
		{
			url = new URL(_imageURL);
		}
		catch(MalformedURLException e)
        {

            e.printStackTrace();
        }
		if (url != null)
		{
			try
			{
				_image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			}
			catch(IOException e)
	        {
	
	            e.printStackTrace();
	        }
		}
        mIsStreamingImage = false;
        ItemListActivity.main.mArrayAdapter.notifyDataSetChanged();
//        if (viewHolder != null)
//        {
//        	viewHolder.image.setImageBitmap(_image);
//        }
	}
	void setBitmap(Bitmap bitmap)
	{
		_image = bitmap;
        if (viewHolder != null)
        {
        	if (viewHolder.curItem != this)
        	{
        		Log.i("ItemMismatch", "ChickenBalls");
        	}
        	viewHolder.image.setImageBitmap(_image);
        }
        else
        {
    		Log.i("MissingViewHolder", "ChickenBalls");
    		if (mIsFeature)
    		{
                ItemListActivity.main.mFeatureAdapter.UpdateItem(this);
    		}
            ItemListActivity.main.mArrayAdapter.notifyDataSetChanged();
        }
        mIsStreamingImage = false;
	}
*/
    void setWebview(String webView)
	{
		_webViewURL = webView;
		
		// TODO - explicitly setup each RSSItem on main thread
//		SetupMedia();
	}
	void setDescription(String description)
	{
		_description = description;
	}
	void setFullDescription(String description)
	{
		_fullDescription = description;
	}
	void setLink(String link)
	{
		_link = link;
	}
	void setEncodedData(String data)
	{
		_encodedData = data;
	}
	void setCategory(String category)
	{
		_category = category;
	}
	void setPubDate(String pubdate)
	{
		_pubdate = pubdate;
		try
		{
		_dateTime = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).parse(pubdate);
		mShortPubDate = new SimpleDateFormat("dd MMM").format(_dateTime);
		}
		catch (Exception e)
		{
		}
	}
	boolean isFeature()
	{
		return mIsFeature;
	}
	void setIsFeature(boolean isFeature)
	{
		mIsFeature = isFeature;
	}
	String getTitle()
	{
		return _title;
	}
	String getDescription()
	{
		return _description;
	}
	String getFullDescription()
	{
		return _fullDescription;
	}
	String getLink()
	{
		return _link;
	}
	String getEncodedData()
	{
		return _encodedData;
	}
	String getImageURL()
	{
		return _imageURL;
	}
/*	Bitmap getImage()
	{
		return _image;
	}*/
	String getWebview()
	{
		return _webViewURL;
	}
	String getCategory()
	{
		return _category;
	}
	String getPubDate()
	{
		return _pubdate;
	}
	Date getDateTime()
	{
		return _dateTime;
	}
	void setDateTime(Date dateTime)
	{
		_dateTime = dateTime;
		if (dateTime != null)
		{
			mShortPubDate = new SimpleDateFormat("dd MMM").format(_dateTime);
		}
	}
	public String toString()
	{
		// limit how much text we display
		if (_title.length() > 42)
		{
			return _title.substring(0, 42) + "...";
		}
		return _title;
	}
	
	public EMediaState GetMediaState()
	{
		return _state;
	}
	
	public void UpdateMediaButton()
	{
		if (_mediaButton != null)
		{
			if (GetMediaType() == EMediaType.Media_Audio)
			{
				if (GetMediaState() == EMediaState.Streaming)
				{
					_mediaButton.setImageResource(R.drawable.streaming);
					Animation a = new RotateAnimation(0.0f, 360.0f,
			                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
			                0.5f);
			        a.setRepeatCount(-1);
			        a.setDuration(1000);
			        _mediaButton.setAnimation(a);
				}
				else if (GetMediaState() == EMediaState.Playing)
				{
					_mediaButton.setAnimation(null);
					_mediaButton.setImageResource(R.drawable.audioplaying);
				}
				else
				{
					_mediaButton.setAnimation(null);
					_mediaButton.setImageResource(R.drawable.audio);
				}
			}
			else if (GetMediaType() == EMediaType.Media_Video)
			{
				if (GetMediaState() == EMediaState.Streaming)
				{
					_mediaButton.setImageResource(R.drawable.streaminganim);
					AnimationDrawable ani = (AnimationDrawable)_mediaButton.getDrawable();
					ani.start();	
				}
				else if (GetMediaState() == EMediaState.Playing)
				{
					_mediaButton.setImageResource(R.drawable.audioplaying);
				}
				else
					_mediaButton.setImageResource(R.drawable.video);
			}
			else
				_mediaButton.setImageResource(R.drawable.text);
		}
	}
	
	public EMediaType GetMediaType()
	{		
		return _mediaType;
	}
	
	public void SetMediaButton(ImageButton mediaButton)
	{
		_mediaButton = mediaButton;
	}
	
	public void SetupMedia()
	{
		String theWebView = _webViewURL;
		_mediaType = EMediaType.Media_Text;
//		_mediaType = EMediaType.Media_Video;
    	if (theWebView != null)
    	{
    		if (theWebView.contains("soundcloud"))
	    	{
				_mediaSource = theWebView.replace("http://w.soundcloud.com/player/?url=", "");
				_mediaSource = _mediaSource.replace("%3A", ":");
				_mediaSource = _mediaSource.replace("%2F", "/");
				Integer cut = _mediaSource.indexOf('&');
				if (cut >= 0)
				{
					_mediaSource = _mediaSource.substring(0, cut);
				}
				_mediaSource = _mediaSource + "/stream?client_id=YOUR_CLIENT_ID";
	
				_mediaType = EMediaType.Media_Audio;
	    	}
    		else if (theWebView.contains("bandcamp"))
    		{
    			int trackidx = theWebView.indexOf("track=");
    			int endpt = theWebView.indexOf("/", trackidx);
    			if ((trackidx >= 0) && (endpt >= 0))
    			{
    				String trackNumber = theWebView.substring(trackidx+6, endpt);
    				_mediaSource = String.format("http://popplers5.bandcamp.com/download/track?enc=mp3-128&id=%s&stream=1", trackNumber);
       				_mediaType = EMediaType.Media_Audio;
    			}
/*    			else
    			{
	    			int albumidx = theWebView.indexOf("album=");
	    			endpt = theWebView.indexOf("/", albumidx);
	    			if ((albumidx >= 0) && (endpt >= 0))
	    			{
	    				String albumNumber = theWebView.substring(albumidx+6, endpt);
	    				_mediaSource = String.format("http://popplers5.bandcamp.com/download/album?enc=mp3-128&id=%s&stream=1", albumNumber);
	       				_mediaType = EMediaType.Media_Audio;
	    			}
    			}
*/    		}
    		else if (theWebView.contains("youtube"))
	    	{
//    			final String getInfo = "http://www.youtube.com/get_video_info?video_id=";
//    			final String getVideo = "http://www.youtube.com/get_video?video_id=%s&t=%s&fmt=18"; 

    			int videoidx = theWebView.indexOf("embed/");
    			int endpt = theWebView.indexOf("?", videoidx);
    			if ((videoidx >= 0) && (endpt >= 0))
    			{
    				videoidx += 6;
    				String videoNumber = theWebView.substring(videoidx, endpt);
    				
    				int width, height;

    				DisplayMetrics dm = new DisplayMetrics();
    				ItemListActivity.main.getWindowManager().getDefaultDisplay().getMetrics(dm);
    				width  = 640;//dm.widthPixels;
    				height = 385;//dm.heightPixels;
    				
    				final String urlMask = "<iframe width=\"%d\" height=\"%d\" src=\"http://www.youtube.com/embed/%s?html5=1\"></iframe>";
//    				final String urlMask = "<iframe src=\"http://www.youtube.com/embed/%s?html5=1\"></iframe>";
//    				final String urlPre = "<iframe width=\"640\" height=\"385\" src=\"http://www.youtube.com/embed/";
//    				final String urlPost = "?html5=1\"></iframe>";
    				
    				_mediaSource = String.format(urlMask, width, height, videoNumber);
//    				_mediaSource = String.format(urlMask, videoNumber);

//    				final String urlPre = "<iframe id=\"player\" type=\"text/html\" width=\"640\" height=\"390\" src=\"http://www.youtube.com/embed/";
//    				final String urlPost = "?html5=1&enablejsapi=1&origin=example.com&autoplay=1&enablejsapi=1\" frameborder=\"0\">";

//    				_mediaSource = urlPre + videoNumber + urlPost;
       				_mediaType = EMediaType.Media_Video;

/*
     				boolean fallback = false;

    				try
    				{
	    				_mediaSource = calculateYouTubeUrl("18", fallback, videoNumber);
	    				if (_mediaSource != null)
	    				{
		       				_mediaType = EMediaType.Media_Video;
	    				}
    				}
    				catch (Exception e)
    				{
    					e.printStackTrace();
    				}
*/
/*    				String urlStr = getInfo+videoNumber;
    				try
    				{
	    				URL url = new URL(urlStr);
	    				
					    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(url.openStream()));
					    String StringBuffer;
					    String stringText = "";
					    while ((StringBuffer = bufferReader.readLine()) != null) 
					    {
					    	stringText += StringBuffer;
					    }
					    bufferReader.close();

		    			int tokenidx = stringText.indexOf("token=");
		    			int tokenendpt = stringText.indexOf("&", tokenidx);

		    			if ((tokenidx >= 0) && (tokenendpt >= 0))
		    			{
		    				tokenidx += 6;
		    				String tokenNumber = stringText.substring(tokenidx, tokenendpt);
		    				_mediaSource = String.format(getVideo, videoNumber, tokenNumber);
		       				_mediaType = EMediaType.Media_Video;
		    			}
					}
    				catch(Exception e)
    				{
    				}
*/
    				}    			
	    	}
    		else if (theWebView.contains("vimeo"))
	    	{
//    			int vididx = theWebView.indexOf("video/");
    			int qidx = theWebView.indexOf("?");
    			
/*    			if ((vididx > 0) && (qidx > 0))
    			{
    				String vimeoURL = "http://vimeo.com/m/#/";//9608152;
    				String number = theWebView.substring(vididx + 6, qidx);
	    			_mediaType = EMediaType.Media_Video;
	    			_mediaSource = vimeoURL + number;
    			}
*/
    			if (qidx >= 0)
    			{
    				String urlEnd = "?player_id=player&autoplay=1&title=0&byline=0&portrait=0&api=1&maxheight=480&maxwidth=800";
	    			_mediaType = EMediaType.Media_Video;
	    			_mediaSource = _webViewURL.substring(0,  qidx) + urlEnd;

    			}
	    	}
    	}
	}
	
	
	public static String calculateYouTubeUrl(String pYouTubeFmtQuality, boolean pFallback,
			String pYouTubeVideoId) throws IOException,
			ClientProtocolException, UnsupportedEncodingException {

		String lUriStr = null;
		HttpClient lClient = new DefaultHttpClient();
		
		final String YOUTUBE_VIDEO_INFORMATION_URL = "http://www.youtube.com/get_video_info?&video_id=";

		HttpGet lGetMethod = new HttpGet(YOUTUBE_VIDEO_INFORMATION_URL + 
										 pYouTubeVideoId);
		
		HttpResponse lResp = null;

		lResp = lClient.execute(lGetMethod);
			
		ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
		String lInfoStr = null;
			
		lResp.getEntity().writeTo(lBOS);
		lInfoStr = new String(lBOS.toString("UTF-8"));
		
		String[] lArgs=lInfoStr.split("&");
		Map<String,String> lArgMap = new HashMap<String, String>();
		for(int i=0; i<lArgs.length; i++){
			String[] lArgValStrArr = lArgs[i].split("=");
			if(lArgValStrArr != null){
				if(lArgValStrArr.length >= 2){
					lArgMap.put(lArgValStrArr[0], URLDecoder.decode(lArgValStrArr[1]));
				}
			}
		}
		
		//Find out the URI string from the parameters
		
		//Populate the list of formats for the video
		String lFmtList = URLDecoder.decode(lArgMap.get("fmt_list"));
		ArrayList<Format> lFormats = new ArrayList<Format>();
		if(null != lFmtList){
			String lFormatStrs[] = lFmtList.split(",");
			
			for(String lFormatStr : lFormatStrs){
				Format lFormat = new Format(lFormatStr);
				lFormats.add(lFormat);
			}
		}
		
		//Populate the list of streams for the video
		String lStreamList = lArgMap.get("url_encoded_fmt_stream_map");
		if(null != lStreamList){
			String lStreamStrs[] = lStreamList.split(",");
			ArrayList<VideoStream> lStreams = new ArrayList<VideoStream>();
			for(String lStreamStr : lStreamStrs){
				VideoStream lStream = new VideoStream(lStreamStr);
				lStreams.add(lStream);
			}	
			
			//Search for the given format in the list of video formats
			// if it is there, select the corresponding stream
			// otherwise if fallback is requested, check for next lower format
			int lFormatId = Integer.parseInt(pYouTubeFmtQuality);
			
			Format lSearchFormat = new Format(lFormatId);
			while(!lFormats.contains(lSearchFormat) && pFallback ){
				int lOldId = lSearchFormat.getId();
				int lNewId = getSupportedFallbackId(lOldId);
				
				if(lOldId == lNewId){
					break;
				}
				lSearchFormat = new Format(lNewId);
			}
			
			int lIndex = lFormats.indexOf(lSearchFormat);
			if(lIndex >= 0){
				VideoStream lSearchStream = lStreams.get(lIndex);
				lUriStr = lSearchStream.getUrl();
			}
			
		}		
		//Return the URI string. It may be null if the format (or a fallback format if enabled)
		// is not found in the list of formats for the video
		return lUriStr;
	}

	
	public static int getSupportedFallbackId(int pOldId){
		final int lSupportedFormatIds[] = {13,  //3GPP (MPEG-4 encoded) Low quality 
										  17,  //3GPP (MPEG-4 encoded) Medium quality 
										  18,  //MP4  (H.264 encoded) Normal quality
										  22,  //MP4  (H.264 encoded) High quality
										  37   //MP4  (H.264 encoded) High quality
										  };
		int lFallbackId = pOldId;
		for(int i = lSupportedFormatIds.length - 1; i >= 0; i--){
			if(pOldId == lSupportedFormatIds[i] && i > 0){
				lFallbackId = lSupportedFormatIds[i-1];
			}			
		}
		return lFallbackId;
	}

	
	
	public String getMediaSource()
	{
		return _mediaSource;
	}
	
	public void SetMediaState(	EMediaState state)
	{
		_state = state;
	}

	public boolean PlayAudio()
	{
		if ((_mediaType == EMediaType.Media_Audio)
			|| (_mediaType == EMediaType.Media_Video))
		{
    		ItemListActivity.main.ToggleMedia(this);
//    		if (ItemListActivity.main.PlayMedia(this))
 //   		{
  //  			_state = EMediaState.Streaming;
 //   		}
//    		else
//    		{
//    			_state = EMediaState.None;
//    		}
    	}		
		else
		{		
			_state = EMediaState.None;
		}
    	
		return _state != EMediaState.None;
	}
	
	public void load(ObjectInputStream ois) throws OptionalDataException, ClassNotFoundException, IOException
	{
		setTitle((String)ois.readObject());
		setImage((String)ois.readObject());
		setDescription((String)ois.readObject());
		setDateTime((Date)ois.readObject());
		setWebview((String)ois.readObject());
		setEncodedData((String)ois.readObject());
		SetCatagories((BitSet)ois.readObject());
	}
	
	public void save(ObjectOutputStream oos) throws IOException
	{
		oos.writeObject(getTitle());
		oos.writeObject(getImageURL());
		oos.writeObject(getDescription());
		oos.writeObject(getDateTime());
		oos.writeObject(getWebview());
		oos.writeObject(getEncodedData());
		oos.writeObject(GetCatagories());
	}

}
