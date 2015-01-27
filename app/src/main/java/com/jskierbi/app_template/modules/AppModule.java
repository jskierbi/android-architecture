package com.jskierbi.app_template.modules;

import android.content.Context;
import com.jskierbi.app_template.base.AppApplication;
import com.jskierbi.commons.dagger.ForApplication;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by jakub on 01/27/2015.
 */
@Module(
		injects = {
				AppApplication.class,
		},
		library = true // can be injected into activities and fragments!
)
public class AppModule {

	private AppApplication mApplication;

	public AppModule(AppApplication appApplication) {
		mApplication = appApplication;
	}

	@Provides @Singleton @ForApplication Context provideAppContext() {
		return mApplication.getApplicationContext();
	}

}
