package com.rhoadster91.floatingsoftkeys;

import java.io.Serializable;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AppInfo implements Serializable 
{
	private static final long serialVersionUID = -7552846503221015795L;
	String packageName;
	
	AppInfo(ResolveInfo rInfo, PackageManager pm)
	{		
	    packageName = rInfo.activityInfo.applicationInfo.packageName;	   
	}
	
}
