package com.jskierbi.commons.navigation;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jakub on 04/10/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FragmentNavigation {

	@IdRes int fragmentContainerId();
	Class defaultFragmentClass();

	@IdRes int toolbarId() default 0;
	@StringRes int doubleBackToExitWithText() default 0;

	@IdRes int drawerLayoutId() default 0;
}
