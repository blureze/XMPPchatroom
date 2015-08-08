package com.javacodegeeks.xmpp;

import java.io.Serializable;

public class MouseCoordinate implements Serializable{
	private static final long serialVersionUID = 1L;
	private int x,y;

	public MouseCoordinate(int MouseX, int MouseY) {
		this.x = MouseX;
		this.y = MouseY;
	}
}
