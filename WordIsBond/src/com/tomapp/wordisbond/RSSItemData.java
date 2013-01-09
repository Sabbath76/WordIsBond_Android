package com.tomapp.wordisbond;

import java.util.BitSet;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RSSItemData implements Parcelable
{
	String 	mTitle = null;
	String 	mDescription = null;
	String 	mHtmlDescription = null;
	String 	mImageURL = null;
	String 	mWebViewURL = null;
	BitSet 	mCatagories = new BitSet();
	Date 	mDateTime = null; 

	public static final Creator<RSSItemData> CREATOR = new Creator<RSSItemData>() 
		{
		public RSSItemData createFromParcel(Parcel source) 
		{
			return new RSSItemData(source);
		}

		public RSSItemData[] newArray(int size) {
			return new RSSItemData[size];
		}
	};

	private RSSItemData(Parcel source) 
	{
		mTitle = source.readString();
		mDescription = source.readString();
		mHtmlDescription = source.readString();
		mImageURL = source.readString();
		mWebViewURL = source.readString();
		long dateTimeMS = source.readLong();
		mDateTime = new Date(dateTimeMS);
	}

	public RSSItemData(RSSItem rssItem) 
	{
		mTitle = rssItem.getTitle();
		mDescription = rssItem.getDescription();
		mHtmlDescription = rssItem.getEncodedData();
		mImageURL = rssItem.getImageURL();
		mWebViewURL = rssItem.getWebview();
		mDateTime = rssItem.getDateTime();
	}

	public int describeContents() 
	{
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(mTitle);
		dest.writeString(mDescription);
		dest.writeString(mHtmlDescription);
		dest.writeString(mImageURL);
		dest.writeString(mWebViewURL);
		if (mDateTime == null)
		{
			dest.writeLong(0);
		}
		else
		{
			dest.writeLong(mDateTime.getTime());
		}
	}

}
