package com.tomapp.wordisbond;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.tomapp.wordisbond.RSSFeed.EFeedType;

import android.os.Environment;

public class RSSFeed 
{
	enum EFeedType
	{
		FEED_MAIN,
		FEED_TABLET,
		FEED_MOBILE,
		FEED_JAZZ_MAIN,
		FEED_TEST,
		FEED_TOTAL
	};
	
	public static final String[] FILENAMES = 
		{
		"feedhistory",
		"feedhistoryTablet",
		"feedhistoryMobile",
		"feedhistoryJazz",
		"feedhistoryTest",
		};

	public static final int[] COLOURS = 
	{
	R.color.hiphop,
	R.color.hiphop,
	R.color.hiphop,
	R.color.jazz,
	R.color.jazz
	};

/*	public static final int[] COLOURS = 
		{
		0xa14733ff,
		0xa14733ff,
		0xa14733ff,
		0xaaaa10ff
		};
*/
	public static final String[] URLS = 
		{
		"http://feeds.feedburner.com/thewordisbond?format=xml",
		"http://www.thewordisbond.com/feed/tablet/?format=xml",
		"http://www.thewordisbond.com/feed/mobile/?format=xml",
		"http://feeds.feedburner.com/WordIsBondJazz?format=xml",
		"",
		};

	public static final String DATAPATH 		= "/Android/data/com.tomapp.wordisbond/files/";
	public static final String FILENAME_ITEM 	= "feed_historyTabletItemF";
	public static final String FILENAME_FEATURE = "feed_historyTabletFeatureF";
	public final int BUFFER_SIZE = 2024;
	public final int SPLIT_SIZE = 20;
	
	private boolean mLoadError = false;
	
	
	private String _title = null;
	private String _pubdate = null;
	private int _itemcount = 0;
	private List<RSSItem> _itemlist;
	private List<RSSItem> _featurelist;
	private List<String> _catagories;

	private int mLoadedItemFile 	= -1;
	private int mLoadedFeatureFile 	= -1;
	private int mNumItemFiles 		= 0;
	private int mNumFeatureFiles 	= 0;

	private EFeedType mFeedType = EFeedType.FEED_TABLET;
	
	
	RSSFeed()
	{
		_itemlist = new Vector<RSSItem>(0); 
		_featurelist = new Vector<RSSItem>(0); 
		_catagories = new Vector<String>(0);
	}
	RSSFeed(RSSFeed source)
	{
		_title = new String(source._title);
		_pubdate = new String(source._pubdate);
		_itemcount = source._itemcount;
		_itemlist = new Vector<RSSItem>(source._itemlist);
		_featurelist = new Vector<RSSItem>(source._featurelist);
		_catagories = new Vector<String>(source._catagories);
	}
	
	void SetFeedType(EFeedType feedType)
	{
		mFeedType = feedType;
	}
	
	public int GetCatagoryId(String catagory)
	{
		int ret = 0;
		for (String string : _catagories)
		{
			if (string.compareToIgnoreCase(catagory) == 0)
			{
				return ret;
			}
			ret++;
		}
		
		_catagories.add(catagory);
		return ret;
	}
	
	void MergeFeed(RSSFeed source, int numNewItems)
	{		
		_featurelist.clear();
		int srcfeatureCount = source._featurelist.size();
		for (int i=0; i<srcfeatureCount; i++)
		{
			_featurelist.add(source._featurelist.get(i));
		}

		RSSItem lastHead = null;
		if (_itemcount > 0)
		{
			lastHead = _itemlist.get(0);
		}
		int srcItemCount = source._itemcount;
		for (int i=0; i<srcItemCount; i++)
		{
			RSSItem newItem = source._itemlist.get(i);
			if ((lastHead != null) && (newItem.getTitle().compareToIgnoreCase(lastHead.getTitle()) == 0))
				break;
			_itemlist.add(i, source._itemlist.get(i));
			_itemcount ++;
		}
	}
	
