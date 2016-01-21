package com.sun.raceDetection.analysis;

import java.util.HashSet;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.util.Chain;

import com.sun.raceDetection.moel.FuncPairs;
import com.sun.raceDetection.moel.Information;
import com.sun.raceDetection.moel.PairType;

/**
 * Information 下  listen处集合进行了修改，变成 classesMapListen. 因为一般一个类对应的listen会分开 ，这里还没做处理
 *
 */
public class FunctionPairAnalysis {
	private static final String ACTIVITY = "Activity";
	private static final String SERVICE_OR_THREAD = "ServiceOrThread";
	private static final String ASYNCTASK = "AsyncTask";
	private static final String LISTENER = "Listener";
	private static final String OTHER = null;
	private static final CharSequence DIALOG = "Dialog";
	private static final CharSequence TOAST = "Toast";
	private String listen1=null;
	private String listen2=null;
	private static FunctionPairAnalysis funPairAnalysis = null;
	
	private FunctionPairAnalysis(){
		
	}
	public static FunctionPairAnalysis getInstance() {
		// TODO Auto-generated method stub
		if(funPairAnalysis == null){
			funPairAnalysis = new FunctionPairAnalysis();
		}
		return funPairAnalysis;
	}
	//处理数据
	public void productMethodMapFuncPairInfoName() {
		// TODO Auto-generated method stub
		for (FuncPairs fPair : Information.funcPairs) {
			Information.methodNameSet.add(fPair.methodOne);
			Information.methodNameSet.add(fPair.methodTwo);
		}
	}
	//开始分析
	public void startFunPairAnalysis() {
		// TODO Auto-generated method stub
		for (String sharedVariable : Information.sharedVariableSignatures) {
			getFunPairsForSharedVar(sharedVariable);
		}
		
		//处理async
		//processAsyncTaskClasses(Information.asyncTaskSet);
	
	}
//	private void processAsyncTaskClasses(HashSet<String> asyncTaskSet) {
//		// TODO Auto-generated method stub
//		Object[] asyncArray = Information.asyncTaskSet.toArray();
//		for (int i = 0; i < asyncArray.length; i++) {
//			SootClass asyncTaskClass = (SootClass) asyncArray[i];
//			processSignleAsyncTask(asyncTaskClass);
//		}
//	}
//	private void processSignleAsyncTask(SootClass asyncTaskClass) {
//		// TODO Auto-generated method stub
//		Chain<SootField> asyncField = asyncTaskClass.getFields();
//		Object[] asyncFieldArrays = asyncField.toArray();
//		for (int i = 0; i < asyncFieldArrays.length; i++) {
//			SootField field = (SootField) asyncFieldArrays[i];
//			if(field.getSignature().contains(DIALOG)||field.getSignature().contains(TOAST)){
//				Information.asyncFuncPair.put(field.getSignature(), asyncTaskClass);
//			}
//		}
//	}
	//获取funPair对
	private void getFunPairsForSharedVar(String sharedVariable) {
		// TODO Auto-generated method stub
		HashSet<String> writeFuns = Information.writeSharedVariableMapFunctions.get(sharedVariable);
		HashSet<String> readFuns = Information.readSharedVariableMapFunctions.get(sharedVariable);
		if(writeFuns==null){
			return;
		}
		getFunPairsFromWriteAndReadFuns(sharedVariable,writeFuns,readFuns);
	}
	//处理读写变量集合
	private void getFunPairsFromWriteAndReadFuns(String sharedVariable,HashSet<String> writeFuns,
			HashSet<String> readFuns) {
		// TODO Auto-generated method stub
		Object[] writeArray = writeFuns.toArray();
		
		//处理写和写之间
		for (int i = 0; i < writeArray.length; i++) {
			String sootMethod1 = writeArray[i].toString();
			for (int j = i+1; j < writeArray.length; j++) {
				String sootMethod2 = writeArray[j].toString();
				getFunPairFromWriteAndWriteMethod(sharedVariable,sootMethod1,sootMethod2);
			}
		}
		if(readFuns!=null)
		{
			Object[] readArray = readFuns.toArray();
			//处理读写之间的
			for (int i = 0; i < writeArray.length; i++) {
				String sootMethod1 = writeArray[i].toString();
				for (int j =0; j < readArray.length; j++) {
					String sootMethod2 = readArray[j].toString();
					getFunPairFromWriteAndReadMethod(sharedVariable,sootMethod1,sootMethod2);
				}
			}
		}
	}
	//处理两个队共享变量进行读操作的方法。
	private void getFunPairFromWriteAndWriteMethod(String sharedVariable,String sootMethod1,
			String sootMethod2) {
		// TODO Auto-generated method stub
		getFunPariFromTwoMethods(sharedVariable, sootMethod1, sootMethod2);
	}
	//处理两个对共享变量进行读写操作的方法。
	private void getFunPairFromWriteAndReadMethod(String sharedVariable,String sootMethod1,
			String sootMethod2) {
		// TODO Auto-generated method stub
		getFunPariFromTwoMethods(sharedVariable, sootMethod1, sootMethod2);
	}
	private void getFunPariFromTwoMethods(String sharedVariable, String sootMethod1,
			String sootMethod2) {
		//类相等直接返回
		String methodClass1 = ((String[])sootMethod1.split(":"))[0].substring(1).trim();
		String methodClass2 = ((String[])sootMethod2.split(":"))[0].substring(1).trim();
		if(methodClass1.equals(methodClass2)){
			return;
		}
//		//处理单线程异步 一个是activity一个是listener, 特征是移除$类名一样
//		if(checkIsSingleThreadAsync(methodClass1,methodClass1)){
//			processSingleThreadAsync(sharedVariable,sootMethod1,sootMethod2);
//			return;
//		}
	//	initFlags();
		int type = checkTypeOfClass(methodClass1,methodClass2);
		if(type!=-1){
			Information.funcPairs.add(new FuncPairs(sharedVariable, sootMethod1, sootMethod2, type));
		}
	}
//	//初始化监听
//	private void initFlags() {
//		// TODO Auto-generated method stub
//		listen1=null;
//		listen2=null;
//	}
	private int checkTypeOfClass(String methodClass1, String methodClass2) {
		// TODO Auto-generated method stub
		String typeClass1 = getTypeOfClass(methodClass1);
		String typeClass2 = getTypeOfClass(methodClass2);
		int sign = getSignFromTwoClassType(typeClass1,typeClass2);
		return sign;
	}
//得到相应的标签
	private int getSignFromTwoClassType(String typeClass1, String typeClass2) {
		// TODO Auto-generated method stub
		if((typeClass1!=OTHER)&&(typeClass2!=OTHER)){
			//ACTIVITY开始
			if(typeClass1==ACTIVITY){
				if(typeClass2==SERVICE_OR_THREAD){
					return PairType.ACTIVITY_SERVICE_OR_THREAD;
				}
				if(typeClass2 ==ASYNCTASK){
					return PairType.ACTIVITY_ASYNCTASK;
				}
				if(typeClass2==LISTENER){
					return PairType.ACTIVITY_LISTENER;
				}
			}
			//service or thread start
			if(typeClass1==SERVICE_OR_THREAD){
				if(typeClass2==ACTIVITY){
					return PairType.ACTIVITY_SERVICE_OR_THREAD;
				}
				if(typeClass2==SERVICE_OR_THREAD){
					return PairType.DOUBLE_SERVICE_OR_THREAD;
				}
				if(typeClass2==ASYNCTASK){
					return PairType.SERVICE_OR_THREAD_ASYNCTASK;
				}
				if(typeClass2==LISTENER){
					return PairType.SERVICE_OR_THREAD_LISTENER;
				}
			}
			if(typeClass1==ASYNCTASK){
				if(typeClass2==ACTIVITY){
					return PairType.ACTIVITY_ASYNCTASK;
				}
				if(typeClass2==SERVICE_OR_THREAD){
					return PairType.SERVICE_OR_THREAD_ASYNCTASK;
				}
				if(typeClass2==ASYNCTASK){
					return PairType.DOUBLE_ASYNCTASK;
				}
				if(typeClass2==LISTENER){
					return PairType.ASYNCTASK_LISTENER;
				}
			}
			if(typeClass1==LISTENER){
				if(typeClass2==ACTIVITY){
					return PairType.ACTIVITY_LISTENER;
				}
				if(typeClass2==SERVICE_OR_THREAD){
					return PairType.SERVICE_OR_THREAD_LISTENER;
				}
				if(typeClass2==ASYNCTASK){
					return PairType.ASYNCTASK_LISTENER;
				}
//				if(typeClass2==LISTENER){
//					return PairType.LISTENER_LISTENER;
//				}
			}
			return -1;
		}
		return -1;
	}
	
