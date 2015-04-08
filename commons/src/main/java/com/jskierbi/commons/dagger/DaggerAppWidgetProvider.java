package com.jskierbi.commons.dagger;

import android.appwidget.AppWidgetProvider;
import dagger.ObjectGraph;

/**
 * Created by jakub on 04/08/2015.
 */
public class DaggerAppWidgetProvider extends AppWidgetProvider implements Injector {

	private ObjectGraph mObjectGraph;
	private boolean mFristAttach = true;

//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//
//		// expand the activity graph with the fragment-specific module(s)
//		ObjectGraph appGraph = ((Injector) activity).getObjectGraph();
//		List<Object> fragmentModules = getModules();
//		mObjectGraph = appGraph.plus(fragmentModules.toArray());
//
//		// make sure it's the first time through; we don't want to re-inject a retained fragment that is going
//		// through a detach/attach sequence.
//		if (mFirstAttach == true) {
//			inject(this);
//			mFirstAttach = false;
//		}
//	}

	@Override public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}

	@Override public ObjectGraph getObjectGraph() {
		return mObjectGraph;
	}
}
