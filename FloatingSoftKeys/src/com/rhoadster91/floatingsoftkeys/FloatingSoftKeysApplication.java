package com.rhoadster91.floatingsoftkeys;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.app.Application;
import android.content.Context;
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
	static boolean showShortcutRequested = false;
	static boolean showVolumeControlRequested = false;
	static int shortcutId = -1;
	static int volumeId = -1;
	static ArrayList<AppInfo> selectedAppsList = null;
	
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
	
	protected static int getDpInPix(int dp) 
	{	    
	    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}

	static boolean isOpen = true;	
	
	protected static void writeAppListToFile(Context context) 
	{
        ObjectOutputStream objectOut = null;
        try {

            FileOutputStream fileOut = context.openFileOutput("selected_apps", Context.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(selectedAppsList);
            fileOut.getFD().sync();

        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (objectOut != null) 
            {                
            	try 
            	{
                    objectOut.close();
                } 
            	catch (IOException e)
                {
                    
                }
            }
        }
    }
	
	@SuppressWarnings("unchecked")
	public static void readAppListFromFile(Context context) 
    {
        ObjectInputStream objectIn = null;
        Object object = null;
        try 
        {
            FileInputStream fileIn = context.getApplicationContext().openFileInput("selected_apps");
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } 
        catch (FileNotFoundException e)
        {
            
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } 
        finally
        {
            if (objectIn != null)
            {
                try 
                {
                    objectIn.close();
                } 
                catch (IOException e)
                {
                    
                }
            }
        }
        if(object!=null)
        	selectedAppsList = (ArrayList<AppInfo>) object;
        else
        	selectedAppsList = new ArrayList<AppInfo>();
        
    }

}
