package com.jskierbi.commons.dagger;

import dagger.ObjectGraph;

public interface Injector {

	/** Inject target object using components object graph */
	void inject(Object obj);

	/** Gets the object graph for implementing component */
	ObjectGraph getObjectGraph();
}
