package com.jskierbi.app_template.services;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.jskierbi.app_template.ui.MainFragment;
import com.jskierbi.commons.navservice.NavService;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class AppNavService extends NavService {

	@Inject
	public AppNavService(Activity activity, FragmentManager fragmentManager) {
		super(activity, fragmentManager);
	}

	@Override protected Fragment defaultFragment() {
		return new MainFragment();
	}
}
