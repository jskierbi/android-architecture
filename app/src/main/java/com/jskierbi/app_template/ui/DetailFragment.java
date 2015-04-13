package com.jskierbi.app_template.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.jskierbi.app_template.R;
import com.jskierbi.commons.navigation.AnimatedSupportFragment;
import com.jskierbi.commons.navigation.FragmentNavigationController;

import javax.inject.Inject;

/**
 * Created by jakub on 01/28/2015.
 */
public class DetailFragment extends AnimatedSupportFragment {

	private static final String TAG = DetailFragment.class.getSimpleName();
	private static final String ARG_NO = "ARG_NO";

	@Inject FragmentNavigationController mFragmentNavigationController;

	public DetailFragment() {
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_NO, 0);
		setArguments(bundle);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);

		{
			Button btnToDetailsAddBackstack = new Button(getActivity());
			btnToDetailsAddBackstack.setText("To details +BACKSTACK");
			btnToDetailsAddBackstack.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					try {
						AnimatedSupportFragment fragment = new DetailFragment();
						fragment.setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right);
						Bundle bundle = new Bundle();
						bundle.putInt(ARG_NO, getArguments().getInt(ARG_NO, 0) + 1);
						fragment.setArguments(bundle);
						mFragmentNavigationController.navigateTo(fragment, true);
					} catch (Exception ex) {
						Log.e(TAG, "Nav button exception", ex);
					}
				}
			});
			layout.addView(btnToDetailsAddBackstack);
		}

		{
			Button btnToDetailsNoBackstack = new Button(getActivity());
			btnToDetailsNoBackstack.setText("To details no backstack");
			btnToDetailsNoBackstack.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					try {
						AnimatedSupportFragment fragment = new DetailFragment();
						fragment.setCustomAnimations(R.anim.from_bottom, R.anim.to_top, R.anim.from_top, R.anim.to_bottom);
						Bundle bundle = new Bundle();
						bundle.putInt(ARG_NO, getArguments().getInt(ARG_NO, 0) + 1);
						fragment.setArguments(bundle);
						mFragmentNavigationController.navigateTo(fragment, false);
					} catch (Exception ex) {
						Log.e(TAG, "Nav button exception", ex);
					}
				}
			});
			layout.addView(btnToDetailsNoBackstack);
		}

		{
			Button btnClearBackstack = new Button(getActivity());
			btnClearBackstack.setText("Clear backstack");
			btnClearBackstack.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					try {
						mFragmentNavigationController.clearBackstack();
					} catch (Exception ex) {

					}
				}
			});
			layout.addView(btnClearBackstack);
		}

		{
			Button btnBack = new Button(getActivity());
			btnBack.setText("Back");
			btnBack.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					try {
						mFragmentNavigationController.navigateBack();
					} catch (Exception ex) {
						Log.e(TAG, "Back button exception", ex);
					}
				}
			});
			layout.addView(btnBack);
		}

		return layout;
	}

	@Override public void onResume() {
		Log.d(TAG, "onResume(): " + getArguments().getInt(ARG_NO, 0));
		super.onResume();
	}
}
