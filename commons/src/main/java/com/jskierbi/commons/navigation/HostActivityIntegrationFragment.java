package com.jskierbi.commons.navigation;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

/**
 * Integrates activity lifecycle with NavService (i.e. save/restore state)
 */
public class HostActivityIntegrationFragment extends Fragment {

	private NavigationController mNavigationController;

	void setNavService(NavigationController navigationController) {
		mNavigationController = navigationController;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override public void onResume() {
		super.onResume();
		mNavigationController.onResume();
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mNavigationController.onActivityCreated(savedInstanceState);
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mNavigationController.onSaveInstanceState(outState);
	}

	@Override public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mNavigationController.onConfigurationChanged(newConfig);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		return mNavigationController.onOptionsItemSelected(item);
	}
}