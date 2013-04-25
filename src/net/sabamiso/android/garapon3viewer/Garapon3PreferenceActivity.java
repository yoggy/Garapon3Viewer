package net.sabamiso.android.garapon3viewer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Garapon3PreferenceActivity extends PreferenceActivity  {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
	}
}
