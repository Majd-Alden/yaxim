package org.yaxim.androidclient;

import org.yaxim.androidclient.data.YaximConfiguration;
import org.yaxim.androidclient.service.YaximBroadcastReceiver;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.IntentFilter;
import android.preference.PreferenceManager;

import de.duenndns.ssl.MemorizingTrustManager;

public class YaximApplication extends Application {
	// identity name and type, see:
	// http://xmpp.org/registrar/disco-categories.html
	public static final String XMPP_IDENTITY_NAME = "yaxim";
	public static final String XMPP_IDENTITY_TYPE = "phone";

	// MTM is needed globally for both the backend (connect)
	// and the frontend (display dialog)
	public MemorizingTrustManager mMTM;

	private YaximConfiguration mConfig;

	public YaximApplication() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mMTM = new MemorizingTrustManager(this);
		mConfig = new YaximConfiguration(this);
		// since Android 7, you need to manually register for network changes
		// https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html#MonitorChanges
		registerReceiver(new YaximBroadcastReceiver(), new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
	}

	public static YaximApplication getApp(Activity ctx) {
		android.util.Log.d("YaximApplication", "app = " + ctx.getApplication());
		return (YaximApplication)ctx.getApplication();
	}
	public static YaximApplication getApp(Service ctx) {
		android.util.Log.d("YaximApplication", "app = " + ctx.getApplication());
		return (YaximApplication)ctx.getApplication();
	}

	public static YaximConfiguration getConfig(Activity ctx) {
		return getApp(ctx).mConfig;
	}
	public static YaximConfiguration getConfig(Service ctx) {
		return getApp(ctx).mConfig;
	}
}

