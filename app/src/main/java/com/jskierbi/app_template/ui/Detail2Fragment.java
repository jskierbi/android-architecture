package com.jskierbi.app_template.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.jskierbi.app_template.base.BaseFragment;
import com.jskierbi.app_template.services.NavService;

import javax.inject.Inject;

/**
 * Created by jakub on 01/28/2015.
 */
public class Detail2Fragment extends BaseFragment {

	private static final String TAG = Detail2Fragment.class.getSimpleName();
	public static final int ID_BTN_BACK = 102;

	@Inject NavService mNavService;

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		Button back = new Button(getActivity());
		back.setText("BACK");
		back.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				try {
					mNavService.navigateBack();
				} catch (Exception ex) {
					Log.e(TAG, "Back click exception!", ex);
				}
			}
		});
		layout.addView(back);
		return layout;
	}
}
