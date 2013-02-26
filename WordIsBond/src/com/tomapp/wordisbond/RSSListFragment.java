package com.tomapp.wordisbond;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.AttributeSet;


public class RSSListFragment extends Fragment implements OnItemClickListener, OnItemSelectedListener, OnTouchListener
{
	private RSSFeed mFeed;
	public final String tag = "RSSReader";
	public RSSArrayAdapter mAdapter = null;
	public FeatureAdapter mFeatureAdapter = null;
	public ListView mItemlist = null;

	public void Initialise(RSSFeed feed, RSSArrayAdapter adapter, FeatureAdapter featureAdapter)
	{
		mFeed = feed;
		mAdapter = adapter;
		mFeatureAdapter = featureAdapter;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
      	menu.add(0,0,0, "Remove RSS File");
    	menu.add(0,1,1, "Restore RSS File");
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	ItemListActivity.main.onMenuItemSelected(0, item);
        
        return true;
    }

	
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) 
    {
    	setHasOptionsMenu(true);
    	
    	mFeed = ItemListActivity.main.mFeed;
    	mAdapter = ItemListActivity.main.mArrayAdapter;
    	mFeatureAdapter = ItemListActivity.main.mFeatureAdapter;
    	
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.main, container, false);
        Bundle args = getArguments();
//        ((TextView) rootView.findViewById(R.id.label)).setText(
 //               Integer.toString(args.getInt("test")));
        
        UpdateDisplay(rootView, inflater);
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);

  //      setListAdapter(mAdapter);
//        ListView itemlist = (ListView) getView().findViewById(R.id.itemlist);
        
//        itemlist.setAdapter(mAdapter);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", 42);
    }
    
    private void UpdateDisplay(View rootView, LayoutInflater inflater)
    {
    	if (mFeatureAdapter != null)
    	{
//    		ListView itemlist = getListView();
	        ListView itemlist = (ListView) rootView.findViewById(R.id.itemlist);
	        
	        itemlist.setAdapter(mAdapter);
    		
	//        setListAdapter(mAdapter);
	                
	        ItemListActivity activity = (ItemListActivity)mAdapter.getContext();
	        itemlist.setOnItemClickListener(activity);
	        itemlist.setOnItemSelectedListener(activity);
	        itemlist.setOnTouchListener(this);
	        
	        itemlist.setSelection(0);

/*            View featureView = inflater.inflate(
                    R.layout.featurelist, itemlist, false);
            ViewPager featurelist = (ViewPager) featureView.findViewById(R.id.horizontallist);

//            HorizontalListView featurelist = (HorizontalListView) featureView.findViewById(R.id.horizontallist);
//	    	HorizontalListView featurelist = (HorizontalListView) rootView.findViewById(R.id.featurelist);
//	    	HorizontalListView featurelist = new HorizontalListView(ItemListActivity.main, attributeSet);//(HorizontalListView) rootView.findViewById(R.id.featurelist);
            ImageView image1 = (ImageView) featureView.findViewById(R.id.image1);
            ImageView image2 = (ImageView) featureView.findViewById(R.id.image2);
            ImageView image3 = (ImageView) featureView.findViewById(R.id.image3);
            ImageView image4 = (ImageView) featureView.findViewById(R.id.image4);
            mFeatureAdapter.setImages(image1, image2, image3, image4);
	    	featurelist.setAdapter(mFeatureAdapter);
	    	*/
//	    	featurelist.setOnItemClickListener(this);
/*	    	featurelist.setOnTouchListener(new OnTouchListener() 
	    	{
				public boolean onTouch(View v, MotionEvent event) 
				{
					return false;
				}
			});*/
/*
	    	mAdapter.setFeatureView(featureView);
	        
	        mItemlist = itemlist;
*/	        
	//        itemlist.post(new Runnable() 
	 //       {
	//            @Override
	//            public void run() 
	//               {
	//            	ItemListActivity.main.
	//            	itemlist.setSelection(pos);
	//               View v = list.getChildAt(pos);
	//                if (v != null) 
	 //               {
	//                    v.requestFocus();
	//                }
	//            }
	//        });
    	}	        
    }

    public void onItemClick(AdapterView parent, View v, int position, long id)
    {
    	Log.i(tag,"item clicked! [" + mFeed.getItem(position).getTitle() + "]");

    	Adapter adapter = parent.getAdapter();
    	if (adapter == mAdapter)
    	{
	      	RSSArrayAdapter RSSAdapter = (RSSArrayAdapter)adapter;
	      	RSSAdapter.OnSelect(position);
    	}
    	else if (adapter == mFeatureAdapter)
    	{
	      	FeatureAdapter featureAdapter = (FeatureAdapter)adapter;
	      	ItemListActivity.main.onFeatureSelect(position);
//	      	featureAdapter.OnSelect(position);
    	}
    		
    }

	public void onItemSelected(AdapterView parent, View v, int position, long id) 
	{
      	RSSArrayAdapter adapter = (RSSArrayAdapter)parent.getAdapter();
      	adapter.OnSelect(position);
	}

	public void onNothingSelected(AdapterView<?> arg0) 
	{
		// TODO Auto-generated method stub
	}   

	public boolean onTouch(View v, MotionEvent event) 
    {
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			{
/*				float x = event.getX();
				float y = event.getY();
				ListView list = (ListView)v;
				int position = list.pointToPosition((int)x, (int)y);
			
				if (position >= 0)
				{
	////				ItemListActivity.main.setSelected(position);
				}
*/				
		//		return true;
			}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		{
		}
			break;
		}
		
		
        return false;
    }

	public void onReleaseSelected() 
	{
	}
	
}
