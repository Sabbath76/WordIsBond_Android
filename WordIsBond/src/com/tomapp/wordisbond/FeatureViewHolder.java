package com.tomapp.wordisbond;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FeatureViewHolder 
{
	  TextView label;
	  TextView date;
	  ImageView image;
	  int position;
	  RSSItem curItem = null;
	  View rootView;
	  ImageButton button;
}
