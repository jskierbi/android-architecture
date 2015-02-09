package com.jskierbi.commons.navservice;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
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
			throw new IllegalStateException("Activity does not implement NavServiceHost interface (required by BaseNavDrawerFragment)");
		}
		NavServiceHost host = (NavServiceHost) getActivity();

		mDrawerLayout = ViewHierarchyHelper.findChildViewOfType(DrawerLayout.class, rootView);
		if (mDrawerLayout == null) {
			throw new IllegalStateException("DrawerLayout not found in activity view hierarchy.");
		}

		mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),
				mDrawerLayout,
				host.toolbar(),
				R.string.open_drawer,
				R.string.close_drawer);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
	}

	@Override public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}
