package com.jskierbi.app_template.base;

import com.jskierbi.app_template.modules.ActivityModule;
import com.jskierbi.commons.dagger.DaggerActionBarActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakub on 01/27/2015.
 */
public class BaseAppActivity extends DaggerActionBarActivity {

	@Override protected List<Object> listModules() {
		return Arrays.<Object>asList(new ActivityModule(this));
	}
}
