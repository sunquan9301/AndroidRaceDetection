package com.sun.raceDetection.utils;

public class TestString {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String str="   class \"com/example/testactlis/TwoActivity\"";
		System.out.println(str);
		String replace = str.replace('/', '.');
		String substring = replace.trim().substring(7, replace.trim().length()-1);
		System.out.println(substring);
	}

}
