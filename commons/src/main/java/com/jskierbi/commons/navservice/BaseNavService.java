package com.jskierbi.commons.navservice;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.jskierbi.commons.R;
import org.parceler.Parcel;
import org.parceler.Parcels;

/**
 * Base NavService class.
 * Manages toolbar home as up and DrawerToggle (if DrawerLayout is available in activity)
 *
 * Integration with Activity:
 * 1. implement {@link NavServiceHost} interface
 * 2. call {@link BaseNavService#onBackPressed()} from {@link Activity#onBackPressed}
 */
public abstract class BaseNavService {

	private static final String TAG = BaseNavService.class.getSimpleName();
	private static final String TAG_HOST_INTEGRATION_FRAGMENT = TAG + "_TAG_HOST_INTEGRATION_FRAGMENT";
	private static final String KEY_INSTANCE_STATE = TAG + "_INSTANCE_STATE";

	private final FragmentManager mFragmentManager;
	private final NavServiceHost mHost;
	private final ActionBarActivity mActivity;
	private final DoubleBackToExitHandler mDoubleBackToExitHandler = new DoubleBackToExitHandler();
	private ActionBarDrawerToggle mDrawerToggle;

	private State mState = new State();
	@Parcel public static class State {

		int mActionbarDisplayOptions;
		boolean mFlgNavUpEnabled = false;
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

		// Initialize HostIntegrationFragment (provides lifecycle callbacks to this class)
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

			final boolean isNavUpEnabled = flgAddToBackstack || mFragmentManager.getBackStackEntryCount() > 0;
			updateHomeAsUpState(isNavUpEnabled);

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

		final boolean isNavUpEnabled = backstackEntryCount > 1;
		updateHomeAsUpState(isNavUpEnabled);
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
		updateHomeAsUpState(false); // Nav back is not available
	}

	/** Default fragment to be put to container */
	protected abstract BaseNavFragment defaultFragment();

	/**
	 * 0 to disable double back to exit, string res to enable.
	 * String res will be used by toast shown on first back click.
	 * @return string res to be displayed on double back to exit, or 0 to disable this feature.
	 */
	protected abstract @StringRes int doubleBackToExit();

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

		{   // Initialize drawer toggle, if DrawerLayout available
			View activityRootView = mActivity.findViewById(android.R.id.content);
			DrawerLayout drawerLayout = ViewHierarchyHelper.findChildViewOfType(DrawerLayout.class, activityRootView);
			if (drawerLayout != null) {
				Log.d(TAG, "Drawer Toggle: ENABLED!");
				mDrawerToggle = new ActionBarDrawerToggle(
						mActivity,
						drawerLayout,
						mHost.toolbar(),
						R.string.open_drawer,
						R.string.close_drawer);
				drawerLayout.setDrawerListener(mDrawerToggle);
				// workaround https://stackoverflow.com/questions/26549008/missing-up-navigation-icon-after-switching-from-ics-actionbar-to-lollipop-toolba/26932351#26932351}
				mDrawerToggle.setHomeAsUpIndicator(mActivity.getV7DrawerToggleDelegate().getThemeUpIndicator());
				// workaround http://stackoverflow.com/questions/26582075/cannot-catch-toolbar-home-button-click-event
				mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) {
						navigateBack();
					}
				});
			} else {
				Log.d(TAG, "Drawer Toggle: DISABLED!");
				mDrawerToggle = null;
			}
		}

		if (savedInstanceState != null) {
			// Restore app state
			State state = Parcels.unwrap(savedInstanceState.getParcelable(KEY_INSTANCE_STATE));
			if (state != null) mState = state;
			if (mState.mActionbarDisplayOptions != 0 && mActivity.getSupportActionBar() != null) {
				mActivity.getSupportActionBar().setDisplayOptions(mState.mActionbarDisplayOptions);
			}
			updateHomeAsUpState(mState.mFlgNavUpEnabled);
		} else {
			// If not restoring state, initialize!
			updateHomeAsUpState(false); // we're initializing - no nav up cause backstack is empty
			if (mDrawerToggle != null) {
				mActivity.getSupportActionBar().setHomeButtonEnabled(true);
				mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				mDrawerToggle.syncState();
			}
		}

		if (mFragmentManager.findFragmentById(mHost.fragmentContainerId()) == null) {
			mFragmentManager.beginTransaction()
					.add(mHost.fragmentContainerId(), mHost.defaultFragment())
					.commit();
		}
	}

	void onResume() {
		updateHomeAsUpState(mState.mFlgNavUpEnabled);
	}

	void onSaveInstanceState(Bundle outState) {
		if (mActivity.getSupportActionBar() != null) {
			mState.mActionbarDisplayOptions = mActivity.getSupportActionBar().getDisplayOptions();
			outState.putParcelable(KEY_INSTANCE_STATE, Parcels.wrap(mState));
		}
	}

	void onConfigurationChanged(Configuration newConfig) {
		if (mDrawerToggle != null) {
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
	}

	boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			Log.d(TAG, "onOptionsItemSelected(): home");
			onBackPressed();
			return true;
		default:
			return false;
		}
	}

	private void updateHomeAsUpState(boolean flgNavBackEnabled) {
		final ActionBar actionBar = mActivity.getSupportActionBar();
		mState.mFlgNavUpEnabled = flgNavBackEnabled;

		if (mDrawerToggle == null) {
			actionBar.setDisplayHomeAsUpEnabled(flgNavBackEnabled);
			actionBar.setHomeButtonEnabled(flgNavBackEnabled);
		} else {
			mDrawerToggle.setDrawerIndicatorEnabled(!mState.mFlgNavUpEnabled);
			mDrawerToggle.syncState();
		}
	}
}
