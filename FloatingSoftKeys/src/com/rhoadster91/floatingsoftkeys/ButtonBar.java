package com.rhoadster91.floatingsoftkeys;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class ButtonBar extends StandOutWindow
{
	int currentLeft, currentTop;
	int thisId;
	static Button backButton = null;
	static Button homeButton = null;
	static Button menuButton = null;
	static ImageView dragButton = null;
	static int windowHeight;
	static int windowWidth;
	static DataOutputStream dos;
	static Process backProcess = null;
	static Process homeProcess = null;
	static Process powerProcess = null;
	static Process menuProcess = null;
	static final String exportClasspath = "export CLASSPATH=/system/framework/input.jar";
	static final String app_process_launch = "app_process /system/bin/ com.android.commands.input.Input keyevent ";
		
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{		
		return super.onStartCommand(intent, flags, startId);
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


	@SuppressWarnings("deprecation")
	@Override
	public void createAndAttachView(int id, FrameLayout frame)
	{			
		thisId = id;
		final int idx = id;		
		FloatingSoftKeysApplication.displayMetrics = this.getResources().getDisplayMetrics();    	
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
		View sp1 = (View)view.findViewById(R.id.space1);
		View sp2 = (View)view.findViewById(R.id.space2);
		View sp3 = (View)view.findViewById(R.id.space3);LinearLayout.LayoutParams space = new LinearLayout.LayoutParams(
		
		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        space.height = FloatingSoftKeysApplication.getSpacingInPix();
        space.width = FloatingSoftKeysApplication.getSpacingInPix();
		sp1.setLayoutParams(space);
		sp2.setLayoutParams(space);
		sp3.setLayoutParams(space);
		sp1.getParent().requestTransparentRegion(sp1);
		sp2.getParent().requestTransparentRegion(sp2);
		sp3.getParent().requestTransparentRegion(sp3);
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
				try
				{
					runCommandAsRoot(app_process_launch + KeyEvent.KEYCODE_BACK, 1);
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}									
			}
		});
		backButton.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v) 
			{
				if(windowHeight < windowWidth)
					collapseToLeft(idx);
				else
					stickToLeft(idx);
				return true;
			}			
		});
		menuButton = (Button)view.findViewById(R.id.buttonMenu);
		menuButton.setLayoutParams(rel_btn);		
		menuButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{				
				try 
				{
					runCommandAsRoot(app_process_launch + KeyEvent.KEYCODE_MENU, 4);
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}					
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
				try 
				{
					runCommandAsRoot(app_process_launch + KeyEvent.KEYCODE_HOME, 2);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}			
			}
		});
		homeButton.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v) 
			{
				try
				{
					switch(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("action", R.string.a_none))
					{
					case R.string.a_none:
						break;
						
					case R.string.a_gnow:
						Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						break;					
						
					case R.string.a_lock:
						runCommandAsRoot(app_process_launch + KeyEvent.KEYCODE_POWER, 3);						
						break;					
					}					
					return true;
				}				
				catch(Exception e)
				{
					e.printStackTrace();
					return true;
				}
			}			
		});
		dragButton = (ImageView)view.findViewById(R.id.imgDrag);
		dragButton.setLayoutParams(rel_btn);
		dragButton.getParent().requestDisallowInterceptTouchEvent(false);
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
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
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
		
	}	

	@Override
	public StandOutLayoutParams getParams(int id, Window window) 
	{
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
				| StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE;
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
		int xy[] = new int[2];
		Window window = getWindow(id);
		window.getLocationOnScreen(xy);
		currentLeft = xy[0];
		currentTop = xy[1];
		
	}
	
	@Override
	public String getPersistentNotificationMessage(int id) 
	{
		return getString(R.string.click_to_close);
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) 
	{
		return StandOutWindow.getCloseIntent(this, ButtonBar.class, id);
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
		final int sy = currentTop - (FloatingSoftKeysApplication.getSizeInPix() / 2);
		handler.post(new Runnable() 
		{
		    public void run() 
		    {
		        long elapsed = SystemClock.uptimeMillis() - start;
		        float t = interpolator.getInterpolation((float) elapsed / duration);
		        double x = t * dx + (1 - t) * sx;
		        updateViewLayout(id, new StandOutLayoutParams(id, 4 * FloatingSoftKeysApplication.getSizeInPix()+ 3 * FloatingSoftKeysApplication.getSpacingInPix(), FloatingSoftKeysApplication.getSizeInPix() , (int) x, sy));
		        createSpringEffect();
		        if (t < 1.0)
		        {
		            // Post again 10ms later.
		            handler.postDelayed(this, 5);
		        }	
		        else
		        {
		        	updateViewLayout(id, new StandOutLayoutParams(id, 4 * FloatingSoftKeysApplication.getSizeInPix()+ 3 * FloatingSoftKeysApplication.getSpacingInPix(), FloatingSoftKeysApplication.getSizeInPix(), StandOutLayoutParams.LEFT - (3 * FloatingSoftKeysApplication.getSizeInPix() + 3 * FloatingSoftKeysApplication.getSpacingInPix()), sy));
		        	dragButton.setOnClickListener(new OnClickListener()
		        	{

						@Override
						public void onClick(View v) 
						{
													
							final long restoreduration = 600;
							final Handler restorehandler = new Handler();
							final long restorestart = SystemClock.uptimeMillis();		
							final Interpolator restoreinterpolator = new DecelerateInterpolator((float) 1.8);		
							final int restoresx = StandOutLayoutParams.LEFT - (3 * FloatingSoftKeysApplication.getSizeInPix() + 3 * FloatingSoftKeysApplication.getSpacingInPix() );
							final int restoredx = 0;
							final int restoresy = currentTop - (FloatingSoftKeysApplication.getSizeInPix() / 2);
							restorehandler.post(new Runnable() 
							{
							    public void run() 
							    {
							        long elapsed = SystemClock.uptimeMillis() - restorestart;
							        float t = restoreinterpolator.getInterpolation((float) elapsed / restoreduration);
							        double x = t * restoredx + (1 - t) * restoresx;
							        updateViewLayout(id, new StandOutLayoutParams(id, 4 * FloatingSoftKeysApplication.getSizeInPix()+ 3 * FloatingSoftKeysApplication.getSpacingInPix(), FloatingSoftKeysApplication.getSizeInPix() , (int) x, restoresy));
							        createSpringEffect();
							        if (t < 1.0)
							        {
							            dragButton.setOnClickListener(null);
							            dragButton.setClickable(false);
							        	restorehandler.postDelayed(this, 5);
							        }	
							        else
							        {
							        	updateViewLayout(id, new StandOutLayoutParams(id, 4 * FloatingSoftKeysApplication.getSizeInPix()+ 3 * FloatingSoftKeysApplication.getSpacingInPix(), FloatingSoftKeysApplication.getSizeInPix(), 0, sy));
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
		        }
		    }
		});				
	}
	
	private void runCommandAsRoot(String command, int which) throws IOException
	{
		switch(which)
		{
		case 1:
			if(backProcess==null)			
				backProcess = Runtime.getRuntime().exec("su");			
			dos = new DataOutputStream(backProcess.getOutputStream());
			break;
			
		case 2:
			if(homeProcess==null)
				homeProcess = Runtime.getRuntime().exec("su");			
			dos = new DataOutputStream(homeProcess.getOutputStream());
			break;
			
		case 3:
			if(powerProcess==null)
				powerProcess = Runtime.getRuntime().exec("su");			
			dos = new DataOutputStream(powerProcess.getOutputStream());			
			break;
		case 4:
			if(menuProcess==null)
				menuProcess = Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(menuProcess.getOutputStream());			
			break;
		}		
		dos.writeBytes(exportClasspath + "\n");
		dos.flush();
		dos.writeBytes(command + "\n");
		dos.flush();		
	}
}
