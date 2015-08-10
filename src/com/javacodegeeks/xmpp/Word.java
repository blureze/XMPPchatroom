package com.javacodegeeks.xmpp;

import java.io.Serializable;

public class Word implements Serializable{
	private static final long serialVersionUID = 1L;
	private int start, end;

	public Word(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
}
