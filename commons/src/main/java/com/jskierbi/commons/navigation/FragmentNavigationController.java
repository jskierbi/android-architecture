package com.jskierbi.commons.navigation;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.jskierbi.commons.R;

/**
 * Base NavService class.
 * Manages toolbar home as up and DrawerToggle (if DrawerLayout is available in activity)
 *
 * Integration with Activity:
 * 1. annotate activity with @NavigationHost
 * 2. call {@link FragmentNavigationController#onBackPressed()} from {@link Activity#onBackPressed}
 */

// TODOJS no toolbar
// TODOJS activity does not extend ActionBarActivity
// TODOJS no drawer
public class FragmentNavigationController {

	protected static final String TAG = FragmentNavigationController.class.getSimpleName();
	protected static final String TAG_HOST_INTEGRATION_FRAGMENT = TAG + "_TAG_HOST_INTEGRATION_FRAGMENT";
	protected static final String KEY_INSTANCE_STATE = TAG + "_INSTANCE_STATE";
	protected static final String STATE_KEY_ACTIONBAR_DISPLAY_OPTIONS = "STATE_KEY_ACTIONBAR_DISPLAY_OPTIONS";
	protected static final String STATE_KEY_FLG_NAV_UP_ENABLED = "STATE_KEY_FLG_NAV_UP_ENABLED";
	protected final FragmentManager mFragmentManager;
	protected final FragmentActivity mActivity;
	protected final ActionBarActivity mActionBarActivity;
	protected final DoubleBackToExitHandler mDoubleBackToExitHandler = new DoubleBackToExitHandler();

	/** @FragmentNavigation annotation parameters form Activity class */
	protected final NavigatonParameters mParams;
	private static class NavigatonParameters {

		protected @IdRes int fragmentContainerId;
		protected Class defaultFragmentClass;
		protected @StringRes int doubleBackToExitWithText;
		protected @IdRes int toolbarId;
		protected @IdRes int drawerLayoutId;
		protected @FragmentNavigation.NavOption int drawerOptions;
	}

	protected ActionBarDrawerToggle mDrawerToggle;
	protected State mState = new State();

	protected static class State {

		int mActionbarDisplayOptions;
		boolean mFlgNavUpEnabled = false;
	}

