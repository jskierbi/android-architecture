package com.jskierbi.commons.dagger.service;

import android.app.Service;
import android.content.Context;
import com.jskierbi.commons.dagger.ForService;
import com.jskierbi.commons.dagger.Injector;
import dagger.Module;
import dagger.Provides;

/**
 * Created by jakub on 04/10/2015.
 */
@Module(library = true)
public class DaggerServiceModule {

	private Injector mInjector;
	private Service mService;

	public DaggerServiceModule(Injector mInjector, Service mService) {
		this.mInjector = mInjector;
		this.mService = mService;
	}

	@Provides @ForService Injector provideServiceInjector() {
		return mInjector;
	}

	@Provides @ForService Context provideServiceContext() {
		return mService;
	}

	@Provides @ForService Service provideService() {
		return mService;
	}
}
