package com.sun.raceDetection.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.JimpleLocal;
import soot.util.Chain;

import com.sun.raceDetection.moel.AssignStmtType;
import com.sun.raceDetection.moel.Constraints;
import com.sun.raceDetection.moel.Information;
import com.sun.raceDetection.moel.ThreadType;
import com.sun.raceDetection.moel.nullNode;
import com.sun.raceDetection.parse.Config;
import com.sun.raceDetection.utils.FuncPairUtils;

public class PredictiveAnalysis extends BodyTransformer {
	public static final String constructorName = "<init>";
	public static final String staticInitializerName = "<clinit>";
	public static FunctionPairAnalysis fpa = FunctionPairAnalysis.getInstance();
	private static final SootClass objectClass = Scene.v().loadClassAndSupport(
			"java.lang.Object");
	private static final CharSequence DIALOG = "Dialog";
	private static final CharSequence TOAST = "Toast";
	private static final SootMethod notify = objectClass
			.getMethod("void notify()");
	private static final SootMethod notifyAll = objectClass
			.getMethod("void notifyAll()");
	private static final SootMethod wait1 = objectClass
			.getMethod("void wait()");
	private static final SootMethod wait2 = objectClass
			.getMethod("void wait(long)");
	private static final SootMethod wait3 = objectClass
			.getMethod("void wait(long,int)");

	private static final SootMethod start = Scene.v()
			.loadClassAndSupport("java.lang.Thread").getMethod("void start()");
	private static final SootMethod join = Scene.v()
			.loadClassAndSupport("java.lang.Thread").getMethod("void join()");

	// ����������
	private String sharedVar;
	private String m1;
	private String m2;
	private int sign;
	// Լ���ļ����·��
	private String singFuncPairPath;
	public Constraints cons;
	public int threadSign = 1;
	private boolean isStartFPairAnalysis=false;

	static{
		// TODO Auto-generated constructor stub
		startFunctionPairAnalysis();
		}
	//funPair����
		private static void startFunctionPairAnalysis() {
			fpa.startFunPairAnalysis();
			fpa.productMethodMapFuncPairInfoName();
		}
	
	private void initFuncPairVars(String funcPairFileName) {
		// TODO Auto-generated method stub
		sharedVar = FuncPairUtils
				.getSharedVariableFromFileName(funcPairFileName);
		m1 = FuncPairUtils.getMethodOneFromFileName(funcPairFileName);
		m2 = FuncPairUtils.getMethodTwoFromFileName(funcPairFileName);
		sign = FuncPairUtils.getSignFromFileName(funcPairFileName);
	}

	@Override
	protected void internalTransform(Body body, String phrase,
			Map<String, String> options) {
		// TODO Auto-generated method stub
		if(!isStartFPairAnalysis){
			startFunctionPairAnalysis();
			isStartFPairAnalysis=true;
		}
		for (SootClass c : Scene.v().getApplicationClasses()) {
			String packageName = c.getPackageName();
			if (Config.skipPackage(packageName)) {
				// System.out.println("Skipping " + sc.getName());
				continue;
			}
			
			if(Information.analysisRecord.get(c.toString())==null){
				Information.analysisRecord.put(c.toString(), 1);
			}else{
				continue;
			}
			if(Information.asyncTaskSet.contains(c.getName())){
				Chain<SootField> asyncField = c.getFields();
				Object[] asyncFieldArrays = asyncField.toArray();
				for (int i = 0; i < asyncFieldArrays.length; i++) {
					SootField field = (SootField) asyncFieldArrays[i];
					if(field.getSignature().contains(DIALOG)||field.getSignature().contains(TOAST)){
						Information.asyncWindowRace++;
					}
				}
			}
			System.out.println("[SootClass]" + c.toString());
			// ��������Ҫ�����ĺ���
			for (SootMethod m : c.getMethods()) {
				// �鿴�ǲ���funcPair����ĺ��� ���������ֱ��������
				if(Information.analysisRecord.get(m.toString())==null){
					Information.analysisRecord.put(m.toString(), 1);
				}else{
					continue;
				}
				if (m.getName().equals("<init>")
						|| m.getName().equals("<clinit>")) {
					continue;
				}
				if (!Information.methodNameSet.contains(m.toString())) {
					continue;
				}

				// initFuncPairVars(Information.methodMapFuncPairFileName.get(m
				// .toString()));
				if (Information.methodNameMapConstraint.get(m.toString()) == null) {
					// ���Ϊ�գ������һ������
					cons = new Constraints();
					Information.methodNameMapConstraint.put(m.toString(), cons);
				} else {
					cons=Information.methodNameMapConstraint.get(m.toString());
				}

				if (m.isConcrete()) {
					// ------------------------------������ȴ���---------------------------------
					Body b = m.retrieveActiveBody();
					// -----TODO��֧����
					// BranchAnalysis
					Iterator<Unit> it = b.getUnits().snapshotIterator();
					while (it.hasNext()) {
						// ���м�1
						cons.OrderCount++;
						Stmt stmt = (Stmt) it.next();
						boolean unused = markJoin(b, stmt)
								|| markStart(b, stmt) || markLock(b, stmt)
								// || markWaitAndNotify(body,stmt)
								|| markFieldAccess(b, stmt)
								// || markArrayAccess(body, stmt)
								|| markAssignment(b, stmt);
						// executed only when the first above returns false;
						markInvocation(b, stmt);
						// markSynchronizedMethodCall(body, stmt);
						// markBranch(an,body, stmt);
					}
				}
				// ��������Order
			}

		}

	}

