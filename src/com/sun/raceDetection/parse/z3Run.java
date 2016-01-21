package com.sun.raceDetection.parse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import com.sun.raceDetection.moel.Information;

public class z3Run {
//	public static int ACTIVITY_LISTENER=1;
//	public static int ACTIVITY_SERVICE_OR_THREAD=2;
//	public static int ACTIVITY_ASYNCTASK=3;
//	public static int DOUBLE_SERVICE_OR_THREAD=4;
//	public static int SERVICE_OR_THREAD_ASYNCTASK=5;
//	public static int SERVICE_OR_THREAD_LISTENER=6;
//	public static int LISTENER_LISTENER=7;
//	public static int DOUBLE_ASYNCTASK = 8;
//	public static int ASYNCTASK_LISTENER=9;
	
	public static void startRun(File file,Integer type)
	{
		// String cmd = "z3 "+file.getAbsolutePath();  
       //  String cmd="z3 C:\\z3-test-demo.stm2";
		//String cmd="ping www.baidu.com";
		//String cmd="z3 "+file.getAbsolutePath();
		File z3File=new File("z3-4.3.2-x64-win\\bin\\z3.exe");
		String cmd=z3File.getAbsolutePath()+" -smt2 \""+file.getAbsolutePath()+"\"";
		// String cmd=file.getAbsolutePath()+" -stm2 "+file2.getAbsolutePath();
		//String cmd="ipconfig";
		 //System.out.println(cmd);
	        //linux  
//	      String cmd = "./fork_wait";  
//	      String cmd = "ls -l";  
//	      String[] cmd=new String[3];  
//	      cmd[0]="/bin/sh";  
//	      cmd[1]="-c";  
//	      cmd[2]="ls -l ./";  
	        Runtime run = Runtime.getRuntime();//�����뵱ǰ Java Ӧ�ó�����ص�����ʱ����  
	        try {  
	            Process p = run.exec(cmd);// ������һ��������ִ������  
	            BufferedInputStream in = new BufferedInputStream(p.getInputStream());  
	            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));  
	            String lineStr;  
	            int countForEachFile=0;
	            while ((lineStr = inBr.readLine()) != null)  
	                //�������ִ�к��ڿ���̨�������Ϣ  
	            	if(lineStr.equals("sat")){
	            		Information.resultCount++;
	            		countForEachFile++;
	            		for (int i = 0; i < Information.resultTypeCount.length; i++) {
							if(type==(i+1)){
								Information.resultTypeCount[i]++;
							}
						}
	            	}
	                //System.out.println(lineStr);// ��ӡ�����Ϣ  
	            //��������Ƿ�ִ��ʧ�ܡ�  
//	            if (p.waitFor() != 0) {  
//	                if (p.exitValue() == 1)//p.exitValue()==0��ʾ����������1������������  
//	                    System.err.println("����ִ��ʧ��!");  
//	            }  
	            Information.eachFileDataRace.add(countForEachFile);
	            inBr.close();  
	            in.close();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	}
}