	void MergeFeed(RSSFeedData source)
	{		
		_featurelist.clear();
		int srcfeatureCount = source.mFeaturelist.size();
		for (int i=0; i<srcfeatureCount; i++)
		{
			_featurelist.add(new RSSItem(source.mFeaturelist.get(i)));
		}

		RSSItem lastHead = null;
		if (_itemcount > 0)
		{
			lastHead = _itemlist.get(0);
		}
		int srcItemCount = source.mItemlist.size();
		for (int i=0; i<srcItemCount; i++)
		{
			RSSItemData newItem = source.mItemlist.get(i);
			if ((lastHead != null) && (newItem.mTitle.compareToIgnoreCase(lastHead.getTitle()) == 0))
				break;
			_itemlist.add(i, new RSSItem(newItem));
			_itemcount ++;
		}
	}
	
	RSSFeedData extractData()
	{
		return new RSSFeedData(_title, _pubdate, _itemlist, _featurelist, _catagories);
	}
	
	void SeparateFeatures()
	{
		_featurelist.clear();
		int featureCount = Math.min(5, _itemcount);
		for (int i=0; i<featureCount; i++)
		{
			RSSItem newItem = _itemlist.get(0);
			_featurelist.add(newItem);
			_itemlist.remove(0);
			_itemcount--;
		}
	}
	
	void SaveTo(ObjectOutputStream oos)
	{
		try
		{
			oos.writeObject(_title);
			oos.writeObject(_pubdate);
			oos.writeObject(_itemcount);
			oos.writeObject(_featurelist.size());
			oos.writeObject(_catagories);
			for (RSSItem item : _itemlist)
			{
				oos.writeObject(item.getTitle());
				oos.writeObject(item.getImageURL());
				oos.writeObject(item.getDescription());
				oos.writeObject(item.getDateTime());
				oos.writeObject(item.getWebview());
				oos.writeObject(item.getEncodedData());
				oos.writeObject(item.GetCatagories());
			}
			for (RSSItem item : _featurelist)
			{
				oos.writeObject(item.getTitle());
				oos.writeObject(item.getImageURL());
				oos.writeObject(item.getDescription());
				oos.writeObject(item.getDateTime());
				oos.writeObject(item.getWebview());
				oos.writeObject(item.getEncodedData());
				oos.writeObject(item.GetCatagories());
			}
		}
		catch (Exception e)
		{
            e.printStackTrace();
		}
	}

	void LoadFrom(ObjectInputStream ois)
	{
		try
		{
			_title = (String)ois.readObject();
			_pubdate = (String)ois.readObject();
			_itemcount = (Integer) ois.readObject();
			Integer featureCount = (Integer) ois.readObject();
			_catagories = (List<String>) ois.readObject();
			for (int i=0; i<_itemcount; i++)
			{
				RSSItem newItem = new RSSItem();
				newItem.setTitle((String)ois.readObject());
				newItem.setImage((String)ois.readObject());
				newItem.setDescription((String)ois.readObject());
				newItem.setDateTime((Date)ois.readObject());
				newItem.setWebview((String)ois.readObject());
				newItem.setEncodedData((String)ois.readObject());
				newItem.SetCatagories((BitSet)ois.readObject());
				_itemlist.add(newItem);
			}
			for (int i=0; i<featureCount; i++)
			{
				RSSItem newItem = new RSSItem();
				newItem.setTitle((String)ois.readObject());
				newItem.setImage((String)ois.readObject());
				newItem.setDescription((String)ois.readObject());
				newItem.setDateTime((Date)ois.readObject());
				newItem.setWebview((String)ois.readObject());
				newItem.setEncodedData((String)ois.readObject());
				newItem.SetCatagories((BitSet)ois.readObject());
				_featurelist.add(newItem);
			}
		}
		catch (Exception e)
		{
            e.printStackTrace();
		}
	}
	
