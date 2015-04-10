package com.jskierbi.commons.navservice;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

/**
 * Integrates activity lifecycle with NavService (i.e. save/restore state)
 */
public class HostActivityIntegrationFragment extends Fragment {

	private NavService mNavService;

	void setNavService(NavService navService) {
		mNavService = navService;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override public void onResume() {
		super.onResume();
		mNavService.onResume();
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mNavService.onActivityCreated(savedInstanceState);
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mNavService.onSaveInstanceState(outState);
	}

	@Override public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mNavService.onConfigurationChanged(newConfig);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		return mNavService.onOptionsItemSelected(item);
	}
}