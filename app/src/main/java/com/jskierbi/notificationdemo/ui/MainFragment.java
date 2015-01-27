package com.jskierbi.notificationdemo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.jskierbi.notificationdemo.R;
import com.jskierbi.notificationdemo.base.BaseFragment;

/**
 * Created by jakub on 01/27/2015.
 */
public class MainFragment extends BaseFragment {

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.inject(this, v);
		return v;
	}
}
