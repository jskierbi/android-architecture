package com.jskierbi.app_template.services;

import android.support.annotation.StringRes;
import com.jskierbi.app_template.R;
import com.jskierbi.app_template.base.BaseAppActivity;
import com.jskierbi.app_template.ui.MainFragment;
import com.jskierbi.commons.navservice.BaseNavFragment;
import com.jskierbi.commons.navservice.BaseNavService;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class NavService extends BaseNavService {

	@Inject
	public NavService(BaseAppActivity activity) {
		super(activity);
	}

	@Override protected BaseNavFragment defaultFragment() {
		return new MainFragment();
	}

	@Override protected @StringRes int doubleBackToExit() {
		return R.string.double_back_to_exit;
	}
}
