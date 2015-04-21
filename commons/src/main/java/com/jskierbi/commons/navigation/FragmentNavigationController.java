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
// TODOJS single drawer
// TODOJS double drawer
public class FragmentNavigationController {

	private static final String TAG = FragmentNavigationController.class.getSimpleName();
	private static final String TAG_HOST_INTEGRATION_FRAGMENT = TAG + "_TAG_HOST_INTEGRATION_FRAGMENT";
	private static final String KEY_INSTANCE_STATE = TAG + "_INSTANCE_STATE";
	private static final String STATE_KEY_ACTIONBAR_DISPLAY_OPTIONS = "STATE_KEY_ACTIONBAR_DISPLAY_OPTIONS";
	private static final String STATE_KEY_FLG_NAV_UP_ENABLED = "STATE_KEY_FLG_NAV_UP_ENABLED";
	private final FragmentManager mFragmentManager;
	private final FragmentActivity mActivity;
	private final ActionBarActivity mActionBarActivity;
	private final DoubleBackToExitHandler mDoubleBackToExitHandler = new DoubleBackToExitHandler();
	private final @IdRes int mContainerId;
	private final Class mDefaultFragment;

	private final boolean mFlgDoubleBackToExit;
	private final @StringRes int mDoubleBackToExitText;

	private final @IdRes int mToolbarId;
	private final @IdRes int mDrawerLayoutId;
	private ActionBarDrawerToggle mDrawerToggle;
	private State mState = new State();
	/**
	 * Creates navigation service that is hosted by activity.
	 * There is headless fragment created and added via FragmentManger inside this constructor
	 * to manage toolbar state (uses Fragments' lifecycle callbacks to save and restore state)
	 */
	public FragmentNavigationController(FragmentActivity activity) {

		FragmentNavigation fragmentNavigation = activity.getClass().getAnnotation(FragmentNavigation.class);
		if (fragmentNavigation == null) {
			throw new IllegalArgumentException("Activity hosting NavService has to be annotated with @NavigationHost");
		}

		mContainerId = fragmentNavigation.fragmentContainerId();
		mToolbarId = fragmentNavigation.toolbarId();
		mFlgDoubleBackToExit = fragmentNavigation.doubleBackToExitEnabled();
		mDoubleBackToExitText = fragmentNavigation.doubleBackToExitText();
		mDrawerLayoutId = fragmentNavigation.drawerLayoutId();
		mDefaultFragment = fragmentNavigation.defaultFragmentClass();

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
		navigateTo(fragment, Backstack.YES);
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
	public void navigateTo(android.support.v4.app.Fragment nextFragment, Backstack addToBackstack) {

		try {
			Fragment currentFragment = mFragmentManager.findFragmentById(mContainerId);
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
			transaction.replace(mContainerId, nextFragment);
			if (addToBackstack == Backstack.YES) {
				transaction.addToBackStack(null);
			}
			transaction.commit();

			final boolean isNavUpEnabled = addToBackstack == Backstack.YES || mFragmentManager.getBackStackEntryCount() > 0;
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
				if ((currentFragment = mFragmentManager.findFragmentById(mContainerId)) != null) {
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
			if (mFlgDoubleBackToExit && mActivity.isTaskRoot()) {
				if (mDoubleBackToExitHandler.isExitOnBack()) {
					mActivity.finish();
				} else {
					Toast.makeText(mActivity, mDoubleBackToExitText, Toast.LENGTH_SHORT).show();
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
			final Fragment currentFragment = mFragmentManager.findFragmentById(mContainerId);
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

		// TODOJS perform runtime checks of annotated fields
		// 1. FragmentContainer should extend FrameLayout
		// 2. If toolbar ID set, it have to exist in view hierarchy
		// 3. If double back to exti set, there should be also text set
		// 4. If primary drawer layout id is set, it have to exist in view hierarchy.
		// 5. If secondary drawer layout id is set, it have to exist in view hierarchy.

		{   // Initialize drawer toggle, if DrawerLayout available
			View activityRootView = mActivity.findViewById(android.R.id.content);
//			final DrawerLayout drawerLayout = ViewHierarchyHelper.findChildViewOfType(DrawerLayout.class, activityRootView);
			final DrawerLayout drawerLayout = (DrawerLayout) mActionBarActivity.findViewById(mDrawerLayoutId);
			if (drawerLayout != null) {
				final Toolbar toolbar = (Toolbar) mActivity.findViewById(mToolbarId);
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
			updateHomeAsUpState(mState.mFlgNavUpEnabled);
		} else {
			// If not restoring state, initialize!
			updateHomeAsUpState(false); // we're initializing - no nav up cause backstack is empty
			if (mDrawerToggle != null) {
				mActionBarActivity.getSupportActionBar().setHomeButtonEnabled(true);
				mActionBarActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				mDrawerToggle.syncState();
			}
		}

		// Add default fragment
		if (mFragmentManager.findFragmentById(mContainerId) == null) {
			try {
				Log.d(TAG, "## Add default fragment!!!");
				Fragment fragment = (Fragment) mDefaultFragment.newInstance();
				mFragmentManager.beginTransaction()
						.add(mContainerId, fragment)
						.commit();
			} catch (InstantiationException ex) {
				Log.e(TAG, "Cannot instantinate default fragment", ex);
			} catch (IllegalAccessException ex) {
				Log.e(TAG, "Cannot instantinate default fragment", ex);
			}
		}
	}
	void onResume() {
		updateHomeAsUpState(mState.mFlgNavUpEnabled);
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

	private void updateHomeAsUpState(boolean flgNavBackEnabled) {
		final ActionBar actionBar = mActionBarActivity.getSupportActionBar();
		mState.mFlgNavUpEnabled = flgNavBackEnabled;

		if (mDrawerToggle == null) {
			actionBar.setDisplayHomeAsUpEnabled(flgNavBackEnabled);
			actionBar.setHomeButtonEnabled(flgNavBackEnabled);
		} else {
			mDrawerToggle.setDrawerIndicatorEnabled(!mState.mFlgNavUpEnabled);
			mDrawerToggle.syncState();
		}
	}

	public enum Backstack {YES, NO}
	public static class State {

		int mActionbarDisplayOptions;
		boolean mFlgNavUpEnabled = false;
	}
}
