package com.jskierbi.commons.robospice;

import android.app.Application;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;

/**
 * Created by jakub on 03/05/2015.
 */
public class SmileSpiceService extends SpiceService {

	@Override public CacheManager createCacheManager(Application application) throws CacheCreationException {
		return null;
	}
}
