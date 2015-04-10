package com.jskierbi.commons.dagger.appwidget;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import com.jskierbi.commons.dagger.Injector;
import dagger.ObjectGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakub on 04/08/2015.
 */
public class DaggerAppWidgetProvider extends AppWidgetProvider implements Injector {

	private Context mContext;
	private ObjectGraph mObjectGraph;

	@Override public void onReceive(Context context, Intent intent) {
		mContext = context;
		mObjectGraph = ((Injector) mContext.getApplicationContext()).getObjectGraph().plus(listModules().toArray());
		mObjectGraph.inject(this);
		super.onReceive(context, intent);
	}

	protected List<Object> listModules() {
		List<Object> modules = new ArrayList<>();
		modules.add(new DaggerAppWidgetProviderModule(mContext, this, this));
		return modules;
	}

	@Override public void inject(Object obj) {
		mObjectGraph.inject(obj);
	}

	@Override public ObjectGraph getObjectGraph() {
		return mObjectGraph;
	}
}
