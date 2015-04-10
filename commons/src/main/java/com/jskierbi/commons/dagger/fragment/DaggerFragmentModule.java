package com.jskierbi.commons.dagger.fragment;

import com.jskierbi.commons.dagger.ForFragment;
import com.jskierbi.commons.dagger.Injector;
import dagger.Module;
import dagger.Provides;

/**
 * Created by jakub on 04/08/2015.
 */
@Module(
		library = true
)
public class DaggerFragmentModule {

	private android.support.v4.app.Fragment mSupportFragment;
	private android.app.Fragment mFragment;
	private Injector mInjector;

	public DaggerFragmentModule(android.support.v4.app.Fragment fragment, Injector injector) {
		mSupportFragment = fragment;
		mInjector = injector;
	}

	public DaggerFragmentModule(android.app.Fragment mFragment, Injector mInjector) {
		this.mFragment = mFragment;
		this.mInjector = mInjector;
	}

	@Provides @ForFragment android.support.v4.app.Fragment provideSupportFragment() {
		return mSupportFragment;
	}

	@Provides @ForFragment android.app.Fragment provideFragment() {
		return mFragment;
	}

	@Provides @ForFragment Injector provideFragmentInjector() {
		return mInjector;
	}
}
