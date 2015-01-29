package com.jskierbi.app_template.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.jskierbi.app_template.R;
import com.jskierbi.app_template.base.BaseFragment;
import com.jskierbi.app_template.services.NavService;

import javax.inject.Inject;

/**
 * Created by jakub on 01/28/2015.
 */
public class DetailFragment extends BaseFragment {

	private static final String TAG = DetailFragment.class.getSimpleName();
	@Inject NavService mNavService;

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		Button button = new Button(getActivity());
		button.setText("To details 2");
		button.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				try {
					BaseFragment fragment = new Detail2Fragment();
					fragment.setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right);
					mNavService.navigateTo(fragment, false);
				} catch (Exception ex) {
					Log.e(TAG, "Nav button exception", ex);
				}
			}
		});
		layout.addView(button);

		Button button2 = new Button(getActivity());
		button2.setText("Back");
		button2.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				try {
					mNavService.navigateBack();
				} catch (Exception ex) {
					Log.e(TAG, "Back button exception", ex);
				}
			}
		});
		layout.addView(button2);

		return layout;
	}
}
