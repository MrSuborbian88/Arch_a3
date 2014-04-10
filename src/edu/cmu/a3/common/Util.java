package edu.cmu.a3.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Util {
	public static String createRandomId(String prefix, int length) {
		String randomness = "";
		if(length <=0)
			length = 4;
		do {
			randomness += UUID.randomUUID().toString();
		} while (randomness.length() < length);
		if(randomness.length() > length)
			randomness = randomness.substring(0,length);
		return prefix + randomness;

	}

	public static void getNextEnter() throws IOException {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while (!(line = br.readLine()).trim().equals(""));
	}

}
