package com.jskierbi.app_template.modules;

import android.support.v4.app.FragmentManager;
import com.jskierbi.app_template.base.BaseAppActivity;
import com.jskierbi.app_template.ui.ActivityWithDrawerToolbar;
import com.jskierbi.app_template.ui.DetailFragment;
import com.jskierbi.app_template.ui.DrawerFragment;
import com.jskierbi.app_template.ui.MainFragment;
import com.jskierbi.commons.navigation.FragmentNavigationController;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by jakub on 01/27/2015.
 */
@Module(
		injects = {
				ActivityWithDrawerToolbar.class,

				MainFragment.class,
				DetailFragment.class,
				DrawerFragment.class
		},
		addsTo = AppModule.class,
		library = true // can be injected to fragments!
)
public class ActivityModule {

	private BaseAppActivity mActivity;

	public ActivityModule(BaseAppActivity activity) {
		mActivity = activity;
	}

	@Provides @Singleton FragmentManager provideFragmentManager() {
		return mActivity.getSupportFragmentManager();
	}

	@Provides @Singleton FragmentNavigationController provideNavService() {
		return new FragmentNavigationController(mActivity);
	}
}
