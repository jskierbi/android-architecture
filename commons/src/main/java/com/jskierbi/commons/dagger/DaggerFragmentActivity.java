package com.jskierbi.commons.dagger;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakub on 04/08/2015.
 */
public class DaggerFragmentActivity extends FragmentActivity implements Injector {

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
