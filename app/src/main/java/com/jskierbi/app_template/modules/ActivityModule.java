package com.jskierbi.app_template.modules;

import android.support.v4.app.FragmentManager;
import com.jskierbi.app_template.base.BaseActivity;
import com.jskierbi.app_template.ui.DetailFragment;
import com.jskierbi.app_template.ui.MainActivity;
import com.jskierbi.app_template.ui.MainFragment;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by jakub on 01/27/2015.
 */
@Module(
		injects = {
				MainActivity.class,

				MainFragment.class,
				DetailFragment.class
		},
		addsTo = AppModule.class,
		library = true // can be injected to fragments!
)
public class ActivityModule {

	private BaseActivity mActivity;

	public ActivityModule(BaseActivity activity) {
		mActivity = activity;
	}

	@Provides @Singleton FragmentManager provideFragmentManager() {
		return mActivity.getSupportFragmentManager();
	}

	@Provides @Singleton BaseActivity provideActivity() {
		return mActivity;
	}
}
