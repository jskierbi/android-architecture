package com.jskierbi.commons.navigation;

import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jakub on 04/10/2015.
 *
 * Use on Activity class to enable FragmentNavigationController integration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FragmentNavigation {

	/**
	 * Default fragment container, will be used to handle fragment navigation
	 */
	@IdRes int fragmentContainerId();

	/**
	 * Fragment of this class will be constructed as default and added to fragment container
	 */
	Class defaultFragmentClass();

	/**
	 * Toolbar id to integrate with, or 0 if toolbar not available
	 */
	@IdRes int toolbarId() default 0;

	/**
	 * If set, enables double back to exit pattern, showing passed text in toast
	 */
	@StringRes int doubleBackToExitWithText() default 0;

	/**
	 * If using DrawerLayout for navigation drawer pattern, pass its id here
	 * to enable FragmentNavigationController integrate it with ActionBarDrawerToggle
	 */
	@IdRes int drawerLayoutId() default 0;

	/**
	 * Navigation drawer options, tells controller when to:
	 * - enable or disable drawer opening
	 * - show or hide drawer toggle on toolbar/actionbar
	 */
	@NavOption int drawerOptions() default ENABLE_ALWAYS_TOGGLE_ON_ROOT;

	/**
	 * Navigation drawer is always enabled, drawer toggle is enabled only when
	 * showing root fragment (fragment backstack count equals 0)
	 * */
	public static final int ENABLE_ALWAYS_TOGGLE_ON_ROOT = 0;

	/**
	 * Navigation drawer and drawer toggle are always enabled (not only for navigation
	 * root)
	 */
	public static final int ENABLE_ALWYAS_TOGGLE_ALWAYS = 1;

	/**
	 * Navigation drawer can be opened only on navigation root (fragment
	 * backstack count equals 0)
	 */
	public static final int ENABLE_ON_ROOT_TOGGLE_ON_ROOT = 2;

	@IntDef({ENABLE_ALWYAS_TOGGLE_ALWAYS,
			ENABLE_ALWAYS_TOGGLE_ON_ROOT,
			ENABLE_ON_ROOT_TOGGLE_ON_ROOT})
	@Retention(RetentionPolicy.SOURCE)
	public @interface NavOption {}

}
