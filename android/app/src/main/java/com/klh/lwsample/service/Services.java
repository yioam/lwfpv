package com.klh.lwsample.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

public class Services extends Service
{
	private final static String TAG = "Services";
	
	private LocalBinder mBinder = new LocalBinder();
	private WifiManager manager = null;
	
	private boolean isWifiConnected = false;

	@Override
	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	public class LocalBinder extends Binder
	{
		public Services getService()
		{
			return Services.this;
		}
	}

	public void init()
	{
		Log.d(TAG, "register wifi network receiver.");
		manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		register();
	}

	public void register()
	{
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(brReceiver, mIntentFilter);
	}
	
	public boolean getWifiConnected()
	{
		return isWifiConnected;
	}

	private BroadcastReceiver brReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
			{
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiState)
				{
					case WifiManager.WIFI_STATE_ENABLED:
					{

					}
						break;
					case WifiManager.WIFI_STATE_DISABLED:
					{
						isWifiConnected = false;
					}
						break;
					case WifiManager.WIFI_STATE_DISABLING:
						break;
				}
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
			{
				Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra)
				{
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					WifiInfo wifiInfo = manager.getConnectionInfo();
					String ssid = wifiInfo.getSSID();
					if (state == State.CONNECTED)
					{
						if (ssid.startsWith("\""))
						{
							ssid = ssid.substring(1, ssid.length() - 1);
						}

						isWifiConnected = true;
					} else if (state == State.DISCONNECTED)
					{
						Log.d(TAG, "Wifi State Change!");
						isWifiConnected = false;
					} 
				}
			}
		}
	};

	public void exit()
	{
		Log.d(TAG, "unregister wifi network receiver.");
		unregisterReceiver(brReceiver);
	}

}
