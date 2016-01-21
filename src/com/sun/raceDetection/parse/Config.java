/**
 *------------------------------------------------------------------
 * Project Name: AndroidRaceDetection
 * Day and Time: 2016年1月1日 下午2:08:04
 * @author Sun Quan
 * @version 1.0
 * com.sun.soot.example Config.java
 * Description: config file including exclusion package name, and relevant soot config
 * -----------------------------------------------------------------
 */
package com.sun.raceDetection.parse;

import java.util.Collections;
import java.util.LinkedList;

import com.sun.raceDetection.utils.FileUtils;

import soot.Scene;
import soot.SootClass;
import soot.options.Options;

public class Config {
	private static Config config = null;
	private static boolean SOOT_INITIALIZED=false;
	private final static String androidJAR="./lib/android.jar";
	//apk path
//private final static String appApk="src/com/sun/raceDetection/benchmark/OI File Manager_v2.0.5_apkpure.com.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/Tomdroid.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/connectbot1.8.6 .apk";
//private final static String appApk="src/com/sun/raceDetection/benchmark/facebook 59.0.0.0.255.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/Aard Dictionary_v1.6.11_apkpure.com.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/Browser_v1.0_apkpure.com.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/com.fsck.k9.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/com.adobe.reader.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/FedEx Mobile_v3.5.2_apkpure.com.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/netflix.4.1.0 build 5588.apk";
	//private final static String appApk="src/com/sun/raceDetection/benchmark/music.apk";
//	 private final static String appApk="src/com/sun/raceDetection/benchmark/Flipkart_v4.3.1_apkpure.com.apk";
//	private final static String appApk="src/com/sun/raceDetection/benchmark/com.pandora.android-6.7 .apk";
	private final static String appApk="src/com/sun/raceDetection/benchmark/instagram.apk";
	
	private static LinkedList<String> excludeList = new  LinkedList<String>();
	//解析的app名称
	public static String appName;
	//约束文件输出路径
	public static String ConstraintsPath;
	private Config(){
		
	}
	//对外提供的单例接口
	public static Config getInstance(){
		if(config==null){
			synchronized (Config.class) {
				if(config ==null){
					config = new Config();
					return config;
				}
			}
		}
		return config;
	}
	//init excludeList
	static{
		excludeList.add("android.support");
		excludeList.add("android.support.v4.app");
		excludeList.add("com.google.android");
		//excludeList.add("com.google");
		excludeList.add("org.apache");
		excludeList.add("com.trilead.ssh2");
		excludeList.add("jsr330_inject");
		excludeList.add("jsr305_annotations");
		excludeList.add("armeabi");
		excludeList.add("assets");
		excludeList.add("js");
		excludeList.add("net.tsz.afinal");
		excludeList.add("com.jcraft.jzlib");
		excludeList.add("net.sourceforge.jsocks");
		appName = appApk.substring(appApk.lastIndexOf("/")+1);
		ConstraintsPath="Constraints\\"+appName;
	}
	
	//skip the packages, which was third-party
	public static boolean skipPackage(String packageName)
    {
    	for(String name : excludeList)
    	if(packageName.startsWith(name))
    		return true;

    		return false;
    }
	//init soot
	public static void initialiseSoot()
	{
		if(SOOT_INITIALIZED)
			return;
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_validate(true);
		
		Options.v().set_output_format(Options.output_format_jimple);
	//Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_process_dir(Collections.singletonList(appApk));
		Options.v().set_force_android_jar(androidJAR);
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_soot_classpath(androidJAR);
		Scene.v().loadNecessaryClasses();
		 Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
	      Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
	      Scene.v().addBasicClass("java.util.Comparator",SootClass.SIGNATURES);
	      Scene.v().addBasicClass("java.lang.ThreadGroup",SootClass.SIGNATURES);
	     // System.out.println("SootClass.SIGNATURES:"+SootClass.SIGNATURES);
		SOOT_INITIALIZED=true;
		//初始化约束文件输出路径
		FileUtils.creatConstraintsDirForApp();
	}
}
