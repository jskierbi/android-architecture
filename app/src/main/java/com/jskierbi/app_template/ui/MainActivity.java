package com.jskierbi.app_template.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.jskierbi.app_template.R;
import com.jskierbi.app_template.base.BaseActivity;
import com.jskierbi.app_template.services.AppNavService;
import com.jskierbi.commons.dagger.ForApplication;
import com.jskierbi.commons.navservice.NavService;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class MainActivity extends BaseActivity implements NavService.Host {

	@InjectView(R.id.content) FrameLayout mContent;
	@InjectView(R.id.toolbar) Toolbar mToolbar;

	@Inject @ForApplication Context mContext;
	@Inject AppNavService mNavService;

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

	@Override public void onBackPressed() {
		mNavService.onBackPressed();
	}
}
