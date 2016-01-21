package com.sun.raceDetection.moel;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.raceDetection.analysis.*;

public class Constraints {
	public int OrderCount=0;//���ڼ�����¼��������

	//˭���õĺ������е��õ� join
	public HashMap<Integer, String> joinInfor=new HashMap<Integer, String>();
	public HashMap<Integer,String> startInfor=new HashMap<Integer, String>();
	//������Ϣ
	public HashMap<String,LockInfoNode> lockInfo=new HashMap<String, LockInfoNode>();
	//�����������д���� �������ж�raceDetection

	//��ı���
	public HashMap<String,ReadAndWriteNode> SharedVarRWSet = new HashMap<String,ReadAndWriteNode>();
	//���ر����б�
	public HashMap<String,ArrayList<AssignRightInfoNode>> assignValueInfo=new HashMap<String,ArrayList<AssignRightInfoNode>>();
	//public HashMap<String,ArrayList<AssignRightInfoNode>> assignValueInfo2=new HashMap<String, ArrayList<AssignRightInfoNode>>();
	//���ص��ú����б�
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
