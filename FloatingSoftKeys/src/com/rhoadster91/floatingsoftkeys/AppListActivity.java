package com.rhoadster91.floatingsoftkeys;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AppListActivity extends Activity 
{

	private static List<ResolveInfo> pkgAppsList = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist);
		refreshList();
		Button bAdd = (Button)findViewById(R.id.button1);
		bAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
				mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				if(pkgAppsList==null)
				{
					pkgAppsList = getApplicationContext().getPackageManager().queryIntentActivities( mainIntent, PackageManager.PERMISSION_GRANTED);
					ResolveInfo []tempArray = new ResolveInfo[pkgAppsList.size()];
					pkgAppsList.toArray(tempArray);
					Arrays.sort(tempArray, new AppListNameComparator());
					pkgAppsList = Arrays.asList(tempArray);
				}
				ListAdapter adapter = new ArrayAdapterWithIcon(AppListActivity.this, pkgAppsList);				
				new AlertDialog.Builder(AppListActivity.this).setTitle("Select App")
                .setAdapter(adapter, new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int item ) 
                    {
                    	FloatingSoftKeysApplication.selectedAppsList.add(new AppInfo(pkgAppsList.get(item), getPackageManager()));
                    	FloatingSoftKeysApplication.writeAppListToFile(getApplicationContext());
                    	refreshList();
                    }
            }).show();
			}			
		});
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		pkgAppsList = null;
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		pkgAppsList = null;
	}

	public class ArrayAdapterWithIcon extends ArrayAdapter<ResolveInfo> 
	{

		List<ResolveInfo> apps;

		public ArrayAdapterWithIcon(Context context, List<ResolveInfo> items) 
		{
		    super(context, android.R.layout.select_dialog_item, items);
		    this.apps = items;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		    View view = super.getView(position, convertView, parent);
		    TextView textView = (TextView) view.findViewById(android.R.id.text1);
		    Drawable icon = apps.get(position).activityInfo.applicationInfo.loadIcon(getPackageManager());
		    icon.setBounds(0, 0, 48, 48);
		    textView.setCompoundDrawables(icon, null, null, null);
		    textView.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));		    
		    textView.setText(apps.get(position).loadLabel(getPackageManager()));
		    return view;
		}

	}
	
	public class SerializedAppNameAdapter extends ArrayAdapter<AppInfo> 
	{

		List<AppInfo> apps;

		public SerializedAppNameAdapter(Context context, List<AppInfo> items) 
		{
		    super(context, android.R.layout.select_dialog_item, items);
		    this.apps = items;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
		    View view = super.getView(position, convertView, parent);
		    TextView textView = (TextView) view.findViewById(android.R.id.text1);
		    ApplicationInfo appInfo;
			try 
			{
				appInfo = getPackageManager().getApplicationInfo(apps.get(position).packageName, 0);
				Drawable icon = getPackageManager().getApplicationIcon(appInfo);				
			    icon.setBounds(0, 0, 48, 48);
			    textView.setCompoundDrawables(icon, null, null, null);
			    textView.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));		    
			    textView.setText(getPackageManager().getApplicationLabel(appInfo));
			 
			}
			catch (NameNotFoundException e) 
			{
				e.printStackTrace();
				
			}
		    return view;
		}

	}
	
	
	
	public class AppListNameComparator implements Comparator<ResolveInfo>
	{

		@Override
		public int compare(ResolveInfo object1, ResolveInfo object2) 
		{
			String str1 = new String("" + object1.loadLabel(getPackageManager()));
			String str2 = new String("" + object2.loadLabel(getPackageManager()));
			str1 = str1.toLowerCase(Locale.getDefault());
			str2 = str2.toLowerCase(Locale.getDefault());
			return str1.compareTo(str2);
		}
		
	}
	
	private void refreshList()
	{
		FloatingSoftKeysApplication.readAppListFromFile(getApplicationContext());
		for(AppInfo a : FloatingSoftKeysApplication.selectedAppsList)
		{
			try 
			{
				getPackageManager().getApplicationInfo(a.packageName, 0);				
			}
			catch (NameNotFoundException e) 
			{
				FloatingSoftKeysApplication.selectedAppsList.remove(FloatingSoftKeysApplication.selectedAppsList.indexOf(a));			
				FloatingSoftKeysApplication.writeAppListToFile(getApplicationContext());
				e.printStackTrace();
			}
		}
		ListView listView = (ListView)findViewById(R.id.listView1);
		ListAdapter adapter = new SerializedAppNameAdapter(AppListActivity.this, FloatingSoftKeysApplication.selectedAppsList);
		
		listView.setAdapter(adapter);
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				final int index = arg2;
				new AlertDialog.Builder(AppListActivity.this).setTitle(getString(R.string.confirm)).setMessage(getString(R.string.remove)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						FloatingSoftKeysApplication.selectedAppsList.remove(index);
		            	FloatingSoftKeysApplication.writeAppListToFile(getApplicationContext());
		            	refreshList();
					}
				}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						
					}
				}) .show();
				
				
				return true;
			}
			
		});
		
	}
		
}
