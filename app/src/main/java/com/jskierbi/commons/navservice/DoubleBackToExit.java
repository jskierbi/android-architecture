package com.jskierbi.commons.navservice;

import android.os.Handler;
import android.os.Message;

/**
 * Created by jakub on 01/29/2015.
 */
class DoubleBackToExit extends Handler {

	private static final long EXIT_DELAY_MS = 2000l;
	private static final int MSG_ELAPSED = 240032;

	private boolean mFlgExitApp = false;

	boolean isExitOnBack() {
		if (mFlgExitApp) {
			return true;
		} else {
			mFlgExitApp = true;
			removeMessages(MSG_ELAPSED);
			sendEmptyMessageDelayed(MSG_ELAPSED, EXIT_DELAY_MS);
			return false;
		}
	}

	@Override public void handleMessage(Message msg) {
		mFlgExitApp = false;
	}
}
