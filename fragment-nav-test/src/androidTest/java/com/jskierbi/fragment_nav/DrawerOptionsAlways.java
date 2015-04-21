package com.jskierbi.fragment_nav;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import com.jskierbi.commons.navigation.FragmentNavigation;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.jskierbi.commons_espresso.ActivityConfigChangeUtils.getCurrentActivity;
import static com.jskierbi.commons_espresso.ActivityConfigChangeUtils.orientationChange;
import static com.jskierbi.commons_espresso.NavigationDrawerViewActions.closeDrawer;
import static com.jskierbi.commons_espresso.NavigationDrawerViewMatcher.isDrawerOpen;
import static org.hamcrest.Matchers.not;

/**
 * Created by jakub on 04/21/2015.
 */
public class DrawerOptionsAlways extends ActivityInstrumentationTestCase2<ActivityWithToolbarAndDrawerAlways> {

	public DrawerOptionsAlways() {
		super(ActivityWithToolbarAndDrawerAlways.class);
	}

	@Override protected void setUp() throws Exception {
		super.setUp();
		getActivity();
	}

	@Override protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDrawerAndToggleAlways() {

		FragmentNavigation fragmentNavigationAnnotation = getActivity().getClass().getAnnotation(FragmentNavigation.class);
		assertNotNull("Activity is annotated with @FragmentNavigation", fragmentNavigationAnnotation);

		{ // Drawer unlocked
			DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(fragmentNavigationAnnotation.drawerLayoutId());
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.START));
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.END));
		}

		// Navigate to fragment
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateTo(new StateSavingFragment());
			}
		});
		getInstrumentation().waitForIdleSync();

		assertEquals("Navigate to successful", 1, getActivity().getSupportFragmentManager().getBackStackEntryCount());
		{ // Drawer unlocked
			DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(fragmentNavigationAnnotation.drawerLayoutId());
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.START));
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.END));
		}

		// Toggle is enabled, try to open drawer
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				// Set content description on toggle, so we can find it later
				getActivity().getDrawerToggleDelegate().setActionBarUpIndicator(
						getActivity().getV7DrawerToggleDelegate().getThemeUpIndicator(),
						R.string.navigate_back);
			}
		});
		onView(withContentDescription(getActivity().getString(R.string.navigate_back)))
				.perform(click());

		// Drawer is open, nav back not performed
		onView(withId(fragmentNavigationAnnotation.drawerLayoutId()))
				.check(matches(isDrawerOpen(GravityCompat.START)))
				.perform(closeDrawer(GravityCompat.START))
				.check(matches(not(isDrawerOpen(GravityCompat.START))));

		// Navigate back
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateBack();
			}
		});
		getInstrumentation().waitForIdleSync();

		assertEquals("Navigate back successful", 0, getActivity().getSupportFragmentManager().getBackStackEntryCount());
		{ // Drawer unlocked
			DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(fragmentNavigationAnnotation.drawerLayoutId());
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.START));
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.END));
		}
	}

	public void testDrawerAndToggleAlwaysOrientationChange() {

		FragmentNavigation fragmentNavigationAnnotation = getActivity().getClass().getAnnotation(FragmentNavigation.class);
		assertNotNull("Activity is annotated with @FragmentNavigation", fragmentNavigationAnnotation);

		{ // Drawer unlocked
			DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(fragmentNavigationAnnotation.drawerLayoutId());
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.START));
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.END));
		}

		// Navigate to fragment
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateTo(new StateSavingFragment());
			}
		});
		getInstrumentation().waitForIdleSync();

		assertEquals("Navigate to successful", 1, getActivity().getSupportFragmentManager().getBackStackEntryCount());
		{ // Drawer unlocked
			DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(fragmentNavigationAnnotation.drawerLayoutId());
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.START));
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.END));
		}

		// Change orientation
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		// Toggle is enabled, try to open drawer
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				// Set content description on toggle, so we can find it later
				getActivity().getDrawerToggleDelegate().setActionBarUpIndicator(
						getActivity().getV7DrawerToggleDelegate().getThemeUpIndicator(),
						R.string.navigate_back);
			}
		});
		onView(withContentDescription(getActivity().getString(R.string.navigate_back)))
				.perform(click());

		// Drawer is open, nav back not performed
		onView(withId(fragmentNavigationAnnotation.drawerLayoutId()))
				.check(matches(isDrawerOpen(GravityCompat.START)))
				.perform(closeDrawer(GravityCompat.START))
				.check(matches(not(isDrawerOpen(GravityCompat.START))));

		// Navigate back
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				getActivity().getFragmentNavigationController().navigateBack();
			}
		});
		getInstrumentation().waitForIdleSync();

		assertEquals("Navigate back successful", 0, getActivity().getSupportFragmentManager().getBackStackEntryCount());
		{ // Drawer unlocked
			DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(fragmentNavigationAnnotation.drawerLayoutId());
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.START));
			assertEquals("Drawer is unlocked", DrawerLayout.LOCK_MODE_UNLOCKED, drawerLayout.getDrawerLockMode(GravityCompat.END));
		}
	}
}
