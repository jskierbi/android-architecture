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
public @interface NavigationHost {

	@IdRes int fragmentContainerId();
	Class defaultFragment();

	@IdRes int toolbarId() default 0;
	boolean doubleBackToExitEnabled() default false;
	@StringRes int doubleBackToExitText() default 0;

	@IdRes int primaryDrawerId() default 0;
	@IdRes int secondaryDrawerId() default 0;
}
