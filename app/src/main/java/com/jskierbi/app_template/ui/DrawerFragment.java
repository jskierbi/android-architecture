package com.jskierbi.app_template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jskierbi.commons.navservice.BaseDrawerFragment;

import java.util.Arrays;
import java.util.List;

public class DrawerFragment extends BaseDrawerFragment {

	@Override protected List<Object> listModules() {
		return Arrays.asList();
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		textView.setText("Nav Drawer Impl");
		return textView;
	}
}
