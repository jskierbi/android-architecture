package com.jskierbi.commons.navservice;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

/**
 * Base NavService class.
 *
 * Integration with Activity:
 * 1. implement {#link Host} interface
 * 2. call {#link NavService#onBackPressed} from {#link Activity#onBackPressed}
 */
public abstract class NavService {

	public interface Host {

		public Toolbar toolbar();
		public @IdRes int fragmentContainerId();
	}

	private static final String TAG = NavService.class.getSimpleName();
	private static final String TAG_HOST_INTEGRATION_FRAGMENT = TAG + "_TAG_HOST_INTEGRATION_FRAGMENT";

	private final FragmentManager mFragmentManager;
	private final Toolbar mToolbar;
	private final @IdRes int mFragmentContainerId;

	/** Subclass constructor implementation to be injected via dagger - fwd argumetns to this constructor */
	public NavService(Activity activity, FragmentManager fragmentManager) {

		// Runtime check
		if (!(activity instanceof Host)) {
			throw new IllegalStateException("Activity has to implement NavService.Host interface!");
		}

		Host host = (Host) activity;
		mToolbar = host.toolbar();
		mFragmentContainerId = host.fragmentContainerId();
		mFragmentManager = fragmentManager;

		// Initialize HostIntegrationFragment
		Fragment integrationFragment = mFragmentManager.findFragmentByTag(TAG_HOST_INTEGRATION_FRAGMENT);
		if (integrationFragment == null) {
			HostActivityIntegrationFragment hostActivityIntegrationFragment = new HostActivityIntegrationFragment();
			hostActivityIntegrationFragment.setNavService(this);
			mFragmentManager.beginTransaction()
					.add(hostActivityIntegrationFragment, TAG_HOST_INTEGRATION_FRAGMENT)
					.commit();
		} else {
			HostActivityIntegrationFragment hostActivityIntegrationFragment = (HostActivityIntegrationFragment) integrationFragment;
			hostActivityIntegrationFragment.setNavService(this);
		}

		// Initialize default fragment
	}

	protected abstract Fragment defaultFragment();

	/////////////////////////////////////////////////
	// Integration via Activity
	/////////////////////////////////////////////////

	public void onBackPressed() {
		// TODO handle up navigation
		Log.d(TAG, "onBackPressed");
	}

	/////////////////////////////////////////////////
	// Integration via HostActivityIntegrationFragment
	/////////////////////////////////////////////////

	void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		// TODO

		if (mFragmentManager.findFragmentById(mFragmentContainerId) == null) {
			mFragmentManager.beginTransaction()
					.add(mFragmentContainerId, defaultFragment())
					.commit();
		}
	}

	void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState");
		// TODO
	}

	boolean onOptionsItemSelected(MenuItem menuItem) {
		Log.d(TAG, "onOptionsItemSelected");
		// TODO
		return true;
	}
}
