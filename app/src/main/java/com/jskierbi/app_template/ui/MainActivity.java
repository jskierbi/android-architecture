package com.jskierbi.app_template.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.jskierbi.app_template.R;
import com.jskierbi.app_template.base.BaseActivity;
import com.jskierbi.app_template.services.NavService;
import com.jskierbi.commons.dagger.ForApplication;
import com.jskierbi.commons.navservice.BaseNavService;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class MainActivity extends BaseActivity implements BaseNavService.Host {

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

	@Override public void onBackPressed() {
		mNavService.onBackPressed();
	}
}
