package com.jskierbi.commons.navservice;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jakub on 02/09/2015.
 */
class ViewHierarchyHelper {

	/** Search view tree recursively and find DrawerLayout */
	static <T extends View> T findChildViewOfType(Class<T> clazz, View view) {

		if (clazz.isInstance(view)) {
			return (T) view;
		}

		if (view instanceof ViewGroup) {
			ViewGroup group = ((ViewGroup) view);
			for (int i = 0; i < group.getChildCount(); ++i) {
				T found = findChildViewOfType(clazz, group.getChildAt(i));
				if (found != null) {
					return found;
				}
			}
		}

		return null;
	}
}
