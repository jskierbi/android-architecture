package com.jskierbi.app_template.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.jskierbi.app_template.R;
import com.jskierbi.app_template.base.BaseAppActivity;
import com.jskierbi.commons.dagger.ForApplication;
import com.jskierbi.commons.navigation.FragmentNavigation;
import com.jskierbi.commons.navigation.FragmentNavigationController;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
@FragmentNavigation(
		fragmentContainerId = R.id.content,
		toolbarId = R.id.toolbar,
		defaultFragmentClass = MainFragment.class
//		primaryDrawerLayoutId = R.id.drawer_layout
)
public class ActivityWithDrawerToolbar extends BaseAppActivity {

//	@InjectView(R.id.content) FrameLayout mContent;
	@InjectView(R.id.toolbar) Toolbar mToolbar;

	@Inject @ForApplication Context mContext;
	@Inject FragmentNavigationController mFragmentNavigationController;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_with_drawer_toolbar);
		ButterKnife.inject(this);
		setSupportActionBar(mToolbar);
	}

	@Override public void onBackPressed() {
		mFragmentNavigationController.onBackPressed();
	}
}
