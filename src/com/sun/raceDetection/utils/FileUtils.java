package com.sun.raceDetection.utils;

import java.io.File;
import java.io.IOException;

import com.sun.raceDetection.moel.Information;
import com.sun.raceDetection.parse.Config;
import com.sun.raceDetection.parse.z3Run;

public class FileUtils {
	public static final String ROOT_PATH="Constraints";
	public static void creatConstraintsDirForApp() {
		// TODO Auto-generated method stub
		File file =new File(Config.ConstraintsPath);
		if(file.exists()){
			file.delete();
		}
		if(!file.exists()){
			file.mkdirs();
		}
		
	}
	public static void calRunResult() {
		// TODO Auto-generated method stub
		File file=new File(Config.ConstraintsPath);
		File[] listFiles = file.listFiles();
		System.out.println("listFileSize"+listFiles.length);
		System.out.println("fileTypeSize"+Information.fileType.size());
		for (int i = 0; i < listFiles.length; i++) {
			//System.out.println(i);
			z3Run.startRun(listFiles[i],Information.fileType.get(i));
		}
	}
}
