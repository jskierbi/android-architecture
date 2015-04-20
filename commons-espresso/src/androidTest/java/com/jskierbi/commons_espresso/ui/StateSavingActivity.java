package com.jskierbi.commons_espresso.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class StateSavingActivity extends Activity {

	private Map<String, String> mParameters = new HashMap<>();

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new View(this));
		if (savedInstanceState != null) {
			for (String key : savedInstanceState.keySet()) {
				mParameters.put(key, savedInstanceState.getString(key));
			}
		}
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (String key : mParameters.keySet()) {
			outState.putString(key, mParameters.get(key));
		}
	}

	public void setStateParameter(String key, String value) {
		mParameters.put(key, value);
	}

	public String getStateParameter(String key) {
		return mParameters.get(key);
	}
}
