package com.jskierbi.app_template.base;

import com.jskierbi.app_template.modules.ActivityModule;
import com.jskierbi.commons.dagger.activity.DaggerActionBarActivity;

import java.util.List;

/**
 * Created by jakub on 01/27/2015.
 */
public class BaseAppActivity extends DaggerActionBarActivity {

	@Override protected List<Object> listModules() {
		List<Object> modules = super.listModules();
		modules.add(new ActivityModule(this));
		return modules;
	}
}
