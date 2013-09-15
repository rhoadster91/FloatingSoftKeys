package com.rhoadster91.floatingsoftkeys;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ru.biovamp.widget.CircleLayout;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class ButtonBar extends StandOutWindow
{
	int currentLeft, currentTop;
	int thisId;
	static int centerX;
	static int centerY;
	static int offsetY;
	static double lockInitX, lockInitY, lockEndX, lockEndY;
	static double endX, endY;
	static Button backButton = null;
	static Button homeButton = null;
	static Button menuButton = null;
	static ImageView dragButton = null;
	static int windowHeight, windowWidth;
	static EventHandler myEventHandler;
	static int oldOrientation;
	static IntentFilter ifConfigChanged;
	static BroadcastReceiver brConfigChanged;
	static BroadcastReceiver brNotifPressed;	
	static int THRESHOLD = 100;
	static final int OFFSET = 90;
	static int currentRingItem = -1;
	static AsyncTask<Void, Void, Void> volumeHideTask;
	static int remainingTime = 4000;
	static Toast showSelectedApp = null;
	static boolean showedLockReleaseNotifAlready = false;
	static Button sp1, sp2, sp3;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{		
		if(FloatingSoftKeysApplication.isOpen)
			return super.onStartCommand(intent, flags, startId);
		else
			return START_STICKY;
	}
	
	@Override
	public String getAppName() 
	{
		return getString(R.string.app_name);
	}
	

	@Override
	public int getAppIcon()
	{
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("shownotif", true))			
			return android.R.drawable.ic_menu_close_clear_cancel;
		else			
			return R.drawable.nothing;		
		
	}


	@Override
	public boolean onTouchBody(int id, Window window, View view, MotionEvent event) 
	{
		if (event.getAction() == android.view.MotionEvent.ACTION_UP) 
		{
			updateWindowLocation(id);
		}
		return super.onTouchBody(id, window, view, event);
	}
	
	@SuppressWarnings("deprecation")
	public void createShortcut(int id, FrameLayout frame)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.shortcut, frame, true);
		CircleLayout cl = (CircleLayout) view.findViewById(R.id.circleLayout1);
		FloatingSoftKeysApplication.readAppListFromFile(getApplicationContext());	
		cl = prepareHardcodedButton(R.drawable.power_normal, cl, false);
		cl = prepareHardcodedButton(R.drawable.power_long, cl, false);
		cl = prepareHardcodedButton(R.drawable.volume, cl, false);		
		for(AppInfo a : FloatingSoftKeysApplication.selectedAppsList)
		{
			try 
			{
				ImageView imgView = new ImageView(getApplicationContext());
				ApplicationInfo appInfo;				
				appInfo = getPackageManager().getApplicationInfo(a.packageName, 0);			
				Drawable icon = getPackageManager().getApplicationIcon(appInfo);
				icon.setBounds(0, 0, 32, 32);
				icon.setAlpha(127);
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion < android.os.Build.VERSION_CODES.JELLY_BEAN)		        
					imgView.setBackgroundDrawable(icon);
				else
					imgView.setBackground(icon);
				LayoutParams shortcutParam = new LayoutParams(FloatingSoftKeysApplication.getDpInPix(32), FloatingSoftKeysApplication.getDpInPix(32));
				imgView.setLayoutParams(shortcutParam);				
				cl.addView(imgView);
			}
			catch (NameNotFoundException e) 
			{
				FloatingSoftKeysApplication.selectedAppsList.remove(FloatingSoftKeysApplication.selectedAppsList.indexOf(a));
				FloatingSoftKeysApplication.writeAppListToFile(getApplicationContext());
				e.printStackTrace();
			}
		}
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params) 
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;			
			}

			@Override
			protected void onPostExecute(Void result) 
			{
				int xy[] = new int[2];
				Window window = getWindow(FloatingSoftKeysApplication.shortcutId);
				window.getLocationOnScreen(xy);
				offsetY = xy[1];
				int biggerDim = (4 * FloatingSoftKeysApplication.getSizeInPix()) + (3 * FloatingSoftKeysApplication.getSpacingInPix());
				double ratio = 1.5;
				biggerDim *= ratio;
				int sTop = centerY - (biggerDim/2) - offsetY;
				updateViewLayout(FloatingSoftKeysApplication.shortcutId, new StandOutLayoutParams(FloatingSoftKeysApplication.shortcutId, biggerDim, biggerDim, xy[0], sTop));
				super.onPostExecute(result);
			}
			
			
		}.execute();
	}
	
	public void createVolumeControl(int id, FrameLayout frame)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.volumecontrol, frame, true);
		Button buttonPlus = (Button) view.findViewById(R.id.button1);
		Button buttonMinus = (Button) view.findViewById(R.id.button2);
		volumeHideTask = new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params) 
			{
				try 
				{
					while(remainingTime>0)
					{
						Thread.sleep(1);
						remainingTime--;
					}
					
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
				close(FloatingSoftKeysApplication.volumeId);
				FloatingSoftKeysApplication.showVolumeControlRequested = false;				
				FloatingSoftKeysApplication.volumeId = -1;
				super.onPostExecute(result);
			}
			
		}.execute();
		buttonPlus.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(myEventHandler==null)
					myEventHandler = new EventHandler(getApplicationContext());
				myEventHandler.sendKeys(KeyEvent.KEYCODE_VOLUME_UP);	
				vibrate(20, false);
				remainingTime=4000;
			}
			
		});
		buttonMinus.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(myEventHandler==null)
					myEventHandler = new EventHandler(getApplicationContext());
				myEventHandler.sendKeys(KeyEvent.KEYCODE_VOLUME_DOWN);	
				vibrate(20, false);
				remainingTime=4000;
			}
			
		});
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void createAndAttachView(int id, FrameLayout frame)
	{	
		int biggerDim = windowHeight>windowWidth?windowHeight:windowWidth;
		final int currentapiVersion = android.os.Build.VERSION.SDK_INT;		
		if(id==StandOutWindow.DEFAULT_ID)
			THRESHOLD =  biggerDim/2;
		FloatingSoftKeysApplication.displayMetrics = this.getResources().getDisplayMetrics();		
		if(FloatingSoftKeysApplication.showShortcutRequested)
		{
			createShortcut(id, frame);
			return;
		}
		if(FloatingSoftKeysApplication.showVolumeControlRequested)
		{
			createVolumeControl(id, frame);
			return;
		}
		thisId = id;
		final int idx = id;		
		oldOrientation = getResources().getConfiguration().orientation;
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.buttons, frame, true);
		if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("horizontal", true))
		{
			windowHeight = FloatingSoftKeysApplication.getSizeInPix();
			windowWidth = (4 * FloatingSoftKeysApplication.getSizeInPix()) + (3 * FloatingSoftKeysApplication.getSpacingInPix());
			LinearLayout buttonLayout = (LinearLayout)view.findViewById(R.id.buttonLayout);
			buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
			
		}
		else
		{
			windowWidth = FloatingSoftKeysApplication.getSizeInPix();
			windowHeight = (4 * FloatingSoftKeysApplication.getSizeInPix()) + (3 * FloatingSoftKeysApplication.getSpacingInPix());
			LinearLayout buttonLayout = (LinearLayout)view.findViewById(R.id.buttonLayout);
			buttonLayout.setOrientation(LinearLayout.VERTICAL);
		}
		currentLeft = (FloatingSoftKeysApplication.displayMetrics.widthPixels - windowWidth) / 2;
		currentTop = (FloatingSoftKeysApplication.displayMetrics.heightPixels - windowHeight) / 2;
		sp1 = (Button)view.findViewById(R.id.space1);
		sp2 = (Button)view.findViewById(R.id.space2);
		sp3 = (Button)view.findViewById(R.id.space3);
		sp1.setClickable(false);
		sp2.setClickable(false);
		sp3.setClickable(false);
		int height = windowHeight>windowWidth?FloatingSoftKeysApplication.getSpacingInPix():windowHeight;
		int width = windowHeight>windowWidth?windowWidth:FloatingSoftKeysApplication.getSpacingInPix();
		LinearLayout.LayoutParams space = new LinearLayout.LayoutParams(width, height);
		sp1.setLayoutParams(space);
		sp2.setLayoutParams(space);
		sp3.setLayoutParams(space);
		backButton = (Button)view.findViewById(R.id.buttonBack);
		LinearLayout.LayoutParams rel_btn = new LinearLayout.LayoutParams(
        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rel_btn.height = FloatingSoftKeysApplication.getSizeInPix();
        rel_btn.width = FloatingSoftKeysApplication.getSizeInPix();
		backButton.setLayoutParams(rel_btn);		
		backButton.setOnClickListener(new OnClickListener()		
		{

			@Override
			public void onClick(View v) 
			{					
				if(myEventHandler==null)
					myEventHandler = new EventHandler(getApplicationContext());
				myEventHandler.sendKeys(KeyEvent.KEYCODE_BACK);
				vibrate(20, false);
								
			}
		});
		backButton.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v) 
			{
				boolean collapseToNotif = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notifcollapse", false);
				if(collapseToNotif)
				{
					if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN)
					{
						getWindow(thisId).setVisibility(View.GONE);
					}
					else
					{
						hide(thisId);						
					}
				}
				else
				{
					if(windowHeight < windowWidth)
						collapseToLeft(idx);
					else
						stickToLeft(idx);
				}
				return true;
			}			
		});		
		backButton.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				double distance;
				boolean correctDirection = false;
				int biggerDim;
				switch(event.getAction())
				{
				case MotionEvent.ACTION_UP:					
					lockEndX = event.getRawX();
					lockEndY = event.getRawY();
					distance = windowWidth>windowHeight?getDistance(lockInitX, 0, lockEndX, 0):getDistance(0, lockInitY, 0, lockEndY);					
					biggerDim = windowWidth>windowHeight?windowWidth:windowHeight;
					if(windowWidth>windowHeight?lockInitX<lockEndX:lockInitY<lockEndY)						
						correctDirection = true;					
					if(distance > biggerDim - FloatingSoftKeysApplication.getSizeInPix() && correctDirection)
					{
						vibrate(50, true);						
						Toast.makeText(getApplicationContext(), "Bar locked", Toast.LENGTH_SHORT).show();
						dragButton.setLongClickable(true);
						OnClickListener blockClicks = new OnClickListener()
						{

							@Override
							public void onClick(View v) 
							{
								Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
								
							}
							
						};
						sp1.setClickable(true);
						sp2.setClickable(true);
						sp3.setClickable(true);
						sp1.setOnClickListener(blockClicks);
						sp2.setOnClickListener(blockClicks);
						sp3.setOnClickListener(blockClicks);		
						dragButton.setOnLongClickListener(new OnLongClickListener()
						{

							@Override
							public boolean onLongClick(View arg0)
							{
								Toast.makeText(getApplicationContext(), "Bar unlocked", Toast.LENGTH_SHORT).show();
								dragButton.setOnLongClickListener(null);
								dragButton.setOnClickListener(null);
								dragButton.setClickable(false);
								dragButton.setLongClickable(false);
								sp1.setOnClickListener(null);
								sp2.setOnClickListener(null);
								sp3.setOnClickListener(null);
								sp1.setClickable(false);
								sp2.setClickable(false);
								sp3.setClickable(false);
								return true;
							}
							
						});
					}					
					break;
				
				case MotionEvent.ACTION_MOVE:
					lockEndX = event.getRawX();
					lockEndY = event.getRawY();
					distance = windowWidth>windowHeight?getDistance(lockInitX, 0, lockEndX, 0):getDistance(0, lockInitY, 0, lockEndY);
					correctDirection = false;
					biggerDim = windowWidth>windowHeight?windowWidth:windowHeight;
					if(windowWidth>windowHeight?lockInitX<lockEndX:lockInitY<lockEndY)						
						correctDirection = true;					
					if(distance > biggerDim - FloatingSoftKeysApplication.getSizeInPix() && correctDirection && !showedLockReleaseNotifAlready)
					{
						vibrate(20, true);
						Toast.makeText(getApplicationContext(), "Release now to lock position", Toast.LENGTH_SHORT).show();
						showedLockReleaseNotifAlready = true;
					}		
					else if(distance < biggerDim - FloatingSoftKeysApplication.getSizeInPix() && showedLockReleaseNotifAlready)
					{
						vibrate(20, true);
						showedLockReleaseNotifAlready = false;
					}
					break;
					
				case MotionEvent.ACTION_DOWN:
					lockInitX = event.getRawX();
					lockInitY = event.getRawY();
					showedLockReleaseNotifAlready = false;
					break;
					
				
					
				}				
				return false;
			}		
			
		});
		menuButton = (Button)view.findViewById(R.id.buttonMenu);
		menuButton.setLayoutParams(rel_btn);		
		menuButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{				
				if(myEventHandler==null)
					myEventHandler = new EventHandler(getApplicationContext());
				myEventHandler.sendKeys(KeyEvent.KEYCODE_MENU);	
				vibrate(20, false);
			}
		});
		menuButton.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v) 
			{
				try 
				{
					Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
					Method getService = serviceManagerClass.getMethod("getService", String.class);
					IBinder retbinder = (IBinder) getService.invoke(serviceManagerClass, "statusbar");
					Class<?> statusBarClass = Class.forName(retbinder.getInterfaceDescriptor());
					Object statusBarObject = statusBarClass.getClasses()[0].getMethod("asInterface", IBinder.class).invoke(null, new Object[] { retbinder });
					Method clearAll = statusBarClass.getMethod("toggleRecentApps");
					clearAll.setAccessible(true);
					clearAll.invoke(statusBarObject);
				} 
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				}
				catch (IllegalArgumentException e) 
				{
					e.printStackTrace();
				} 
				catch (IllegalAccessException e) 
				{
					e.printStackTrace();
				} 
				catch (InvocationTargetException e) 
				{
					e.printStackTrace();
				}
				catch (NoSuchMethodException e) 
				{
					e.printStackTrace();
				} 
				catch (RemoteException e) 
				{
					e.printStackTrace();
				}
				return true;
			}			
		});
		homeButton = (Button)view.findViewById(R.id.buttonHome);
		homeButton.setLayoutParams(rel_btn);		
		homeButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{				
				if(myEventHandler==null)
					myEventHandler = new EventHandler(getApplicationContext());
				myEventHandler.sendKeys(KeyEvent.KEYCODE_HOME);		
				vibrate(20, false);
			}
		});
		homeButton.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v) 
			{
				if(FloatingSoftKeysApplication.shortcutId == -1)
					FloatingSoftKeysApplication.shortcutId = getUniqueId();				
				FloatingSoftKeysApplication.showShortcutRequested = true;
				showSelectedApp = null;
				updateWindowLocation(thisId);				
				centerX = currentLeft + (windowWidth/2);
				centerY = currentTop + (windowHeight/2);
				StandOutWindow.show(ButtonBar.this, ButtonBar.class, FloatingSoftKeysApplication.shortcutId);
				return true;
			}			
		});
		homeButton.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				switch(event.getAction())
				{
				case MotionEvent.ACTION_UP:
					endX = event.getRawX();
					endY = event.getRawY();
					if(FloatingSoftKeysApplication.showShortcutRequested)
					{
						close(FloatingSoftKeysApplication.shortcutId);
						FloatingSoftKeysApplication.showShortcutRequested = false;
						int newRingItem = getRingItemFromAngle(getAngleInDegrees(centerX, centerY, endX, endY, OFFSET), getDistance(centerX, centerY, endX, endY));
						if(newRingItem<0)
						{
							
						}
						else if(newRingItem==0)
						{
							if(myEventHandler==null)
								myEventHandler = new EventHandler(getApplicationContext());
							myEventHandler.sendKeys(KeyEvent.KEYCODE_POWER);		
							vibrate(20, false);
						}
						else if(newRingItem==1)
						{
							if(myEventHandler==null)
								myEventHandler = new EventHandler(getApplicationContext());
							myEventHandler.sendDownTouchKeys(KeyEvent.KEYCODE_POWER);
							new AsyncTask<Void, Void, Void>()
							{

								@Override
								protected Void doInBackground(Void... params)
								{
									try 
									{
										Thread.sleep(1500);
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
									myEventHandler.sendUpTouchKeys(KeyEvent.KEYCODE_POWER);
									super.onPostExecute(result);
								}
								
								
								
							}.execute();
							vibrate(20, false);
						}
						else if(newRingItem==2)
						{
							if(FloatingSoftKeysApplication.volumeId!=-1)
								remainingTime = 4000;
							else
							{
								FloatingSoftKeysApplication.showVolumeControlRequested = true;								
								remainingTime = 4000;
								FloatingSoftKeysApplication.volumeId = getUniqueId();
								StandOutWindow.show(getApplicationContext(), ButtonBar.class, FloatingSoftKeysApplication.volumeId);
							}
						}
						else
						{
							Intent i;							
							try 
							{
							    i = getPackageManager().getLaunchIntentForPackage(FloatingSoftKeysApplication.selectedAppsList.get(newRingItem - 3).packageName);
							    if (i == null)
							        throw new PackageManager.NameNotFoundException();
							    i.addCategory(Intent.CATEGORY_LAUNCHER);
							    startActivity(i);
							} catch (PackageManager.NameNotFoundException e) 
							{

							}
						}
					}
					break;
				
				case MotionEvent.ACTION_MOVE:
					endX = event.getRawX();
					endY = event.getRawY();					
					int newRingItem = getRingItemFromAngle(getAngleInDegrees(centerX, centerY, endX, endY, OFFSET), getDistance(centerX, centerY, endX, endY));
					if(newRingItem!=currentRingItem && FloatingSoftKeysApplication.showShortcutRequested)
					{
						vibrate(20, true);						
						currentRingItem = newRingItem;
						try
						{
							Bundle bundle = new Bundle();
							bundle.putInt("index", newRingItem);
							sendData(getApplicationContext(), ButtonBar.class, FloatingSoftKeysApplication.shortcutId, 35, bundle, ButtonBar.class, thisId);
							if(showSelectedApp!=null)
								showSelectedApp.cancel();
							String appLabel = new String();
							switch(newRingItem)
							{			
							case -1:
								break;
								
							case 0:
								appLabel = getString(R.string.a_lock);
								break;
							
							case 1:
								appLabel = getString(R.string.a_lock_long);
								break;
							
							case 2:
								appLabel = getString(R.string.a_vol);
								break;
							
							default:
								appLabel = new String(""+getPackageManager().getApplicationInfo(FloatingSoftKeysApplication.selectedAppsList.get(newRingItem - 3).packageName, PackageManager.PERMISSION_GRANTED).loadLabel(getPackageManager()));
								break;
							}
							if(newRingItem>-1)
							{
								showSelectedApp = Toast.makeText(getApplicationContext(), getString(R.string.a_selected) + ": " + appLabel, Toast.LENGTH_SHORT);							
								showSelectedApp.show();
							}
						}
						catch(Exception e)
						{
							Toast.makeText(getApplicationContext(), "Exception raised", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
					break;
				
					
				}				
				return false;
			}		
			
		});
		dragButton = (ImageView)view.findViewById(R.id.imgDrag);
		dragButton.setLayoutParams(rel_btn);
		dragButton.getParent().requestDisallowInterceptTouchEvent(false);
		loadTheme(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("theme_name", getString(R.string.default_theme)));		
		Drawable backDrawable = getResources().getDrawable(R.drawable.back); 
		Drawable menuDrawable = getResources().getDrawable(R.drawable.menu); 
		Drawable homeDrawable = getResources().getDrawable(R.drawable.home);		
		Drawable dragDrawable = getResources().getDrawable(R.drawable.drag);	
		if(FloatingSoftKeysApplication.customBack!=null)
		{
			backDrawable = FloatingSoftKeysApplication.customBack; 
			menuDrawable = FloatingSoftKeysApplication.customMenu; 
			homeDrawable = FloatingSoftKeysApplication.customHome;		
			dragDrawable = FloatingSoftKeysApplication.customDrag;		
		}		
		backDrawable.setAlpha(255 - ((255 * FloatingSoftKeysApplication.transparency) / 100));
		menuDrawable.setAlpha(255 - ((255 * FloatingSoftKeysApplication.transparency) / 100));
		homeDrawable.setAlpha(255 - ((255 * FloatingSoftKeysApplication.transparency) / 100));
		if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("hidedrag", false))
			dragDrawable.setAlpha(255 - ((255 * FloatingSoftKeysApplication.transparency) / 100));
		else
			dragDrawable.setAlpha(0);
		if (currentapiVersion < android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
			
    		backButton.setBackgroundDrawable(backDrawable);
			menuButton.setBackgroundDrawable(menuDrawable);
			homeButton.setBackgroundDrawable(homeDrawable);
			dragButton.setBackgroundDrawable(dragDrawable);
        } 
        else
        {
        	backButton.setBackground(backDrawable);
    		menuButton.setBackground(menuDrawable);
    		homeButton.setBackground(homeDrawable);
    		dragButton.setBackground(dragDrawable);
        }
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN)
		{
			IntentFilter ifNotifPressed = new IntentFilter();
			ifNotifPressed.addAction("FSKNotifIntentBack");
			ifNotifPressed.addAction("FSKNotifIntentHome");
			ifNotifPressed.addAction("FSKNotifIntentMenu");
			ifNotifPressed.addAction("FSKNotifIntentShow");
			ifNotifPressed.addAction("FSKNotifIntentLockLong");
			ifNotifPressed.addAction("FSKNotifIntentClose");
			brNotifPressed = new BroadcastReceiver()
			{

				@Override
				public void onReceive(Context arg0, Intent arg1) 
				{
					if(arg1.getAction().contentEquals("FSKNotifIntentBack"))
					{
						if(myEventHandler==null)
							myEventHandler = new EventHandler(getApplicationContext());
						myEventHandler.sendKeys(KeyEvent.KEYCODE_BACK);	
					}
					else if(arg1.getAction().contentEquals("FSKNotifIntentHome"))
					{
						if(myEventHandler==null)
							myEventHandler = new EventHandler(getApplicationContext());
						myEventHandler.sendKeys(KeyEvent.KEYCODE_HOME);
					}
					else if(arg1.getAction().contentEquals("FSKNotifIntentMenu"))
					{
						if(myEventHandler==null)
							myEventHandler = new EventHandler(getApplicationContext());
						myEventHandler.sendKeys(KeyEvent.KEYCODE_MENU);
					}
					else if(arg1.getAction().contentEquals("FSKNotifIntentShow"))
					{
						getWindow(thisId).setVisibility(View.VISIBLE);
					}
					else if(arg1.getAction().contentEquals("FSKNotifIntentLockLong"))
					{
						if(myEventHandler==null)
							myEventHandler = new EventHandler(getApplicationContext());
						myEventHandler.sendDownTouchKeys(KeyEvent.KEYCODE_POWER);
						new AsyncTask<Void, Void, Void>()
						{

							@Override
							protected Void doInBackground(Void... params)
							{
								try 
								{
									Thread.sleep(1500);
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
								myEventHandler.sendUpTouchKeys(KeyEvent.KEYCODE_POWER);
								super.onPostExecute(result);
							}							
						}.execute();
					}
					else if(arg1.getAction().contentEquals("FSKNotifIntentClose"))
					{
						getWindow(thisId).setVisibility(View.VISIBLE);
						StandOutWindow.closeAll(ButtonBar.this, ButtonBar.class);
					}
				}
				
			};
			registerReceiver(brNotifPressed, ifNotifPressed);
			
		}
		ifConfigChanged = new IntentFilter();
		ifConfigChanged.addAction("android.intent.action.CONFIGURATION_CHANGED");
		brConfigChanged = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context context, Intent intent) 
			{
				Configuration config = getResources().getConfiguration();
				if(config.orientation==oldOrientation)
					return;
				oldOrientation = config.orientation;				
				FloatingSoftKeysApplication.displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
				int dim1 = FloatingSoftKeysApplication.displayMetrics.heightPixels;
				int dim2 = FloatingSoftKeysApplication.displayMetrics.widthPixels;
				float curLeft, curTop, newLeft, newTop;
				curLeft = (float) (currentLeft + (windowWidth/2));
				curTop = (float) (currentTop + (windowHeight/2));
				float ratioNewLeft = curLeft / dim1;
				float ratioNewTop = curTop / dim2;
				newLeft = ratioNewLeft * dim2;
				newTop =  ratioNewTop * dim1;
				newLeft = newLeft - (windowWidth/2);
				newTop = newTop - (windowHeight/2);
				currentLeft = (int)newLeft;
				currentTop = (int)newTop;
				updateViewLayout(thisId, new StandOutLayoutParams(thisId, windowWidth, windowHeight, (int)newLeft, (int)newTop));			
							
			}
			
		};
		registerReceiver(brConfigChanged, ifConfigChanged);
		
	}	

	@Override
	public boolean onClose(int id, Window window) 
	{
		if(FloatingSoftKeysApplication.showShortcutRequested || FloatingSoftKeysApplication.showVolumeControlRequested)
			return super.onClose(id, window);
		try
		{
			unregisterReceiver(brConfigChanged);
			unregisterReceiver(brNotifPressed);			
			FloatingSoftKeysApplication.isOpen = false;
		}
		catch(Exception e)
		{
			
			
		}		
		return super.onClose(id, window);
	}

	@Override
	public StandOutLayoutParams getParams(int id, Window window) 
	{		
		FloatingSoftKeysApplication.displayMetrics = getResources().getDisplayMetrics();
		if(FloatingSoftKeysApplication.showShortcutRequested)
		{
			int biggerDim = (4 * FloatingSoftKeysApplication.getSizeInPix()) + (3 * FloatingSoftKeysApplication.getSpacingInPix());
			double ratio = 1.5;
			biggerDim *= ratio;
			int sLeft = centerX - (biggerDim/2); 			
			return new StandOutLayoutParams(id, biggerDim, biggerDim, sLeft, StandOutLayoutParams.TOP);
		}
		if(FloatingSoftKeysApplication.showVolumeControlRequested)
		{
			int dp = FloatingSoftKeysApplication.getDpInPix(32);
			return new StandOutLayoutParams(id, dp*3, dp, StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER);
		}
		
		if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("horizontal", true))
		{
			windowHeight = FloatingSoftKeysApplication.getSizeInPix();
			windowWidth = (4 * FloatingSoftKeysApplication.getSizeInPix()) + (3 * FloatingSoftKeysApplication.getSpacingInPix());
			
		}
		else
		{
			windowWidth = FloatingSoftKeysApplication.getSizeInPix();
			windowHeight = (4 * FloatingSoftKeysApplication.getSizeInPix()) + (3 * FloatingSoftKeysApplication.getSpacingInPix());
		}
		return new StandOutLayoutParams(id, windowWidth, windowHeight,
				StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER);		
	}

	@Override
	public int getFlags(int id) 
	{
		return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE
				| StandOutFlags.FLAG_WINDOW_HIDE_ENABLE;
	}
	
	private void createSpringEffect()
	{
		int []xy = new int [2];
		getWindow(thisId).getLocationOnScreen(xy);
		int buttonLoc[] = new int[4];
		for(int i=0;i<4;i++)
			buttonLoc[i] = i * (FloatingSoftKeysApplication.getSizeInPix() + FloatingSoftKeysApplication.getSpacingInPix());
		if(xy[0] >= 0)
		{
			LinearLayout.LayoutParams rel_btn = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        rel_btn.height = FloatingSoftKeysApplication.getSizeInPix();
	        rel_btn.width = FloatingSoftKeysApplication.getSizeInPix();
	        rel_btn.rightMargin = 0;
	        rel_btn.leftMargin = 0;
	        backButton.setLayoutParams(rel_btn);
	        homeButton.setLayoutParams(rel_btn);
	        menuButton.setLayoutParams(rel_btn);
	        dragButton.setLayoutParams(rel_btn);
			return;
		}
		if(-xy[0]>=buttonLoc[0])
		{
			LinearLayout.LayoutParams rel_btn = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        rel_btn.height = FloatingSoftKeysApplication.getSizeInPix();
	        rel_btn.width = FloatingSoftKeysApplication.getSizeInPix();
	        rel_btn.rightMargin = xy[0];
	        rel_btn.leftMargin = -xy[0];
	        backButton.setLayoutParams(rel_btn);
		}
		
		if(-xy[0]>=buttonLoc[1])
		{
			LinearLayout.LayoutParams rel_btn = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        rel_btn.height = FloatingSoftKeysApplication.getSizeInPix();
	        rel_btn.width = FloatingSoftKeysApplication.getSizeInPix();
	        rel_btn.leftMargin = -buttonLoc[1] - xy[0];
	        rel_btn.rightMargin = buttonLoc[1] + xy[0];
	        homeButton.setLayoutParams(rel_btn);
		}
		
		if(-xy[0]>=buttonLoc[2])
		{
			LinearLayout.LayoutParams rel_btn = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        rel_btn.height = FloatingSoftKeysApplication.getSizeInPix();
	        rel_btn.width = FloatingSoftKeysApplication.getSizeInPix();
	        rel_btn.leftMargin = -buttonLoc[2] - xy[0];
	        rel_btn.rightMargin = buttonLoc[2] + xy[0];
	        menuButton.setLayoutParams(rel_btn);
		}        
	}

	private void updateWindowLocation(int id)
	{
		try
		{			
			int xy[] = new int[2];
			Window window = getWindow(id);			
			window.getLocationOnScreen(xy);		
			currentLeft = xy[0];			
			currentTop = xy[1];
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}	

	@Override
	public void onMove(int id, Window window, View view, MotionEvent event) {
		//updateWindowLocation(id);
		super.onMove(id, window, view, event);
	}

	@Override
	public String getPersistentNotificationMessage(int id) 
	{
		return getString(R.string.click_to_close);
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) 
	{		
		return StandOutWindow.getCloseAllIntent(this, ButtonBar.class);
	}	

	private void collapseToLeft(final int id)
	{			
		updateWindowLocation(id);
		final long duration = 600;
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();		
		final Interpolator interpolator = new DecelerateInterpolator((float) 1.8);		
		final int dx = StandOutLayoutParams.LEFT - (3 * FloatingSoftKeysApplication.getSizeInPix() + 3 * FloatingSoftKeysApplication.getSpacingInPix() );
		final int sx = currentLeft;
		final int sy = currentTop;
		offsetY = 0;
		handler.post(new Runnable() 
		{
		    public void run() 
		    {		    	
		        long elapsed = SystemClock.uptimeMillis() - start;
		        float t = interpolator.getInterpolation((float) elapsed / duration);
		        double x = t * dx + (1 - t) * sx;
		        updateViewLayout(id, new StandOutLayoutParams(id, 4 * FloatingSoftKeysApplication.getSizeInPix()+ 3 * FloatingSoftKeysApplication.getSpacingInPix(), FloatingSoftKeysApplication.getSizeInPix() , (int) x, sy - offsetY));
		        updateWindowLocation(id);
		        if(offsetY==0)
		        	offsetY = currentTop - sy;
		        createSpringEffect();
		        if (t < 1.0)
		        {
		            // Post again 10ms later.
		            handler.postDelayed(this, 5);
		        }	
		        else
		        {
		        	updateViewLayout(id, new StandOutLayoutParams(id, 4 * FloatingSoftKeysApplication.getSizeInPix()+ 3 * FloatingSoftKeysApplication.getSpacingInPix(), FloatingSoftKeysApplication.getSizeInPix(), StandOutLayoutParams.LEFT - (3 * FloatingSoftKeysApplication.getSizeInPix() + 3 * FloatingSoftKeysApplication.getSpacingInPix()), sy - offsetY));
		        	updateWindowLocation(id);
		        	dragButton.setOnClickListener(new OnClickListener()
		        	{

						@Override
						public void onClick(View v) 
						{
							updateWindowLocation(id);				        							
							final long restoreduration = 600;
							final Handler restorehandler = new Handler();
							final long restorestart = SystemClock.uptimeMillis();		
							final Interpolator restoreinterpolator = new DecelerateInterpolator((float) 1.8);		
							final int restoresx = StandOutLayoutParams.LEFT - (3 * FloatingSoftKeysApplication.getSizeInPix() + 3 * FloatingSoftKeysApplication.getSpacingInPix() );
							final int restoredx = 0;
							final int restoresy = currentTop;
							offsetY = 0;
							restorehandler.post(new Runnable() 
							{
							    public void run() 
							    {
							        long elapsed = SystemClock.uptimeMillis() - restorestart;
							        float t = restoreinterpolator.getInterpolation((float) elapsed / restoreduration);
							        double x = t * restoredx + (1 - t) * restoresx;
							        updateViewLayout(id, new StandOutLayoutParams(id, 4 * FloatingSoftKeysApplication.getSizeInPix()+ 3 * FloatingSoftKeysApplication.getSpacingInPix(), FloatingSoftKeysApplication.getSizeInPix() , (int) x, restoresy - offsetY));
							        updateWindowLocation(id);
							        if(offsetY==0)
							        	offsetY = currentTop - sy;
							        createSpringEffect();
							        if (t < 1.0)
							        {
							            dragButton.setOnClickListener(null);
							            dragButton.setClickable(false);
							        	restorehandler.postDelayed(this, 5);
							        }	
							        else
							        {
							        	updateWindowLocation(id);							        	
							        	updateViewLayout(id, new StandOutLayoutParams(id, 4 * FloatingSoftKeysApplication.getSizeInPix()+ 3 * FloatingSoftKeysApplication.getSpacingInPix(), FloatingSoftKeysApplication.getSizeInPix(), 0, restoresy - offsetY));
							        								    		
							        }
							    }
							});
						}
		        		
		        	});
		        }
		    }
		});
		
				
	}
	
	private void stickToLeft(final int id)
	{			
		updateWindowLocation(id);
		final long duration = 600;
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();		
		final Interpolator interpolator = new DecelerateInterpolator((float) 1.8);		
		final int dx = 0;
		final int sx = currentLeft;
		final int sy = currentTop - (FloatingSoftKeysApplication.getSizeInPix() / 2);
		handler.post(new Runnable() 
		{
		    public void run() 
		    {
		        long elapsed = SystemClock.uptimeMillis() - start;
		        float t = interpolator.getInterpolation((float) elapsed / duration);
		        double x = t * dx + (1 - t) * sx;
		        updateViewLayout(id, new StandOutLayoutParams(id, windowWidth, windowHeight, (int) x, sy));
		        createSpringEffect();
		        if (t < 1.0)
		        {
		            // Post again 10ms later.
		            handler.postDelayed(this, 5);
		        }	
		        else
		        {
		        	updateViewLayout(id, new StandOutLayoutParams(id, windowWidth, windowHeight, 0, sy));	
		        	updateWindowLocation(id);	  		
		        	
		        }
		    }
		});				
	}	
	
	private void vibrate(int time, boolean force)
	{
		if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("vibrate", false) || force)
		{
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(time);
		}
	}	
	
	private double getAngleInDegrees(double x1, double y1, double x2, double y2, int offset)
	{		
		double angle = Math.toDegrees(Math.atan2((y2-y1),(x2-x1)));
		angle = angle - offset;
		if(angle<0)
			angle = angle + 360;		
		return angle;
	}
	
	private double getDistance(double x1, double y1, double x2, double y2)
	{
		double dx = x2 - x1;
		double dy = y2 - y1;
		return Math.sqrt((dx*dx) + (dy*dy));
				
	}
	
	private int getRingItemFromAngle(double angleInDegrees, double distance)
	{
		if(distance<THRESHOLD)
			return -1;
		FloatingSoftKeysApplication.readAppListFromFile(getApplicationContext());
		int totalCount = FloatingSoftKeysApplication.selectedAppsList.size() + 3;	
		int angle = (int) angleInDegrees;		
		return (angle * totalCount) / 360;
	}
	
	

	@SuppressWarnings("deprecation")
	@Override
	public void onReceiveData(int id, int requestCode, Bundle data,
			Class<? extends StandOutWindow> fromCls, int fromId) {
		if(requestCode==35 && id==FloatingSoftKeysApplication.shortcutId)
		{
			Window window = getWindow(id);			
			int index = data.getInt("index");			
			LinearLayout lila = (LinearLayout) window.findViewById(R.id.LinearLayout1);
			lila.removeAllViews();
			CircleLayout cl = new CircleLayout(getApplicationContext());			
			cl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			cl = prepareHardcodedButton(R.drawable.power_normal, cl, index==0);
			cl = prepareHardcodedButton(R.drawable.power_long, cl, index==1);
			cl = prepareHardcodedButton(R.drawable.volume, cl, index==2);	
			
			int i = 3;
			FloatingSoftKeysApplication.readAppListFromFile(getApplicationContext());		
			for(AppInfo a : FloatingSoftKeysApplication.selectedAppsList)
			{
				try 
				{					
					ImageView imgView = new ImageView(getApplicationContext());
					ApplicationInfo appInfo;				
					appInfo = getPackageManager().getApplicationInfo(a.packageName, 0);			
					Drawable icon = getPackageManager().getApplicationIcon(appInfo);
					icon.setBounds(0, 0, 32, 32);
					if(i!=index)
						icon.setAlpha(127);
					else
						icon.setAlpha(255);
					int currentapiVersion = android.os.Build.VERSION.SDK_INT;
					if (currentapiVersion < android.os.Build.VERSION_CODES.JELLY_BEAN)		        
						imgView.setBackgroundDrawable(icon);
					else
						imgView.setBackground(icon);
					LayoutParams shortcutParam = new LayoutParams(FloatingSoftKeysApplication.getDpInPix(32), FloatingSoftKeysApplication.getDpInPix(32));
					imgView.setLayoutParams(shortcutParam);				
					cl.addView(imgView);
					i++;
				}
				catch (NameNotFoundException e) 
				{
					FloatingSoftKeysApplication.selectedAppsList.remove(FloatingSoftKeysApplication.selectedAppsList.indexOf(a));
					FloatingSoftKeysApplication.writeAppListToFile(getApplicationContext());
					e.printStackTrace();
				}
			}
			lila.addView(cl);			
		}
		super.onReceiveData(id, requestCode, data, fromCls, fromId);
	}
	

	@SuppressWarnings("deprecation")
	private CircleLayout prepareHardcodedButton(int id, CircleLayout cl, boolean selected)
	{
		ImageView imgView = new ImageView(getApplicationContext());
		Drawable icon = getResources().getDrawable(id);
		icon.setBounds(0, 0, 32, 32);
		if(!selected)
			icon.setAlpha(127);
		else
			icon.setAlpha(255);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < android.os.Build.VERSION_CODES.JELLY_BEAN)		        
			imgView.setBackgroundDrawable(icon);
		else
			imgView.setBackground(icon);
		LayoutParams shortcutParam = new LayoutParams(FloatingSoftKeysApplication.getDpInPix(32), FloatingSoftKeysApplication.getDpInPix(32));
		imgView.setLayoutParams(shortcutParam);		
		cl.addView(imgView);
		return cl;
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
			Toast.makeText(this, getString(R.string.load_failed), Toast.LENGTH_LONG).show();
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPref.edit();								
			editor.putInt("theme", 0);
			editor.putString("theme_name", getString(R.string.default_theme));							
			editor.commit();						
		}		
    }
	

	@Override
	public String getHiddenNotificationMessage(int id) 
	{
		// TODO Auto-generated method stub
		return getString(R.string.min_text);
	}

	@Override
	public Intent getHiddenNotificationIntent(int id) {
		return StandOutWindow.getShowIntent(this, ButtonBar.class, thisId);
	}
	
	@Override
	public int getHiddenIcon() {
		return android.R.drawable.ic_menu_info_details;
	}

	@Override
	public String getHiddenNotificationTitle(int id) {
		return getAppName() + " Hidden";
	}

	@SuppressWarnings("deprecation")
	@Override
	public Notification getPersistentNotification(int id) 
	{
		int icon = getAppIcon();
		long when = System.currentTimeMillis();
		Context c = getApplicationContext();
		String contentTitle = getPersistentNotificationTitle(id);
		String contentText = getPersistentNotificationMessage(id);
		String tickerText = String.format("%s: %s", contentTitle, contentText);
		Intent notificationIntent = getPersistentNotificationIntent(id);
		PendingIntent contentIntent = null;
		if (notificationIntent != null) 
		{
			contentIntent = PendingIntent.getService(this, 0,
					notificationIntent,
					// flag updates existing persistent notification
					PendingIntent.FLAG_UPDATE_CURRENT);
		}
		PendingIntent backIntent = PendingIntent.getBroadcast(this, 0, new Intent("FSKNotifIntentBack"), 0);
		PendingIntent homeIntent = PendingIntent.getBroadcast(this, 1, new Intent("FSKNotifIntentHome"), 0);
		PendingIntent menuIntent = PendingIntent.getBroadcast(this, 2, new Intent("FSKNotifIntentMenu"), 0);
		PendingIntent showIntent = PendingIntent.getBroadcast(this, 3, new Intent("FSKNotifIntentShow"), 0);
		PendingIntent closeIntent = PendingIntent.getBroadcast(this, 4, new Intent("FSKNotifIntentClose"), 0);
		PendingIntent lockLongIntent = PendingIntent.getBroadcast(this, 3, new Intent("FSKNotifIntentLockLong"), 0);
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(c, contentTitle, contentText,
				contentIntent);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN)
		{
			RemoteViews mNotificationTemplate = new RemoteViews(getPackageName(),
	                R.layout.notification_template_base);
			
			mNotificationTemplate.setTextViewText(R.id.notification_base_line_one, contentTitle);
			mNotificationTemplate.setTextViewText(R.id.notification_base_line_two, getString(R.string.min_text));
			mNotificationTemplate.setOnClickPendingIntent(R.id.notification_base_back, backIntent);
			mNotificationTemplate.setOnClickPendingIntent(R.id.notification_base_home, homeIntent);
			mNotificationTemplate.setOnClickPendingIntent(R.id.notification_base_menu, menuIntent);
			mNotificationTemplate.setOnClickPendingIntent(R.id.notification_base_close, closeIntent);
			mNotificationTemplate.setOnClickPendingIntent(R.id.notification_base_lock_long, lockLongIntent);
	       Notification.Builder mBuilder= new Notification.Builder(this)
	                .setSmallIcon(icon)
	                .setContentIntent(showIntent)
	                .setPriority(Notification.PRIORITY_DEFAULT)
	                .setContent(mNotificationTemplate);
	       if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("lpnotif", false))
	    	   mBuilder.setPriority(Notification.PRIORITY_MIN);
	       else
	    	   mBuilder.setPriority(Notification.PRIORITY_MAX);
			notification = mBuilder.build();
	    }
		return notification;		
	}
	
}
