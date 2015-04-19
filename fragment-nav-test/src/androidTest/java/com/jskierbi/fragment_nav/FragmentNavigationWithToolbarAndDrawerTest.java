package com.jskierbi.fragment_nav;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.jskierbi.commons.navigation.FragmentNavigation;

import java.util.List;
import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static com.jskierbi.fragment_nav.ActivityConfigChangeUtils.getCurrentActivity;
import static com.jskierbi.fragment_nav.ActivityConfigChangeUtils.orientationChange;


public class FragmentNavigationWithToolbarAndDrawerTest
		extends ActivityInstrumentationTestCase2<ActivityWithToolbarAndDrawer> {

	private static final String TAG = FragmentNavigationWithToolbarAndDrawerTest.class.getSimpleName();


	public FragmentNavigationWithToolbarAndDrawerTest() {
		super(ActivityWithToolbarAndDrawer.class);
	}

	@Override protected void setUp() throws Exception {
		super.setUp();
	}

	@Override protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDefaultFragmentConfigState() {

		final String key = "INSTANCE_STATE_ID";
		final String value = UUID.randomUUID().toString();

		FragmentNavigation fragmentNavigation = getActivity().getClass().getAnnotation(FragmentNavigation.class);
		final int fragmentContainerId = fragmentNavigation.fragmentContainerId();

		Activity activity = getActivity();

		{   // Set fragment parameter to be saved and then restored
			DummyFragment fragment = (DummyFragment) getActivity().getSupportFragmentManager().findFragmentById(fragmentContainerId);
			assertNotNull("Default fragment is available", fragment);
			fragment.setStateParameter(key, value);
		}
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		assertNotSame("Restarted activity", activity, getActivity());

		{   // Check, whether fragment state is restored properly
			Log.d(TAG, "@ get Fragment list");
			List<Fragment> fragmentList = getActivity().getSupportFragmentManager().getFragments();
			Log.d(TAG, "@ Fragment list size: " + fragmentList.size());

			for (Fragment fragment : fragmentList) {
				if (fragment != null) {
					Log.d(TAG, "@ Fragment: " + fragment.getClass());
				} else {
					Log.d(TAG, "@ Fragment: null");
				}
			}

			DummyFragment fragment = (DummyFragment) getActivity().getSupportFragmentManager().findFragmentById(fragmentContainerId);
//			assertNotNull("Default fragment is available", fragment);
//			assertEquals("Default fragment: state is restored after orientation change", value, fragment.getStateParameter(key));
		}
	}

//	public void testNavigateTo() {
//		Fragment detailFragment = new DummyFragment();
//		mFragmentNavigationController.navigateTo(detailFragment);
//		getInstrumentation().waitForIdleSync();
//		assertEquals("Previous fragment added to backstack", 1, mFragmentManager.getBackStackEntryCount());
//		onView(isRoot()).perform(changeOrientation());
//		assertEquals("Previous fragment added to backstack", 1, mFragmentManager.getBackStackEntryCount());
//	}
}