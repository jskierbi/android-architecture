package com.jskierbi.commons.navservice;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.jskierbi.commons.dagger.DaggerFragment;

/**
 * Contains code to fix loosing fragment transaction animation after screen rotation
 * To be used with {@link com.jskierbi.commons.navservice.BaseNavService}
 */
public abstract class BaseNavFragment extends DaggerFragment {

	private static final String STATE_ENTER_ANIM = "STATE_ENTER_ANIM";
	private static final String STATE_EXIT_ANIM = "STATE_EXIT_ANIM";
	private static final String STATE_POP_ENTER_ANIM = "STATE_POP_ENTER_ANIM";
	private static final String STATE_POP_EXIT_ANIM = "STATE_POP_EXIT_ANIM";
	private static final String STATE_CHANGING_CONFIGURATIONS = "STATE_CHANGING_CONFIGURATIONS";

	private @AnimRes int mEnter = 0;
	private @AnimRes int mExit = 0;
	private @AnimRes int mPopEnter = 0;
	private @AnimRes int mPopExit = 0;
	private boolean mIsChangingConfigurations = false;

	public void setCustomAnimations(@AnimRes int enterAnim,
	                                @AnimRes int exitAnim,
	                                @AnimRes int popEnterAnim,
	                                @AnimRes int popExitAnim) {
		mEnter = enterAnim;
		mExit = exitAnim;
		mPopEnter = popEnterAnim;
		mPopExit = popExitAnim;
	}

	public void setEnterAnim(@AnimRes int animRes) {
		mEnter = animRes;
	}
	public void setExitAnim(@AnimRes int animRes) {
		mExit = animRes;
	}
	public void setPopEnterAnim(@AnimRes int animRes) {
		mPopEnter = animRes;
	}
	public void setPopExitAnim(@AnimRes int animRes) {
		mPopExit = animRes;
	}

	public @AnimRes int getEnterAnim() {
		return mEnter;
	}
	public @AnimRes int getExitAnim() {
		return mExit;
	}
	public @AnimRes int getPopEnterAnim() {
		return mPopEnter;
	}
	public @AnimRes int getPopExitAnim() {
		return mPopExit;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mEnter = savedInstanceState.getInt(STATE_ENTER_ANIM);
			mExit = savedInstanceState.getInt(STATE_EXIT_ANIM);
			mPopEnter = savedInstanceState.getInt(STATE_POP_ENTER_ANIM);
			mPopExit = savedInstanceState.getInt(STATE_POP_EXIT_ANIM);
			mIsChangingConfigurations = savedInstanceState.getBoolean(STATE_CHANGING_CONFIGURATIONS);
		}
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_ENTER_ANIM, mEnter);
		outState.putInt(STATE_EXIT_ANIM, mExit);
		outState.putInt(STATE_POP_ENTER_ANIM, mPopEnter);
		outState.putInt(STATE_POP_EXIT_ANIM, mPopExit);
		outState.putBoolean(STATE_CHANGING_CONFIGURATIONS, mIsChangingConfigurations);
	}

	@Override public void onResume() {
		super.onResume();
		mIsChangingConfigurations = false;
	}

	@Override public void onPause() {
		super.onPause();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mIsChangingConfigurations = getActivity().isChangingConfigurations();
		}
	}

	@SuppressLint("NewApi")
	@Override public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

		// This implementation fixes loosing transition animations on orientation changes:
		// @see http://stackoverflow.com/questions/8837408/fragment-lost-transition-animation-after-configuration-change
		// @see https://code.google.com/p/android/issues/detail?id=25994&can=4&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars

		// Do not support animations PRE-3.0, isChangingConfigurations() is not available!
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return null;
		}

		try {
			int anim;
			if (mIsChangingConfigurations) {
				// Do not play animation if changing configurations
				anim = 0;
			} else if (nextAnim != 0) {
				// If animation from transaction was not lost -> load it
				anim = nextAnim;
			} else {
				// This is case where animation from transaction could be lost - load
				// animation saved in fragment. If enter -> we're popEntering, if exit -> we're popExiting
				anim = enter ? mPopEnter : mPopExit;
			}
			if (anim != 0) {
				return AnimationUtils.loadAnimation(getActivity(), anim);
			}
		} catch (Exception ignore) {}

		return null;
	}
}
