package com.sun.raceDetection.moel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import soot.SootClass;

public class Information {
	//---------------------------------------------------------------�������������д��������---------------------------------------------------------------------------------
	//���������
	public  static HashSet<String> sharedVariableSignatures = new HashSet<String>();
	//д����-��Ӧ��������
	public static HashMap<String, HashSet<String>> writeSharedVariableMapFunctions = new HashMap<String, HashSet<String>>();
	//������map ��Ӧ��������
	public static HashMap<String, HashSet<String>> readSharedVariableMapFunctions = new HashMap<String, HashSet<String>>();
	//---------------------------------------------------------------class��----------------------------------------------------------------------------------
	//public static HashSet<String> classInfo=new HashSet<String>();
	//activity��
	public static HashSet<String> activitySet=new HashSet<String>();
	//service��
	public static HashSet<String> serviceSet = new HashSet<String>();
	//asynTask��
	public static HashSet<String> asyncTaskSet = new HashSet<String>();
	//��ͳ���߳��ࡣ
	public static HashSet<String> runSet = new HashSet<String>();
	//-----------------����
//	//�������ڷ���
//	public static HashMap<SootMethod,String> lifecycleSet = new HashMap<SootMethod,String>();
	//�¼�Դmap class
	public static HashMap<String, HashSet<String>> classMapListener = new  HashMap<String, HashSet<String>>();
	//public static HashMap<String, HashSet<SootClass>> listenerMapClass = new HashMap<String, HashSet<SootClass>>();
	//listen����
	//---------------------------------------------------------------FuncPair��----------------------------------------------------------------------------------
	//public static HashSet<SootClass> listenerSet = new HashSet<SootClass>();
	public static HashSet<FuncPairs> funcPairs = new HashSet<FuncPairs>();
	public static HashMap<String,SootClass> asyncFuncPair = new HashMap<String,SootClass>();
	public static HashSet<String> methodNameSet=new HashSet<String>();
	//public static HashMap<String,String> methodMapFuncPairFileName=new HashMap<String, String>();
	//--------------------------------------------------------------�ļ���ӳ�䵽Constraints----------------------------------------------------------------------------------
	public static HashMap<String,Constraints> methodNameMapConstraint = new HashMap<String, Constraints>();
	public static HashMap<String,Integer> parseRecord=new HashMap<String, Integer>();
	public static HashMap<String,Integer> analysisRecord=new HashMap<String, Integer>();
	public static int resultCount=0;
	public static ArrayList<Integer> fileType=new ArrayList<Integer>();
	public static Integer[] resultTypeCount={0,0,0,0,0,0,0,0,0};
	public static String[] resultType={"ACTIVITY_LISTENER","ACTIVITY_SERVICE_OR_THREAD","ACTIVITY_ASYNCTASK","DOUBLE_SERVICE_OR_THREAD",
		"SERVICE_OR_THREAD_ASYNCTASK","SERVICE_OR_THREAD_LISTENER","LISTENER_LISTENER","DOUBLE_ASYNCTASK","ASYNCTASK_LISTENER"};
	public static int asyncWindowRace=0;
	public static ArrayList<Integer> eachFileDataRace = new ArrayList<Integer>();
	
	//------------------------------------------------------------constraints type information----------------------------------------------------------------
	public static int orderCons=0;
	public static int mayRaceCons=0;
	public static int RWCons=0;
	public static int LockCons=0;
	public static int startAndJoinCons=0;
	public static int AndroidSpecificCons=0;
	public static int AllCons=0;
	//����ͳ��
	public static int LocLines=0;
	public static HashSet<String> getSharedVariableSignatures() {
		return sharedVariableSignatures;
	}

	public static void setSharedVariableSignatures(
			HashSet<String> sharedVariableSignatures) {
		Information.sharedVariableSignatures = sharedVariableSignatures;
	}
}
