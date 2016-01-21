package com.sun.raceDetection.analysis;

public class AssignRightInfoNode {
	public int OrderCount;
	public int type;
	public String first;
	public String operator;
	public String second;
	public AssignRightInfoNode(int orderCount, int type, String first,
			String operator, String second) {
		super();
		OrderCount = orderCount;
		this.type = type;
		this.first = first;
		this.operator = operator;
		this.second = second;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return OrderCount+","+first;
	}
}
