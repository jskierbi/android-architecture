package com.jskierbi.commons.dagger;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for creating injectable Activities
 */
public abstract class DaggerActionBarActivity extends ActionBarActivity implements Injector {

	private ObjectGraph mObjectGraph;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Application graph plus activity modules, then inject
		Injector appInjector = (Injector) getApplication();
		mObjectGraph = appInjector.getObjectGraph().plus(listModules().toArray());
		mObjectGraph.inject(this);
	}

	@Override protected void onDestroy() {
		mObjectGraph = null;
		super.onDestroy();
	}

	protected List<Object> listModules() {
		List<Object> modules = new ArrayList<Object>();
		modules.add(new DaggerActivityModule(this, this));
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
