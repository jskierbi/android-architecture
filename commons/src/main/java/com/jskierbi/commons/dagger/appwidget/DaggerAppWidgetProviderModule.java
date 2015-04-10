package com.jskierbi.commons.dagger.appwidget;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import com.jskierbi.commons.dagger.ForAppWidget;
import com.jskierbi.commons.dagger.Injector;
import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class DaggerAppWidgetProviderModule {

	private Context mContext;
	private AppWidgetProvider mAppWidgetProvider;
	private Injector mInjector;

	public DaggerAppWidgetProviderModule(Context mContext, AppWidgetProvider mAppWidgetProvider, Injector mInjector) {
		this.mContext = mContext;
		this.mAppWidgetProvider = mAppWidgetProvider;
		this.mInjector = mInjector;
	}

	@Provides @ForAppWidget Context provideAppWidgetContext() {
		return mContext;
	}

	@Provides @ForAppWidget Injector provideAppWidgetInjector() {
		return mInjector;
	}

	@Provides @ForAppWidget AppWidgetProvider provideAppWidget() {
		return mAppWidgetProvider;
	}
}
