package com.jskierbi.commons_espresso.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jakub on 04/14/2015.
 */
public class StateSavingFragment extends Fragment {

	private final Map<String, String> mParameters = new HashMap<>();

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			for (String key : savedInstanceState.keySet()) {
				mParameters.put(key, savedInstanceState.getString(key));
			}
		}
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (String key : mParameters.keySet()) {
			outState.putString(key, mParameters.get(key));
		}
	}

	@Override public View onCreateView(LayoutInflater inflater,
	                                   @Nullable ViewGroup container,
	                                   @Nullable Bundle savedInstanceState) {
		return new View(getActivity());
	}

	public void setStateParameter(String key, String value) {
		mParameters.put(key, value);
	}

	public String getStateParameter(String key) {
		return mParameters.get(key);
	}
}
