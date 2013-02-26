package com.tomapp.wordisbond;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MainViewPager extends ViewPager
{
	public MainViewPager(Context context) 
	{
		super(context);
	}
    public MainViewPager(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
    }   
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) 
    {
    	int currentItem = getCurrentItem();
        if ((currentItem == 1) && (ItemListActivity.main.getSelected() == 0))
        {
        	return false;
        }
        
        if ((event.getAction() == MotionEvent.ACTION_CANCEL) || (event.getAction() == MotionEvent.ACTION_UP))
        {
        	ItemListActivity.main.onReleaseSelected();
        }
        

        return super.onInterceptTouchEvent(event);
    }

}
