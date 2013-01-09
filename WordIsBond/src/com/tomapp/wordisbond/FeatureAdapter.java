package com.tomapp.wordisbond;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
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
/*
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		FeatureViewHolder viewHolder = null;
		View featureView = convertView;
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			featureView = inflater.inflate(R.layout.featurelayout, parent, false);

			viewHolder = new FeatureViewHolder();
			viewHolder.label = (TextView) featureView.findViewById(R.id.label);
			viewHolder.date = (TextView) featureView.findViewById(R.id.date);
			viewHolder.image = (ImageView) featureView.findViewById(R.id.icon);
			
			featureView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (FeatureViewHolder)featureView.getTag();
		}
		RSSItem item = mFeed.getFeature(position);
		viewHolder.curItem = item;
//		item.viewHolder = viewHolder;
		viewHolder.label.setText(item.getTitle());
		if (item.mShortPubDate != null)
			viewHolder.date.setText(item.mShortPubDate);
		// Change the icon for Windows and iPhone
		item.loadImage();
		if (item.getImage() != null)
		{
			viewHolder.image.setImageBitmap(item.getImage());
		}
		else
		{
			viewHolder.image.setImageResource(R.drawable.bond_logo);
		}

		ViewPager horizListView = (ViewPager) parent;
		mPager = horizListView;
		int leftViewIndex = horizListView.getCurrentItem();// horizListView.getLeftViewIndex();
		if (leftViewIndex != mLeftViewIndex)
		{
			mImages[0].setImageBitmap(mFeed.getFeature(leftViewIndex+0).getImage());
			mImages[1].setImageBitmap(mFeed.getFeature(leftViewIndex+1).getImage());
			mImages[2].setImageBitmap(mFeed.getFeature(leftViewIndex+2).getImage());
			mImages[3].setImageBitmap(mFeed.getFeature(leftViewIndex+3).getImage());
			mLeftViewIndex = leftViewIndex;
		}

		return featureView;
	}
*/	
/*	void UpdateItem(RSSItem rssItem)
	{
		int numViewHolders = mViewBuffer.size();
		for (int i=0; i<numViewHolders; i++)
		{
			FeatureViewHolder viewHolder = (FeatureViewHolder)mViewBuffer.get(i);
			if (viewHolder.curItem == rssItem)
			{
				if (rssItem.getImage() != null)
				{
					viewHolder.image.setImageBitmap(rssItem.getImage());
				}
				else
				{
					viewHolder.image.setImageResource(R.drawable.bond_logo);
				}
				
				break;
			}
		}
		
		for (int i=0; i<4; i++)
		{
			RSSItem imageItem = mFeed.getFeature(mLeftViewIndex+i);
			if (imageItem == rssItem)
			{
				mImages[i].setImageBitmap(rssItem.getImage());
			}
		}
	}*/
	
    @Override
    public void startUpdate(ViewGroup container)
    {
		ViewPager viewPager = (ViewPager) container;

		int totalFeatures = mFeed.getFeatureCount();
		int currentItem = viewPager.getCurrentItem();
		int maxFeature = Math.min(currentItem+(NUM_FEATURE_SLOTS/2), totalFeatures);
		int minFeature = Math.max(maxFeature-NUM_FEATURE_SLOTS, 0);
		maxFeature = Math.min(minFeature+NUM_FEATURE_SLOTS, totalFeatures);
		if (minFeature != mLeftViewIndex)
		{
			int imageID = 0;
			for (int i=minFeature; i<maxFeature; i++)
			{
				RSSItem feature = mFeed.getFeature(i);
		        ItemListActivity.main.imageDownloader.download(feature.getImageURL(), mImages[imageID]);
//IMGFIDDLE				ItemListActivity.main.mImageManager.displayImage(feature.getImageURL(), mImages[imageID], R.drawable.bond_logo, i);
//				mImages[i].setImageBitmap(mFeed.getFeature(currentItem+i).getImage());
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
//IMGFIDDLE		ItemListActivity.main.mImageManager.displayImage(item.getImageURL(), viewHolder.image, R.drawable.bond_logo, position);
/*		item.loadImage();
		if (item.getImage() != null)
		{
			viewHolder.image.setImageBitmap(item.getImage());
		}
		else
		{
			viewHolder.image.setImageResource(R.drawable.bond_logo);
		}
*/		viewHolder.button.setTag(item);
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
		
		int imageID = 0;
		for (int i=mLeftViewIndex; i < mRightViewIndex; i++)
		{
			if (v == mImages[imageID])
			{
				mPager.setCurrentItem(i, true);
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
	}

}
