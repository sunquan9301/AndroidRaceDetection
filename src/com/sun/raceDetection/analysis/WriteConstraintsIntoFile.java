package com.sun.raceDetection.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.raceDetection.moel.Constraints;
import com.sun.raceDetection.moel.FuncPairs;
import com.sun.raceDetection.moel.Information;
import com.sun.raceDetection.moel.PairType;
import com.sun.raceDetection.moel.ThreadType;
import com.sun.raceDetection.moel.nullNode;
import com.sun.raceDetection.parse.Config;

public class WriteConstraintsIntoFile {
//	public 
//	this.singFuncPairPath = Config.ConstraintsPath + "\\" + sharedVar.substring(1, sharedVar.length()-1) + "-"
//			+ m1.getName() + "-" + m2.getName() + "-" + sign + ".txt";
	private String singFuncPairPath;
	private File file;
	private String sharedVariable;
	private String method1;
	private String method2;
	private String fileContentInfo;
	private int sign;
	private Constraints cons1;
	private Constraints cons2;
	//约束文件标序号
	private int fileCount=0;
	public void startWriteConstraints() {
		// TODO Auto-generated method stub
//		for (FuncPairs funcPairs : Information.funcPairs) {
//			writeConstraintsForFuncPair(funcPairs);
//		}
		Object[] array = Information.funcPairs.toArray();
		for (int i = 0; i < array.length; i++) {
			FuncPairs funcPairs = (FuncPairs) array[i];
			initView(funcPairs);
			processLis();
			if(fliter(funcPairs)){
				continue;
			}
			//处理
//			if(sign==PairType.ACTIVITY_LISTENER
////					||
////					sign==PairType.SERVICE_OR_THREAD_LISTENER||
////					sign==PairType.ASYNCTASK_LISTENER
//					//--------服务在后台运行  listen在前台。。属于不同线程
//					){
//				processLis();
//			}
			Information.fileType.add(funcPairs.sign);
			writeConstraintsForFuncPair(funcPairs,fileCount++);
		}
		System.out.println(fileCount);
	}

	private void initView(FuncPairs funcPairs) {
		// TODO Auto-generated method stub
		sharedVariable=funcPairs.sharedVariable;
		method1=funcPairs.methodOne;
		method2=funcPairs.methodTwo;
		sign=funcPairs.sign;
		cons1 = Information.methodNameMapConstraint.get(method1);
		cons2 = Information.methodNameMapConstraint.get(method2);
	}

	private boolean fliter(FuncPairs funcPairs) {
		// TODO Auto-generated method stub
		if(method1.contains("onCreate")||
				method1.contains("onStart")||
				method2.contains("onCreate")||
				method2.contains("onStart"))
		{
			return true;
		}
		
		
		if(checkIsFliter("onMenuItemClick","onPrepareOptionsMenu")
//				||checkIsFliter("Click","onPause")
//				||checkIsFliter("Click","onStop")
//				||checkIsFliter("Click","onDestroy")
//				||checkIsFliter("press","onPause")
//				||checkIsFliter("press","onStop")
//				||checkIsFliter("press","onDestroy")
				){
			return true;
		}
		if(sign==PairType.ACTIVITY_LISTENER){
			if(!(getClassNameFromMethod(method1).contains(getClassNameFromMethod(method2))||
					getClassNameFromMethod(method2).contains(getClassNameFromMethod(method1)))){
				if(method1.contains("Click")||method2.contains("Click")||method1.contains("Press")||method2.contains("Press")){
					return true;
				}
			}
		}
		//过滤包含listen但不是def-use的
//		if(sign==PairType.ACTIVITY_LISTENER||
//				sign==PairType.SERVICE_OR_THREAD_LISTENER||
//				sign==PairType.ASYNCTASK_LISTENER){
//			ReadAndWriteNode RWNode1 = cons1.SharedVarRWSet.get(sharedVariable);
//			ReadAndWriteNode RWNode2 = cons2.SharedVarRWSet.get(sharedVariable);
//		if(cons1.invokeNull.size()==0&&cons2.invokeNull.size()==0&&RWNode1.writeNullSet.size()==0&&RWNode2.writeNullSet.size()==0){
//				return true;
//			}
//		}
//		
		
		
		
		return false;
	}

