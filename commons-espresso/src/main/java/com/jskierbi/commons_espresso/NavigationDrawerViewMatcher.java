package com.jskierbi.commons_espresso;

import android.support.v4.widget.DrawerLayout;
import android.view.View;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by jakub on 04/19/2015.
 */
public class NavigationDrawerViewMatcher {

	/**
	 * @param gravity either GravityCompat.START or GravityCompat.END
	 */
	public static Matcher<View> isDrawerOpen(final int gravity) {
		return new TypeSafeMatcher<View>() {
			@Override public void describeTo(Description description) {
				description.appendText("Drawer is open");
			}
			@Override public boolean matchesSafely(View view) {
				return ((DrawerLayout) view).isDrawerOpen(gravity);
			}
		};
	}

	/**
	 * @param gravity either GravityCompat.START or GravityCompat.END
	 */
	public static Matcher<View> isDrawerVisible(final int gravity) {
		return new TypeSafeMatcher<View>() {
			@Override public void describeTo(Description description) {
				description.appendText("Drawer is visible");
			}
			@Override public boolean matchesSafely(View view) {
				return ((DrawerLayout) view).isDrawerVisible(gravity);
			}
		};
	}
}
