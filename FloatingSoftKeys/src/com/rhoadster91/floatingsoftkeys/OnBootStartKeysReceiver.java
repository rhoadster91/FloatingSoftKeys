package com.rhoadster91.floatingsoftkeys;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;

import wei.mark.standout.StandOutWindow;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class OnBootStartKeysReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		if(PreferenceManager.getDefaultSharedPreferences(arg0).getBoolean("startonboot", false))
		{
			try 
			{
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(arg0);
				FloatingSoftKeysApplication.size = sharedPref.getInt("size", 32);
				FloatingSoftKeysApplication.spacing  = sharedPref.getInt("spacing", 0);;
				FloatingSoftKeysApplication.transparency  = sharedPref.getInt("transparency", 0);
				FloatingSoftKeysApplication.displayMetrics = arg0.getResources().getDisplayMetrics();
				loadTheme(sharedPref.getString("themename", arg0.getString(R.string.default_theme)), arg0);
				while(RootTools.getShell(true)==null)
					Thread.sleep(100);
				StandOutWindow.show(arg0, ButtonBar.class, StandOutWindow.DEFAULT_ID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void loadTheme(String themeName, Context context)
    {
    	if(themeName.contentEquals(context.getString(R.string.default_theme)))
    	{
    		FloatingSoftKeysApplication.customBack = null;
    		FloatingSoftKeysApplication.customMenu = null;
    		FloatingSoftKeysApplication.customHome = null;
    		FloatingSoftKeysApplication.customDrag = null;    		
    		return;
    	}
    	FloatingSoftKeysApplication.customBack = Drawable.createFromPath(Environment.getExternalStorageDirectory() + "/Floating Soft Keys/" + themeName.concat("/back.png"));
		FloatingSoftKeysApplication.customMenu = Drawable.createFromPath(Environment.getExternalStorageDirectory() + "/Floating Soft Keys/" + themeName.concat("/menu.png"));
		FloatingSoftKeysApplication.customHome = Drawable.createFromPath(Environment.getExternalStorageDirectory() + "/Floating Soft Keys/" + themeName.concat("/home.png"));
		FloatingSoftKeysApplication.customDrag = Drawable.createFromPath(Environment.getExternalStorageDirectory() + "/Floating Soft Keys/" + themeName.concat("/drag.png"));
		if(FloatingSoftKeysApplication.customBack == null)
		{
			Toast.makeText(context, context.getString(R.string.load_failed), Toast.LENGTH_LONG).show();
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = sharedPref.edit();								
			editor.putInt("theme", 0);
			editor.putString("theme_name", context.getString(R.string.default_theme));							
			editor.commit();						
		}
    }

}
