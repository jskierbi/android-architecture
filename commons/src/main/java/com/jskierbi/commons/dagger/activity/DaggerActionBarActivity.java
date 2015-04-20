package com.jskierbi.commons.dagger.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.jskierbi.commons.dagger.Injector;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for creating injectable Activities
 */
public abstract class DaggerActionBarActivity extends ActionBarActivity implements Injector {

	private ObjectGraph mObjectGraph;

	@Override protected void onCreate(Bundle savedInstanceState) {
		// Application graph plus activity modules, then inject
		// Create graph before super.onCreate()!!!
		Injector appInjector = (Injector) getApplication();
		mObjectGraph = appInjector.getObjectGraph().plus(listModules().toArray());

		super.onCreate(savedInstanceState);
		// Call inject after super.onCreate()!!!
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
