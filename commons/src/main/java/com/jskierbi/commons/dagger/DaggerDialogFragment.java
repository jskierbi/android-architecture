package com.jskierbi.commons.dagger;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import dagger.ObjectGraph;

import java.util.List;

/**
 * Base class for creating injectible fragment subclasses.
 */
public abstract class DaggerDialogFragment extends DialogFragment {

	private ObjectGraph mObjectGraph;

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (mObjectGraph == null) {
			DaggerActivity activity = (DaggerActivity) getActivity();
			mObjectGraph = activity.getObjectGraph().plus(listModules().toArray());
			mObjectGraph.inject(this);
		}
	}

	public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}

	protected abstract List<Object> listModules();
}
