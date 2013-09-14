package com.rhoadster91.floatingsoftkeys;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import wei.mark.standout.StandOutWindow;
import android.net.Uri;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
    	TextView tvTip = (TextView)findViewById(R.id.textView6);
    	tvTip.setOnClickListener(new OnClickListener()
    	{
			@Override
			public void onClick(View v) 
			{
				String url = "http://forum.xda-developers.com/showthread.php?p=43214543#post43214543";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}    		
    	});    			
    	if(!FloatingSoftKeysApplication.isOpen)
    	{
    		FloatingSoftKeysApplication.isOpen = true;
    		StandOutWindow.show(context, ButtonBar.class, StandOutWindow.DEFAULT_ID);			
    	}
    	((TextView)findViewById(R.id.textView2)).setText(getString(R.string.transparency) + ": " + FloatingSoftKeysApplication.transparency);
		final CheckBox checkCustomIcons = (CheckBox)findViewById(R.id.checkBox1);
		CheckBox checkOnBootStart = (CheckBox)findViewById(R.id.checkBox2);
		checkOnBootStart.setChecked(sharedPref.getBoolean("startonboot", false));
		checkOnBootStart.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("startonboot", isChecked).commit();				
			}
			
		});
		final CheckBox checkCollapseToNotif = (CheckBox)findViewById(R.id.checkBox7);		
		checkCollapseToNotif.setChecked(sharedPref.getBoolean("notifcollapse", false));
		checkCollapseToNotif.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("notifcollapse", isChecked).commit();				
			}
			
		});
		final CheckBox checkLowPriorityNotif = (CheckBox)findViewById(R.id.checkBox8);		
		checkLowPriorityNotif.setChecked(sharedPref.getBoolean("lpnotif", false));
		if(android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.JELLY_BEAN)
		{
			checkLowPriorityNotif.setVisibility(View.GONE);
		}
		else
		{
			checkLowPriorityNotif.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
	
				@Override
				public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
				{
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("lpnotif", isChecked).commit();				
				}
				
			});
		}
		final CheckBox checkShowNotif = (CheckBox)findViewById(R.id.checkBox4);
		checkShowNotif.setChecked(sharedPref.getBoolean("shownotif", true));
		checkShowNotif.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;				
				if(currentapiVersion>=16 && !isChecked)
				{
					new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.hide_title)).setMessage(getString(R.string.hide_text)).setPositiveButton(getString(R.string.gotoappinfo), new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int which) 
						{
							checkShowNotif.setChecked(true);
							showAppInfo("com.rhoadster91.floatingsoftkeys");
						}
					}).setNegativeButton(getString(R.string.putnothingicon), new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int which) 
						{
							
						}
					}) .show();
				}
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("shownotif", isChecked).commit();
				
			}
			
		});
		CheckBox checkHorizOrientation = (CheckBox)findViewById(R.id.checkBox3);
		checkHorizOrientation.setChecked(sharedPref.getBoolean("horizontal", true));
		checkHorizOrientation.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("horizontal", isChecked).commit();				
			}
			
		});
		CheckBox checkInvisibleDrag = (CheckBox)findViewById(R.id.checkBox5);
		checkInvisibleDrag.setChecked(sharedPref.getBoolean("hidedrag", false));
		checkInvisibleDrag.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("hidedrag", isChecked).commit();				
			}
			
		});
		CheckBox checkVibrate = (CheckBox)findViewById(R.id.checkBox6);
		checkVibrate.setChecked(sharedPref.getBoolean("vibrate", false));
		checkVibrate.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("vibrate", isChecked).commit();				
			}
			
		});
		processAppList();
		Button changeAction = (Button)findViewById(R.id.button1);
		changeAction.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent showAppList = new Intent(MainActivity.this, AppListActivity.class);
				startActivity(showAppList);
			}			
		});
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
		((Button)findViewById(R.id.button2)).setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				StandOutWindow.closeAll(context, ButtonBar.class);				
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
							FloatingSoftKeysApplication.isOpen = true;							
							StandOutWindow.show(context, ButtonBar.class, StandOutWindow.DEFAULT_ID);
							super.onPostExecute(result);
						}					
						
					}.execute();
				}						
			}        	
        });
        try 
		{
        	int lastVersion = sharedPref.getInt("version", 0);
            PackageInfo pInfo;    		
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			int curVersion = pInfo.versionCode;
			String changelogText = new String();
			Scanner sc = new Scanner(getResources().getAssets().open("changelog.txt"));
			while(sc.hasNext())
			{
				String str = sc.nextLine();
				if(isInteger(str) && !str.trim().contentEquals(""))
				{
					int thisVersionCode = Integer.parseInt(str);
					if(thisVersionCode <= lastVersion)
					{
						sc.close();
						break;
					}
				}
				if(!isInteger(str))
				{
					changelogText = changelogText.concat(str);	
					changelogText = changelogText.concat("\n");
				}
				
			}
	        if(lastVersion<curVersion)
	        {
	        	new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.changelog)).setMessage(changelogText).setPositiveButton(getString(R.string.cool), new DialogInterface.OnClickListener() 
	    		{
	    			public void onClick(DialogInterface dialog, int which) 
	    			{
	    				
	    			}
	    		})
	    		.show();
	        	sharedPref.edit().putInt("version", curVersion).commit();
	        }
		}
		catch (NameNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }    
    
    
    private void processAppList() 
    {
    	final TextView tvAction = (TextView)findViewById(R.id.textView5);
		FloatingSoftKeysApplication.readAppListFromFile(getApplicationContext());
		ArrayList<String> listOfApps = new ArrayList<String>();
		for(AppInfo a : FloatingSoftKeysApplication.selectedAppsList)
		{
			try 
			{
				ApplicationInfo appInfo = getPackageManager().getApplicationInfo(a.packageName, PackageManager.PERMISSION_GRANTED);
				listOfApps.add(new String(""+getPackageManager().getApplicationLabel(appInfo)));
			} 
			catch (NameNotFoundException e) 
			{
				FloatingSoftKeysApplication.selectedAppsList.remove(FloatingSoftKeysApplication.selectedAppsList.indexOf(a));			
				FloatingSoftKeysApplication.writeAppListToFile(getApplicationContext());				
				e.printStackTrace();				
			}
			
		}
		String finalText = new String();
		for(int i=0;i<listOfApps.size();i++)
		{
			if(i==listOfApps.size() - 2)
			{
				finalText = finalText.concat(listOfApps.get(i) + " and ");				
			}
			else if(i==listOfApps.size() - 1)
			{
				finalText = finalText.concat(listOfApps.get(i) + ".");
			}
			else
			{
				finalText = finalText.concat(listOfApps.get(i) + ", ");
			}
		}
		if(finalText.trim().contentEquals(""))
			tvAction.setText(R.string.a_none);
		else
			tvAction.setText(finalText);
		tvAction.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.home_long_press)).setMessage(tvAction.getText().toString()).setPositiveButton(getString(R.string.cool), new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.dismiss();
					}
				}).show();
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
    
    public void showAppInfo(String packageName) 
    {
		Intent intent = new Intent();
		intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", packageName, null);
		intent.setData(uri);
		startActivity(intent);
	}
    
    private boolean isInteger(String str)
    {
    	try
    	{
    		Integer.parseInt(str);
    		return true;
    	}
    	catch(NumberFormatException nfe)
    	{
    		return false;
    	}
    }


	@Override
	protected void onResume()
	{
		processAppList();
		super.onResume();
	}
    
    
}
