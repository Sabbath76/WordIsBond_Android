package com.tomapp.wordisbond;

import java.util.List;
import java.util.Vector;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RSSFeedData implements Parcelable
{
	String mTitle 	= null;
	String mPubdate = null;
	List<RSSItemData> mItemlist;
	List<RSSItemData> mFeaturelist;
	List<String> mCatagories;

	public static final Creator<RSSFeedData> CREATOR = new Creator<RSSFeedData>() 
	{
		public RSSFeedData createFromParcel(Parcel source) 
		{
			return new RSSFeedData(source);
		}

		public RSSFeedData[] newArray(int size) {
			return new RSSFeedData[size];
		}
	};

	private RSSFeedData(Parcel source) 
	{
		mTitle 	 = source.readString();
		mPubdate = source.readString();
		mItemlist = source.readArrayList(RSSItemData.class.getClassLoader());
		mFeaturelist = source.readArrayList(RSSItemData.class.getClassLoader());
		mCatagories = new Vector<String>(0);
		source.readStringList(mCatagories);
	}
	
	public RSSFeedData(String _title, String _pubdate, List<RSSItem> _itemlist,
			List<RSSItem> _featurelist, List<String> _catagories) 
	{
		mTitle = _title;
		mPubdate = _pubdate;
		mItemlist = new Vector<RSSItemData>(0);
		mFeaturelist = new Vector<RSSItemData>(0);
		for(RSSItem rssItem : _itemlist)
		{
			mItemlist.add(new RSSItemData(rssItem));
		}
		for(RSSItem rssItem : _featurelist)
		{
			mFeaturelist.add(new RSSItemData(rssItem));
		}
		mCatagories = _catagories;
	}

	public int describeContents() 
	{
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(mTitle);
		dest.writeString(mPubdate);
		dest.writeList(mItemlist);
		dest.writeList(mFeaturelist);
		dest.writeStringList(mCatagories);
	}	
}
