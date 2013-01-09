package com.tomapp.wordisbond;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RSSArrayAdapter extends ArrayAdapter<RSSItem> implements ViewTreeObserver.OnGlobalLayoutListener
{
	private final Context context;
	List<RSSItem> items;
	int mSelectedPos = -1;
	int mOldSelectedPos = -1;
	int mUpdateAnimation = -1;
	int mDefaultImageHeight = -1;
	int mNumUpdates = 0;
	boolean mHasFeatures = false;
	boolean mHasNewer = false;
	boolean mHasOlder = false;
	private int mColourResource = -1;
	
	private View mFeatureView = null;

	
	enum EType
	{
		Type_Features,
		Type_Earlier,
		Type_Item,
		Type_Later
	};
	
	public class SType
	{
		public EType type;
		public int itemIdx;
		
		SType(EType _type, int _idx)
		{
			type = _type;
			itemIdx = _idx;
		}
	}

	public RSSArrayAdapter(Context _context, List<RSSItem> _objects) 
	{
		super(_context, R.layout.rowlayout, _objects);

		context = _context;
		items = _objects;
	}

	public void OnSelect(int position)
	{
		mNumUpdates = 0;
		mUpdateAnimation = -1;
		mOldSelectedPos = mSelectedPos;
		if (mSelectedPos == position)
			mSelectedPos = -1;
		else
			mSelectedPos = position;
		notifyDataSetChanged();
		
		SType type = getType(position);
		switch(type.type)
		{
		case Type_Earlier:
			ItemListActivity.main.loadEarilerItems();
			break;
		case Type_Later:
			ItemListActivity.main.loadLaterItems();
			break;
		}
	}
	

	@Override
	public int getCount()
	{
		int count = items.size();
		if (mHasFeatures)
		{
			count++;
		}
		if (mHasNewer)
		{
			count++;
		}
		if (mHasOlder)
		{
			count++;
		}
		return count;
	}
	
	SType getType(int idx)
	{
		if (mHasFeatures)
		{
			if (idx == 0)
			{
				return new SType(EType.Type_Features, -1);
			}
			idx--;
		}
		if (mHasNewer)
		{
			if (idx == 0)
			{
				return new SType(EType.Type_Later, -1);
			}
			idx--;
		}
		if (mHasOlder)
		{
			if (idx == items.size())
			{
				return new SType(EType.Type_Earlier, -1);
			}
		}
		return new SType(EType.Type_Item, idx);
	}

    public void onGlobalLayout() 
    {
    	SType type = getType(mSelectedPos);
    	
    	if (type.type == EType.Type_Item)
    	{
			RSSItem item = items.get(type.itemIdx);
	    	if ((item != null) && (item.viewHolder != null) && (item.viewHolder.curItem == item))
	       	{
	           	int lineHeight = item.viewHolder.desc.getLineHeight();
	           	int lineCount = item.viewHolder.desc.getLineCount();
		        int newHeight = lineHeight * lineCount;
		        item.viewHolder.desc.getLayoutParams().height = newHeight;
		        item.viewHolder.desc.requestLayout();
	       	}
    	}
    }
    
    @Override
	public int getItemViewType(int position) 
    {
    	SType type = getType(position);

    	switch(type.type)
    	{
    	case Type_Features:
    		return 0;
    	case Type_Earlier:
    	case Type_Later:
    		return 1;
    	case Type_Item:
   		default:    			
    		return 2;
    	}
    }
    
    @Override
    public int getViewTypeCount() 
    {
        return 3;
    }

    class ViewPagerClickListener extends GestureDetector.SimpleOnGestureListener
    {
    	ViewPager mViewPager;
    	
    	ViewPagerClickListener(ViewPager viewPager)
    	{
    		mViewPager = viewPager;
    	}
    	
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) 
        {
        	ItemListActivity.main.onFeatureSelect(mViewPager.getCurrentItem());

        	
        	return true;
        }
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{		
		SType type = getType(position);
		
		if (type.type == EType.Type_Features)
		{
			if (mFeatureView == null)
			{
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
	            View featureView = inflater.inflate(
	                    R.layout.featurelist, parent, false);
	            ViewPager featurelist = (ViewPager) featureView.findViewById(R.id.horizontallist);

				View v = featureView.findViewById(R.id.divider1);
				if (v != null)
				{
					v.setBackgroundResource(mColourResource);
				}
				v = featureView.findViewById(R.id.divider2);
				if (v != null)
				{
					v.setBackgroundResource(mColourResource);
				}

	            ImageView image1 = (ImageView) featureView.findViewById(R.id.image1);
	            ImageView image2 = (ImageView) featureView.findViewById(R.id.image2);
	            ImageView image3 = (ImageView) featureView.findViewById(R.id.image3);
	            ImageView image4 = (ImageView) featureView.findViewById(R.id.image4);
	            FeatureAdapter featureAdapter = ItemListActivity.main.mFeatureAdapter;
	            image1.setOnClickListener(featureAdapter);
	            image2.setOnClickListener(featureAdapter);
	            image3.setOnClickListener(featureAdapter);
	            image4.setOnClickListener(featureAdapter);
	            
	            final GestureDetector tapGestureDetector = new GestureDetector(new ViewPagerClickListener(featurelist));

	            featurelist.setOnTouchListener(new OnTouchListener() 
	            {
	                    public boolean onTouch(View v, MotionEvent event) 
	                    {
	                        tapGestureDetector.onTouchEvent(event);
	                        return false;
	                    }
	            });

	            featurelist.setOnClickListener(featureAdapter);
	            featureAdapter.setImages(image1, image2, image3, image4);
		    	featurelist.setAdapter(featureAdapter);

		    	mFeatureView = featureView;
			}
			return mFeatureView;
		}
		else if ((type.type == EType.Type_Earlier)
				|| (type.type == EType.Type_Later))
		{
			View rowView = convertView;
			
			if (convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.rowloadmore, parent, false);
			}
			
			return rowView;
		}
		else if (type.type == EType.Type_Item)
		{
			RSSItem item = items.get(type.itemIdx);
	
			View rowView = convertView;
			SViewHolder viewHolder = null;
			if ((convertView == null) || (convertView == mFeatureView))
			{
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.rowlayout, parent, false);
				viewHolder = new SViewHolder();
	
				viewHolder.label = (TextView) rowView.findViewById(R.id.label);
				viewHolder.date = (TextView) rowView.findViewById(R.id.date);
				viewHolder.image = (ImageView) rowView.findViewById(R.id.icon);
				viewHolder.desc  = (TextView) rowView.findViewById(R.id.description);
				viewHolder.button  = (ImageButton) rowView.findViewById(R.id.playMedia);
				
				SViewHolder.smViewHolders.add(viewHolder);
				
				rowView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (SViewHolder)rowView.getTag();
			}
			viewHolder.SetUser(item);
			viewHolder.label.setText(item.getTitle());
			if (item.mShortPubDate != null)
				viewHolder.date.setText(item.mShortPubDate);
			rowView.setBackgroundResource(mColourResource);
			// Change the icon for Windows and iPhone

//			Bitmap image = ItemListActivity.main.mImageManager.getImage(item.getImageURL());

//			if (image != null)
//			{
//				viewHolder.image.setImageBitmap(image);
//			}
//			else
//			{
//				viewHolder.image.setImageResource(R.drawable.bond_logo);
//			}

	        ItemListActivity.main.imageDownloader.download(item.getImageURL(), (ImageView) viewHolder.image);

	        int imageHeight = viewHolder.image.getDrawable().getIntrinsicHeight();
	        int imageWidth  = viewHolder.image.getDrawable().getIntrinsicWidth();

	        Bitmap image = null;
///IMGFIDDLE			Bitmap image = ItemListActivity.main.mImageManager.displayImage(item.getImageURL(), viewHolder.image, R.drawable.bond_logo, type.itemIdx+ItemListActivity.main.mFeed.getFeatureCount());
/*			
			item.loadImage();
			if (item.getImage() != null)
			{
				viewHolder.image.setImageBitmap(item.getImage());
			}
			else
			{
				viewHolder.image.setImageResource(R.drawable.bond_logo);
			}
		*/
	//		viewHolder.button.setSelected(item.IsPlaying());
			viewHolder.button.setTag(item);
	//		viewHolder.button.setClickable(item.GetMediaType() != EMediaType.Media_Text);
			item.SetMediaButton(viewHolder.button);
			item.UpdateMediaButton();
			
			viewHolder.button.setOnClickListener(new OnClickListener() {
				
	//		@Override
				public void onClick(View buttonView) {
	//				if (isChecked)
					{
				    	RSSItem itemTag = (RSSItem)buttonView.getTag();
				    	if ((itemTag.GetMediaType() == EMediaType.Media_Audio)
				    			|| (itemTag.GetMediaType() == EMediaType.Media_Video))
				    	{
					    	boolean isPlaying = itemTag.PlayAudio();
					    	buttonView.setSelected(isPlaying);
				    	}
					}
				}
			});
	
			
			if (mDefaultImageHeight <= 0)
			{
				mDefaultImageHeight = viewHolder.image.getLayoutParams().height;
			}
			
			int newHeight = mDefaultImageHeight;
			if (mSelectedPos == position)
			{
				if ((viewHolder.image != null))// && (image != null))
				{
					float widthScale = (float)viewHolder.image.getWidth() / (float)imageWidth;//image.getWidth();
					newHeight = (int)(widthScale * (float)imageHeight);//(float)image.getHeight());
				}
	
				viewHolder.desc.setVisibility(View.VISIBLE);
		        viewHolder.desc.setText(Html.fromHtml(item.getDescription()));
			}
			else
			{
				viewHolder.desc.setText(null);
				viewHolder.desc.setVisibility(View.GONE);
			}
			if (viewHolder.image.getLayoutParams().height != newHeight)
			{
				viewHolder.image.getLayoutParams().height = newHeight;
				viewHolder.image.requestLayout();
			}
			
			if (position < mUpdateAnimation)
			{
				mNumUpdates++;
			}
			mUpdateAnimation = position;
			
			return rowView;
		}
		
		return null;
	}

	public void notifyImageLoaded(int index, Bitmap bmp) 
	{
		RSSFeed feed = ItemListActivity.main.mFeed;
		if (index >= feed.getFeatureCount())
		{
			
			RSSItem item = feed.getItem(index-feed.getFeatureCount());
			for(SViewHolder viewHolder : SViewHolder.smViewHolders)
			{
				if ((viewHolder.curItem == item) && (item.viewHolder == viewHolder))
				{
					viewHolder.image.setImageBitmap(bmp);
					return;
				}
			}

			notifyDataSetChanged();
		}
	}

	public void UpdateFeed(RSSFeed feed) 
	{
		mHasFeatures = (feed.getFeatureCount() > 0);
		mHasNewer = feed.hasLaterItemFile();
		mHasOlder = feed.hasEarlierItemFile();
		
		mColourResource = feed.getColour();
		if (mFeatureView != null)
		{
			View v = mFeatureView.findViewById(R.id.divider1);
			if (v != null)
			{
				v.setBackgroundResource(mColourResource);
			}
			v = mFeatureView.findViewById(R.id.divider2);
			if (v != null)
			{
				v.setBackgroundResource(mColourResource);
			}
		}
	}

//	public void setFeatureView(View featureView) 
//	{
//		mFeatureView  = featureView;
//	}
}