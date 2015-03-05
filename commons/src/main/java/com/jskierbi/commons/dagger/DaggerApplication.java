package com.jskierbi.commons.dagger;

import android.app.Application;
import dagger.ObjectGraph;

import java.util.List;

/**
 * Base class for creating injectible application subclass.
 */
public abstract class DaggerApplication extends Application {

	private ObjectGraph mObjectGraph;

	@Override public void onCreate() {
		super.onCreate();
		mObjectGraph = ObjectGraph.create(listModules().toArray());
		mObjectGraph.inject(this);
	}

	public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}

	ObjectGraph getObjectGraph() {
		return mObjectGraph;
	}

	protected abstract List<Object> listModules();
}
