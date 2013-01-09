package com.tomapp.wordisbond;

import com.tomapp.wordisbond.RSSFeedData;
import com.tomapp.wordisbond.RSSServiceListener;

interface RemoteService 
{
	void TriggerParse();
	
	RSSFeedData GetLatestResult();
	
	void RegisterListener(RSSServiceListener listener);
	void UnregisterListener(RSSServiceListener listener);
}
