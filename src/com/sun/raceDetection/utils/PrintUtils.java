/**
 *------------------------------------------------------------------
 * Project Name: AndroidRaceDetection
 * Day and Time: 2016年1月1日 下午2:55:11
 * @author Sun Quan
 * @version 1.0
 * com.sun.raceDetection.utils PrintUtils.java
 * Description:print information
 * -----------------------------------------------------------------
 */
package com.sun.raceDetection.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.sun.raceDetection.moel.Constraints;
import com.sun.raceDetection.moel.FuncPairs;
import com.sun.raceDetection.moel.Information;

import soot.SootClass;
import soot.SootMethod;

public class PrintUtils {
	public static void printAllInformation() {
		//共享变量
		System.out.println("sharedVariable_size:"+Information.sharedVariableSignatures.size());
		//PrintUtils.printSharedVariable(Information.getSharedVariableSignatures());
		//读写集合
		//PrintUtils.printReadOrWriteSharedVariablesFunction(Information.writeSharedVariableMapFunctions);
		//PrintUtils.printReadOrWriteSharedVariablesFunction(Information.readSharedVariableMapFunctions);
		//事件监听
		//线程相关信息
		System.out.println("all loc size"+Information.LocLines);
		System.out.println("activity_size: "+Information.activitySet.size());
		System.out.println("service_size: "+Information.serviceSet.size());
		System.out.println("asyncTask_size: "+Information.asyncTaskSet.size());
		System.out.println("runThread_size: "+Information.runSet.size());
		System.out.println("listener_size:"+printListenMapClass(Information.classMapListener));
		//PrintUtils.printLifecycleMethod(Information.lifecycleSet);
	}
	private static void printLifecycleMethod(
			HashMap<SootMethod, String> lifecycleSet) {
		// TODO Auto-generated method stub
		System.out.println(lifecycleSet);
		System.out.println(lifecycleSet.size());
	}

	public static void printSharedVariable(
			HashSet<String> sharedVariableSignatures) {
		// TODO Auto-generated method stub
		for (String string : sharedVariableSignatures) {
			System.out.println("shared variable:"+string);
		}
		
	}

	public static void printReadOrWriteSharedVariablesFunction(
			HashMap<String, HashSet<String>> readOrWriteSharedVariableMapFunctions) {
		// TODO Auto-generated method stub
		Object[] array = readOrWriteSharedVariableMapFunctions.keySet().toArray();
//		for (int i = 0; i < array.length; i++) {
//			System.out.println(array[i].toString()+":"+getStringFromArrayList(writeSharedVariableMapFunctions.get(array[i].toString()).toArray()));
//		}
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i].toString()+":"+readOrWriteSharedVariableMapFunctions.get(array[i].toString()).size());
		}
		System.out.println(array.length);
	}
	
	private static String getStringFromArrayList(Object[] strs){
		String result="";
		for (int i = 0; i < strs.length; i++) {
			result+=strs[i].toString()+"  ,  ";
		}
		return result;
	}

	public static int printListenMapClass(
			HashMap<String, HashSet<String>> classMapListener) {
		// TODO Auto-generated method stub
		int result=0;
		Object[] array = classMapListener.keySet().toArray();
		for (int i = 0; i < array.length; i++) {
			result+=classMapListener.get(array[i].toString()).size();
		}
		return result;
	}
	public static void printFuncPairs(HashSet<FuncPairs> funcPairs) {
		// TODO Auto-generated method stub
		 int ACTIVITY_LISTENER=0;
		int ACTIVITY_SERVICE_OR_THREAD=0;
		int ACTIVITY_ASYNCTASK=0;
		int DOUBLE_SERVICE_OR_THREAD=0;
		int SERVICE_OR_THREAD_ASYNCTASK=0;
		int SERVICE_OR_THREAD_LISTENER=0;
		int LISTENER_LISTENER=0;
		int DOUBLE_ASYNCTASK = 0;
		int ASYNCTASK_LISTENER=0;
		Object[] array = funcPairs.toArray();
		for (int i = 0; i < array.length; i++) {
			FuncPairs func = (FuncPairs) array[i];
			//System.out.println(func);
			switch (func.sign) {
			case 1:
				ACTIVITY_LISTENER++;
				break;
			case 2:
				ACTIVITY_SERVICE_OR_THREAD++;
				break;
			case 3:
				ACTIVITY_ASYNCTASK++;
				break;
			case 4:
				DOUBLE_SERVICE_OR_THREAD++;
				break;
			case 5:
				SERVICE_OR_THREAD_ASYNCTASK++;
				break;
			case 6:
				SERVICE_OR_THREAD_LISTENER++;
				break;
			case 7:
				LISTENER_LISTENER++;
				break;
			case 8:
				DOUBLE_ASYNCTASK++;
				break;
			case 9:
				ASYNCTASK_LISTENER++;
				break;

			default:
				break;
			}
		}
		//单独处理共享Window -Act-Async
		//ACTIVITY_ASYNCTASK+=Information.asyncFuncPair.keySet().size();
		System.out.println("all size:	"+array.length);
		System.out.println("Act_Lis:	"+ACTIVITY_LISTENER);
		System.out.println("Act_Thread:	"+ACTIVITY_SERVICE_OR_THREAD);
		System.out.println("Act_Async:	"+ACTIVITY_ASYNCTASK);
		System.out.println("Ser_Async:	"+SERVICE_OR_THREAD_ASYNCTASK);
		System.out.println("Dou_Thread:	"+DOUBLE_SERVICE_OR_THREAD);
		System.out.println("Ser_Lis:	"+SERVICE_OR_THREAD_LISTENER);
		System.out.println("Lis_Lis:	"+LISTENER_LISTENER);
		System.out.println("Dou_Async	"+DOUBLE_ASYNCTASK);
		System.out.println("Async_Lis:	"+ASYNCTASK_LISTENER);
	}
	public static void printConstraintInfo() {
		// TODO Auto-generated method stub
		System.out.println("---------------Constraints---------------------");
		System.out.println(Information.methodNameMapConstraint.keySet().size());
		Object[] array = Information.methodNameMapConstraint.keySet().toArray();
		for (int i = 0; i < array.length; i++) {
			System.out.println("[methodName:   ]"+array[i].toString());
			Constraints cons = Information.methodNameMapConstraint.get(array[i].toString());
			System.out.println(cons);
		}
	}
	public static void printResultTypeCount(Integer[] resultTypeCount) {
		// TODO Auto-generated method stub
		for (int i = 0; i < resultTypeCount.length; i++) {
			System.out.println("["+Information.resultType[i]+":]"+resultTypeCount[i]);
		}
	}
}

