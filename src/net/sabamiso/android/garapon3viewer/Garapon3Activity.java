package net.sabamiso.android.garapon3viewer;

import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Garapon3Activity extends Activity {
	WebView webview;
	Handler handler = new Handler();

	SharedPreferences prefs;
	final int PREFERENCE_REQUEST_CODE = 100;

	int login_submit_count = 0;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_garapon3);

		webview = (WebView) findViewById(R.id.webview);

		//
		prefs = PreferenceManager.getDefaultSharedPreferences(this); // MODE_PRIVATE
		String url = prefs.getString(
				getResources().getString(R.string.pref_url_key), "");
		//if (url == "")
		//	intentPreference();

		// webview settings
		webview.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url_str) {
				URL url;
				try {
					url = new URL(url_str);
				} catch (MalformedURLException e) {
					return;
				}

				if ("/auth/login.garapon".equals(url.getPath())) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							fillLoginForm();
						}
					}, 100);
				}
				super.onPageFinished(view, url_str);
			}
		});
		webview.setVerticalScrollbarOverlay(true);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setPluginsEnabled(true);
		webview.getSettings().setPluginState(PluginState.ON);
		webview.getSettings().setBuiltInZoomControls(false);
		webview.setInitialScale(100);

		webview.loadUrl(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_garapon3, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
		case R.id.menu_reload:
			webview.reload();
			return true;
		case R.id.menu_settings:
			intentPreference();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		webview.onResume();
		webview.resumeTimers();
	}

	@Override
	protected void onPause() {
		super.onPause();
		webview.pauseTimers();
		webview.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pressBackButton();
			return true;
		}
		return false;
	}

	public void fillLoginForm() {
		String user = prefs.getString(
				getResources().getString(R.string.pref_user_key), "");
		String pass = prefs.getString(
				getResources().getString(R.string.pref_pass_key), "");

		webview.loadUrl("javascript:$('#loginid').val('" + user + "');");
		webview.loadUrl("javascript:$('#passwd').val('" + pass + "');");
		webview.loadUrl("javascript:window.document.forms[0].submit();");
		
		login_submit_count ++;
		if (login_submit_count > 2) {
			webview.loadData("", "text/html", "UTF-8");
		}
	}

	public void pressBackButton() {
		URL url;
		try {
			url = new URL(webview.getUrl());
		} catch (MalformedURLException e) {
			return;
		}

		String ref = url.getRef();
		if (ref != null) {
			webview.loadUrl("javascript:history.back();");
		}
	}

	public void intentPreference() {
		Intent intent = new Intent(
				this,
				net.sabamiso.android.garapon3viewer.Garapon3PreferenceActivity.class);
		startActivityForResult(intent, PREFERENCE_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PREFERENCE_REQUEST_CODE) {
			login_submit_count = 0;
			String url = prefs.getString(
					getResources().getString(R.string.pref_url_key), "");
			webview.loadUrl(url);
		}
	}
}
