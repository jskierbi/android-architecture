package com.jskierbi.commons.dagger.application;

import android.app.Application;
import com.jskierbi.commons.dagger.Injector;
import dagger.ObjectGraph;

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
		modules.add(new DaggerApplicationModule(this, this));
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

}