	private void processLis() {
			ReadAndWriteNode RWNode1 = cons1.SharedVarRWSet.get(sharedVariable);
			ReadAndWriteNode RWNode2 = cons2.SharedVarRWSet.get(sharedVariable);
			for (nullNode nu : cons1.invokeNull) {
				if(checkReadValueIsField(cons1.assignValueInfo,nu.sourceInvoke)){
					RWNode1.writeSet.add(nu.orderCount);
				}
			}
			for (nullNode nu : cons2.invokeNull) {
				if(checkReadValueIsField(cons2.assignValueInfo,nu.sourceInvoke)){
					RWNode2.writeSet.add(nu.orderCount);
				}
			}
			
		}
//	private void processLis() {
//		ReadAndWriteNode RWNode1 = cons1.SharedVarRWSet.get(sharedVariable);
//		ReadAndWriteNode RWNode2 = cons2.SharedVarRWSet.get(sharedVariable);
//		for (nullNode nu : cons1.invokeNull) {
//			if(checkReadValueIsField(cons1.assignValueInfo,nu.sourceInvoke)){
//				RWNode1.writeNullSet.add(nu.orderCount);
//			}
//		}
//		for (nullNode nu : cons2.invokeNull) {
//			if(checkReadValueIsField(cons2.assignValueInfo,nu.sourceInvoke)){
//				RWNode2.writeNullSet.add(nu.orderCount);
//			}
//		}
//		
//	}
	private boolean checkReadValueIsField(HashMap<String, ArrayList<AssignRightInfoNode>> assignValueInfo, String sourceInvoke) {
		// TODO Auto-generated method stub
		if(assignValueInfo.get(sourceInvoke.trim())!=null && assignValueInfo.get(sourceInvoke.trim()).size()>0){
			for (AssignRightInfoNode node : assignValueInfo.get(sourceInvoke.trim())) {
				if(node.first.contains(sharedVariable)){
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkIsFliter(String string, String string2) {
		// TODO Auto-generated method stub
		if((method1.contains(string)&&method2.contains(string2))
				||(method2.contains(string)&&method1.contains(string2)))
		{
			return true;
		}
		return false;
	}

	private void writeConstraintsForFuncPair(FuncPairs funcPairs,int index) {
//		System.out.println("******************************************");
		System.out.println("[sign:]"+Information.resultType[sign-1]+"[sharedVar:]"+sharedVariable+"[method1:]"+method1+"[method2:]"+method2);
//		System.out.println(method1);
//		System.out.println(method2);
//		System.out.println(sign);
//		System.out.println(cons1);
//		System.out.println("----------------");
//		System.out.println(cons2);
//		System.out.println("----------------");
//		System.out.println(cons1.getDetailInformation());
//		System.out.println("----------------");
//		System.out.println(cons2.getDetailInformation());
		singFuncPairPath=Config.ConstraintsPath+"\\"+index+".stm2";
		fileContentInfo=((String[])sharedVariable.split(":"))[0].substring(1)
				+((String[])sharedVariable.split(":"))[1].substring(0, ((String[])sharedVariable.split(":"))[1].length()-1)
				+"-"+((String[])method1.split(":"))[0].substring(1) //方法一所在类
				+"-"+((String[])method1.split(":"))[1].substring(0, ((String[])method1.split(":"))[1].length()-1) //方法一所在名
				+"-"+((String[])method2.split(":"))[0].substring(1) //方法二所在类
				+"-"+((String[])method2.split(":"))[1].substring(0, ((String[])method2.split(":"))[1].length()-1) //方法二所在名
				+"-"+sign+".stm2";
		//System.out.println(singFuncPairPath);
		file = new File(singFuncPairPath);
		if (!file.exists()) {
			try {
				file.createNewFile();
				//System.out.println(file.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		WriteConstraintsFromTwoMethod();
	}

	private void WriteConstraintsFromTwoMethod() {
		// TODO Auto-generated method stub
		FileWriter fw=null;
		BufferedWriter bw=null;
		try {
			fw=new FileWriter(file);
			bw=new BufferedWriter(fw);
			//------------------------------------------写注释------------------------------------
			bw.write(";"+fileContentInfo+"\n");
			//-----------------------------------------写序列约束---------------------------------
			//定义O0作为初始值
			for (int i = 0; i <= cons1.OrderCount; i++) {
				bw.write("(declare-const O"+i+" Int)\n") ;
				bw.flush();
			}
			bw.write("\n");
			bw.write("\n");
			for (int i = 1; i <= cons2.OrderCount; i++) {
				bw.write("(declare-const O"+(i+cons1.OrderCount)+" Int)\n") ;
				bw.flush();
			}
			bw.write("\n");
			bw.write("\n");
			bw.write("\n");
			bw.write("\n");
			
			for (int i = 0; i < cons1.OrderCount; i++) {
				bw.write("(assert (< O"+i+" O"+(i+1)+"))\n") ;
				Information.orderCons++;
				Information.AllCons++;
				bw.flush();
			}
			bw.write("\n");
			bw.write("\n");
			for (int i = 1; i < cons2.OrderCount; i++) {
				bw.write("(assert (< O"+(i+cons1.OrderCount)+" O"+(i+cons1.OrderCount+1)+"))\n") ;
				Information.orderCons++;
				Information.AllCons++;
				bw.flush();
			}
			bw.write("\n");
			bw.write("\n");
			bw.write("\n");
			bw.write("\n");
			
			//--------------------------锁约束-----------------------------------
			if(cons1.lockInfo.keySet().size()>0){
				Object[] array = cons1.lockInfo.keySet().toArray();
				for (int i = 0; i < array.length; i++) {
					String lockObject=array[i].toString();
					if(cons2.lockInfo.containsKey(lockObject)){
						LockInfoNode lockInfoNode1 = cons1.lockInfo.get(lockObject);
						LockInfoNode lockInfoNode2 = cons2.lockInfo.get(lockObject);
						for (int j = 0; j < lockInfoNode1.lockOrder.size(); j++) {
							int acquire1=lockInfoNode1.lockOrder.get(j);
							int release1=lockInfoNode1.unLockOrder.get(j);
							for (int k = 0; k < lockInfoNode2.lockOrder.size(); k++) {
								int acquire2=lockInfoNode2.lockOrder.get(k)+cons1.OrderCount;
								int release2=lockInfoNode2.unLockOrder.get(k)+cons1.OrderCount;
								System.out.println("---------锁--------------");
								bw.write("(assert (or (< O"+release1+" O"+acquire2+") (< O"+release2+" O"+acquire1+")))\n");
								Information.LockCons++;
								Information.AllCons++;
								bw.flush();
							}
						}
						
					}
				}
			}
			bw.write("\n");
			bw.write("\n");
			//-----------------------------数据竞争约束----------------------------------------
			ReadAndWriteNode RWNode1 = cons1.SharedVarRWSet.get(sharedVariable);
			ReadAndWriteNode RWNode2 = cons2.SharedVarRWSet.get(sharedVariable);
			if(RWNode1!=null&&RWNode2!=null){
				
//				if(sign==PairType.ACTIVITY_LISTENER||
//						sign==PairType.SERVICE_OR_THREAD_LISTENER||
//						sign==PairType.ASYNCTASK_LISTENER)
//				{
//					//1读，2写
//					//(push)
//					//(check-sat)
//					//(assert(= OG3 OG26))
//					//(get-model)
//					//(pop)
//					for (int i = 0; i < RWNode1.readSet.size(); i++) {
//						int race1 = RWNode1.readSet.get(i);
//						for (int j = 0; j < RWNode2.writeNullSet.size(); j++) {
//							int race2=RWNode2.writeNullSet.get(j)+cons1.OrderCount;
//							bw.write("(push)\n");
//							bw.flush();
//							bw.write("(check-sat)\n");
//							bw.flush();
//							bw.write("(assert (= O"+race1+" O"+race2+"))\n");
//							bw.flush();
//							bw.write("(get-model)\n");
//							bw.flush();
//							bw.write("(pop)\n");
//							bw.flush();
//							bw.write("\n");
//							bw.write("\n");
//						}
//					}
//					//1写，2读
//					for (int i = 0; i < RWNode1.writeNullSet.size(); i++) {
//						int race1 = RWNode1.writeNullSet.get(i);
//						for (int j = 0; j < RWNode2.readSet.size(); j++) {
//							int race2=RWNode2.readSet.get(j)+cons1.OrderCount;
//							bw.write("(push)\n");
//							bw.flush();
//							bw.write("(check-sat)\n");
//							bw.flush();
//							bw.write("(assert (= O"+race1+" O"+race2+"))\n");
//							bw.flush();
//							bw.write("(get-model)\n");
//							bw.flush();
//							bw.write("(pop)\n");
//							bw.flush();
//							bw.write("\n");
//							bw.write("\n");
//						}
//					}
//				}else{
					//1读，2写
					//(push)
					//(check-sat)
					//(assert(= OG3 OG26))
					//(get-model)
					//(pop)
					for (int i = 0; i < RWNode1.readSet.size(); i++) {
						int race1 = RWNode1.readSet.get(i);
						for (int j = 0; j < RWNode2.writeSet.size(); j++) {
							int race2=RWNode2.writeSet.get(j)+cons1.OrderCount;
							bw.write("(push)\n");
							bw.flush();
							bw.write("(check-sat)\n");
							bw.flush();
							bw.write("(assert (= O"+race1+" O"+race2+"))\n");
							Information.mayRaceCons++;
							Information.AllCons++;
							bw.flush();
							bw.write("(get-model)\n");
							bw.flush();
							bw.write("(pop)\n");
							bw.flush();
							bw.write("\n");
							bw.write("\n");
						}
					}
					//1写，2读
					for (int i = 0; i < RWNode1.writeSet.size(); i++) {
						int race1 = RWNode1.writeSet.get(i);
						for (int j = 0; j < RWNode2.readSet.size(); j++) {
							int race2=RWNode2.readSet.get(j)+cons1.OrderCount;
							bw.write("(push)\n");
							bw.flush();
							bw.write("(check-sat)\n");
							bw.flush();
							bw.write("(assert (= O"+race1+" O"+race2+"))\n");
							Information.mayRaceCons++;
							Information.AllCons++;
							bw.flush();
							bw.write("(get-model)\n");
							bw.flush();
							bw.write("(pop)\n");
							bw.flush();
							bw.write("\n");
							bw.write("\n");
						}
					}
					//1.写，2写
					for (int i = 0; i < RWNode1.writeSet.size(); i++) {
						int race1 = RWNode1.writeSet.get(i);
						for (int j = 0; j < RWNode2.writeSet.size(); j++) {
							int race2=RWNode2.writeSet.get(j)+cons1.OrderCount;
							bw.write("(push)\n");
							bw.flush();
							bw.write("(check-sat)\n");
							bw.flush();
							bw.write("(assert (= O"+race1+" O"+race2+"))\n");
							Information.mayRaceCons++;
							Information.AllCons++;
							bw.flush();
							bw.write("(get-model)\n");
							bw.flush();
							bw.write("(pop)\n");
							bw.flush();
							bw.write("\n");
							bw.write("\n");
						}
					}
				}
				
				
			
			
			//-----------------------------同步约束--------------------------------
//			public static int ACTIVITY_LISTENER=1;
//			public static int ACTIVITY_SERVICE_OR_THREAD=2;
//			public static int ACTIVITY_ASYNCTASK=3;
//			public static int DOUBLE_SERVICE_OR_THREAD=4;
//			public static int SERVICE_OR_THREAD_ASYNCTASK=5;
//			public static int SERVICE_OR_THREAD_LISTENER=6;
//			public static int LISTENER_LISTENER=7;
//			public static  int DOUBLE_ASYNCTASK = 8;
//			public static int ASYNCTASK_LISTENER=9;
			int Order1;
			int Order2;
			switch (sign) {
			case PairType.ACTIVITY_LISTENER:
				
				System.out.println("---------PairType.ACTIVITY_LISTENER:--------------");

				Order1=getStartActivityOrListenerLocation(cons1,method2);
				Order2=getStartActivityOrListenerLocation(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;
			case PairType.ACTIVITY_SERVICE_OR_THREAD:
				System.out.println("---------PairType.ACTIVITY_SERVICE_OR_THREAD:--------------");
				Order1=getStartActivityOrServiceOrThread(cons1,method2);
				Order2=getStartActivityOrServiceOrThread(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;
			case PairType.ACTIVITY_ASYNCTASK:
				System.out.println("---------PairType.ACTIVITY_ASYNCTASK:--------------");
				Order1=getStartActivityOrASYNCTASK(cons1,method2);
				Order2=getStartActivityOrASYNCTASK(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;
			case PairType.DOUBLE_SERVICE_OR_THREAD:
				System.out.println("---------PairType.DOUBLE_SERVICE_OR_THREAD:--------------");
				Order1=getDoubleServiceOrThread(cons1,method2);
				Order2=getDoubleServiceOrThread(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;
			case PairType.SERVICE_OR_THREAD_ASYNCTASK:
				System.out.println("---------PairType.SERVICE_OR_THREAD_ASYNCTASK:--------------");
				Order1=getServiceOrThreadOrAsyncTask(cons1,method2);
				Order2=getServiceOrThreadOrAsyncTask(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;
			case PairType.SERVICE_OR_THREAD_LISTENER:
				System.out.println("---------PairType.SERVICE_OR_THREAD_LISTENER:--------------");
				Order1=getServiceOrThreadOrListener(cons1,method2);
				Order2=getServiceOrThreadOrListener(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;
			case PairType.LISTENER_LISTENER:
				System.out.println("---------PairType.LISTENER_LISTENER:--------------");
				Order1=getListenerOrListener(cons1,method2);
				Order2=getListenerOrListener(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;
			case PairType.DOUBLE_ASYNCTASK:
				System.out.println("---------PairType.DOUBLE_ASYNCTASK:--------------");
				Order1=getDoubleAsyncTask(cons1,method2);
				Order2=getDoubleAsyncTask(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;
			case PairType.ASYNCTASK_LISTENER:
				System.out.println("---------PairType.ASYNCTASK_LISTENER:--------------");
				Order1=getAsyncTaskOrListener(cons1,method2);
				Order2=getAsyncTaskOrListener(cons2,method1);
				if(Order1!=-1){
					bw.write("(assert (< O"+Order1+" O"+(cons1.OrderCount+1)+"))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				if(Order2!=-1){
					bw.write("(assert (< O"+(Order2+cons1.OrderCount)+" O0))\n");
					Information.AndroidSpecificCons++;
					Information.AllCons++;
					bw.flush();
				}
				break;

			default:
				break;
			}
			bw.write("\n");
			bw.write("\n");
			//------------------------------start------------------------------
			Object[] array1 = cons1.startInfor.keySet().toArray();
			for (int i = 0; i < array1.length; i++) {
				int start1=Integer.parseInt(array1[i].toString());
				System.out.println("---------start-------------");
				bw.write("(assert (< O"+start1+" O"+(cons1.OrderCount+1)+"))\n");
				Information.startAndJoinCons++;
				Information.AllCons++;
				bw.flush();
			}
			bw.write("\n");
			bw.write("\n");
			Object[] array2 = cons2.startInfor.keySet().toArray();
			for (int i = 0; i < array2.length; i++) {
				int start2=Integer.parseInt(array2[i].toString())+cons1.OrderCount;
				System.out.println("---------start-------------");
				bw.write("(assert (< O"+start2+" O0))\n");
				Information.startAndJoinCons++;
				Information.AllCons++;
				bw.flush();
			}
			bw.write("\n");
			bw.write("\n");
			//---------------------------------join---------------------------------
			Object[] joinArray1 = cons1.startInfor.keySet().toArray();
			for (int i = 0; i < joinArray1.length; i++) {
				int join1=Integer.parseInt(joinArray1[i].toString());
				System.out.println("---------join-------------");
				bw.write("(assert (< O"+(cons1.OrderCount+cons2.OrderCount)+" O"+join1+"))\n");
				Information.startAndJoinCons++;
				Information.AllCons++;
				bw.flush();
			}
			bw.write("\n");
			bw.write("\n");
			Object[] joinArray2 = cons2.startInfor.keySet().toArray();
			for (int i = 0; i < joinArray2.length; i++) {
				int join2=Integer.parseInt(joinArray2[i].toString())+cons1.OrderCount;
				System.out.println("---------join-------------");
				bw.write("(assert (< O"+cons1.OrderCount+" O"+join2+"))\n");
				Information.startAndJoinCons++;
				Information.AllCons++;
				bw.flush();
			}
			bw.write("\n");
			bw.write("\n");
			//-----------------------------------RWConstraints--------------------
			//添加初始值
			Object[] array = cons1.SharedVarRWSet.keySet().toArray();
			for (int i = 0; i < array.length; i++) {
				String Var=array[i].toString();
				if((Var!=sharedVariable)&&(cons2.SharedVarRWSet.get(Var)!=null)){
					ReadAndWriteNode rw1 = cons1.SharedVarRWSet.get(Var);
					ReadAndWriteNode rw2 = cons2.SharedVarRWSet.get(Var);
					//第一个读和第二个写集合。
					ArrayList<Integer> r1=rw1.readSet;
					ArrayList<Integer> w2=rw2.writeSet;
					w2.add(0);
					if(r1.size()!=0 && w2.size()!=0){
						for (int j = 0; j < w2.size(); j++) {
							int wOrder=w2.get(j);
							if(wOrder!=0)
							{
								wOrder=w2.get(j)+cons1.OrderCount;
							}
							for (int k = 0; k < r1.size(); k++) {
								int rOrder=r1.get(k);
								String result="(< O"+wOrder+" O"+rOrder+")";
								for (int l = 0; l < w2.size(); l++) {
									if(l!=j){
										int otherW=w2.get(l);
										if(otherW!=0)
										{
											otherW=w2.get(l)+cons1.OrderCount;
										}
										result="(and "+result+" "+"(or (< O"+otherW+" O"+wOrder+") (< O"+rOrder+" O"+otherW+")))";
									}
								}
								result="(assert "+result+")\n";
								//System.out.println("---------RW-------------");
								bw.write(result);
								Information.RWCons++;
								Information.AllCons++;
								bw.flush();
							}
						}
					}
					
					
					ArrayList<Integer> r2=rw2.readSet;
					ArrayList<Integer> w1=rw1.writeSet;
					w1.add(0);
					if(w1.size()!=0 && r2.size()!=0){
						for (int j = 0; j < w1.size(); j++) {
							int wOrder=w1.get(j);
							if(wOrder!=0)
							{
								wOrder=w1.get(j)+cons1.OrderCount;
							}
							for (int k = 0; k < r2.size(); k++) {
								int rOrder=r2.get(k)+cons1.OrderCount;
								String result="(< O"+wOrder+" O"+rOrder+")";
								for (int l = 0; l < w1.size(); l++) {
									if(l!=j){
										int otherW=w1.get(l);
										if(otherW!=0)
										{
											otherW=w1.get(l)+cons1.OrderCount;
										}
										result="(and "+result+" "+"(or (< O"+otherW+" O"+wOrder+") (< O"+rOrder+" O"+otherW+")))";
									}
								}
								result="(assert "+result+")\n";
								//System.out.println("---------RW-------------");
								bw.write(result);
								Information.RWCons++;
								Information.AllCons++;
								bw.flush();
							}
						}
					}
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private int getAsyncTaskOrListener(Constraints cons, String method) {
		// TODO Auto-generated method stub
		int orderStartAsycnTask=getStartASYNCTASKLocation(cons, method);
		if(orderStartAsycnTask!=-1){
			return orderStartAsycnTask;
		}
		int orderStartListener=getStartListenLocation(cons, method);
		if(orderStartListener!=-1){
			return orderStartListener;
		}
		return -1;
	}

	private int getDoubleAsyncTask(Constraints cons, String method) {
		// TODO Auto-generated method stub
		int orderStartAsycnTask=getStartASYNCTASKLocation(cons, method);
		if(orderStartAsycnTask!=-1){
			return orderStartAsycnTask;
		}
		return -1;
	}

	private int getListenerOrListener(Constraints cons, String method) {
		// TODO Auto-generated method stub
		int orderStartListener=getStartListenLocation(cons, method);
		if(orderStartListener!=-1){
			return orderStartListener;
		}
		return -1;
	}

	private int getServiceOrThreadOrListener(Constraints cons, String method) {
		// TODO Auto-generated method stub
		int orderStartService=getActivityOrServiceLocation(cons, method,ThreadType.START_SERVICE);
		if(orderStartService!=-1){
			return orderStartService;
		}
//		int orderStartThread=getStartThreadLocation(cons, method);
//		if(orderStartThread!=-1){
//			return orderStartThread;
//		}
		int orderStartListener=getStartListenLocation(cons, method);
		if(orderStartListener!=-1){
			return orderStartListener;
		}
		return -1;
	}

	private int getServiceOrThreadOrAsyncTask(Constraints cons,
			String method) {
		// TODO Auto-generated method stub
		int orderStartService=getActivityOrServiceLocation(cons, method,ThreadType.START_SERVICE);
		if(orderStartService!=-1){
			return orderStartService;
		}
//		int orderStartThread=getStartThreadLocation(cons, method);
//		if(orderStartThread!=-1){
//			return orderStartThread;
//		}
		int orderStartAsycnTask=getStartASYNCTASKLocation(cons, method);
		if(orderStartAsycnTask!=-1){
			return orderStartAsycnTask;
		}
		return -1;
	}

	private int getDoubleServiceOrThread(Constraints cons, String method) {
		// TODO Auto-generated method stub
		int orderStartService=getActivityOrServiceLocation(cons, method,ThreadType.START_SERVICE);
		if(orderStartService!=-1){
			return orderStartService;
		}
//		int orderStartThread=getStartThreadLocation(cons, method);
//		if(orderStartThread!=-1){
//			return orderStartThread;
//		}
		return -1;
	}

	private int getStartActivityOrASYNCTASK(Constraints cons, String method) {
		// TODO Auto-generated method stub
		int orderStartActivity=getStartActivityLocation(cons, method);
		if(orderStartActivity!=-1){
			return orderStartActivity;
		}
		int orderStartAsync=getStartASYNCTASKLocation(cons,method);
		if(orderStartAsync!=-1){
			return orderStartAsync;
		}
		return -1;
	}

	private int getStartASYNCTASKLocation(Constraints cons, String method) {
		// TODO Auto-generated method stub
		String className=((String[])method.split(":"))[0].substring(1);
		Object[] array = cons.assignValueInfo.keySet().toArray();
		for (int i = 0; i < array.length; i++) {
			ArrayList<AssignRightInfoNode> arrayList = cons.assignValueInfo.get(array[i].toString());
			for (AssignRightInfoNode assignRight : arrayList) {
				if(assignRight.first.trim().length()<=4)
				{
					continue;
				}
				if(assignRight.first.trim().substring(4).equals(className)){
					return assignRight.OrderCount;
				}
			}
		}
		return -1;
	}

	private int getStartActivityOrServiceOrThread(Constraints cons,
			String method) {
		// TODO Auto-generated method stub
		int orderStartActivity=getStartActivityLocation(cons, method);
		if(orderStartActivity!=-1){
			return orderStartActivity;
		}
		int orderStartService=getStartServiceLocation(cons,method);
		if(orderStartService!=-1){
			return orderStartService;
		}
//		int orderStartThread=getStartThreadLocation(cons,method);
//		if(orderStartThread!=-1){
//			return orderStartThread;
//		}
		return -1;
	}


	private int getStartThreadLocation(Constraints cons, String method) {
		// TODO Auto-generated method stub
		String result="";
		Object[] array = cons.startInfor.keySet().toArray();
		for (int i = 0; i < array.length; i++) {
			result+=array[i].toString();
		}
		if(result!=""){
			return Integer.parseInt(result);
		}
		return -1;
	}

	//检查前面的是否开启后面的
	private int getStartActivityOrListenerLocation(Constraints cons,
			String method) {
		// TODO Auto-generated method stub
		int orderStartActivity=getStartActivityLocation(cons,method);
		if(orderStartActivity!=-1){
			return orderStartActivity;
		}
		int orderStartListener=getStartListenLocation(cons,method);
		if(orderStartListener!=-1){
			return orderStartListener;
		}
		return -1;
	}
	private int getStartServiceLocation(Constraints cons, String method) {
		// TODO Auto-generated method stub
		return getActivityOrServiceLocation(cons, method,ThreadType.START_SERVICE);
	}

	//判断是否开启另一个activity.
		private int getStartActivityLocation(Constraints cons, String method) {
			return getActivityOrServiceLocation(cons, method,ThreadType.START_ACTIVITY);
		}
	//ACT ,Service
	//获取startOrService启动位置
		private int getActivityOrServiceLocation(Constraints cons, String method, String type) {
			String className=((String[])method.split(":"))[0].substring(1);
			//class "com/example/testactlis/TwoActivity"
			ArrayList<InvokeRightInfoNode> arrayList = cons.invokeInfo.get(type);
			if(arrayList!=null){
				for (InvokeRightInfoNode invoke : arrayList) {
					String intent = invoke.targetValue;
					if(cons.invokeInfo.get(ThreadType.INTENT)!=null){
						ArrayList<InvokeRightInfoNode> arrayList2 = cons.invokeInfo.get(ThreadType.INTENT);
						for (InvokeRightInfoNode intentNode : arrayList2) {
							String intentTargetClass=getClassName(intentNode.targetValue);
							if((intentNode.sourceValue.trim().equalsIgnoreCase(intent.trim())) && (intentTargetClass.equals(className))){
								return intentNode.orderCount;
							}
						}
					}
				}
			}
			return -1;
		}
		//Listener
	private int getStartListenLocation(Constraints cons, String method) {
		// TODO Auto-generated method stub
		//System.out.println("###Listen###"+cons.OrderCount);
		String className=((String[])method.split(":"))[0].substring(1);
		ArrayList<InvokeRightInfoNode> arrayList = cons.invokeInfo.get(ThreadType.LISTENER);
		if(arrayList!=null){
			for (InvokeRightInfoNode invoke : arrayList) {
				String listenerValue=invoke.targetValue;
				//System.out.println("###Listen###"+invoke.targetValue);
				if(cons.assignValueInfo.get(listenerValue)!=null){
					ArrayList<AssignRightInfoNode> arrayList2 = cons.assignValueInfo.get(listenerValue);
					for (AssignRightInfoNode assignRightInfoNode : arrayList2) {
						//-------------------------------可能出问题----------------------------------------
						if(assignRightInfoNode.first.trim().length()<=4)
						{
							continue;
						}
						if(assignRightInfoNode.first.trim().substring(4).equals(className)){
							//if(assignRightInfoNode.first.trim().contains(className)){
							return assignRightInfoNode.OrderCount;
						}
					}
				}
			}
		}
		return -1;
	}
	
	private String getClassNameFromMethod(String methodName){
		return ((String[])methodName.split(":"))[0].substring(1);
	}

	private String getClassName(String targetValue) {
		// TODO Auto-generated method stub
		String replace = targetValue.replace('/', '.');
		String substring = replace.trim().substring(7, replace.trim().length()-1);
		return substring;
	}
	
	
}