	private String getTypeOfClass(String methodClass) {
		// TODO Auto-generated method stub
		//activity type
		if(Information.activitySet.contains(methodClass)){
			return ACTIVITY;
		}
		//service or thread type
		if(Information.serviceSet.contains(methodClass)||Information.runSet.contains(methodClass)){
			return SERVICE_OR_THREAD;
		}
		//asynctask type
		if(Information.asyncTaskSet.contains(methodClass))
		{
			return ASYNCTASK;
		}
		//listener type
		if(Information.classMapListener.containsKey(methodClass)){
			return LISTENER;
		}
//		Object[] sootClassObjects = Information.classMapListener.keySet().toArray();
//		for (int i = 0; i < sootClassObjects.length; i++) {
//			if(((SootClass)(sootClassObjects[i])).getName().equals(methodClass)){
////				HashSet<String> listeners = Information.classMapListener.get((SootClass)(sootClassObjects[i]));
////				String strListener= getStringFromHashSet(listeners);
//				return LISTENER;
//			}
//		}
		return OTHER;
	}
//	private String getStringFromHashSet(HashSet<String> listeners) {
//		// TODO Auto-generated method stub
//		Object[] objListeners = listeners.toArray();
//		String s = "";
//		for (int i = 0; i < objListeners.length; i++) {
//			s+=","+objListeners[i].toString();
//		}
//		return s;
//	}
//	//处理单线程异步
//	private void processSingleThreadAsync(String sharedVariable,SootMethod sootMethod1,
//			SootMethod sootMethod2) {
//		// TODO Auto-generated method stub
//		Information.funcPairs.add(new FuncPairs(sharedVariable, sootMethod1, sootMethod2, PairType.ACTIVITY_LISTENER));
//	}
//	//判断是否是单线程异步
//	private boolean checkIsSingleThreadAsync(String strSootMethod1,
//			String strSootMethod2) {
//		// TODO Auto-generated method stub
//		String methodClass1 = classNameRemoveSign(strSootMethod1);
//		String methodClass2 = classNameRemoveSign(strSootMethod2);
//		if(methodClass1.equals(methodClass2)){
//			return true;
//		}else{
//			return false;
//		}
//	}
//	//移除$符号
//	private String classNameRemoveSign(String strSootMethod) {
//		// TODO Auto-generated method stub
//		if(!(strSootMethod.contains("$"))){
//			return strSootMethod;
//		}else{
//			return strSootMethod.substring(0, strSootMethod.indexOf("$"));
//		}
//	}



}
