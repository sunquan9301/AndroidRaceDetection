package com.sun.raceDetection.analysis;

public class RaceNode {
	public int lineCount;
	public int threadId;
	public RaceNode(int lineCount, int threadId) {
		super();
		this.lineCount = lineCount;
		this.threadId = threadId;
	}
	
}
