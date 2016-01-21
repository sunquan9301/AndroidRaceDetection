package com.sun.raceDetection.analysis;

public class InvokeRightInfoNode {
	public String sourceValue;
	public int orderCount;
	public String targetValue;
	public InvokeRightInfoNode(String sourceValue, int orderCount,
			String targetValue) {
		super();
		this.sourceValue = sourceValue;
		this.orderCount = orderCount;
		this.targetValue = targetValue;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return orderCount+","+sourceValue+","+targetValue;
	}
}
