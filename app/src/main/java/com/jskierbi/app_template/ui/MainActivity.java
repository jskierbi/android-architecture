package com.jskierbi.app_template.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.jskierbi.app_template.R;
import com.jskierbi.app_template.base.BaseAppActivity;
import com.jskierbi.commons.dagger.ForApplication;
import com.jskierbi.commons.navservice.NavService;
import com.jskierbi.commons.navservice.NavServiceHost;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class MainActivity extends BaseAppActivity implements NavServiceHost {

	@InjectView(R.id.content) FrameLayout mContent;
	@InjectView(R.id.toolbar) Toolbar mToolbar;

	@Inject @ForApplication Context mContext;
	@Inject NavService mNavService;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		setSupportActionBar(mToolbar);
	}

	@Override public Toolbar toolbar() {
		return mToolbar;
	}
	@Override public int fragmentContainerId() {
		return R.id.content;
	}
	@Override public Fragment defaultFragment() {
		return new MainFragment();
	}
	@Override public int doubleBackToExit() {
		return 0;
	}

	@Override public void onBackPressed() {
		mNavService.onBackPressed();
	}
}