	// TODO ��֧������
		/**
		 * 1.Act-Ser startService 2.Act-Async new Async 3.Act-Listen setListen
		 * 4.Ser-ser(Thread) start 5.
		 */
		private void markInvocation(Body body, Stmt stmt) {
			// TODO Auto-generated method stub
			if (stmt.containsInvokeExpr()
					&& !stmt.getInvokeExpr().getMethod().getName().equals("super"))// hasBody
			{
				// ��ȡ���������ж��Ǵ�ͳ���̻߳��ǿ���listen���߳�
				String invokeMethod = stmt.getInvokeExpr().getMethod().toString();
				List<ValueBox> useBoxes = stmt.getInvokeExpr().getUseBoxes();
				Object[] array = useBoxes.toArray();
				if (array.length == 2) {
					if (invokeMethod.contains(ThreadType.RUNNABLE)) {
						// ��ͳ���߳� source thread.start���ڱ��� target �����ࡣ
						addIntoInvokeRightInfoSet(ThreadType.RUNNABLE,
								array);
						return;
					}
					if (invokeMethod.contains(ThreadType.LISTENER)
							|| invokeMethod.contains(ThreadType.TEXT_WATCHER)) {
						// ���� source. ��������� target������
						addIntoInvokeRightInfoSet(ThreadType.LISTENER,
								array);
						return;
					}
					if (invokeMethod.contains(ThreadType.START_ACTIVITY)) {
						// activity source.����activity�����ࡣ target:intent. intent
						// �Ĳ���ΪĿ��class
						addIntoInvokeRightInfoSet(
								ThreadType.START_ACTIVITY, array);
						return;
					}
					if (invokeMethod.contains(ThreadType.START_SERVICE)) {
						// serviceͰactivity
						addIntoInvokeRightInfoSet(ThreadType.START_SERVICE,
								array);
						return;
					}
					// ����asyncTask�ڸ�ֵ��䴦���������
					// TODO ����intent
					return;
				}
				if (array.length == 3) {
					// intent����
					if (invokeMethod.contains(ThreadType.INTENT)) {
						addIntentIntoInvokeRightInfoSet(ThreadType.INTENT,
								array);
						return;
					}
				}
				if (stmt.getInvokeExpr().getMethod().toString().contains("release")
						||stmt.getInvokeExpr().getMethod().toString().contains("close")
						||stmt.getInvokeExpr().getMethod().toString().contains("stop")
						||stmt.getInvokeExpr().getMethod().toString().contains("finish")) {
					if(array.length==1){
						cons.invokeNull.add(new nullNode(cons.OrderCount, ((ValueBox) array[0]).getValue().toString()));
					}
				}

			}
		}


