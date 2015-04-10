package com.jskierbi.commons.dagger.service;

import android.app.Service;
import com.jskierbi.commons.dagger.Injector;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakub on 04/10/2015.
 */
public abstract class DaggerService extends Service implements Injector {

	private ObjectGraph mObjectGraph;

	@Override public void onCreate() {
		super.onCreate();

		ObjectGraph graph = ((Injector) getApplication()).getObjectGraph().plus(listModules().toArray());
		graph.inject(this);
	}

	protected List<Object> listModules() {
		List<Object> modules = new ArrayList<>();
		modules.add(new DaggerServiceModule(this, this));
		return modules;
	}

	@Override public ObjectGraph getObjectGraph() {
		return mObjectGraph;
	}

	@Override public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}
}
