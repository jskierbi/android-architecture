package com.jskierbi.commons.dagger.fragment;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import com.jskierbi.commons.dagger.Injector;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for creating injectible fragment subclasses.
 */
public class DaggerDialogFragment extends DialogFragment implements Injector {

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

	/** Returns list of modules for Activity. */
	protected List<Object> listModules() {
		List<Object> modules = new ArrayList<Object>();
		modules.add(new DaggerFragmentModule(this, this));
		return modules;
	}

	@Override
	public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}

	@Override public ObjectGraph getObjectGraph() {
		return mObjectGraph;
	}

}
