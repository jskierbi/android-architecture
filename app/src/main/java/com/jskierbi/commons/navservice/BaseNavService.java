package com.jskierbi.commons.navservice;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import org.parceler.Parcel;

/**
 * Base NavService class.
 *
 * Integration with Activity:
 * 1. implement {@link NavServiceHost} interface
 * 2. call {@link BaseNavService#onBackPressed()} from {@link Activity#onBackPressed}
 */
public abstract class BaseNavService {

	private static final String TAG = BaseNavService.class.getSimpleName();
	private static final String TAG_HOST_INTEGRATION_FRAGMENT = TAG + "_TAG_HOST_INTEGRATION_FRAGMENT";
	private static final String STATE_ACTIONBAR_DISPLAY_OPTIONS = TAG + "_STATE_ACTIONBAR_DISPLAY_OPTIONS";

	private final FragmentManager mFragmentManager;
	private final NavServiceHost mHost;
	private final ActionBarActivity mActivity;
	private final DoubleBackToExitHandler mDoubleBackToExitHandler = new DoubleBackToExitHandler();

	private final State mState = new State();
	@Parcel public static class State {

	}

	/**
	 * Creates navigation service that is hosted by activity.
	 * There is headless fragment created and added via FragmentManger inside this constructor
	 * to manage toolbar state (uses Fragments' lifecycle callbacks to save and restore state)
	 *
	 * @param activity to host this navigation service, have to extend
	 *                 {@link ActionBarActivity} and implement {@link NavServiceHost} interface.
	 */
	public BaseNavService(ActionBarActivity activity) {

		// Runtime check
		if (!(activity instanceof NavServiceHost)) {
			throw new IllegalArgumentException("Activity has to implement NavService.Host interface!");
		}

		mActivity = activity;
		mHost = (NavServiceHost) activity;
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

			// TOOD home as up
			// Home as up enabled
			if (flgAddToBackstack || mFragmentManager.getBackStackEntryCount() > 0) {
				mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} else {
				// If activity is not task root, user can navigate back
				mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(!mActivity.isTaskRoot());
				// TODO to sync drawer toggle state call mNavigationDrawerFragment.getActionBarDrawerToggle().syncState();
			}

		} catch (Exception ex) {
			Log.e(TAG, "Exception while adding fragment to backstack!!!", ex);
		}
	}

	public void navigateBack() {
		final int backstackEntryCount = mFragmentManager.getBackStackEntryCount();
		if (backstackEntryCount > 0) {
			// Navigate back in fragment hierarchy
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
				// TODO navdrawer vs. disable homeasup
				mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(backstackEntryCount > 1);
			} catch (Exception ex) {
				Log.e(TAG, "Exception trying to pop fragment!", ex);
			}
		} else {
			// Finish activity, handle double back to exit
			if (doubleBackToExit() != 0 && mActivity.isTaskRoot()) {
				if (mDoubleBackToExitHandler.isExitOnBack()) {
					mActivity.finish();
				} else {
					Toast.makeText(mActivity, doubleBackToExit(), Toast.LENGTH_SHORT).show();
				}
			} else {
				mActivity.finish();
			}
		}

		// TODO home as up
		// Home as up enabled
		if (backstackEntryCount > 1) {
			// User can navigate back at least one more time
			mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			//
			mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(!mActivity.isTaskRoot());
			// TODO to sync drawer toggle state call mNavigationDrawerFragment.getActionBarDrawerToggle().syncState();
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
		if (savedInstanceState != null) {
			final int displayOptions = savedInstanceState.getInt(STATE_ACTIONBAR_DISPLAY_OPTIONS);
			if (displayOptions != 0 && mActivity.getSupportActionBar() != null) {
				mActivity.getSupportActionBar().setDisplayOptions(displayOptions);
			}
		}

		if (mFragmentManager.findFragmentById(mHost.fragmentContainerId()) == null) {
			mFragmentManager.beginTransaction()
					.add(mHost.fragmentContainerId(), mHost.defaultFragment())
					.commit();
		}
	}

	void onSaveInstanceState(Bundle outState) {
        if (mActivity.getSupportActionBar() != null) {
	        outState.putInt(STATE_ACTIONBAR_DISPLAY_OPTIONS, mActivity.getSupportActionBar().getDisplayOptions());
        }
	}

	boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return false;
		}
	}
}
