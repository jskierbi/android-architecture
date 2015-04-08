package com.jskierbi.commons.dagger;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for creating injectible application subclass.
 */
public abstract class DaggerApplication extends Application implements Injector {

	private ObjectGraph mObjectGraph;

	@Override public void onCreate() {
		super.onCreate();
		mObjectGraph = ObjectGraph.create(listModules().toArray());
		mObjectGraph.inject(this);
	}

	protected List<Object> listModules() {
		List<Object> modules = new ArrayList<Object>();
		modules.add(new ApplicationModule(this, this));
		return modules;
	}

	@Override
	public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}

	@Override
	public ObjectGraph getObjectGraph() {
		return mObjectGraph;
	}

	@Module(library = true)
	public static class ApplicationModule {

		private Application mApplication;
		private Injector mInjector;

		public ApplicationModule(Application mApplication, Injector mInjector) {
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
}
