package com.jskierbi.commons.dagger.activity;

import android.app.Activity;
import android.content.Context;
import com.jskierbi.commons.dagger.ForActivity;
import com.jskierbi.commons.dagger.Injector;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by jakub on 04/08/2015.
 */
@Module(
		library = true
)
public class DaggerActivityModule {

	private Activity mActivity;
	private Injector mInjector;

	public DaggerActivityModule(Activity activity, Injector injector) {
		mActivity = activity;
		mInjector = injector;
	}

	@Provides @ForActivity @Singleton Context provideActivityContext() {
		return mActivity;
	}

	@Provides Activity provideActivity() {
		return mActivity;
	}

	@Provides @ForActivity Injector provideActivityInjector() {
		return mInjector;
	}
}
