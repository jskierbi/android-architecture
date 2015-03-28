package com.jskierbi.commons.navservice;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
* Created by jakub on 02/06/2015.
*/
public interface NavServiceHost {

	/** Returns default toolbar from hosting activity */
	public Toolbar toolbar();

	/** Id of default container, that holds fragments */
	public @IdRes int fragmentContainerId();

	/** Initial fragment to be shown by nav service */
	public Fragment defaultFragment();
}
