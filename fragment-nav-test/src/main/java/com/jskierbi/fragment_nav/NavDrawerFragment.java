package com.jskierbi.fragment_nav;

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
		return inflater.inflate(R.layout.navigation_drawer_fragment, container, false);
	}
}
