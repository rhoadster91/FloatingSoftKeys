package com.rhoadster91.floatingsoftkeys;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import wei.mark.standout.StandOutWindow;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

public class MainActivity extends Activity 
{

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File themeFile = new File(Environment.getExternalStorageDirectory() + "/Floating Soft Keys/");
    	themeFile.mkdirs();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	FloatingSoftKeysApplication.size = sharedPref.getInt("size", 32);
    	FloatingSoftKeysApplication.transparency = sharedPref.getInt("transparency", 0);
    	FloatingSoftKeysApplication.spacing = sharedPref.getInt("spacing", 0);
    	((SeekBar)findViewById(R.id.seekBar1)).setProgress(FloatingSoftKeysApplication.transparency);            
    	((EditText)findViewById(R.id.editText1)).setText("" + FloatingSoftKeysApplication.size);
    	((EditText)findViewById(R.id.editText2)).setText("" + FloatingSoftKeysApplication.spacing);
    	FloatingSoftKeysApplication.displayMetrics = this.getResources().getDisplayMetrics();
    	final Context context = this;  		
		StandOutWindow.show(this, ButtonBar.class, StandOutWindow.DEFAULT_ID);
		((TextView)findViewById(R.id.textView2)).setText(getString(R.string.transparency) + ": " + FloatingSoftKeysApplication.transparency);
		final CheckBox checkCustomIcons = (CheckBox)findViewById(R.id.checkBox1);
		if(sharedPref.getInt("theme", 0)==0)
			checkCustomIcons.setChecked(false);
		else
			checkCustomIcons.setChecked(true);
		loadTheme(sharedPref.getString("theme_name", getString(R.string.default_theme)));
		checkCustomIcons.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			    	builder.setTitle(getString(R.string.available_themes));
			    	final ArrayList<String>themeList = new ArrayList<String>();
			    	themeList.add(getString(R.string.default_theme));
			    	File themeFile = new File(Environment.getExternalStorageDirectory() + "/Floating Soft Keys/");
			    	themeFile.mkdirs();
			    	FilenameFilter directoryFilter = new FilenameFilter() 
		            {
		                public boolean accept(File dir, String filename) 
		                {
		                    File sel = new File(dir, filename);
		                    if (!sel.canRead()) 
		                    	return false;
		                    if(!new File(dir, filename + "/back.png").exists())
		                    	return false;
		                    if(!new File(dir, filename + "/home.png").exists())
		                    	return false;		                    
		                    if(!new File(dir, filename + "/menu.png").exists())
		                    	return false;		                    
		                    if(!new File(dir, filename + "/drag.png").exists())
		                    	return false;
		                    
		                    return sel.isDirectory();                    
		                }
		            };
		           
		            String[] directoryList = themeFile.list(directoryFilter);	
		            if(directoryList!=null)
		            {
			            Arrays.sort(directoryList);		            
			            for(String file : directoryList)
			            	themeList.add(file);
		            }
		            int directoryCount = themeList.size();
		            String []list = new String[directoryCount];
		            themeList.toArray(list);
			    	int x = sharedPref.getInt("theme", 0);
			    	builder.setSingleChoiceItems(list, x, new DialogInterface.OnClickListener()
			    	{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							if(which==0)								
								checkCustomIcons.setChecked(false);		
							
							SharedPreferences.Editor editor = sharedPref.edit();								
							editor.putInt("theme", which);
							editor.putString("theme_name", themeList.get(which));							
							editor.commit();
							loadTheme(themeList.get(which));
							dialog.dismiss();
						}    		
			    	});
			    	builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
			    	{
						
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{							
							if(sharedPref.getInt("theme", 0)==0)
								checkCustomIcons.setChecked(false);
						}
					});
			    	builder.show();
				}
				else
				{
					SharedPreferences.Editor editor = sharedPref.edit();								
					editor.putInt("theme", 0);
					editor.putString("theme_name", getString(R.string.default_theme));
					loadTheme(getString(R.string.default_theme));
					editor.commit();
				}
				
			}
			
		});
		((SeekBar)findViewById(R.id.seekBar1)).setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{				
				((TextView)findViewById(R.id.textView2)).setText(getString(R.string.transparency) + ": " + seekBar.getProgress());
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
        ((Button)findViewById(R.id.buttonReload)).setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) 
			{
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPref.edit();
				String no = ((EditText)findViewById(R.id.editText1)).getText().toString();
				String space = ((EditText)findViewById(R.id.editText2)).getText().toString();
				if(!no.contentEquals("") && !space.contentEquals(""))
				{
					editor.putInt("size", Integer.parseInt(no));
					editor.putInt("spacing", Integer.parseInt(space));
					editor.putInt("transparency", ((SeekBar)findViewById(R.id.seekBar1)).getProgress());
					editor.commit();
					FloatingSoftKeysApplication.size = Integer.parseInt(no);
					FloatingSoftKeysApplication.spacing = Integer.parseInt(space);
					FloatingSoftKeysApplication.transparency = ((SeekBar)findViewById(R.id.seekBar1)).getProgress();
					StandOutWindow.closeAll(context, ButtonBar.class);
					new AsyncTask<Void, Void, Void>()
					{

						@Override
						protected Void doInBackground(Void... params) 
						{
							try 
							{
								Thread.sleep(2000);
							}
							catch (InterruptedException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) 
						{
							StandOutWindow.show(context, ButtonBar.class, StandOutWindow.DEFAULT_ID);
							super.onPostExecute(result);
						}					
						
					}.execute();
				}						
			}        	
        });
    }    
    
    
    private void loadTheme(String themeName)
    {
    	if(themeName.contentEquals(getString(R.string.default_theme)))
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
			Toast.makeText(getApplicationContext(), getString(R.string.load_failed), Toast.LENGTH_LONG).show();
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = sharedPref.edit();								
			editor.putInt("theme", 0);
			editor.putString("theme_name", getString(R.string.default_theme));							
			editor.commit();
			CheckBox checkCustomIcons = (CheckBox)findViewById(R.id.checkBox1);
			checkCustomIcons.setChecked(false);			
		}
    }
}
