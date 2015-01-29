package com.jskierbi.app_template.base;

import com.jskierbi.app_template.modules.FragmentModule;
import com.jskierbi.commons.navservice.BaseNavFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakub on 01/27/2015.
 */
public class BaseFragment extends BaseNavFragment {

	@Override protected List<Object> listModules() {
		return Arrays.<Object>asList(new FragmentModule(this));
	}
}
