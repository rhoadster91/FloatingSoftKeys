package com.rhoadster91.floatingsoftkeys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

public class EventHandler 
{
	private CommandShell cmd = null;
	private Context context;
	private InputShell mInputShell = null;

	public EventHandler(Context paramContext) 
	{
		this.context = paramContext;
	}

	public CommandShell getCommandShell() throws Exception 
	{
		if (this.cmd == null) 
		{
			CommandShell localCommandShell = new CommandShell("su");
			this.cmd = localCommandShell;
		}
		return this.cmd;
	}

	@SuppressWarnings("resource")
	public InputShell getInputShell() throws Exception 
	{
		String str1 = null;
		InputStream localInputStream = null;
		FileOutputStream localFileOutputStream = null;
		byte[] arrayOfByte = new byte[4096];
		InputShell localInputShell1 = this.mInputShell;
		AssetManager localAssetManager;
		File localFile;
		int m;
		if (localInputShell1 == null) 
		{
			localAssetManager = this.context.getResources().getAssets();
			str1 = this.context.getFilesDir().getAbsolutePath();
			int i = Build.VERSION.SDK_INT;
			if (i > 15) 
			{
				String str2 = str1 + "/input2_jb.jar";
				localFile = new File(str2);
				str2 = "input2_jb.jar";
				localInputStream = localAssetManager.open(str2);
			}
			else if (i > 10 && i < 16) 
			{
				String str2 = str1 + "/input2_hc.jar";
				localFile = new File((String) str2);
				str2 = "input2_hc.jar";
				localInputStream = localAssetManager.open(str2);
			}
			else 
			{
				String str2 = str1 + "/input2.jar";
				localFile = new File(str2);
				str2 = "input2.jar";
				localInputStream = localAssetManager.open(str2);
			}

			localFileOutputStream = new FileOutputStream(localFile);
			m = localInputStream.read(arrayOfByte);
			if (m == -1)
				return null;
			localFileOutputStream.write(arrayOfByte, 0, m);
			localFileOutputStream.close();
			localInputStream.close();
			this.mInputShell = new InputShell("su", str1);
		}
		return this.mInputShell;
	}

	public int sendDownTouchKeys(int paramInt) 
	{
		try 
		{
			InputShell localInputShell = getInputShell();
			String str1 = "down " + paramInt;
			localInputShell.runCommand(str1);
			return 0;
		}
		catch (Exception localException) 
		{
			String str2 = localException.getMessage();
			Log.e("Floating Soft Keys", str2);
			localException.printStackTrace();
			return 1;
		}
	}

	public int sendDownTouchKeys(int paramInt1, int paramInt2) 
	{
		try 
		{
			InputShell localInputShell = getInputShell();
			String str1 = "downr " + paramInt1 + " " + paramInt2;
			localInputShell.runCommand(str1);
			return 0;
		}
		catch (Exception localException) 
		{
			String str2 = localException.getMessage();
			Log.e("Floating Soft Keys", str2);
			localException.printStackTrace();
			return 1;
		}
	}

	public int sendKeys(int paramInt) 
	{
		try 
		{
			InputShell localInputShell = getInputShell();
			String str1 = "keycode " + paramInt;
			localInputShell.runCommand(str1);
			return 0;
		}
		catch (Exception localException) 
		{
			String str2 = localException.getMessage();
			Log.e("Floating Soft Keys", str2);
			localException.printStackTrace();
			return 1;
		}
	}

	public int sendUpTouchKeys(int paramInt) 
	{
		try 
		{
			InputShell localInputShell = getInputShell();
			String str1 = "up " + paramInt;
			localInputShell.runCommand(str1);
			return 0;
		}
		catch (Exception localException) 
		{
			String str2 = localException.getMessage();
			Log.e("Floating Soft Keys", str2);
			localException.printStackTrace();
			return 1;
		}
	}

	class CommandShell 
	{
		OutputStream o;
		Process p;

		CommandShell(String arg2) throws Exception 
		{
			Process localProcess = Runtime.getRuntime().exec(arg2);
			this.p = localProcess;
			OutputStream localOutputStream = this.p.getOutputStream();
			this.o = localOutputStream;
		}

		public void close() throws Exception 
		{
			this.o.flush();
			this.o.close();
			this.p.destroy();
		}

		public void system(String paramString) throws Exception 
		{
			OutputStream localOutputStream = this.o;
			String str = String.valueOf(paramString);
			byte[] arrayOfByte = (str + "\n").getBytes("ASCII");
			localOutputStream.write(arrayOfByte);
		}
	}

	public class InputShell 
	{
		OutputStream o;
		Process p;
		InputShell(String paramString1, String arg3) throws Exception 
		{
			this.p = Runtime.getRuntime().exec(paramString1);
			this.o = this.p.getOutputStream();
			system("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
			if (Build.VERSION.SDK_INT > 15) {
				String str1 = "export CLASSPATH=" + arg3 + "/input2_jb.jar";
				system(str1);
			} else if ((Build.VERSION.SDK_INT > 10)
					&& (Build.VERSION.SDK_INT < 16)) {
				String str3 = "export CLASSPATH=" + arg3 + "/input2_hc.jar";
				system(str3);
			} else {
				String str4 = "export CLASSPATH=" + arg3 + "/input2.jar";
				system(str4);
			}
			String str2 = "exec app_process " + arg3 + " com.smart.swkey.input";
			system(str2);
		}

		private void system(String paramString) throws Exception 
		{
			OutputStream localOutputStream = this.o;
			String str = String.valueOf(paramString);
			byte[] arrayOfByte = (str + "\n").getBytes("ASCII");
			localOutputStream.write(arrayOfByte);
		}

		public void close() throws Exception 
		{
			this.o.flush();
			this.o.close();
			this.p.destroy();
		}

		public void runCommand(String paramString) throws Exception 
		{
			system(paramString);
		}
	}
}