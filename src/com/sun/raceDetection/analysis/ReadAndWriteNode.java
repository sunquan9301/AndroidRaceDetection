package com.sun.raceDetection.analysis;

import java.util.ArrayList;

public class ReadAndWriteNode {
	public ArrayList<Integer> readSet=new ArrayList<Integer>();
	public ArrayList<Integer> writeSet=new ArrayList<Integer>();
	public ArrayList<Integer> writeNullSet=new ArrayList<Integer>(); 
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return readSet.toString()+","+writeSet.toString();
	}
}