	void SaveItems(ObjectOutputStream oos, int from, int to)
	{
		try
		{
			oos.writeObject(_title);
			oos.writeObject(_pubdate);
			int itemCount = to-from;
			oos.writeObject(itemCount);
			oos.writeObject(_catagories);
			for (int itemIdx = from; itemIdx < to; itemIdx++)
			{
				RSSItem item = _itemlist.get(itemIdx);
				item.save(oos);
			}
		}
		catch (Exception e)
		{
            e.printStackTrace();
		}
	}
	
	void LoadItems(ObjectInputStream ois)
	{
		try
		{
			_title = (String)ois.readObject();
			_pubdate = (String)ois.readObject();
			_itemcount = (Integer) ois.readObject();
			_catagories = (List<String>) ois.readObject();
			_itemlist.clear();
			for (int i=0; i<_itemcount; i++)
			{
				RSSItem newItem = new RSSItem();
				newItem.load(ois);
				_itemlist.add(newItem);
			}
		}
		catch (Exception e)
		{
            e.printStackTrace();
		}
	}

	void LoadFeatures(ObjectInputStream ois)
	{
		try
		{
			int featureCount = (Integer) ois.readObject();
			_featurelist.clear();
			for (int i=0; i<featureCount; i++)
			{
				RSSItem newItem = new RSSItem();
				newItem.load(ois);
				_featurelist.add(newItem);
			}
		}
		catch (Exception e)
		{
            e.printStackTrace();
		}
	}

	void SaveFeatures(ObjectOutputStream oos, int from, int to)
	{
		try
		{
			int featureCount = to - from;
			oos.writeObject(featureCount);
			for (int itemIdx = from; itemIdx < to; itemIdx++)
			{
				RSSItem item = _featurelist.get(itemIdx);
				item.save(oos);
			}
		}
		catch (Exception e)
		{
            e.printStackTrace();
		}
	}

	int addItem(RSSItem item, int location)
	{
		_itemlist.add(location, item);
		_itemcount++;
		return _itemcount;
	}
	int addFeature(RSSItem item, int location)
	{
		_featurelist.add(location, item);
		return _featurelist.size();
	}
	RSSItem getItem(int location)
	{
		return _itemlist.get(location);
	}
	List<RSSItem> getAllItems()
	{
		return _itemlist;
	}
	int getItemCount()
	{
		return _itemcount;
	}

	RSSItem getFeature(int location)
	{
		return _featurelist.get(location);
	}
	List<RSSItem> getAllFeatures()
	{
		return _featurelist;
	}
	int getFeatureCount()
	{
		return _featurelist.size();
	}

	
	void setTitle(String title)
	{
		_title = title;
	}
	void setPubDate(String pubdate)
	{
		_pubdate = pubdate;
	}
	String getTitle()
	{
		return _title;
	}
	String getPubDate()
	{
		return _pubdate;
	}

	void UpdateFeatures(String catagory)
	{
		int catagoryID = GetCatagoryId(catagory);
		_featurelist.clear();
		for (RSSItem item : _itemlist) 
		{
			boolean isFeature = item.MatchesCatagory(catagoryID);
			item.setIsFeature(isFeature);
			if (isFeature)
			{
				_featurelist.add(item);
				
				if (_featurelist.size() == 12)
				{
					return;
				}
			}
		}
	}
	public int findItemIndex(RSSItem item) 
	{
		return _itemlist.indexOf(item);
	}
	
	public void UpdateItems(RSSFeed feed) 
	{
		mLoadedItemFile = feed.mLoadedItemFile;
		mNumItemFiles = feed.mNumItemFiles;

		_itemlist.clear();
		_itemcount = 0;
		for (RSSItem item : feed._itemlist)
		{
			_itemlist.add(item);
			_itemcount++;
		}

		for (RSSItem itemNew : feed._itemlist)
		{
			itemNew.SetupMedia();
		}
	}	
	
