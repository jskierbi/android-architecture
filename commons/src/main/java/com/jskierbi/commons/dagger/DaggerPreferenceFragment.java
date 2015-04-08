package com.jskierbi.commons.dagger;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.preference.PreferenceFragment;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for creating injectible fragment subclasses.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class DaggerPreferenceFragment extends PreferenceFragment implements Injector {

	private ObjectGraph mObjectGraph;

	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (mObjectGraph == null) {
			Injector activityInjector = (Injector) getActivity();
			mObjectGraph = activityInjector.getObjectGraph().plus(listModules().toArray());
			mObjectGraph.inject(this);
		}
	}

	@Override public void onDestroy() {
		mObjectGraph = null;
		super.onDestroy();
	}

	protected List<Object> listModules() {
		List<Object> modules = new ArrayList<>();
		modules.add(new DaggerFragmentModule(this, this));
		return modules;
	}

	@Override public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}

	@Override public ObjectGraph getObjectGraph() {
		return mObjectGraph;
	}
}
