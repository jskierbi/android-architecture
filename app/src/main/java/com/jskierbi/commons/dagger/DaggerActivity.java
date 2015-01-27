package com.jskierbi.commons.dagger;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import dagger.ObjectGraph;

import java.util.List;

/**
 * Base class for creating injectable Activities
 */
public abstract class DaggerActivity extends FragmentActivity {

	private ObjectGraph mObjectGraph;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Application graph plus activity modules, then inject
		DaggerApplication application = (DaggerApplication) getApplication();
		mObjectGraph = application.getObjectGraph().plus(listModules().toArray());
		mObjectGraph.inject(this);
	}

	/** Returns list of modules for this component */
	protected abstract List<Object> listModules();

	/** Helper method to inject arbitrary object by this components object graph */
	public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}

	ObjectGraph getObjectGraph() {
		return mObjectGraph;
	}
}