	public void UpdateFeatures(RSSFeed feed) 
	{
		mLoadedFeatureFile = feed.mLoadedFeatureFile;
		mNumFeatureFiles = feed.mNumFeatureFiles;
		
		_featurelist.clear();
		for (RSSItem item : feed._featurelist)
		{
			_featurelist.add(item);
		}

		for (RSSItem itemNew : feed._featurelist)
		{
			itemNew.SetupMedia();
		}
	}	
	
	public boolean hasEarlierItemFile()
	{
		return (mLoadedItemFile > 0);
	}
	public boolean hasEarlierFeatureFile()
	{
		return (mLoadedFeatureFile > 0);
	}
	
	public boolean hasLaterItemFile()
	{
		return (mLoadedItemFile < mNumItemFiles-1);
	}
	public boolean hasLaterFeatureFile()
	{
		return (mLoadedFeatureFile < mNumFeatureFiles-1);
	}
	
	public String getFilename(boolean isItems)
	{
    	String filename = FILENAMES[mFeedType.ordinal()];
    	if (isItems)
    	{
    		filename = filename+"_Item";
    	}
    	else
    	{
    		filename = filename+"_Feature";
    	}
    	return filename;
	}
	
	public String getURL()
	{
		return URLS[mFeedType.ordinal()];
	}
	
	public boolean loadData(boolean isItems, boolean newer, Logger logger) 
	{
		mLoadError = false;
		
		String filename = getFilename(isItems);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+DATAPATH;
        String state = Environment.getExternalStorageState();
        final String fname = filename;
        if (Environment.MEDIA_MOUNTED.equals(state)) 
        {
            try 
            {
            	File pathPattern = new File(path);
            	if (!pathPattern.exists())
            	{
                    pathPattern.mkdirs();
            	}
            	if (!pathPattern.canRead())
            	{
                	if (logger != null)
                	{
	                	logger.Push("Cannot read from directory: " + path + filename);
                	}
            	}
            	FilenameFilter filter = new FilenameFilter() 
            	{
            		public boolean accept(File directory, String fileName) 
            		{
            		    return fileName.contains("_") && fileName.startsWith(fname);
            		}
            	};
            	File[] fileList = pathPattern.listFiles(filter);
                if ((fileList == null) || (fileList.length == 0)) 
                {
                	if (fileList == null)
                	{
                		mLoadError = true;
                	}
                	if (logger != null)
                	{
                		if (fileList == null)
                		{
		                	logger.Push("No file found: (fileList empty)" + path + filename);
                		}
                		else
                		{
		                	logger.Push("No file found: " + path + filename);
                		}
                	}
                	return false;
                }
                // Open input stream
                int fileNum = fileList.length-1;
                
                if (isItems)
                {
            		if (mLoadedItemFile < 0)
            		{
            			fileNum = fileList.length-1;
            		}
            		else if (newer)
            		{
            			fileNum = Math.min(mLoadedItemFile+1, fileList.length-1);            			
            		}
            		else
            		{            			
            			fileNum = Math.max(mLoadedItemFile-1, 0);            			
            		}
                }
                else
                {
            		if (mLoadedFeatureFile < 0)
            		{
            			fileNum = fileList.length-1;
            		}
            		else if (newer)
            		{
            			fileNum = Math.min(mLoadedFeatureFile+1, fileList.length-1);            			
            		}
            		else
            		{            			
            			fileNum = Math.max(mLoadedFeatureFile-1, 0);            			
            		}
                }

            	if (logger != null)
            	{
                	logger.Push("Num files: " + fileList.length + " Loading: " + fileNum);
                	logger.Push("Loading: " + fileList[fileNum].getAbsolutePath());
            	}

                FileInputStream fIn = new FileInputStream(fileList[fileNum]);
                
                try
                {
	                BufferedInputStream bis = new BufferedInputStream(fIn, BUFFER_SIZE); 
	                ObjectInputStream ois = new ObjectInputStream(bis);
	
	                if (isItems)
	                {
		                LoadItems(ois);
		                mNumItemFiles = fileList.length;
		                mLoadedItemFile = fileNum;

		                if (logger != null)
		            	{
		                	logger.Push("Loaded: " + _itemcount);
		            	}
	                }
	                else
	                {
		                LoadFeatures(ois);
		                mNumFeatureFiles = fileList.length;
		                mLoadedFeatureFile = fileNum;
		                
		                if (logger != null)
		            	{
		                	logger.Push("Loaded: " + _featurelist.size());
		            	}
	                }
                }
                catch (IOException e) 
                {
	                if (logger != null)
	            	{
	                	logger.Push("Exception loading: " + path);
	                	logger.Push("Error: " + e.getMessage());
	            	}
	                
                	//--- Save off a copy of the offending file!
                	File dst = new File("ERROR_"+path);
                    OutputStream out = new FileOutputStream(dst);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fIn.read(buf)) > 0) 
                    {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    e.printStackTrace();
                }
                
                fIn.close();

/*                //--- HACK!!!!
                for (int i=0; i<_itemcount; i++)
                {
                	_itemlist.add(_itemlist.get(i));
                }
                _itemcount *= 2;
                
                saveFile(true);
*/
                return true;
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }

        
        return false;
	}

