package com.jskierbi.app_template.modules;

import com.jskierbi.app_template.base.AppApplication;
import com.jskierbi.commons.dagger.application.DaggerApplicationModule;
import dagger.Module;

/**
 * Created by jakub on 01/27/2015.
 */
@Module(
		injects = {
				AppApplication.class,
		},
		addsTo = DaggerApplicationModule.class,
		library = true // can be injected into activities and fragments!
)
public class AppModule {}
