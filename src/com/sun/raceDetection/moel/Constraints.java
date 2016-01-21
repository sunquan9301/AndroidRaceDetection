package com.sun.raceDetection.moel;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.raceDetection.analysis.*;

public class Constraints {
	public int OrderCount=0;//用于计数记录方法长度

	//谁调用的和在哪行调用的 join
	public HashMap<Integer, String> joinInfor=new HashMap<Integer, String>();
	public HashMap<Integer,String> startInfor=new HashMap<Integer, String>();
	//锁的信息
	public HashMap<String,LockInfoNode> lockInfo=new HashMap<String, LockInfoNode>();
	//本共享变量读写集合 ，用于判断raceDetection

	//别的变量
	public HashMap<String,ReadAndWriteNode> SharedVarRWSet = new HashMap<String,ReadAndWriteNode>();
	//本地变量列表
	public HashMap<String,ArrayList<AssignRightInfoNode>> assignValueInfo=new HashMap<String,ArrayList<AssignRightInfoNode>>();
	//public HashMap<String,ArrayList<AssignRightInfoNode>> assignValueInfo2=new HashMap<String, ArrayList<AssignRightInfoNode>>();
	//本地调用函数列表
	public HashMap<String,ArrayList<InvokeRightInfoNode>> invokeInfo=new HashMap<String,ArrayList<InvokeRightInfoNode>>();
	//public HashMap<String,ArrayList<InvokeRightInfoNode>> invokeInfo2=new HashMap<String,ArrayList<InvokeRightInfoNode>>();
	public ArrayList <nullNode> invokeNull=new ArrayList<nullNode>();
	public String getDetailInformation(){
		 return "[Order:   ]"+OrderCount+"\n"
				 +"[join:   ]"+joinInfor.toString()+"\n"
		 +"[start:   ]"+startInfor.toString()+"\n"
			+"[lock:   ]"+lockInfo.toString()+"\n"
			+"[sharedRW:   ]"+SharedVarRWSet.toString()+"\n"
			+"[assign:   ]"+assignValueInfo.toString()+"\n"
			+"[invoke:   ]"+invokeInfo.toString()+"\n";
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Order:   ]"+OrderCount+"\n"
				+"[join:   ]"+joinInfor.keySet().size()+"\n"
				+"[start:   ]"+startInfor.keySet().size()+"\n"
				+"[lock:   ]"+lockInfo.keySet().size()+"\n"
				+"[sharedRW:   ]"+SharedVarRWSet.keySet().size()+"\n"
				+"[assign:   ]"+assignValueInfo.keySet().size()+"\n"
				+"[invoke:   ]"+invokeInfo.keySet().size()+"\n";
	}
}
