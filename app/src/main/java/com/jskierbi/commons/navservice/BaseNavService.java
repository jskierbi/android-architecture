package com.jskierbi.commons.navservice;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
public abstract class BaseNavService {

	public interface Host {

		public Toolbar toolbar();
		public @IdRes int fragmentContainerId();
		public Fragment defaultFragment();
	}

	private static final String TAG = BaseNavService.class.getSimpleName();
	private static final String TAG_HOST_INTEGRATION_FRAGMENT = TAG + "_TAG_HOST_INTEGRATION_FRAGMENT";

	private final FragmentManager mFragmentManager;
	private final Host mHost;
	private final Activity mActivity;

	/** Subclass constructor implementation to be injected via dagger - fwd argumetns to this constructor */
	public BaseNavService(Activity activity, FragmentManager fragmentManager) {

		// Runtime check
		if (!(activity instanceof Host)) {
			throw new IllegalStateException("Activity has to implement NavService.Host interface!");
		}

		mActivity = activity;
		mHost = (Host) activity;
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
	}

	// 2.3 does not support fragment transition animations!!!

	public void navigateTo(Fragment fragment) {
		navigateTo(fragment, true);
	}

	public void navigateTo(Fragment fragment, boolean flgAddToBackstack) {

		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.replace(mHost.fragmentContainerId(), fragment);
		if (flgAddToBackstack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	public void navigateBack() {
		if (mFragmentManager.getBackStackEntryCount() > 0) {
			mFragmentManager.popBackStack();
		} else {
			mActivity.finish();
		}
	}

	public void clearBackstack() {

	}

	protected abstract Fragment defaultFragment();

	// TODO implement navigateTo
	// TODO implement setTitle
	// TODO implement homeAsUp enabed/disabled

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

		if (mFragmentManager.findFragmentById(mHost.fragmentContainerId()) == null) {
			mFragmentManager.beginTransaction()
					.add(mHost.fragmentContainerId(), mHost.defaultFragment())
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
