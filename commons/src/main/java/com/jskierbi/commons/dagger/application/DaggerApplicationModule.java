package com.jskierbi.commons.dagger.application;

import android.app.Application;
import android.content.Context;
import com.jskierbi.commons.dagger.ForApplication;
import com.jskierbi.commons.dagger.Injector;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by jakub on 04/10/2015.
 */
@Module(library = true)
public class DaggerApplicationModule {

	private Application mApplication;
	private Injector mInjector;

	public DaggerApplicationModule(Application mApplication, Injector mInjector) {
		this.mApplication = mApplication;
		this.mInjector = mInjector;
	}

	@Provides @ForApplication @Singleton Context provideApplicationContext() {
		return mApplication.getApplicationContext();
	}

	@Provides @ForApplication Injector provideApplicationInjector() {
		return mInjector;
	}
}
