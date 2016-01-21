/**
 *------------------------------------------------------------------
 * Project Name: AndroidRaceDetection
 * Day and Time: 2016��1��1�� ����2:26:37
 * @author Sun Quan
 * @version 1.0
 * com.sun.soot.example recordNecessaryInformation.java
 * Description:record shared variable and event response function
 * -----------------------------------------------------------------
 */
package com.sun.raceDetection.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jbco.gui.RunnerThread;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.Stmt;
import soot.util.Chain;

import com.sun.raceDetection.analysis.FunctionPairAnalysis;
import com.sun.raceDetection.moel.Information;
import com.sun.raceDetection.moel.sootClassAndMethods;
import com.sun.raceDetection.utils.PrintUtils;

public class recordNecessaryInformation extends BodyTransformer {
	private static final String RUN = "run";

//	private boolean isOnCreateOrOnResumeMethod=false;
//	private boolean isOnStartCommand=false;
//	private boolean isOnBind=false;
//	
	private static ArrayList<String> lifecycleMethods=new ArrayList<String>();
	static{
		lifecycleMethods.add("onGreate");
		lifecycleMethods.add("onStart");
		lifecycleMethods.add("onResume");
		lifecycleMethods.add("onPause");
		lifecycleMethods.add("onStop");
		lifecycleMethods.add("onDestory");
		lifecycleMethods.add("onRestart");
		lifecycleMethods.add("onStartCommand");
		lifecycleMethods.add("onBind");
		lifecycleMethods.add("onUnbind");
		
		
	}

	@Override
	protected void internalTransform(Body body, String phrase,
			Map<String, String> options) {
		
		for (SootClass c : Scene.v().getApplicationClasses()) {
			String packageName = c.getPackageName();
			if (Config.skipPackage(packageName)) {
				// System.out.println("Skipping " + sc.getName());
				continue;
			}
			if(Information.parseRecord.get(c.toString())==null){
				Information.parseRecord.put(c.toString(), 1);
			}else{
				continue;
			}
			System.out.println("[sootClass]" + c);
			//���ݳ���ʶ��activity,service, asyncTask
			//Information.classInfo.add(c.getName()+":"+c.getSuperclass());
			recordClassFromSuperClass(c);
			//���ݽӿ�ʶ���¼������ʹ�ͳ��run����
			recordClassFromInterfaces(c);
			for (SootMethod m : c.getMethods()) {
				//------------------------------�������ȴ���---------------------------------
				if(m.getName().equals("<init>")||m.getName().equals("<clinit>")){
					continue;
				}
				if(Information.parseRecord.get(m.toString())==null){
					Information.parseRecord.put(m.toString(), 1);
				}else{
					continue;
				}
//				if(lifecycleMethods.contains(m.getName())){
//					Information.lifecycleSet.put(m, c.getName());
//				}
				//------------------------------�������ȴ���---------------------------------
				if (m.isConcrete()) {
					//------------------------------������ȴ���---------------------------------

					Body b = m.retrieveActiveBody();
					// System.out.println("[body]"+b);

					Iterator<Unit> i = b.getUnits().snapshotIterator();
					while (i.hasNext()) {
						Stmt stmt = (Stmt) i.next();
						Information.LocLines++;
						if (stmt.containsFieldRef()) {
							// System.out.println("stmt_FieldRef():   "+stmt.getFieldRef());
							assert (stmt instanceof AssignStmt) : "Unknown FieldReffing Stmt";
							{
								// check if it is a write
								boolean write = (((DefinitionStmt) stmt)
										.getLeftOp() instanceof FieldRef);
								boolean read = (((DefinitionStmt) stmt).getRightOp() instanceof FieldRef);
								if (write) {
								//	System.out.println("[stmt]:   " + stmt);
								//	System.out.println(" [stmt_sharedVariableSignatures]:   "+ stmt.getFieldRef().getField().getSignature());
									//��ӵ����������
//									Information.sharedVariableSignatures.add("class:"+c+"|"+c.getName()+"Method:"+m+"|"+m.getName()+stmt
//											.getFieldRef().getField()
//											.getSignature());
									Information.sharedVariableSignatures.add(stmt
											.getFieldRef().getField()
											.getSignature());
									
									//����д�����Ժ�����ӳ��
									addIntoReadOrWriteSharedVariableMapFunctions(Information.writeSharedVariableMapFunctions,m, stmt);
								}
								
								//�ж϶�����
								if(read){
									//����������Ժ�����ӳ��
									addIntoReadOrWriteSharedVariableMapFunctions(Information.readSharedVariableMapFunctions, m, stmt);
								}
							}
						}
					}
					//------------------------------������ȴ���---------------------------------
				}
			}
		}
		//System.out.println(sharedVariableSignatures.size());
		//PrintUtils.printSharedVariable(sharedVariableSignatures);
		//pair����
	}

//�����Ϣ����д��������
	private void addIntoReadOrWriteSharedVariableMapFunctions(HashMap<String, HashSet<String>> SharedVariableMapFunctions, SootMethod m, Stmt stmt) {
		if(SharedVariableMapFunctions.get(stmt.getFieldRef().getField().getSignature())==null){
			HashSet<String> functions = new HashSet<String>();
			//sootClassAndMethods scm=new sootClassAndMethods(c.getName(),m);
			functions.add(m.toString());
			SharedVariableMapFunctions.put(stmt
				.getFieldRef().getField()
				.getSignature(), functions);
		}else{
			SharedVariableMapFunctions.get(stmt.getFieldRef().getField().getSignature()).add(m.toString());
		}
	}
	

	private void recordClassFromInterfaces(SootClass c) {
		// TODO Auto-generated method stub
		Chain<SootClass> interfaces = c.getInterfaces();
		Iterator<SootClass> iterator = interfaces.iterator();
		while(iterator.hasNext()){
			SootClass next = iterator.next();
			//��ͳ���߳�
			if(next.getName().contains("Runnable")){
				Information.runSet.add(c.getName());
				continue;
			}
			//����Dialog
			if(next.getName().contains("Dialog")){
				Information.activitySet.add(c.getName());
				continue;
			}
			//����
			if(next.getName().contains("Listener")||next.getName().contains("TextWatcher")){
				if(Information.classMapListener.get(c)==null){
					HashSet<String> listeners= new HashSet<String>();
					listeners.add(next.getName());
					Information.classMapListener.put(c.getName(), listeners);
					continue;
				}else{
					Information.classMapListener.get(c.getName()).add(next.getName());
					continue;
				}
			}
		}
	}
	private void recordClassFromSuperClass(SootClass c) {
		// TODO Auto-generated method stub
		SootClass superclass = c.getSuperclass();
		if(superclass.getName().contains("Activity")){
			Information.activitySet.add(c.getName());
			return;
		}
		if(superclass.getName().contains("Thread")){
			Information.runSet.add(c.getName());
			return;
		}
		if(superclass.getName().contains("Service")){
			Information.serviceSet.add(c.getName());
			return;
		}
		if(superclass.getName().contains("AsyncTask")){
			Information.asyncTaskSet.add(c.getName());
			return;
		}
		
	}

}
