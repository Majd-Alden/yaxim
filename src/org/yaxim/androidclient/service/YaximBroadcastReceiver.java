package org.yaxim.androidclient.service;

import org.yaxim.androidclient.util.PreferenceConstants;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;


public class YaximBroadcastReceiver extends BroadcastReceiver {
	static final String TAG = "yaxim.BroadcastReceiver";
	private static int networkType = -1;
	
	public static void initNetworkStatus(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		networkType = -1;
		if (networkInfo != null) {
			Log.d(TAG, "Init: ACTIVE NetworkInfo: "+networkInfo.toString());
			if (networkInfo.isConnected()) {
				networkType = networkInfo.getType();
			}
		}
		Log.d(TAG, "initNetworkStatus -> " + networkType);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive " + intent);
		// prepare intent
		Intent xmppServiceIntent = new Intent(context, XMPPService.class);

		if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
			Log.d(TAG, "System shutdown, stopping yaxim.");
			context.stopService(xmppServiceIntent);
		} else
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			boolean connstartup = PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(PreferenceConstants.CONN_STARTUP, false);
			if (!connstartup) // ignore event, we are not running
				return;

			asyncRefreshDNS();

			// there are three possible situations here: disconnect, reconnect, connection change
			ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

			boolean isConnected = (networkInfo != null) && (networkInfo.isConnected() == true);
			boolean wasConnected = (networkType != -1);
			if (wasConnected && !isConnected) {
				Log.d(TAG, "we got disconnected");
				networkType = -1;
				xmppServiceIntent.setAction("disconnect");
			} else
			if (isConnected && (networkInfo.getType() != networkType)) {
				Log.d(TAG, "we got (re)connected: " + networkInfo.toString());
				networkType = networkInfo.getType();
				xmppServiceIntent.setAction("reconnect");
			} else
			if (isConnected && (networkInfo.getType() == networkType)) {
				Log.d(TAG, "we stay connected, sending a ping");
				xmppServiceIntent.setAction("ping");
			} else
				return;
			context.startService(xmppServiceIntent);
		} else
		if (intent.getAction().equals("org.yaxim.androidclient.ACTION_MESSAGE_HEARD")) {
			Log.d(TAG, "heard " + intent);
			xmppServiceIntent.setAction("respond");
			String jid = intent.getStringExtra("jid");
			if (jid == null) return;
			xmppServiceIntent.setData(Uri.parse(jid));
			context.startService(xmppServiceIntent);
		} else
		if (intent.getAction().equals("org.yaxim.androidclient.ACTION_MESSAGE_REPLY")) {
			Log.d(TAG, "reply " + intent);
			Bundle reply = android.support.v4.app.RemoteInput.getResultsFromIntent(intent);
			String replystring = null;
			if (reply != null) {
				replystring = reply.getCharSequence("voicereply").toString();
				Log.d(TAG, "got reply: " + replystring);
			}
			xmppServiceIntent.setAction("respond");
			String jid = intent.getStringExtra("jid");
			if (jid == null) return;
			xmppServiceIntent.setData(Uri.parse(jid));
			xmppServiceIntent.putExtra("message", replystring);
			context.startService(xmppServiceIntent);
		}
	}

	private void asyncRefreshDNS() {
		new Thread() {
			@Override
			public void run() {
				// refresh DNS servers from android prefs
				try {
					org.xbill.DNS.ResolverConfig.refresh();
					org.xbill.DNS.Lookup.refreshDefault();
				} catch (Exception e) {
				    // sometimes refreshDefault() will cause a NetworkOnMainThreadException;
				    // ignore and hope for the best.
				    Log.i(TAG, "DNS init failed: " + e);
				}
			}
		}.start();
	}
}

