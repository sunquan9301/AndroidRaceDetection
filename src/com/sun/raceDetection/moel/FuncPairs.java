package com.sun.raceDetection.moel;


public class FuncPairs {
	public String sharedVariable = null;
	public String methodOne = null;
	public String methodTwo = null;
	public int sign = -1;
	public FuncPairs(String sharedVariable, String methodOne,
			String methodTwo, int sINGLE_ASYNC) {
		super();
		this.sharedVariable = sharedVariable;
		this.methodOne = methodOne;
		this.methodTwo = methodTwo;
		this.sign = sINGLE_ASYNC;
	}

	public String toFileName(){
		return sharedVariable+"_"+methodOne+"_"+methodTwo+"_"+String.valueOf(sign);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Var:	]"+sharedVariable+"\n"
				+"[M1:		]"+methodOne+"\n"
				+"[M2:	]"+methodTwo+"\n"
				+"[sign:	]"+sign;
	}
	
}
