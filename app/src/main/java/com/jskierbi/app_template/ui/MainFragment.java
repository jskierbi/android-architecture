package com.jskierbi.app_template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jskierbi.app_template.R;
import com.jskierbi.app_template.base.BaseAppFragment;
import com.jskierbi.commons.navservice.NavService;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class MainFragment extends BaseAppFragment {

	@Inject NavService mNavService;

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.inject(this, v);
		return v;
	}

	@OnClick(R.id.btn_nav_details) void navDetailsClick() {
		BaseAppFragment fragment = new DetailFragment();
		fragment.setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right);
		mNavService.navigateTo(fragment);
	}
}
