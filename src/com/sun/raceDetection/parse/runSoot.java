/**
 *------------------------------------------------------------------
 * Project Name: AndroidRaceDetection
 * Day and Time: 2016年1月1日 下午2:12:54
 * @author Sun Quan
 * @version 1.0
 * com.sun.soot.example runSoot.java
 * Description: a entry to run all the project.
 * -----------------------------------------------------------------
 */
package com.sun.raceDetection.parse;

import java.util.Arrays;

import soot.PackManager;
import soot.Transform;

import com.sun.raceDetection.analysis.PredictiveAnalysis;
import com.sun.raceDetection.analysis.WriteConstraintsIntoFile;
import com.sun.raceDetection.moel.Information;
import com.sun.raceDetection.utils.FileUtils;
import com.sun.raceDetection.utils.PrintUtils;

public class runSoot {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//初始化配置
		Config.initialiseSoot();
		//记录必要信息
		long startTime = System.currentTimeMillis();
		startRunTools();
		WriteConstraintsIntoFile writeConstraints=new WriteConstraintsIntoFile();
		writeConstraints.startWriteConstraints();
		//------------------TODO 处理asyncTask-------------------------
		FileUtils.calRunResult();
		System.out.println("运行时间："+(System.currentTimeMillis()-startTime));
		System.out.println("cons:"+Information.AllCons);
		System.out.println("orderCons:"+Information.orderCons);
		System.out.println("mayRaceCons:"+Information.mayRaceCons);
		System.out.println("RWCons:"+Information.RWCons);
		System.out.println("LockCons:"+Information.LockCons);
		System.out.println("startAndJoinCons:"+Information.startAndJoinCons);
		System.out.println("AndroidSpecificCons:"+Information.AndroidSpecificCons);
		System.out.println("funcPairSize："+Information.funcPairs.size());
		System.out.println("window race:"+Information.asyncWindowRace);
		System.out.println("race:"+Information.resultCount);
		System.out.println("eachFileRace:"+Information.eachFileDataRace.size()+"[content: ]"+Arrays.toString(Information.eachFileDataRace.toArray()));
		PrintUtils.printResultTypeCount(Information.resultTypeCount);
		
	}
	private static void startRunTools() {
		// TODO Auto-generated method stub
		recordNecessaryInformation record=new recordNecessaryInformation();
		PackManager.v().getPack("jtp").add(new Transform("jtp.recordNecessaryInformation", record));
//		PackManager.v().runPacks();
//		PackManager.v().writeOutput();
//		
//		Config.initialiseSoot();
		PredictiveAnalysis pa=new PredictiveAnalysis();
		PackManager.v().getPack("jtp").add(new Transform("jtp.PredictiveAnalysis",pa));
		PackManager.v().runPacks();
		//PackManager.v().writeOutput();
		
		
		PrintUtils.printAllInformation();
		PrintUtils.printFuncPairs(Information.funcPairs);
//		System.out.println("--------------:"+Information.methodNameSet.size());
//		PrintUtils.printConstraintInfo();
	}


}
