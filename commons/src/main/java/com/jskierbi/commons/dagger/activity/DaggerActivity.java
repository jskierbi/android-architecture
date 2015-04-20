package com.jskierbi.commons.dagger.activity;

import android.app.Activity;
import android.os.Bundle;
import com.jskierbi.commons.dagger.Injector;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakub on 04/08/2015.
 */
public class DaggerActivity extends Activity implements Injector {

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
