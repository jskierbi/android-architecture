package com.jskierbi.commons_espresso.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavDrawerFragment extends Fragment {

	@Override public View onCreateView(LayoutInflater inflater,
	                                   @Nullable ViewGroup container,
	                                   @Nullable Bundle savedInstanceState) {
		return new View(getActivity());
	}
}
