package com.jskierbi.commons.navservice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

public class HostActivityIntegrationFragment extends Fragment {

	private static final String TAG = HostActivityIntegrationFragment.class.getSimpleName();
	private NavService mNavService;

	void setNavService(NavService navService) {
		Log.d(TAG, "setNavService");
		mNavService = navService;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated");
		mNavService.onActivityCreated(savedInstanceState);
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mNavService.onSaveInstanceState(outState);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		return mNavService.onOptionsItemSelected(item);
	}
}