	/**
	 * Creates navigation service that is hosted by activity.
	 * There is headless fragment created and added via FragmentManger inside this constructor
	 * to manage toolbar state (uses Fragments' lifecycle callbacks to save and restore state)
	 */
	public FragmentNavigationController(FragmentActivity activity) {
		mParams = readActivityAnnotations(activity);

		// TODO use delegate here
		mActivity = activity;
		mActionBarActivity = mActivity instanceof ActionBarActivity ?
				(ActionBarActivity) activity :
				null;
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
	// TODO change parameter to base Fragment
	// TODO support both android.app.Fragment and android.support.v4.app.Fragment
	public void navigateTo(android.support.v4.app.Fragment fragment) {
		navigateTo(fragment, BackstackAdd.YES);
	}
	/**
	 * Base navigation method. Adds proper support for transition animations.
	 * Animations are defined in BaseNavFragment using {@link AnimatedSupportFragment#setCustomAnimations}
	 *
	 * Addressed problems:
	 * * When changing orientations, custom animations from transactions are lost. This is fixed in {@link AnimatedSupportFragment}
	 * * When navigating back, popExit for current fragment and popEnter for previous fragment are inconsistent.
	 * This method sets popEnter for previous fragment using popEnter from this fragment - so popEnter in each
	 * fragment is de facto used for previous fragment. This enables to define fragment transitions on sigle fragment object.
	 *
	 * @param nextFragment to navigate to
	 * @param addToBackstack whether to add transaction to backstack or not.
	 */
	public void navigateTo(android.support.v4.app.Fragment nextFragment, BackstackAdd addToBackstack) {

		try {
			Fragment currentFragment = mFragmentManager.findFragmentById(mParams.fragmentContainerId);
			AnimatedSupportFragment nextFragmentAnimated = nextFragment instanceof AnimatedSupportFragment ?
					(AnimatedSupportFragment) nextFragment : null;
			if (currentFragment instanceof AnimatedSupportFragment) {
				// For current fragment, set animation that will be played when navigating back
				// This is to enable defining animations on single fragment that will be respected by previous
				// fragment in backstack
				((AnimatedSupportFragment) currentFragment).setPopEnterAnim(
						nextFragmentAnimated == null ? 0 : nextFragmentAnimated.getPopEnterAnim());
			}

			// Use case: exit via home, return to application. When popEnter animation set by fragment is used,
			// each time user returns to app, animation will be played - this is wrong, no animation should be played.
			// PopEnter animation will be set for nextFragment in next navigateTo call (nextFragment will become
			// currentFragment in next navigateTo call)
			if (nextFragmentAnimated != null) {
				nextFragmentAnimated.setPopEnterAnim(0);
			}

			FragmentTransaction transaction = mFragmentManager.beginTransaction();
			// Disable animation Pre 4.0.
			// Animation fixes in BaseNavFragment uses {@link Activity#isChangingConfigurations}, which is unavailable
			// for pre 3.0
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && nextFragmentAnimated != null) {
				transaction.setCustomAnimations(
						nextFragmentAnimated.getEnterAnim(),
						nextFragmentAnimated.getExitAnim(),
						nextFragmentAnimated.getPopEnterAnim(),
						nextFragmentAnimated.getPopExitAnim());
			}
			transaction.replace(mParams.fragmentContainerId, nextFragment);
			if (addToBackstack == BackstackAdd.YES) {
				transaction.addToBackStack(null);
			}
			transaction.commit();

			final boolean isNavUpEnabled = addToBackstack == BackstackAdd.YES || mFragmentManager.getBackStackEntryCount() > 0;
			updateDrawerAndToggle(isNavUpEnabled);

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
				if ((currentFragment = mFragmentManager.findFragmentById(mParams.fragmentContainerId)) != null) {
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
			if (mParams.doubleBackToExitWithText != 0 && mActivity.isTaskRoot()) {
				if (mDoubleBackToExitHandler.isExitOnBack()) {
					mActivity.finish();
				} else {
					Toast.makeText(mActivity, mParams.doubleBackToExitWithText, Toast.LENGTH_SHORT).show();
				}
			} else {
				mActivity.finish();
			}
		}

		final boolean isNavUpEnabled = backstackEntryCount > 1;
		updateDrawerAndToggle(isNavUpEnabled);
	}
	public void clearBackstack() {
		final int backstackEntryCount = mFragmentManager.getBackStackEntryCount();
		for (int i = mFragmentManager.getBackStackEntryCount(); i > 0; --i) {
			// Current fragment could has been added outside transaction - remove it!
			final Fragment currentFragment = mFragmentManager.findFragmentById(mParams.fragmentContainerId);
			if (backstackEntryCount > 0 && currentFragment != null) {
				mFragmentManager.beginTransaction()
						.remove(currentFragment)
						.commit();
			}
			// Execute transaction immediate, so in next loop we can get current fragment and remove it!
			mFragmentManager.popBackStackImmediate();
		}
		updateDrawerAndToggle(false); // Nav back is not available
	}
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

		// Validate Acitivty annotation parameters
		if (null == mActivity.findViewById(mParams.fragmentContainerId)) {
			throw new IllegalStateException("Fragment container view not found by id. " +
					"Check Activity @FragmentNavigation annotation parameter fragmentContainerId.");
		}
		if (0 != mParams.toolbarId && null == mActivity.findViewById(mParams.toolbarId)) {
			throw new IllegalStateException("toolbarId defined but not found in view hierarchy." +
					"Check Activity @FragmentNavigation annotation parameter toolbarId");
		}
		if (0 != mParams.drawerLayoutId && null == mActivity.findViewById(mParams.drawerLayoutId)) {
			throw new IllegalStateException("drawerLayoutId defined but not found in view hierarchy." +
					"Check Activity @FragmentNavigation annotation parameter drawerLayoutId");
		}
		// If string is not found, throws an exception
		if (0 != mParams.doubleBackToExitWithText) {
			mActivity.getString(mParams.doubleBackToExitWithText);
		}

		// Initialize drawer toggle, if DrawerLayout available
		{
			View activityRootView = mActivity.findViewById(android.R.id.content);
			final DrawerLayout drawerLayout = (DrawerLayout) mActionBarActivity.findViewById(mParams.drawerLayoutId);
			if (drawerLayout != null) {
				final Toolbar toolbar = (Toolbar) mActivity.findViewById(mParams.toolbarId);
				// Handle toolbar home clicks here
				final View.OnClickListener toolbarNavListener = new View.OnClickListener() {
					@Override public void onClick(View v) {
						if (mDrawerToggle.isDrawerIndicatorEnabled()) {
							if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
								drawerLayout.closeDrawer(GravityCompat.START);
							} else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
								drawerLayout.closeDrawer(GravityCompat.END);
							} else {
								drawerLayout.openDrawer(GravityCompat.START);
							}
						} else {
							navigateBack();
						}
					}
				};
				Log.d(TAG, "Drawer Toggle: ENABLED!");
				mDrawerToggle = new ActionBarDrawerToggle(
						mActivity,
						drawerLayout,
						toolbar, // TODOJS fix this when no toolbar available!!!
						R.string.open_drawer,
						R.string.close_drawer) {

					// Handle onOptionsItemSelected here
					@Override public boolean onOptionsItemSelected(MenuItem item) {
						if (item != null && item.getItemId() == android.R.id.home) {
							toolbarNavListener.onClick(drawerLayout);
							return true;
						}
						return false;
					}
				};
				drawerLayout.setDrawerListener(mDrawerToggle);
				// workaround https://stackoverflow.com/questions/26549008/missing-up-navigation-icon-after-switching-from-ics-actionbar-to-lollipop-toolba/26932351#26932351}
				mDrawerToggle.setHomeAsUpIndicator(mActionBarActivity.getV7DrawerToggleDelegate().getThemeUpIndicator());
				// workaround http://stackoverflow.com/questions/26582075/cannot-catch-toolbar-home-button-click-event
				toolbar.setNavigationOnClickListener(toolbarNavListener);
			} else {
				Log.d(TAG, "Drawer Toggle: DISABLED!");
				mDrawerToggle = null;
			}
		}

		if (savedInstanceState != null) {
			// Restore app state
			mState.mActionbarDisplayOptions = savedInstanceState.getInt(STATE_KEY_ACTIONBAR_DISPLAY_OPTIONS);
			mState.mFlgNavUpEnabled = savedInstanceState.getBoolean(STATE_KEY_FLG_NAV_UP_ENABLED);

			if (mState.mActionbarDisplayOptions != 0 && mActionBarActivity.getSupportActionBar() != null) {
				mActionBarActivity.getSupportActionBar().setDisplayOptions(mState.mActionbarDisplayOptions);
			}
			updateDrawerAndToggle(mState.mFlgNavUpEnabled);
		} else {
			// If not restoring state, initialize!
			updateDrawerAndToggle(false); // we're initializing - no nav up cause backstack is empty
			if (mDrawerToggle != null) {
				mActionBarActivity.getSupportActionBar().setHomeButtonEnabled(true);
				mActionBarActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				mDrawerToggle.syncState();
			}
		}

		// Add default fragment
		if (mFragmentManager.findFragmentById(mParams.fragmentContainerId) == null) {
			try {
				Log.d(TAG, "## Add default fragment!!!");
				Fragment fragment = (Fragment) mParams.defaultFragmentClass.newInstance();
				mFragmentManager.beginTransaction()
						.add(mParams.fragmentContainerId, fragment)
						.commit();
			} catch (InstantiationException e) {
				Log.e(TAG, "Cannot instantinate default fragment", e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "Cannot instantinate default fragment", e);
			}
		}
	}
	void onResume() {
		updateDrawerAndToggle(mState.mFlgNavUpEnabled);
	}
	void onSaveInstanceState(Bundle outState) {
		if (mActionBarActivity.getSupportActionBar() != null) {
			mState.mActionbarDisplayOptions = mActionBarActivity.getSupportActionBar().getDisplayOptions();
			outState.putInt(STATE_KEY_ACTIONBAR_DISPLAY_OPTIONS, mState.mActionbarDisplayOptions);
			outState.putBoolean(STATE_KEY_FLG_NAV_UP_ENABLED, mState.mFlgNavUpEnabled);
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

	protected NavigatonParameters readActivityAnnotations(Activity activity) {
		FragmentNavigation fragmentNavigation = activity.getClass().getAnnotation(FragmentNavigation.class);
		if (fragmentNavigation == null) {
			throw new IllegalArgumentException("Activity hosting NavService has to be annotated with @NavigationHost");
		}

		NavigatonParameters annotated = new NavigatonParameters();
		annotated.fragmentContainerId = fragmentNavigation.fragmentContainerId();
		annotated.defaultFragmentClass = fragmentNavigation.defaultFragmentClass();
		annotated.toolbarId = fragmentNavigation.toolbarId();
		annotated.doubleBackToExitWithText = fragmentNavigation.doubleBackToExitWithText();
		annotated.drawerLayoutId = fragmentNavigation.drawerLayoutId();
		annotated.drawerOptions = fragmentNavigation.drawerOptions();

		// Runtime checks
		if (annotated.drawerLayoutId == 0 && annotated.drawerOptions != FragmentNavigation.ENABLE_ALWAYS_TOGGLE_ON_ROOT) {
			throw new IllegalArgumentException("drawerOptions set but no drawerLayoutId defined! drawerOptions has " +
					"any meaning only in conjunction with drawer layout");
		}

		return annotated;
	}

	protected void updateDrawerAndToggle(boolean flgNavBackEnabled) {
		final ActionBar actionBar = mActionBarActivity.getSupportActionBar();
		mState.mFlgNavUpEnabled = flgNavBackEnabled;

		if (mDrawerToggle == null) {
			actionBar.setDisplayHomeAsUpEnabled(flgNavBackEnabled);
			actionBar.setHomeButtonEnabled(flgNavBackEnabled);
		} else {
			switch (mParams.drawerOptions) {
			case FragmentNavigation.ENABLE_ALWAYS_TOGGLE_ON_ROOT:
				mDrawerToggle.setDrawerIndicatorEnabled(!mState.mFlgNavUpEnabled);
				mDrawerToggle.syncState();
				break;
			case FragmentNavigation.ENABLE_ALWYAS_TOGGLE_ALWAYS:
				mDrawerToggle.setDrawerIndicatorEnabled(true);
				mDrawerToggle.syncState();
				break;
			case FragmentNavigation.ENABLE_ON_ROOT_TOGGLE_ON_ROOT:
				mDrawerToggle.setDrawerIndicatorEnabled(!mState.mFlgNavUpEnabled);
				((DrawerLayout) mActivity.findViewById(mParams.drawerLayoutId)).setDrawerLockMode(flgNavBackEnabled ?
						DrawerLayout.LOCK_MODE_LOCKED_CLOSED :
						DrawerLayout.LOCK_MODE_UNLOCKED);
				mDrawerToggle.syncState();
				break;
			}
		}
	}
}
