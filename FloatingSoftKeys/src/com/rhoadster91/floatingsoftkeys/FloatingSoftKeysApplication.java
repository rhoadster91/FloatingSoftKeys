package com.rhoadster91.floatingsoftkeys;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class FloatingSoftKeysApplication extends Application
{
	static int size = 32;
	static int transparency;
	static int spacing = 0;
	static DisplayMetrics displayMetrics;
	static Drawable customBack = null;
	static Drawable customMenu = null;
	static Drawable customHome = null;
	static Drawable customDrag = null;
	
	protected static int getSizeInPix()
	{		
		return (int)((size * displayMetrics.density) + 0.5);
	}
	
	protected static int getSpacingInPix()
	{		
		return (int)((spacing * displayMetrics.density) + 0.5);
	}
	
	protected static int getPixInDp(int pix)
	{		
		return (int) ((pix/displayMetrics.density)+0.5);
	}

	static boolean isOpen = true;	
	
}
