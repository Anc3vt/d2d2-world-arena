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
package ru.ancevt.d2d2.debug;

import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObject;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.IColored;
import ru.ancevt.d2d2.display.Resizable;
import ru.ancevt.d2d2.event.Event;

public class DebugBorder extends DisplayObjectContainer implements Resizable, IColored {
	
	private final PlainRect l;
	private final PlainRect t;
	private final PlainRect r;
	private final PlainRect b;
	
	private float width;
	private float height;
	
	private static final Color COLOR_BLACK = Color.BLACK;
	private static final Color COLOR_WHITE = Color.WHITE;
	
	private byte timer;
	
	private DisplayObject assignTarget;
	
	public DebugBorder() {
		this(10, 10);
	}
	
	public DebugBorder(DisplayObject assignTo) {
		this(assignTo.getWidth(), assignTo.getHeight());
		assign(assignTo);
	}
	
	public DebugBorder(float width, float height) {
		l = new PlainRect();
		t = new PlainRect();
		r = new PlainRect();
		b = new PlainRect();
		
		add(l);
		add(t);
		add(r);
		add(b);
		
		setSize(width, height);

		addEventListener(Event.EACH_FRAME, this::eachFrame);
	}

	private void rebuild() {
		t.setScaleX(width);
		l.setScaleY(height);
		
		b.setScaleX(width);
		r.setScaleY(height);
		
		r.setX(width);
		b.setY(height);
	}
	
	@Override
	public void setColor(Color color) {
		l.setColor(color);
		t.setColor(color);
		r.setColor(color);
		b.setColor(color);
	}

	@Override
	public void setColor(int rgb) {
		l.setColor(rgb);
		t.setColor(rgb);
		r.setColor(rgb);
		b.setColor(rgb);
	}

	@Override
	public Color getColor() {
		return l.getColor();
	}

	@Override
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		rebuild();
	}

	@Override
	public void setWidth(float value) {
		this.width = value;
		rebuild();
	}

	@Override
	public void setHeight(float value) {
		this.height = value;
		rebuild();
	}
	
	@Override
	public final float getWidth() {
		return width;
	}
	
	@Override
	public float getHeight() {
		return height;
	}

	public final void assign(final DisplayObject assignTo) {
		assignTarget = assignTo;
	}
	
	public void eachFrame(Event event) {
		
		if(assignTarget != null) {
			setSize(
				assignTarget.getWidth() * assignTarget.getAbsoluteScaleX(), 
				assignTarget.getHeight() * assignTarget.getAbsoluteScaleY()
			);
			
			setXY(assignTarget.getX(), assignTarget.getY());
			
			if(assignTarget.hasParent() && getParent() != assignTarget.getParent()) {
				if(hasParent()) getParent().remove(this);
				assignTarget.getParent().add(this);
			}
			
			timer ++;
			final Color color = timer % 20 < 10 ? COLOR_BLACK : COLOR_WHITE;
			if(timer >= 20) timer = 0;
			
			l.setColor(color);
			r.setColor(color);
			t.setColor(color);
			b.setColor(color);
		}
	}
	
	
}
