package com.jskierbi.app_template.base;

import com.jskierbi.app_template.modules.FragmentModule;
import com.jskierbi.commons.dagger.DaggerFragment;

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
