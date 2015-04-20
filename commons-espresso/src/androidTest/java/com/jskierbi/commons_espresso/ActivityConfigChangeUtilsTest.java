package com.jskierbi.commons_espresso;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import com.jskierbi.commons_espresso.ui.StateSavingActivity;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static com.jskierbi.commons_espresso.ActivityConfigChangeUtils.getCurrentActivity;
import static com.jskierbi.commons_espresso.ActivityConfigChangeUtils.orientationChange;

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

	public void testMultipleOrientationChange() {

		final String KEY1 = "KEY1";
		final String VALUE1 = UUID.randomUUID().toString();

		final StateSavingActivity firstIncarnation = getActivity();
		firstIncarnation.setStateParameter(KEY1, VALUE1);

		// First orientation change
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		final StateSavingActivity secondIncarnation = getActivity();
		assertNotSame("Activity is recreated", firstIncarnation, secondIncarnation);
		assertEquals("Activity state is restored", VALUE1, secondIncarnation.getStateParameter(KEY1));

		// Second orientation change
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		final StateSavingActivity thirdIncarnation = getActivity();
		assertNotSame("Activity is recreated", secondIncarnation, thirdIncarnation);
		assertEquals("Activity state is restored", VALUE1, thirdIncarnation.getStateParameter(KEY1));

		// Third orientation change
		onView(isRoot()).perform(orientationChange());
		setActivity(getCurrentActivity(getInstrumentation()));

		final StateSavingActivity fourthIncarnation = getActivity();
		assertNotSame("Activity is recreated", thirdIncarnation, fourthIncarnation);
		assertEquals("Activity state is restored", VALUE1, fourthIncarnation.getStateParameter(KEY1));
	}
}
