package com.jskierbi.notificationdemo.base;

import com.jskierbi.commons.dagger.DaggerFragment;
import com.jskierbi.notificationdemo.modules.FragmentModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakub on 01/27/2015.
 */
public class BaseFragment extends DaggerFragment {

	@Override protected List<Object> listModules() {
		return Arrays.<Object>asList(new FragmentModule(this));
	}
}
