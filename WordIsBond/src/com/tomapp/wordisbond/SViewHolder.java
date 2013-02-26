package com.tomapp.wordisbond;

import java.util.List;
import java.util.Vector;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SViewHolder 
{
	static List<SViewHolder> smViewHolders = new Vector<SViewHolder>(0);
	TextView label;
	TextView date;
	ImageView image;
	TextView desc;
	ImageButton button;
	int position;
	RSSItem curItem = null;
	RSSArrayAdapter.EType type;
	
	public SViewHolder(RSSArrayAdapter.EType _type)
	{
		type = _type;
	}
	
	RSSArrayAdapter.EType GetType()
	{
		return type;
	}
	  
	void SetUser(RSSItem newItem)
	{
		if (curItem != newItem)
		{
			if (curItem != null)
			{
				if (curItem.viewHolder != this)
				{
					Log.i("Cheese", "balls");
				}
				curItem.viewHolder = null;
				curItem.release();
			}

			if (newItem.viewHolder == null)
			{
				newItem.addRef();
			}
			else
			{
				newItem.viewHolder.curItem = null;
			}
			curItem = newItem;
			curItem.viewHolder = this;
		}
	}
}
