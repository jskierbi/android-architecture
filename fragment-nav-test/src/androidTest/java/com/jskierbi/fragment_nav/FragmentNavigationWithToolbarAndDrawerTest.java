package com.jskierbi.fragment_nav;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import com.jskierbi.commons.navigation.FragmentNavigationController;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static com.jskierbi.fragment_nav.OrientationChangeAction.orientationLandscape;

public class FragmentNavigationWithToolbarAndDrawerTest
		extends ActivityInstrumentationTestCase2<ActivityWithToolbarAndDrawer> {

	private static final String TAG = FragmentNavigationWithToolbarAndDrawerTest.class.getSimpleName();
	private FragmentNavigationController mFragmentNavigationController;
	private FragmentManager mFragmentManager;

	public FragmentNavigationWithToolbarAndDrawerTest() {
		super(ActivityWithToolbarAndDrawer.class);
	}

	@Override protected void setUp() throws Exception {
		super.setUp();
		final ActivityWithToolbarAndDrawer activity = getActivity();
		mFragmentNavigationController = activity.getFragmentNavigationController();
		mFragmentManager = activity.getSupportFragmentManager();
	}

	@Override protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSimpleOrientationChangeDefaultFragmentState() {
//		FragmentNavigation fragmentNavigation = getActivity().getClass().getAnnotation(FragmentNavigation.class);
//		Fragment fragment = mFragmentManager.findFragmentById(fragmentNavigation.fragmentContainerId());
//
//		final String key = "INSTANCE_STATE_ID";
//		final String value = UUID.randomUUID().toString();
//		Bundle bundle = new Bundle();
//		bundle.putString(key, value);
//		fragment.setArguments(bundle);
		onView(isRoot()).perform(orientationLandscape());
//		getInstrumentation().waitForIdleSync();
//		fragment = mFragmentManager.findFragmentById(fragmentNavigation.fragmentContainerId());
//		assertEquals("Default fragment: state is restored after orientation change", key, fragment.getArguments().getString(key));
	}

//	public void testNavigateTo() {
//		Fragment detailFragment = new DummyFragment();
//		mFragmentNavigationController.navigateTo(detailFragment);
//		getInstrumentation().waitForIdleSync();
//		assertEquals("Previous fragment added to backstack", 1, mFragmentManager.getBackStackEntryCount());
//		onView(isRoot()).perform(orientationLandscape());
//		assertEquals("Previous fragment added to backstack", 1, mFragmentManager.getBackStackEntryCount());
//	}
}