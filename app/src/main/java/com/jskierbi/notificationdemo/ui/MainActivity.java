package com.jskierbi.notificationdemo.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.jskierbi.commons.dagger.ForApplication;
import com.jskierbi.notificationdemo.base.BaseActivity;
import com.jskierbi.notificationdemo.R;

import javax.inject.Inject;

/**
 * Created by jakub on 01/27/2015.
 */
public class MainActivity extends BaseActivity {

	@InjectView(R.id.content) FrameLayout mContent;

	@Inject @ForApplication Context mContext;
	@Inject FragmentManager mFragmentManager;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);

		if (mFragmentManager.findFragmentById(R.id.content) == null) {
			// TODO nav service!!
		}
	}
}
