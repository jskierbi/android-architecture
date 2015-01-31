package com.jskierbi.commons.navservice;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Base NavService class.
 *
 * Integration with Activity:
 * 1. implement {@link Host} interface
 * 2. call {@link BaseNavService#onBackPressed()} from {@link Activity#onBackPressed}
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
	private final DoubleBackToExit mDoubleBackToExit = new DoubleBackToExit();

	/**
	 * Creates navigation service that is hosted by activity.
	 * There is headless fragment created and added via FragmentManger inside this constructor
	 * to manage toolbar state (uses Fragments' lifecycle callbacks to save and restore state)
	 *
	 * @param activity to host this navigation service, have to extend
	 *                 {@link FragmentActivity} and implement {@link Host} interface.
	 */
	public BaseNavService(FragmentActivity activity) {

		// Runtime check
		if (!(activity instanceof Host)) {
			throw new IllegalArgumentException("Activity has to implement NavService.Host interface!");
		}

		mActivity = activity;
		mHost = (Host) activity;
		mFragmentManager = activity.getSupportFragmentManager();

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

	public void navigateTo(BaseNavFragment fragment) {
		navigateTo(fragment, true);
	}

	/**
	 * Base navigation method. Adds proper support for transition animations.
	 * Animations are defined in BaseNavFragment using {@link BaseNavFragment#setCustomAnimations}
	 *
	 * Addressed problems:
	 * * When changing orientations, custom animations from transactions are lost. This is fixed in {@link BaseNavFragment}
	 * * When navigating back, popExit for current fragment and popEnter for previous fragment are inconsistent.
	 * This method sets popEnter for previous fragment using popEnter from this fragment - so popEnter in each
	 * fragment is de facto used for previous fragment. This enables to define fragment transitions on sigle fragment object.
	 *
	 * @param nextFragment to navigate to
	 * @param flgAddToBackstack whether to add transaction to backstack or not.
	 */
	public void navigateTo(BaseNavFragment nextFragment, boolean flgAddToBackstack) {

		try {
			Fragment currentFragment = mFragmentManager.findFragmentById(mHost.fragmentContainerId());
			if (currentFragment instanceof BaseNavFragment) {
				// For current fragment, set animation that will be played when navigating back
				// This is to enable defining animations on single fragment that will be respected by previous
				// fragment in backstack
				((BaseNavFragment) currentFragment).setPopEnterAnim(nextFragment.getPopEnterAnim());
			}

			// Use case: exit via home, return to application. When popEnter animation set by fragment is used,
			// each time user returns to app, animation will be played - this is wrong, no animation should be played.
			// PopEnter animation will be set for nextFragment in next navigateTo call (nextFragment will become
			// currentFragment in next navigateTo call)
			nextFragment.setPopEnterAnim(0);

			FragmentTransaction transaction = mFragmentManager.beginTransaction();
			// Disable animation Pre 4.0.
			// Animation fixes in BaseNavFragment uses {@link Activity#isChangingConfigurations}, which is unavailable
			// for pre 3.0
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				transaction.setCustomAnimations(
						nextFragment.getEnterAnim(),
						nextFragment.getExitAnim(),
						nextFragment.getPopEnterAnim(),
						nextFragment.getPopExitAnim());
			}
			transaction.replace(mHost.fragmentContainerId(), nextFragment);
			if (flgAddToBackstack) {
				transaction.addToBackStack(null);
			}
			transaction.commit();
		} catch (Exception ex) {
			Log.e(TAG, "Exception while adding fragment to backstack!!!", ex);
		}
	}

	public void navigateBack() {
		if (mFragmentManager.getBackStackEntryCount() > 0) {
			try {
				Fragment currentFragment;
				if ((currentFragment = mFragmentManager.findFragmentById(mHost.fragmentContainerId())) != null) {
					// Current fragment can be added/replaced without adding to backstack. If this is the case,
					// after popping backstack current fragment will still be visible - so we need to remove it manually
					// before popping!
					mFragmentManager.beginTransaction()
							.remove(currentFragment)
							.commit();
				}
				mFragmentManager.popBackStack();
			} catch (Exception ex) {
				Log.e(TAG, "Exception trying to pop fragment!", ex);
			}
		} else {
			if (doubleBackToExit() != 0 && mActivity.isTaskRoot()) {
				if (mDoubleBackToExit.isExitOnBack()) {
					mActivity.finish();
				} else {
					Toast.makeText(mActivity, doubleBackToExit(), Toast.LENGTH_SHORT).show();
				}
			} else {
				mActivity.finish();
			}
		}
	}

	public void clearBackstack() {
		final int backstackEntryCount = mFragmentManager.getBackStackEntryCount();
		for (int i = mFragmentManager.getBackStackEntryCount(); i > 0; --i) {
			// Current fragment could has been added outside transaction - remove it!
			final Fragment currentFragment = mFragmentManager.findFragmentById(mHost.fragmentContainerId());
			if (backstackEntryCount > 0 && currentFragment != null) {
				mFragmentManager.beginTransaction()
						.remove(currentFragment)
						.commit();
			}
			// Execute transaction immediate, so in next loop we can get current fragment and remove it!
			mFragmentManager.popBackStackImmediate();
		}

	}

	/** Default fragment to be put to container */
	protected abstract BaseNavFragment defaultFragment();

	/**
	 * 0 to disable double back to exit, string res to enable.
	 * String res will be used by toast shown on first back click.
	 * @return string res to be displayed on double back to exit, or 0 to disable this feature.
	 */
	protected abstract @StringRes int doubleBackToExit();

	// TODO implement homeAsUp enabed/disabled
	// TODO implement NavDrawer

	/////////////////////////////////////////////////
	// Integration via Activity
	/////////////////////////////////////////////////
	public void onBackPressed() {
		navigateBack();
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
