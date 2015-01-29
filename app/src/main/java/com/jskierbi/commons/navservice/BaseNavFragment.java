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
 * @see http://stackoverflow.com/questions/8837408/fragment-lost-transition-animation-after-configuration-change
 * @see https://code.google.com/p/android/issues/detail?id=25994&can=4&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars
 */
public abstract class BaseNavFragment extends DaggerFragment {

	private static final String STATE_ENTER_ANIM = "STATE_ENTER_ANIM";
	private static final String STATE_EXIT_ANIM = "STATE_EXIT_ANIM";
	private static final String STATE_POP_ENTER_ANIM = "STATE_POP_ENTER_ANIM";
	private static final String STATE_POP_EXIT_ANIM = "STATE_POP_EXIT_ANIM";

	private @AnimRes int mEnter = 0;
	private @AnimRes int mExit = 0;
	private @AnimRes int mPopEnter = 0;
	private @AnimRes int mPopExit = 0;


	public void setCustomAnimations(@AnimRes int enterAnim,
	                                @AnimRes int exitAnim,
	                                @AnimRes int popEnterAnim,
	                                @AnimRes int popExitAnim) {
		mEnter = enterAnim;
		mExit = exitAnim;
		mPopEnter = popEnterAnim;
		mPopExit = popExitAnim;
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
		}
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_ENTER_ANIM, mEnter);
		outState.putInt(STATE_EXIT_ANIM, mExit);
		outState.putInt(STATE_POP_ENTER_ANIM, mPopEnter);
		outState.putInt(STATE_POP_EXIT_ANIM, mPopExit);
	}

	@SuppressLint("NewApi")
	@Override public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

		// Do not support animations PRE-4.0
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return null;
		}

		try {
			int anim;
			if (getActivity().isChangingConfigurations()) {
				anim = 0;
			} else if (nextAnim != 0) {
				anim = nextAnim;
			} else {
				anim = enter ? mPopEnter : mPopExit;
			}
			if (anim != 0) {
				return AnimationUtils.loadAnimation(getActivity(), anim);
			}
		} catch (Exception ignore) {}

		return null;
	}
}
