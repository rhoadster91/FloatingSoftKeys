package com.rhoadster91.floatingsoftkeys;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class HelpActivity extends ActionBarActivity 
{

	int selectedButton = -1;
	int viewFlipperPage = 0;
	static Button buttonBackHelp; 
	static Button buttonHomeHelp;
	static Button buttonMenuHelp;
	static Button buttonDragHelp;
	static ViewFlipper viewFlipper;
	boolean autoModeDisable = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);		
		setContentView(R.layout.activity_help);
		viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper1);
		initViewFlipper();
		autoModeDisable = false;
		selectedButton = -1;
		buttonBackHelp = (Button)findViewById(R.id.button1);
		buttonHomeHelp = (Button)findViewById(R.id.button2);
		buttonMenuHelp = (Button)findViewById(R.id.button3);
		buttonDragHelp = (Button)findViewById(R.id.button4);
		buttonBackHelp.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				autoModeDisable = true;
				updateSelectedButton(0);				
			}
			
		});
		buttonHomeHelp.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				autoModeDisable = true;
				updateSelectedButton(1);				
			}
			
		});
		buttonMenuHelp.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				autoModeDisable = true;
				updateSelectedButton(2);				
			}
			
		});
		buttonDragHelp.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				autoModeDisable = true;
				updateSelectedButton(3);				
			}
			
		});
		new AsyncTask<Void, Void, Void>() 
		{			
			@Override
			protected Void doInBackground(Void... params)
			{
				while(!autoModeDisable)
				{
					try 
					{
						Thread.sleep(3000);						
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					publishProgress();
				}
				return null;
			}
			
			
			
			@Override
			protected void onProgressUpdate(Void... values) 
			{
				if(!autoModeDisable)
					updateSelectedButton((selectedButton + 1)%4);			
				super.onProgressUpdate(values);
			}
		}.execute();
	}
	
	private void updateSelectedButton(int selected)
	{
		if(selectedButton==selected)
			return;
		switch(selectedButton)
		{
		case 0:
			animateDeselectButton(buttonBackHelp);
			break;
		case 1:
			animateDeselectButton(buttonHomeHelp);
			break;
		case 2:
			animateDeselectButton(buttonMenuHelp);
			break;
		case 3:
			animateDeselectButton(buttonDragHelp);
			break;			
		}
		selectedButton = selected;
		switch(selectedButton)
		{
		case 0:
			animateSelectButton(buttonBackHelp);
			break;
		case 1:
			animateSelectButton(buttonHomeHelp);
			break;
		case 2:
			animateSelectButton(buttonMenuHelp);
			break;
		case 3:
			animateSelectButton(buttonDragHelp);
			break;			
		}
		updateViewFlipperPage();
	}
	
	private void animateSelectButton(Button b)
	{
		ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, (float)0.5, Animation.RELATIVE_TO_SELF, (float)0.5);				
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setFillEnabled(true);
		scaleAnimation.setDuration(200);	
		scaleAnimation.setInterpolator(new OvershootInterpolator(6f));
		b.startAnimation(scaleAnimation);
	}
	
	private void animateDeselectButton(Button b)
	{
		ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, (float)0.5, Animation.RELATIVE_TO_SELF, (float)0.5);				
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setFillEnabled(true);
		scaleAnimation.setDuration(200);				
		b.startAnimation(scaleAnimation);
	}		
	

	@Override
	protected void onDestroy()
	{
		autoModeDisable = true;
		super.onDestroy();
	}
	
	private void initViewFlipper()
	{
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout lila = (LinearLayout) inflater.inflate(R.layout.helpflipperlayout, null);
		viewFlipper.addView(lila);
		
		lila = (LinearLayout) inflater.inflate(R.layout.helpflipperlayout, null);
		((TextView) lila.findViewById(R.id.tvTitle)).setText(getText(R.string.homebutton));
		((TextView) lila.findViewById(R.id.tvPressAction)).setText(getText(R.string.homebuttonpress));
		((TextView) lila.findViewById(R.id.tvLongPressAction)).setText(getText(R.string.homebuttonlongpress));
		viewFlipper.addView(lila);
		
		lila = (LinearLayout) inflater.inflate(R.layout.helpflipperlayout, null);
		((TextView) lila.findViewById(R.id.tvTitle)).setText(getText(R.string.menubutton));
		((TextView) lila.findViewById(R.id.tvPressAction)).setText(getText(R.string.menubuttonpress));
		((TextView) lila.findViewById(R.id.tvLongPressAction)).setText(getText(R.string.menubuttonlongpress));
		viewFlipper.addView(lila);
		
		lila = (LinearLayout) inflater.inflate(R.layout.helpflipperlayout, null);
		((TextView) lila.findViewById(R.id.tvTitle)).setText(getText(R.string.dragbutton));
		((TextView) lila.findViewById(R.id.tvPressAction)).setText(getText(R.string.dragbuttonpress));
		((TextView) lila.findViewById(R.id.tvLongPressAction)).setText(getText(R.string.dragbuttonlongpress));
		viewFlipper.addView(lila);		
		
		
	}
	
	private void updateViewFlipperPage()
	{
		int dir = 1;
		Animation animFlipInPrevious = AnimationUtils.loadAnimation(this, R.anim.flipinnext);
		Animation animFlipOutPrevious = AnimationUtils.loadAnimation(this, R.anim.flipoutnext);
		Animation animFlipInNext = AnimationUtils.loadAnimation(this, R.anim.flipinprevious);
		Animation animFlipOutNext = AnimationUtils.loadAnimation(this, R.anim.flipoutprevious);

		if(viewFlipperPage<selectedButton)
			dir = 1;
		else if(viewFlipperPage>selectedButton)
			dir = -1;
		while(viewFlipperPage!=selectedButton)
		{
			viewFlipperPage+=dir;
			if(dir==1)
			{
				viewFlipper.setInAnimation(animFlipInNext);
	            viewFlipper.setOutAnimation(animFlipOutNext);
				viewFlipper.showNext();
			}
			else
			{
				viewFlipper.setInAnimation(animFlipInPrevious);
	            viewFlipper.setOutAnimation(animFlipOutPrevious);				
				viewFlipper.showPrevious();
			}
		}
	}

}
