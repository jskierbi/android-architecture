package com.jskierbi.commons.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import com.jskierbi.fragment_nav.StateSavingActivity;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static com.jskierbi.commons.test.ActivityConfigChangeUtils.getCurrentActivity;
import static com.jskierbi.commons.test.ActivityConfigChangeUtils.orientationChange;

/**
 * Created by jakub on 04/19/2015.
 */
public class ActivityConfigChangeUtilsTest extends ActivityInstrumentationTestCase2<StateSavingActivity> {

	public ActivityConfigChangeUtilsTest() {
		super(StateSavingActivity.class);
	}

	@Override protected void setUp() throws Exception {
		super.setUp();
		getActivity();
	}

	public void testOrientationChange() {

		final Activity beforeOrientationChange = getActivity();
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));
		assertNotSame("Activity before orientation change and after orientation change are the same",
				beforeOrientationChange,
				getActivity());
	}

	public void testOrientationChangeSaveState() {
		final StateSavingActivity beforeOrientationChange = getActivity();
		final String key = "SAVEABLE_PARAMETER_KEY";
		final String value = UUID.randomUUID().toString();
		beforeOrientationChange.setStateParameter(key, value);

		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		assertNotSame("Activities are different after recreation", beforeOrientationChange, getActivity());
		assertEquals("Parameters are equal after activity recreation", value, getActivity().getStateParameter(key));
	}
}
