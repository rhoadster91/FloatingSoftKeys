package com.rhoadster91.floatingsoftkeys;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);		
		setContentView(R.layout.activity_about);
		TextView tvVersion = (TextView)findViewById(R.id.tvVersion);
		PackageInfo pInfo;    		
		try 
		{
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String curVersion = pInfo.versionName;			
			tvVersion.setText(getString(R.string.version) + " " + curVersion);			
		} 
		catch (NameNotFoundException e) 
		{
			e.printStackTrace();
		}
		Button sendFeedback = (Button)findViewById(R.id.button1);
		sendFeedback.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent send = new Intent(Intent.ACTION_SENDTO);
				String uriText = "mailto:" + Uri.encode("rhoadster91@gmail.com") + 
				          "?subject=" + Uri.encode("Feedback: Floating Soft Keys") + 
				          "&body=" + Uri.encode("");
				Uri uri = Uri.parse(uriText);
				send.setData(uri);
				startActivity(Intent.createChooser(send, null));
			}
			
		});
		Button leaveRating = (Button)findViewById(R.id.button2);
		leaveRating.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try 
				{
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.rhoadster91.floatingsoftkeys")));
				} 
				catch (android.content.ActivityNotFoundException anfe) 
				{
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.rhoadster91.floatingsoftkeys")));
				}
				
			}
			
		});
	}

}
