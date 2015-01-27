package com.jskierbi.app_template.services;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.jskierbi.app_template.ui.MainFragment;
import com.jskierbi.commons.navservice.BaseNavService;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class NavService extends BaseNavService {

	@Inject
	public NavService(Activity activity, FragmentManager fragmentManager) {
		super(activity, fragmentManager);
	}

	@Override protected Fragment defaultFragment() {
		return new MainFragment();
	}
}
