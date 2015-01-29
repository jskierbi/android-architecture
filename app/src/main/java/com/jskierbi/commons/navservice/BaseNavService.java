package com.jskierbi.commons.navservice;

import android.app.Activity;
import android.os.Build;
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

	public void navigateTo(BaseNavFragment fragment) {
		navigateTo(fragment, true);
	}

	/**
	 * Base navigation method. Adds proper support for transition animations.
	 * Animations are defined in BaseNavFragment using {#link BaseNavFragment#setCustomAnimations}
	 *
	 * Addressed problems:
	 * * When changing orientations, custom animations from transactions are lost. This is fixed in {@link BaseNavFragment}
	 * * When navigating back, popExit for current fragment and popEnter for previous fragment are inconsistent.
	 * This method sets popEnter for previous fragment using popEnter from this fragment - so popEnter in each
	 * fragment is de facto used for previous fragment. This enables to define fragment transitions on sigle fragment object.
	 *
	 * @param fragment to navigate to
	 * @param flgAddToBackstack whether to add transaction to backstack or not.
	 */
	public void navigateTo(BaseNavFragment fragment, boolean flgAddToBackstack) {

		try {
			FragmentTransaction transaction = mFragmentManager.beginTransaction();
			Fragment currentFragment = mFragmentManager.findFragmentById(mHost.fragmentContainerId());
			if (currentFragment instanceof BaseNavFragment) {
				// For current fragment, set animation that will be played when navigating back
				// This is to enable defining animations on single fragment that will be respected by previous
				// fragment in backstack
				((BaseNavFragment) currentFragment).setPopEnterAnim(fragment.getPopEnterAnim());
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				// Animations diabled for pre ICS
				transaction.setCustomAnimations(
						fragment.getEnterAnim(),
						fragment.getExitAnim(),
						fragment.getPopEnterAnim(),
						fragment.getPopExitAnim());
			}
			transaction.replace(mHost.fragmentContainerId(), fragment);
			if (flgAddToBackstack) {
				transaction.addToBackStack(null);
			}
			transaction.commit();
		} catch (Exception ex) {
			Log.e(TAG, "Exception while adding fragment to backstack!!!", ex);
		}
	}

	/**
	 * Navigates back
	 */
	public void navigateBack() {
		if (mFragmentManager.getBackStackEntryCount() > 0) {
			try {
				Fragment currentFragment;
				if ((currentFragment = mFragmentManager.findFragmentById(mHost.fragmentContainerId())) != null) {
					// Current fragment can be added/replaced without adding to backstack. If this is the case,
					// after popping backstack current fragment will still be visible - so we need to remove it manually!
					mFragmentManager.beginTransaction()
							.remove(currentFragment)
							.commit();
				}
				mFragmentManager.popBackStack();
			} catch (Exception ex) {
				Log.e(TAG, "Exception trying to pop fragment!", ex);
			}
		} else {
			mActivity.finish();
		}
	}

	public void clearBackstack() {

	}

	protected abstract BaseNavFragment defaultFragment();

	// TODO implement navigateTo
	// TODO implement setTitle
	// TODO implement homeAsUp enabed/disabled

	/////////////////////////////////////////////////
	// Integration via Activity
	/////////////////////////////////////////////////
	public void onBackPressed() {
		// TODO handle up navigation
		Log.d(TAG, "onBackPressed");
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
