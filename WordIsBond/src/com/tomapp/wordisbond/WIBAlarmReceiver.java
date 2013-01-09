package com.tomapp.wordisbond;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WIBAlarmReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.i("WIB", "WIBAlarmReceiver invoked, starting WIBService in background");
		context.startService(new Intent(context, WordIsBondService.class));
	}

}
