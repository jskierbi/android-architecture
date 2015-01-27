package com.jskierbi.notificationdemo.base;

import com.jskierbi.commons.dagger.DaggerActivity;
import com.jskierbi.notificationdemo.modules.ActivityModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakub on 01/27/2015.
 */
public class BaseActivity extends DaggerActivity {

	@Override protected List<Object> listModules() {
		return Arrays.<Object>asList(new ActivityModule(this));
	}
}
