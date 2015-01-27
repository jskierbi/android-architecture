package com.jskierbi.notificationdemo.base;

import com.jskierbi.commons.dagger.DaggerApplication;
import com.jskierbi.notificationdemo.modules.AppModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakub on 01/27/2015.
 */
public class AppApplication extends DaggerApplication {

	@Override protected List<Object> listModules() {
		return Arrays.<Object>asList(new AppModule(this));
	}
}
