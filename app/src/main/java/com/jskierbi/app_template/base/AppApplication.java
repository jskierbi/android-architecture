package com.jskierbi.app_template.base;

import com.jskierbi.app_template.modules.AppModule;
import com.jskierbi.commons.dagger.application.DaggerApplication;

import java.util.List;

/**
 * Created by jakub on 01/27/2015.
 */
public class AppApplication extends DaggerApplication {

	@Override protected List<Object> listModules() {
		List<Object> modules = super.listModules();
		modules.add(new AppModule());
		return modules;
	}
}
