package com.jskierbi.fragment_nav;

import android.test.ActivityInstrumentationTestCase2;
import com.jskierbi.commons.navigation.FragmentNavigation;
import com.jskierbi.commons.navigation.FragmentNavigationController;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static com.jskierbi.commons.test.ActivityConfigChangeUtils.getCurrentActivity;
import static com.jskierbi.commons.test.ActivityConfigChangeUtils.orientationChange;


public class FragmentNavigationWithToolbarAndDrawerTest
		extends ActivityInstrumentationTestCase2<ActivityWithToolbarAndDrawer> {

	private static final String TAG = FragmentNavigationWithToolbarAndDrawerTest.class.getSimpleName();

	public FragmentNavigationWithToolbarAndDrawerTest() {
		super(ActivityWithToolbarAndDrawer.class);
	}

	@Override protected void setUp() throws Exception {
		super.setUp();
		getActivity();
	}

	@Override protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDefaultFragmentConfigChangeState() {

		final String KEY1 = "INSTANCE_STATE_ID";
		final String VALUE1 = UUID.randomUUID().toString();

		FragmentNavigation fragmentNavigationAnnotation = getActivity().getClass().getAnnotation(FragmentNavigation.class);
		assertNotNull("Activity is annotated with @FragmentNavigation", fragmentNavigationAnnotation);

		{   // Set fragment parameter to be saved and then restored
			StateSavingFragment fragment = (StateSavingFragment) getActivity()
					.getSupportFragmentManager()
					.findFragmentById(fragmentNavigationAnnotation.fragmentContainerId());
			assertNotNull("Default fragment is properly added by ", fragment);
			// Set some parameters to be restored on fragment after orientation change
			fragment.setStateParameter(KEY1, VALUE1);
		}

		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		{   // Check, whether fragment state is restored properly
			StateSavingFragment fragment = (StateSavingFragment) getActivity()
					.getSupportFragmentManager()
					.findFragmentById(fragmentNavigationAnnotation.fragmentContainerId());
			assertNotNull("Default fragment is available", fragment);
			assertEquals("Default fragment: state is restored after orientation change", VALUE1, fragment.getStateParameter(KEY1));
		}
	}

	public void testNavigateTo() {
		final StateSavingFragment fragment = new StateSavingFragment();
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateTo(fragment);
			}
		});
		getInstrumentation().waitForIdleSync();
		assertEquals("Previous fragment added to backstack", 1, getActivity().getSupportFragmentManager().getBackStackEntryCount());

		// Orientation change
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));
		assertEquals("Previous fragment added to backstack", 1, getActivity().getSupportFragmentManager().getBackStackEntryCount());
	}

	public void testNavigateBack() {
		final StateSavingFragment nextFragment = new StateSavingFragment();

		final String KEY1 = "STATE_KEY1";
		final String KEY2 = "STATE_KEY2";
		final String VALUE1 = UUID.randomUUID().toString();
		final String VALUE2 = UUID.randomUUID().toString();

		FragmentNavigation fragmentNavigationAnnotation = getActivity().getClass().getAnnotation(FragmentNavigation.class);
		assertNotNull("Activity is annotated with @FragmentNavigation", fragmentNavigationAnnotation);

		{   // Set fragment parameter to be saved and then restored
			StateSavingFragment fragment = (StateSavingFragment) getActivity()
					.getSupportFragmentManager()
					.findFragmentById(fragmentNavigationAnnotation.fragmentContainerId());
			assertNotNull("Default fragment is properly added by ", fragment);
			// Set some parameters to be restored on fragment after orientation change
			fragment.setStateParameter(KEY1, VALUE1);
		}

		// Navigate to fragment
		nextFragment.setStateParameter(KEY2, VALUE2);
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateTo(nextFragment);
			}
		});
		getInstrumentation().waitForIdleSync();
		assertEquals("Previous fragment added to backstack", 1, getActivity().getSupportFragmentManager().getBackStackEntryCount());
		assertSame("Navigated to proper fragment",
				nextFragment,
				getActivity().getSupportFragmentManager().findFragmentById(fragmentNavigationAnnotation.fragmentContainerId()));

		// Change orientation
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));
		assertEquals("Previous fragment added to backstack", 1, getActivity().getSupportFragmentManager().getBackStackEntryCount());

		{
			StateSavingFragment fragment = (StateSavingFragment) getActivity()
					.getSupportFragmentManager()
					.findFragmentById(fragmentNavigationAnnotation.fragmentContainerId());
			assertNotNull("navigated to proper fragment", fragment);
			assertEquals("Fragment state restored properly", VALUE2, fragment.getStateParameter(KEY2));
		}

		// Navigate back
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateBack();
			}
		});
		getInstrumentation().waitForIdleSync();
		assertEquals("Previous fragment popped from backstack", 0, getActivity().getSupportFragmentManager().getBackStackEntryCount());

		{
			StateSavingFragment fragment = (StateSavingFragment) getActivity()
					.getSupportFragmentManager()
					.findFragmentById(fragmentNavigationAnnotation.fragmentContainerId());
			assertNotNull("Navigated back to original fragment", fragment);
			assertEquals("Original fragment sstate restored properly", VALUE1, fragment.getStateParameter(KEY1));
		}
	}

	public void testNavigateNoBackstack() {

		final String KEY = "VALUE_KEY";
		final String VALUE1 = "VALUE_1";
		final String VALUE2 = "VALUE_2";
		final String VALUE3 = "VALUE_3";

		FragmentNavigation fragmentNavigationAnnotation = getActivity().getClass().getAnnotation(FragmentNavigation.class);
		assertNotNull("Activity is annotated with @FragmentNavigation", fragmentNavigationAnnotation);

		final StateSavingFragment fragment1 = new StateSavingFragment();
		fragment1.setStateParameter(KEY, VALUE1);
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateTo(fragment1, FragmentNavigationController.Backstack.NO);
			}
		});
		getInstrumentation().waitForIdleSync();
		assertEquals("Backstack is empty", 0, getActivity().getSupportFragmentManager().getBackStackEntryCount());

		final StateSavingFragment fragment2 = new StateSavingFragment();
		fragment2.setStateParameter(KEY, VALUE2);
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateTo(fragment2, FragmentNavigationController.Backstack.YES);
			}
		});
		getInstrumentation().waitForIdleSync();
		assertEquals("Backstack has 1 entry", 1, getActivity().getSupportFragmentManager().getBackStackEntryCount());

		// Change orientation
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		final StateSavingFragment fragment3 = new StateSavingFragment();
		fragment3.setStateParameter(KEY, VALUE3);
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateTo(fragment3, FragmentNavigationController.Backstack.NO);
			}
		});
		getInstrumentation().waitForIdleSync();

		// Change orientation
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		assertEquals("Backstack has 1 entry", 1, getActivity().getSupportFragmentManager().getBackStackEntryCount());
		{
			StateSavingFragment fragment = (StateSavingFragment) getActivity()
					.getSupportFragmentManager()
					.findFragmentById(fragmentNavigationAnnotation.fragmentContainerId());
			assertNotNull("navigated to proper fragment", fragment);
			assertEquals("Fragment state restored properly", VALUE3, fragment.getStateParameter(KEY));
		}

		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateBack();
			}
		});
		getInstrumentation().waitForIdleSync();

		assertEquals("Backstack is empty", 0, getActivity().getSupportFragmentManager().getBackStackEntryCount());
		{
			StateSavingFragment fragment = (StateSavingFragment) getActivity()
					.getSupportFragmentManager()
					.findFragmentById(fragmentNavigationAnnotation.fragmentContainerId());
			assertNotNull("navigated to proper fragment", fragment);
			assertEquals("Fragment state restored properly", VALUE1, fragment.getStateParameter(KEY));
		}
	}

	public void testNavigateNoBackstackNavigateBack() {

	}
}