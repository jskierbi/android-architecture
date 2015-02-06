package com.jskierbi.commons.navservice;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.ViewGroup;
import com.jskierbi.app_template.R;
import com.jskierbi.commons.dagger.DaggerFragment;

/**
 *
 */
public abstract class BaseDrawerFragment extends DaggerFragment {

	// @Inject BaseNavService mNavService;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View rootView = getActivity().findViewById(android.R.id.content);


		if (!(getActivity() instanceof NavServiceHost)) {
			throw new IllegalStateException("Activity does not implement NavSerivceHost interface (required by BaseNavDrawerFragment)");
		}
		NavServiceHost host = (NavServiceHost) getActivity();

		mDrawerLayout = findChildViewOfType(DrawerLayout.class, rootView);
		if (mDrawerLayout == null) {
			throw new IllegalStateException("DrawerLayout not found in activity view hierarchy.");
		}

		mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),
				mDrawerLayout,
				host.toolbar(),
				R.string.open_drawer,
				R.string.close_drawer);
	}

	/** Search view tree recursively and find DrawerLayout */
	private static <T extends View> T findChildViewOfType(Class<T> clazz, View view) {

		if (clazz.isInstance(view)) {
			return (T) view;
		}

		if (view instanceof ViewGroup) {
			ViewGroup group = ((ViewGroup) view);
			for (int i = 0; i < group.getChildCount(); ++i) {
				T found = findChildViewOfType(clazz, group.getChildAt(i));
				if (found != null) {
					return found;
				}
			}
		}

		return null;
	}
}
