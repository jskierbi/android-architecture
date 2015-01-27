package com.jskierbi.app_template.modules;

import com.jskierbi.app_template.base.BaseFragment;
import dagger.Module;

/**
 * Created by jakub on 01/27/2015.
 */
@Module(
		injects = {},
		addsTo = ActivityModule.class
)
public class FragmentModule {

	private BaseFragment mFragment;

	public FragmentModule(BaseFragment fragment) {
		mFragment = fragment;
	}
}
