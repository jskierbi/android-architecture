package com.jskierbi.app_template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jskierbi.app_template.R;
import com.jskierbi.commons.navigation.AnimatedSupportFragment;
import com.jskierbi.commons.navigation.FragmentNavigationController;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class MainFragment extends AnimatedSupportFragment {

	@Inject FragmentNavigationController mFragmentNavigationController;

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.inject(this, v);
		return v;
	}

	@OnClick(R.id.btn_nav_details) void navDetailsClick() {
		AnimatedSupportFragment fragment = new DetailFragment();
		fragment.setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right);
		mFragmentNavigationController.navigateTo(fragment);
	}

	@OnClick(R.id.btn_nav_details_no_backstack) void navDetailsNoBackstackClick() {
		AnimatedSupportFragment fragment = new DetailFragment();
		fragment.setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right);
		mFragmentNavigationController.navigateTo(fragment, FragmentNavigationController.Backstack.NO);
	}
}
