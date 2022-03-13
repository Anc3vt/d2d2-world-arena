/*
 *   D2D2 World
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
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.gameobject.IDirectioned;
import com.ancevt.d2d2world.gameobject.IGameObject;

public class Camera {
	
	public static final int DEFAULT_VIEWPORT_WIDTH = 420;
	public static final int DEFAULT_VIEWPORT_HEIGHT = 240;
	public static float DEFAULT_ZOOM = 1.0f; 
	
	private static final float MIN_ZOOM = 0.2f;
	private static final float MAX_ZOOM = 3.0f;
	
	private final World world;
	private boolean boundsLock;

	private float viewportWidth;
	private float viewportHeight;
	
	private int boundWidth;
	private int boundHeight;
	private IGameObject attachedTo;
	private int direction;
	private boolean autoZoom;

	public Camera(World world) {
		this.world = world;
		setViewportSize(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT);
	}

	public void setViewportSize(float viewportWidth, float viewportHeight) {
		setViewportWidth(viewportWidth);
		setViewportHeight(viewportHeight);
	}

	public void setViewportWidth(float viewportWidth) {
		this.viewportWidth = viewportWidth;
	}

	public void setViewportHeight(float viewportHeight) {
		this.viewportHeight = viewportHeight;
	}

	public float getViewportWidth() {
		return viewportWidth;
	}

	public float getViewportHeight() {
		return viewportHeight;
	}
	
	public float getZoom() {
		return cameraLayer().getScaleX();
	}
	
	public void zoom(float delta) {
		setZoom(getZoom() * delta);
	}

	public void setZoom(float zoom) {
		if(zoom < 1.1f) zoom = 1.0f;
		
		cameraLayer().setScale(zoom, zoom);
		if(cameraLayer().getScaleX() < MIN_ZOOM)
			cameraLayer().setScale(MIN_ZOOM, MIN_ZOOM);
		else if(cameraLayer().getScaleX() > MAX_ZOOM)
			cameraLayer().setScale(MAX_ZOOM, MAX_ZOOM);
		
		if(isBoundsLock()) fixBounds();
	}

	public float getX() {
		return -world.getX();
	}
	
	public float getY() {
		return -world.getY();
	}
	
	public final void setXY(float x, float y) {
		world.setXY(-x, -y);
	}
	
	public final void setX(float x) {
		world.setX(-x);
	}
	
	public final void setY(float y) {
		world.setY(-y);
	}
	
	public final void move(float toX, float toY) {
		world.move(-toX / getZoom(), -toY / getZoom());
		if(isBoundsLock()) fixBounds();
	}
	
	public final void moveX(float value) {
		world.moveX(-value / getZoom());
		if(isBoundsLock()) fixBounds();
	}
	
	public final void moveY(float value) {
		world.moveY(-value / getZoom());
		if(isBoundsLock()) fixBounds();
	}
	
	public final DisplayObjectContainer cameraLayer() {
		return world.getParent();
	}

	public boolean isBoundsLock() {
		return boundsLock;
	}

	public void setBoundsLock(boolean boundsLock) {
		this.boundsLock = boundsLock;
		if(boundsLock) fixBounds();
	}
	
	private void fixBounds() {
		if (boundWidth == 0 || boundHeight == 0) return;

		float halfViewportWidth = viewportWidth / 2;
		float halfViewportHeight = viewportHeight / 2;

		float z = getZoom();
		
		float minLimitX = halfViewportWidth / z;
		float minLimitY = halfViewportHeight / z;
		float maxLimitX = (boundWidth - halfViewportWidth / z);
		float maxLimitY = (boundHeight - halfViewportHeight / z); 
		
		try {
			if(getX() < minLimitX) setX(minLimitX); else
			if(getX() > maxLimitX) setX(maxLimitX);
	
			if(getY() < minLimitY) setY(minLimitY); else
			if(getY() > maxLimitY) setY(maxLimitY);
		} catch(StackOverflowError e) {
			setZoom(getZoom() + 0.05f);
		}
	}

	public int getBoundHeight() {
		return boundHeight;
	}

	public void setBoundHeight(int boundHeight) {
		this.boundHeight = boundHeight;
	}

	public int getBoundWidth() {
		return boundWidth;
	}

	public void setBoundWidth(int boundWidth) {
		this.boundWidth = boundWidth;
	}
	
	public void setBounds(int w, int h) {
		setBoundWidth(w);
		setBoundHeight(h);
		if(isBoundsLock()) fixBounds();
	}

	public IGameObject getAttachedTo() {
		return attachedTo;
	}

	public void setAttachedTo(IGameObject attachedTo) {
		this.attachedTo = attachedTo;
	}
	
	public final void process() {
		processAttached();
		processAutoZoom();
	}
	
	private void processAutoZoom() {
		if(!autoZoom) return;
		
		float speed = 0.005f;
		
		float zoom = getZoom();
		
		if(Math.abs(zoom - 1.0f) < 0.005f) {
			setZoom(DEFAULT_ZOOM);
			return;
		}
		
		if(zoom > 1.0f) setZoom(zoom - speed); else
		if(zoom < 1.0f) setZoom(zoom + speed);
	}
	
	private void processAttached() {
		if(attachedTo == null) return;

		if(attachedTo instanceof IDirectioned d) {
			setDirection(d.getDirection());
		}
		
		boolean left = getDirection() == Direction.LEFT;
		
		float smooth = 25f;
		float side = 80.0f;
		
		float aX = attachedTo.getX();
		float aY = attachedTo.getY();
		float x = getX() + (left ? side : -side);
		float y = getY();
		
		if(aX > x) {
			float t = (aX - x) / smooth;
			moveX(t);
		} else 
		if(aX < x) {
			float t = (x - aX) / smooth;
			moveX(-t);
		}
		if(aY > y) {
			float t = (aY - y) / (smooth/2);
			moveY(t);
		} else
		if(aY < y) {
			float t = (y - aY) / (smooth/2);
			moveY(-t);
		}
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public boolean isAutoZoom() {
		return autoZoom;
	}

	public void setAutoZoom(boolean autoZoom) {
		this.autoZoom = autoZoom;
	}
}


























