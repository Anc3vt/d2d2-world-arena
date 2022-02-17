/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ru.ancevt.d2d2.display;


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
