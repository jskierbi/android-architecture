package com.jskierbi.commons.navigation;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

/**
 * Integrates activity lifecycle with NavService (i.e. save/restore state)
 */
public class HostActivityIntegrationFragment extends Fragment {

	private FragmentNavigationController mFragmentNavigationController;

	void setNavService(FragmentNavigationController fragmentNavigationController) {
		mFragmentNavigationController = fragmentNavigationController;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override public void onResume() {
		super.onResume();
		mFragmentNavigationController.onResume();
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mFragmentNavigationController.onActivityCreated(savedInstanceState);
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mFragmentNavigationController.onSaveInstanceState(outState);
	}

	@Override public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mFragmentNavigationController.onConfigurationChanged(newConfig);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		return mFragmentNavigationController.onOptionsItemSelected(item);
	}
}