package com.tomapp.wordisbond;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


public class SwipePagerAdapter extends FragmentPagerAdapter 
{
	RSSFeed mFeed;
	RSSArrayAdapter mAdapter;
	FeatureAdapter mFeatureAdapter;
	public RSSVideoFragment mVideoFragment;
	RSSListFragment mListFragment;
	RSSItemView mItemFragment;
	boolean mShowSweep;
		
	public SwipePagerAdapter(FragmentManager fm) 
	{		
	    super(fm);
	}
	
	@Override
	public float getPageWidth(int position)
	{
		// if horizontal???
//		if (position == 1)
//			return 0.5f;
		
		if (ItemListActivity.main.isFullScreen() 
			&& ((position == 1) || (position == 2)))
		{
			return 0.5f;
		}
		else if (mShowSweep && (position == 1))
		{
			return 0.8f;
		}
		else
		{
			return 1.0f;
		}
	}
	
	public void Initialise(RSSFeed feed, RSSArrayAdapter adapter, FeatureAdapter featureAdapter)
	{
		mFeed = feed;
		mAdapter = adapter;
		mFeatureAdapter = featureAdapter;
	}
	
	public void refreshFragments(FragmentManager fm, ViewPager viewPager)
	{
		Fragment frag1 = fm.findFragmentByTag("android:switcher:" + viewPager.getId() + ":0");;
		if (frag1 != null)
		{
			mVideoFragment = (RSSVideoFragment)frag1;
		}
		Fragment frag2 = fm.findFragmentByTag("android:switcher:" + viewPager.getId() + ":1");;
		if (frag2 != null)
		{
			mListFragment = (RSSListFragment)frag2;
		}
		Fragment frag3 = fm.findFragmentByTag("android:switcher:" + viewPager.getId() + ":2");;
		if (frag3 != null)
		{
			mItemFragment = (RSSItemView)frag3;
		}
	}
	
	public void SetSelectedItem(int i)
	{
		if ((mItemFragment != null) && (i>=0))
		{
			mItemFragment.SetItem(mFeed.getItem(i));
		}
//		if (mVideoFragment != null)
//		{
//			mVideoFragment.SetItem(mFeed.getItem(i));
//		}
	}

	public void SetSelectedFeature(int i)
	{
		if (mItemFragment != null)
		{
			mItemFragment.SetItem(mFeed.getFeature(i));
		}
/*		if (mVideoFragment != null)
		{
			mVideoFragment.SetItem(mFeed.getFeature(i));
		}
*/	}


	@Override
	public Fragment getItem(int i) 
	{
		if (i==0)
		{
			RSSVideoFragment fragmentV = new RSSVideoFragment();
	        Bundle args = new Bundle();
	        // Our object is just an integer :-P
	        args.putInt("test", i + 1);
	        fragmentV.setArguments(args);
//	        fragmentV.SetItem(mFeed.getItem(0));
	        mVideoFragment = fragmentV;
	        return fragmentV;
		}
		else if (i==1)
		{
			RSSListFragment fragmentL = new RSSListFragment();
	        Bundle args = new Bundle();
	        // Our object is just an integer :-P
	        args.putInt("test", i + 1);
	        fragmentL.setArguments(args);
	        fragmentL.Initialise(mFeed, mAdapter, mFeatureAdapter);
	        mListFragment = fragmentL;
	        return fragmentL;
		}
		else
		{
			RSSItemView fragment = new RSSItemView();
	        Bundle args = new Bundle();
	        // Our object is just an integer :-P
	        args.putInt("test", i + 1);
	        fragment.setArguments(args);
	        if (mFeed.getItemCount() >= 1)
	        {
	        	fragment.SetItem(mFeed.getItem(0));
	        }
	        mItemFragment = fragment;
	        return fragment;
		}
    }
	
	@Override
	public int getCount()
	{
		return 3;
	}

	public void onReleaseSelected() 
	{
		mListFragment.onReleaseSelected();
	}

	public void showSweep(boolean showSweep) 
	{
		mShowSweep = showSweep;
	}
}
