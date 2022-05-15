
package com.ancevt.d2d2.display;


class DisplayObjectAbsoluteComputer {

	private DisplayObjectAbsoluteComputer(){}

	static float getAbsoluteX(DisplayObject displayObject) {
		float result = displayObject.getX();

		IDisplayObjectContainer parent = displayObject.getParent();

		while (parent != null && !(parent instanceof Stage)) {
			result *= parent.getScaleX();
			result += parent.getX();
			parent = parent.getParent();
		}

		return result;
	}

	static float getAbsoluteY(final DisplayObject displayObject) {
		float result = displayObject.getY();

		IDisplayObjectContainer parent = displayObject.getParent();

		while (parent != null && !(parent instanceof Stage)) {
			result *= parent.getScaleY();
			result += parent.getY();
			parent = parent.getParent();
		}

		return result;
	}

	static float getAbsoluteScaleX(final DisplayObject displayObject) {
		float result = displayObject.getScaleX();

		IDisplayObjectContainer parent = displayObject.getParent();

		while (parent != null && !(parent instanceof Stage)) {
			result *= parent.getScaleX();
			parent = parent.getParent();
		}

		return result;
	}

	static float getAbsoluteScaleY(final DisplayObject displayObject) {
		float result = displayObject.getScaleY();

		IDisplayObjectContainer parent = displayObject.getParent();

		while (parent != null && !(parent instanceof Stage)) {
			result *= parent.getScaleY();
			parent = parent.getParent();
		}

		return result;
	}

	static float getAbsoluteAlpha(final DisplayObject displayObject) {
		float result = displayObject.getAlpha();

		IDisplayObjectContainer parent = displayObject.getParent();

		while (parent != null && !(parent instanceof Stage)) {
			result *= parent.getAlpha();
			parent = parent.getParent();
		}

		return result;
	}

	static float getAbsoluteRotation(final DisplayObject displayObject) {
		float result = displayObject.getRotation();

		IDisplayObjectContainer parent = displayObject.getParent();

		while (parent != null && !(parent instanceof Stage)) {
			result += parent.getRotation();
			parent = parent.getParent();
		}

		return result;
	}
}