		private void addIntentIntoInvokeRightInfoSet(
				String type, Object[] array) {
			// TODO Auto-generated method stub
			if (cons.invokeInfo.get(type) == null) {
				ArrayList<InvokeRightInfoNode> invokeInfos = new ArrayList<InvokeRightInfoNode>();
				invokeInfos.add(new InvokeRightInfoNode(((ValueBox) array[2])
						.getValue().toString(), cons.OrderCount,
						((ValueBox) array[1]).getValue().toString()));
				cons.invokeInfo.put(type, invokeInfos);
			} else {
				cons.invokeInfo.get(type).add(
						new InvokeRightInfoNode(((ValueBox) array[2]).getValue()
								.toString(), cons.OrderCount, ((ValueBox) array[1])
								.getValue().toString()));
			}
		}
		// ����������
		private void addIntoInvokeRightInfoSet(
				String type, Object[] array) {
			if (cons.invokeInfo.get(type) == null) {
				ArrayList<InvokeRightInfoNode> invokeInfos = new ArrayList<InvokeRightInfoNode>();
				invokeInfos.add(new InvokeRightInfoNode(((ValueBox) array[1])
						.getValue().toString(), cons.OrderCount,
						((ValueBox) array[0]).getValue().toString()));
				cons.invokeInfo.put(type, invokeInfos);
			} else {
				cons.invokeInfo.get(type).add(
						new InvokeRightInfoNode(((ValueBox) array[1]).getValue()
								.toString(), cons.OrderCount, ((ValueBox) array[0])
								.getValue().toString()));
			}
		}

		/**
		 * ��ű��ر����ĸ�ֵ��Ϣ��2��������2��hashMap�洢�ֱ�Ϊcons.assignStmtInfoValue1��2 keyֵΪ��ߵı���
		 * valueΪ�ұߵĲ�������һ��node�洢 OrderCount, op1 , operator,��op2
		 * ���ͷ�Ϊ����,���ر����������Ʊ��ʽ������
		 */
		private boolean markAssignment(Body body, Stmt stmt) {
			// TODO Auto-generated method stub
			if (stmt instanceof AssignStmt) {
				AssignStmt s = (AssignStmt) stmt;
				String leftOp = s.getLeftOp().toString();
				Value right = s.getRightOp();
				if (right instanceof AbstractBinopExpr) {
					String r1 = ((AbstractBinopExpr) right).getOp1().toString();
					String r2 = ((AbstractBinopExpr) right).getOp2().toString();
					String op = right
							.toString()
							.substring(r1.length(),
									right.toString().length() - r2.length()).trim();
						addToAssignValueInfo(cons.assignValueInfo, leftOp,
								AssignStmtType.ABSTRACT_BINOP_EXPR, r1, op, r2);
				} else {
					if (right instanceof Constant) {
						// ��������
						
							addToAssignValueInfo(cons.assignValueInfo, leftOp,
									AssignStmtType.CONSTANT, right.toString(),
									null, null);
							return true;
						
					} else if (right instanceof JimpleLocal) {
						
							addToAssignValueInfo(cons.assignValueInfo, leftOp,
									AssignStmtType.JimpleLocal, right.toString(),
									null, null);
							return true;
					} else {
						
							addToAssignValueInfo(cons.assignValueInfo, leftOp,
									AssignStmtType.Other, right.toString(), null,
									null);
							return true;
					}
				}
			}
			return false;
		}

		private void addToAssignValueInfo(
				HashMap<String, ArrayList<AssignRightInfoNode>> assignValueInfo,
				String leftOp, int type, String firstOp, String operator,
				String secondOp) {
			if (assignValueInfo.get(leftOp) == null) {
				ArrayList<AssignRightInfoNode> rightInfos = new ArrayList<AssignRightInfoNode>();
				rightInfos.add(new AssignRightInfoNode(cons.OrderCount, type,
						firstOp, operator, secondOp));
				assignValueInfo.put(leftOp, rightInfos);
			} else {
				assignValueInfo.get(leftOp).add(
						new AssignRightInfoNode(cons.OrderCount, type, firstOp,
								operator, secondOp));
			}
		}

