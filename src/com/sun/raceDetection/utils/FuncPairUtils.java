package com.sun.raceDetection.utils;

public class FuncPairUtils {
	//	return sharedVariable+"_"+methodOne+"_"+methodTwo+"_"+String.valueOf(sign);
	public static String getSharedVariableFromFileName(String funcPairName){
		return ((String[])funcPairName.split("_"))[0];
	}
	public static String getMethodOneFromFileName(String funcPairName){
		return ((String[])funcPairName.split("_"))[1];
	}
	public static String getMethodTwoFromFileName(String funcPairName){
		return ((String[])funcPairName.split("_"))[2];
	}
	public static int getSignFromFileName(String funcPairName){
		return Integer.parseInt(((String[])funcPairName.split("_"))[3]);
	}
}
