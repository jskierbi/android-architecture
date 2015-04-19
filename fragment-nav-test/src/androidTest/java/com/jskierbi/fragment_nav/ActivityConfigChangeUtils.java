package com.jskierbi.fragment_nav;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.internal.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor;
import android.support.test.runner.lifecycle.Stage;
import android.util.Log;
import android.view.View;
import com.android.support.test.deps.guava.collect.Iterables;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

/**
 * An Espresso ViewAction that changes the mOrientation of the screen
 */
public class ActivityConfigChangeUtils {

	public enum Orientation {
		LANDSCAPE,
		PORTRAIT,
		CHANGE
	}

	public static Activity getCurrentActivity(Instrumentation instrumentation) {
		final List<Activity> activities = new ArrayList<>();
		instrumentation.runOnMainSync(new Runnable() {
			@Override public void run() {
				ActivityLifecycleMonitor monitor = ActivityLifecycleMonitorRegistry.getInstance();
				activities.addAll(monitor.getActivitiesInStage(Stage.RESUMED));
			}
		});

		return Iterables.getFirst(activities, null);
	}

	public static ViewAction orientationLandscape() {
		return new ChangeOrientationAction(Orientation.LANDSCAPE);
	}

	public static ViewAction orientationPortrait() {
		return new ChangeOrientationAction(Orientation.PORTRAIT);
	}

	public static ViewAction orientationChange() {
		return new ChangeOrientationAction(Orientation.CHANGE);
	}

	private static class ChangeOrientationAction implements ViewAction {

		private static final String TAG = ActivityConfigChangeUtils.class.getSimpleName();
		private final Orientation mOrientation;
		private ChangeOrientationAction(Orientation orientation) {
			this.mOrientation = orientation;
		}

		@Override
		public Matcher<View> getConstraints() {
			return isRoot();
		}

		@Override
		public String getDescription() {
			return "change mOrientation to " + mOrientation;
		}

		@Override
		public void perform(UiController uiController, View view) {

			// Settle UI
			uiController.loopMainThreadUntilIdle();
			final Activity activity = (Activity) view.getContext();

			// Figure out target orientation
			final int currentOrientation = activity.getResources().getConfiguration().orientation;
			int targetOrientation;
			switch (mOrientation) {
			case CHANGE:
				targetOrientation = currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
						ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
						ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				break;
			case LANDSCAPE:
				targetOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				break;
			case PORTRAIT:
			default:
				targetOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				break;
			}

			// Change orientation if different from requested
			if (currentOrientation != targetOrientation) {
				activity.setRequestedOrientation(targetOrientation);
				waitForActivityToRecreate(uiController, activity);
			} else {
				Log.w(TAG, "Activity is already in requested orientation configuration (" + targetOrientation + ")");
			}
		}

		private void waitForActivityToRecreate(UiController uiController, Activity activityBeforeChange) {

			// 1. Wait for activity to finish if not yet recreated with proper orientation
			ActivityLifecycleMonitor activityLifecycleMonitor = ActivityLifecycleMonitorRegistry.getInstance();
			final int MAX_WAIT_LOOPS = 100;
			int wait = 0;
			while (Stage.DESTROYED != activityLifecycleMonitor.getLifecycleStageOf(activityBeforeChange)) {
				uiController.loopMainThreadForAtLeast(10l);
				if (++wait > MAX_WAIT_LOOPS)
					throw new RuntimeException("Activity is not being destroyed. Did you forget to " +
							"finish the activity?");
			}

			// 2. Wait for new activity to resume (loop until idle)
			Collection<Activity> resumedActivities = activityLifecycleMonitor.getActivitiesInStage(Stage.RESUMED);
			if (resumedActivities.isEmpty()) {
				uiController.loopMainThreadUntilIdle();
				resumedActivities = activityLifecycleMonitor.getActivitiesInStage(Stage.RESUMED);
			}

			// 3. If activity is not yet resumed, wait actively up to 30 seconds
			if (resumedActivities.isEmpty()) {
				List<Activity> activities = new ArrayList<>();
				activities.addAll(activityLifecycleMonitor.getActivitiesInStage(Stage.PRE_ON_CREATE));
				activities.addAll(activityLifecycleMonitor.getActivitiesInStage(Stage.RESTARTED));
				if (activities.isEmpty()) {
					throw new RuntimeException("No activities found. Did you forget to launch the activity "
							+ "by calling getActivity() or startActivitySync or similar?");
				}
				// well at least there are some activities in the pipeline - lets see if they resume.

				long[] waitTimes =
						{10, 50, 100, 500, TimeUnit.SECONDS.toMillis(2), TimeUnit.SECONDS.toMillis(30)};

				for (int waitIdx = 0; waitIdx < waitTimes.length; ++waitIdx) {
					Log.w(TAG, "No activity currently resumed - waiting: " + waitTimes[waitIdx] + "ms for one to appear.");
					uiController.loopMainThreadForAtLeast(waitTimes[waitIdx]);
					resumedActivities = activityLifecycleMonitor.getActivitiesInStage(Stage.RESUMED);
					if (!resumedActivities.isEmpty()) {
						return; // one of the pending activities has resumed
					}
				}
				throw new NoActivityResumedException("No activities in stage RESUMED. Did you forget to "
						+ "launch the activity. (test.getActivity() or similar)?");
			}
		}
	}
}