package com.jskierbi.commons.otto;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;

/**
 * Created by jakub on 01/27/2015.
 */
public class EventBus {

	private static Bus busInstance;
	private static Bus busInstanceMainThread;

	public static Bus getDefault() {
		if (busInstance == null) {
			busInstance = new Bus();
		}
		return busInstance;
	}

	public static Bus ui() {
		if (busInstanceMainThread == null) {
			busInstanceMainThread = new BusMainThread(getDefault());
		}
		return busInstanceMainThread;
	}

	public static void register(Object obj) {
		getDefault().register(obj);
	}
	public static void unregister(Object obj) {
		getDefault().unregister(obj);
	}

	/** Bus for posting directly on main thread */
	private static class BusMainThread extends Bus {

		private final Bus mBus;
		private final Handler mHandler = new Handler(Looper.getMainLooper());
		public BusMainThread(final Bus bus) {
			if (bus == null) {throw new NullPointerException("bus must not be null");}
			mBus = bus;
		}

		@Override public void register(Object obj) { mBus.register(obj);}
		@Override public void unregister(Object obj) { mBus.unregister(obj);}
		@Override public void post(final Object event) {
			if (Looper.myLooper() == Looper.getMainLooper()) {
				mBus.post(event);
			} else {
				mHandler.post(new Runnable() {
					@Override public void run() { mBus.post(event);}
				});
			}
		}
	}
}
