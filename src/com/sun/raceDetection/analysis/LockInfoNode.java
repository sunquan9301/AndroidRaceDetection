package com.sun.raceDetection.analysis;

import java.util.ArrayList;

public class LockInfoNode {
	//Integer[0] OrderCount; Integer[1] ThreadSign
	public ArrayList<Integer> lockOrder=new ArrayList<Integer>();
	public ArrayList<Integer> unLockOrder=new ArrayList<Integer>();
}
