package com.jskierbi.fragment_nav;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import com.jskierbi.commons.navigation.FragmentNavigation;
import com.jskierbi.commons.navigation.FragmentNavigationController;

/**
 * Created by jakub on 04/14/2015.
 */
@FragmentNavigation(
		defaultFragmentClass = StateSavingFragment.class,
		fragmentContainerId = R.id.fragment_container,
		primaryDrawerLayoutId = R.id.drawer_layout,
		toolbarId =  R.id.toolbar,
		doubleBackToExitEnabled = true
)
public class ActivityWithToolbarAndDrawer extends ActionBarActivity {

	private FragmentNavigationController mFragmentNavigationController;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_with_tollbar_and_drawer);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		mFragmentNavigationController = new FragmentNavigationController(this);
	}

	public FragmentNavigationController getFragmentNavigationController() {
		return mFragmentNavigationController;
	}
}
