package com.jskierbi.commons.navservice;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
* Created by jakub on 02/06/2015.
*/
public interface NavServiceHost {

	public Toolbar toolbar();
	public @IdRes int fragmentContainerId();
	public Fragment defaultFragment();
}