		// û���Ǿ����ֵ�Ա�ı����������������жϵ�ǰsharedVariable�Ƿ���race
		private boolean markFieldAccess(Body body, Stmt stmt) {
			// TODO Auto-generated method stub
			if (stmt.containsFieldRef()) {
				assert (stmt instanceof AssignStmt) : "Unknown FieldReffing Stmt";
				SootField sootfield = stmt.getFieldRef().getField();
				String sig = sootfield.getSignature();
				boolean write = (((DefinitionStmt) stmt).getLeftOp() instanceof FieldRef);
				boolean read = (((DefinitionStmt) stmt).getRightOp() instanceof FieldRef);

				if (cons.SharedVarRWSet.get(sig) == null) {
					ReadAndWriteNode rwNode = new ReadAndWriteNode();
					if (write) {
						rwNode.writeSet.add(cons.OrderCount);
						if(((DefinitionStmt)stmt).getRightOp().toString().trim().equals(null)){
							rwNode.writeNullSet.add(cons.OrderCount);
						}
					}
					if (read) {
						rwNode.readSet.add(cons.OrderCount);
					}
					cons.SharedVarRWSet.put(sig, rwNode);
					return true;
				} else {
					if (write) {
						cons.SharedVarRWSet.get(sig).writeSet.add(cons.OrderCount);
						if(((DefinitionStmt)stmt).getRightOp().toString().trim().equals(null)){
							cons.SharedVarRWSet.get(sig).writeNullSet.add(cons.OrderCount);
						}
					}
					if (read) {
						cons.SharedVarRWSet.get(sig).readSet.add(cons.OrderCount);
					}
				}
				return true;
			}
			return false;
		}

		// private boolean markWaitAndNotify(Body body, Stmt stmt) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		/**
		 * ʶ�������м�¼���ֱ�Ϊ������map two arraylist one is lock another is unlock.
		 */
		private boolean markLock(Body body, Stmt stmt) {
			// TODO Auto-generated method stub
			if (stmt instanceof EnterMonitorStmt) {
				if (cons.lockInfo.get(getArgFromStmt(stmt)) == null) {
					LockInfoNode lockInfoNode = new LockInfoNode();
					lockInfoNode.lockOrder.add(cons.OrderCount);
					cons.lockInfo.put(getArgFromStmt(stmt), lockInfoNode);
				} else {
					cons.lockInfo.get(getArgFromStmt(stmt)).lockOrder
							.add(cons.OrderCount);
				}
				return true;
			}
			if (stmt instanceof ExitMonitorStmt) {
				if (cons.lockInfo.get(getArgFromStmt(stmt)) == null) {
					LockInfoNode lockInfoNode = new LockInfoNode();
					lockInfoNode.unLockOrder.add(cons.OrderCount);
					cons.lockInfo.put(getArgFromStmt(stmt), lockInfoNode);
				} else {
					cons.lockInfo.get(getArgFromStmt(stmt)).unLockOrder
							.add(cons.OrderCount);
				}
				return true;
			}
			return false;
		}

		/**
		 * ��ȡ����е���ز���
		 */
		private String getArgFromStmt(Stmt stmt) {
			// TODO Auto-generated method stub
			return stmt.getUseBoxes().iterator().next().getValue().toString();
		}

		private boolean markStart(Body body, Stmt stmt) {
			// TODO Auto-generated method stub
			if (stmt.containsInvokeExpr()) {
				if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
					if (stmt.getInvokeExpr().getMethod() == start) {
						System.out.println("start_Method:   " + stmt);
						cons.startInfor.put(cons.OrderCount, stmt.getInvokeExpr()
								.getUseBoxes().iterator().next().getValue()
								.toString());
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * ʶ��join��䣬��¼��Ϣ ��Ϣ1�� ˭���õ� ��Ϣ2������������
		 */
		private boolean markJoin(Body body, Stmt stmt) {
			// TODO Auto-generated method stub
			if (stmt.containsInvokeExpr()) {
				if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
					if (stmt.getInvokeExpr().getMethod() == join) {
						System.out.println("join_Method:   " + stmt);
						cons.joinInfor.put(cons.OrderCount, stmt.getInvokeExpr()
								.getUseBoxes().iterator().next().getValue()
								.toString());
						return true;
					}
				}
			}
			return false;
		}
}