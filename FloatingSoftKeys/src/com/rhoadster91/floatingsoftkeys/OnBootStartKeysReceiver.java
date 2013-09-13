package com.rhoadster91.floatingsoftkeys;

import wei.mark.standout.StandOutWindow;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;

public class OnBootStartKeysReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		if(PreferenceManager.getDefaultSharedPreferences(arg0).getBoolean("startonboot", false))
		{
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(arg0);
				FloatingSoftKeysApplication.size = sharedPref.getInt("size", 32);
				FloatingSoftKeysApplication.spacing  = sharedPref.getInt("spacing", 0);;
				FloatingSoftKeysApplication.transparency  = sharedPref.getInt("transparency", 0);
				FloatingSoftKeysApplication.displayMetrics = arg0.getResources().getDisplayMetrics();
				new AsyncTask<Void, Void, Void>()
				{

					@Override
					protected Void doInBackground(Void... params) 
					{
						String state = new String();
						do
						{
							try 
							{
								Thread.sleep(1000);
							} 
							catch (InterruptedException e) 
							{
								e.printStackTrace();
							}
							state = Environment.getExternalStorageState();							
						}while(!Environment.MEDIA_MOUNTED.equals(state));
						return null;
					}
					
				}.execute();				
				StandOutWindow.show(arg0, ButtonBar.class, StandOutWindow.DEFAULT_ID);
			
		}
	}
}
