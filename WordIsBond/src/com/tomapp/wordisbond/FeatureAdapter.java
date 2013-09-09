package com.tomapp.wordisbond;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FeatureAdapter extends PagerAdapter implements OnClickListener
{
	private RSSFeed mFeed;
	private final Context mContext;
	private ImageView mImages[] = null;
	
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = -1;

	boolean mHasNewer = false;
	boolean mHasOlder = false;
	private int mColourResource = -1;
	
	final int NUM_FEATURE_SLOTS = 4;
	
	private Vector<FeatureViewHolder> mViewBuffer = null;
	private ViewPager mPager = null;
	

	FeatureAdapter(Context context, RSSFeed feed)
	{
		mContext = context;
		mFeed = feed;
		
		mViewBuffer = new Vector<FeatureViewHolder>();
		mImages = new ImageView[NUM_FEATURE_SLOTS];
	}
	
	void setImages(ImageView image1, ImageView image2, ImageView image3, ImageView image4)
	{
		mImages[0] = image1;
		mImages[1] = image2;
		mImages[2] = image3;
		mImages[3] = image4;
	}
	
	void UpdateContent(RSSFeed feed)
	{
		mFeed = feed;
		
		mHasNewer = feed.hasLaterFeatureFile();
		mHasOlder = feed.hasEarlierFeatureFile();
		mColourResource = feed.getColour();
	}
	
	boolean mForceUpdateAll = false;
	
	public void forceRefresh()
	{
		mLeftViewIndex = -1;
		mForceUpdateAll = true;
		notifyDataSetChanged();
		mForceUpdateAll = true;
	}
	
	public int getItemPosition(Object object) 
	{
		if (mForceUpdateAll)
		{
			return POSITION_NONE;
		}
		else
		{
			return super.getItemPosition(object);
		}
	}
	public int getCount() 
	{
		return mFeed.getFeatureCount();
	}

	public Object getItem(int arg0) 
	{
		return null;
	}

	public long getItemId(int position) 
	{
		return 0;
	}
	
    @Override
    public void startUpdate(ViewGroup container)
    {
		ViewPager viewPager = (ViewPager) container;

		int totalFeatures = mFeed.getFeatureCount();
		int currentItem = viewPager.getCurrentItem();
		int firstFeatureID = mHasNewer ? -1 : 0;
		int lastFeatureID = totalFeatures + (mHasOlder ? 1 : 0);
		int maxFeature = Math.min(currentItem+(NUM_FEATURE_SLOTS/2), lastFeatureID);
		int minFeature = Math.max(maxFeature-NUM_FEATURE_SLOTS, firstFeatureID);
		maxFeature = Math.min(minFeature+NUM_FEATURE_SLOTS, lastFeatureID);
		int colour = ItemListActivity.main.getResources().getColor(mColourResource);
		if (minFeature != mLeftViewIndex)
		{
			int imageID = 0;
			for (int i=minFeature; i<maxFeature; i++)
			{
				if (i == -1)
				{
					mImages[imageID].setImageDrawable(new ColorDrawable(colour));
				}
				else if (i == totalFeatures)
				{
					mImages[imageID].setImageDrawable(new ColorDrawable(colour));
				}
				else
				{
					RSSItem feature = mFeed.getFeature(i);
			        ItemListActivity.main.imageDownloader.download(feature.getImageURL(), mImages[imageID]);
				}
				imageID++;
			}
			mLeftViewIndex = minFeature;
			mRightViewIndex = maxFeature;
		}
    }


	@Override
    public Object instantiateItem(View collection, int position) 
	{
		ViewPager viewPager = (ViewPager) collection;
		
		mPager = viewPager;
		
		FeatureViewHolder viewHolder = null;
		int numViewHolders = mViewBuffer.size();
		for (int i=0; i<numViewHolders; i++)
		{
			FeatureViewHolder viewHolderI = mViewBuffer.get(i);
			if (viewHolderI.curItem == null)
			{
				viewHolder = viewHolderI;
				break;
			}
		}
		
		if(viewHolder == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			viewHolder = new FeatureViewHolder();
			View featureView 	= inflater.inflate(R.layout.featurelayout, viewPager, false);
			viewHolder.label 	= (TextView) featureView.findViewById(R.id.label);
			viewHolder.image 	= (ImageView) featureView.findViewById(R.id.icon);
			viewHolder.button  	= (ImageButton) featureView.findViewById(R.id.playMedia);
			viewHolder.rootView = featureView;
			
			mViewBuffer.add(viewHolder);
		}

		RSSItem item = mFeed.getFeature(position);
		viewHolder.curItem = item;
		String label = item.getTitle();
		if (item.mShortPubDate != null)
		{
			label += "\n" + item.mShortPubDate;
		}
		viewHolder.label.setText(label);
        ItemListActivity.main.imageDownloader.download(item.getImageURL(), viewHolder.image);

		viewHolder.button.setTag(item);
		item.SetMediaButton(viewHolder.button);
		item.UpdateMediaButton();
		
		
		viewHolder.button.setOnClickListener(new OnClickListener() 
			{
	//		@Override
				public void onClick(View buttonView) 
				{
			    	RSSItem itemTag = (RSSItem)buttonView.getTag();
			    	if ((itemTag.GetMediaType() == EMediaType.Media_Audio)
			    			|| (itemTag.GetMediaType() == EMediaType.Media_Video))
			    	{
				    	boolean isPlaying = itemTag.PlayAudio();
				    	buttonView.setSelected(isPlaying);
			    	}
				}
			});

		
		viewPager.addView(viewHolder.rootView, 0);

        return viewHolder;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) 
    {
		ViewPager viewPager = (ViewPager) collection;
		FeatureViewHolder viewHolder = (FeatureViewHolder)view;
		viewHolder.curItem = null;
        viewPager.removeView(viewHolder.rootView);
    }
    
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) 
	{
		FeatureViewHolder viewHolder = (FeatureViewHolder)arg1;
		return (arg0 == viewHolder.rootView);
	}

	public void onClick(View v) 
	{
		if (v == mPager)
		{
			int featureIdx = mPager.getCurrentItem();
			ItemListActivity.main.onFeatureSelect(featureIdx);
		}
		
		int totalFeatures = mFeed.getFeatureCount();
		int imageID = 0;
		for (int i=mLeftViewIndex; i < mRightViewIndex; i++)
		{
			if (v == mImages[imageID])
			{
				if (i == -1)
				{
					ItemListActivity.main.loadLaterFeatures();
				}
				else if (i == totalFeatures)
				{
					ItemListActivity.main.loadEarilerFeatures();
				}
				else
				{
					mPager.setCurrentItem(i, true);
				}
				break;
			}
			imageID++;
		}
	}

	public void notifyImageLoaded(int index, Bitmap bitmap) 
	{
		if (index < mFeed.getFeatureCount())
		{
			RSSItem item = mFeed.getFeature(index);
			for (FeatureViewHolder holder : mViewBuffer)
			{
				if (holder.curItem == item)
				{
					holder.image.setImageBitmap(bitmap);
				}
			}
			
			if ((index >= mLeftViewIndex) && (index < mLeftViewIndex+4))
			{
				mImages[index - mLeftViewIndex].setImageBitmap(bitmap);
			}
		}
	}

	public void Intialise(RSSFeed feed) 
	{
		mFeed = feed;
		
		mHasNewer = feed.hasLaterFeatureFile();
		mHasOlder = feed.hasEarlierFeatureFile();
		mColourResource = feed.getColour();
	}

}