    public void saveFile(boolean isItems) 
    {
    	String filename = getFilename(isItems);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+DATAPATH;
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
                int numItems = isItems ? getItemCount() : getFeatureCount();
                int numRecords = Math.max(numItems / SPLIT_SIZE, 1);
                int currentItem = numItems;
                int startFileID = isItems ? mNumItemFiles-1 : mNumFeatureFiles-1;
                startFileID = Math.max(startFileID, 0);
                
                if (numRecords > 1)
                {
	                // Open output stream
                	String newFilename = String.format("%s_%03d", path + "SPLIT_" + filename, startFileID);
	                FileOutputStream fOut = new FileOutputStream(newFilename);
	                BufferedOutputStream bos = new BufferedOutputStream(fOut, BUFFER_SIZE);   
	                ObjectOutputStream oos = new ObjectOutputStream(bos);
	                
	                if (isItems)
	                {
		                SaveItems(oos, 0, numItems);
	                }
	                else
	                {
		                SaveFeatures(oos, 0, numItems);
	                }
                	
	                // Close output stream
	                oos.flush();
	                fOut.close();
                }
                
                for (int i=0; i<numRecords; i++)
                {
	                // Open output stream
                	String newFilename = String.format("%s_%03d", path + filename, startFileID+i);
	                FileOutputStream fOut = new FileOutputStream(newFilename);
	                BufferedOutputStream bos = new BufferedOutputStream(fOut, BUFFER_SIZE);   
	                ObjectOutputStream oos = new ObjectOutputStream(bos);
	
	                // write integers as separated ascii's
	                int from = (i==(numRecords-1)) ? 0 : currentItem - SPLIT_SIZE;
	                if (isItems)
	                {
		                SaveItems(oos, from, currentItem);
	                }
	                else
	                {
		                SaveFeatures(oos, from, currentItem);
	                }
	
	                // Close output stream
	                oos.flush();
	                fOut.close();
	                
	                currentItem -= SPLIT_SIZE;
                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
	public void setupFileInfo(RSSFeed originalFeed) 
	{
		mLoadedFeatureFile = originalFeed.mLoadedFeatureFile;
		mLoadedItemFile = originalFeed.mLoadedItemFile;
		mNumFeatureFiles = originalFeed.mNumFeatureFiles;
		mNumItemFiles = originalFeed.mNumItemFiles;
	}
	public int getColour() 
	{
		return COLOURS[mFeedType.ordinal()];
	}
	
	public EFeedType getType() 
	{
		return mFeedType;
	}
	public boolean hadLoadError() 
	{
		return mLoadError;
	}
}
