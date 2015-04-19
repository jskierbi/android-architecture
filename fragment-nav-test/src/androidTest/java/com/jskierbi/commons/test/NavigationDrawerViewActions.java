package com.jskierbi.commons.test;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Created by jakub on 04/19/2015.
 */
public class NavigationDrawerViewActions {

	/**
	 * @param gravity either GravityCompat.START or GravityCompat.END
	 */
	public static ViewAction openDrawer(final int gravity) {
		return new ViewAction() {
			@Override public Matcher<View> getConstraints() {
				return isAssignableFrom(DrawerLayout.class);
			}
			@Override public String getDescription() {
				return "open drawer";
			}
			@Override public void perform(UiController uiController, View view) {
				DrawerLayout drawerLayout = (DrawerLayout) view;
				drawerLayout.openDrawer(gravity);
				uiController.loopMainThreadUntilIdle();
				int count = 0;
				while (!drawerLayout.isDrawerVisible(gravity) || !drawerLayout.isDrawerOpen(gravity)) {
					uiController.loopMainThreadForAtLeast(10l);
					uiController.loopMainThreadUntilIdle();
					if (++count > 200) throw new RuntimeException("Cannot open drawer!");
				}
			}
		};
	}

	/**
	 * @param gravity either GravityCompat.START or GravityCompat.END
	 */
	public static ViewAction closeDrawer(final int gravity) {
		return new ViewAction() {
			@Override public Matcher<View> getConstraints() {
				return isAssignableFrom(DrawerLayout.class);
			}
			@Override public String getDescription() {
				return "close drawer";
			}
			@Override public void perform(UiController uiController, View view) {
				DrawerLayout drawerLayout = (DrawerLayout) view;
				drawerLayout.closeDrawer(gravity);
				uiController.loopMainThreadUntilIdle();
				int count = 0;
				while (drawerLayout.isDrawerVisible(gravity) && drawerLayout.isDrawerOpen(gravity)) {
					uiController.loopMainThreadForAtLeast(10l);
					uiController.loopMainThreadUntilIdle();
					if (++count > 200) throw new RuntimeException("Cannot close drawer!");
				}
			}
		};
	}
}
