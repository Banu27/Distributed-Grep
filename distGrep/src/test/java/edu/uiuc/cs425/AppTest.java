package edu.uiuc.cs425;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.junit.BeforeClass;

public class AppTest {

	static int nInFreqCount;
	static int nMidFreqCount;
	static int nFreqCount;

	static final int SEARCH_STRNG_INDEX = 0;

	public static boolean SetupServices() {

	}

	public static boolean SetupLogs() {
		final String[] WORD_LIST_INFREQUENT = { "borrow", "possession", "shiners", "trample", "toolshed", "emotionless",
				"gum", "educate", "bewildered", "obese", "fancy", "shiners", "languid", "minister", "seashore",
				"disagree", "bells", "drink", "upbeat", "nasty", "consist", "atrocity", "eastern", "dent", "dreamless",
				"mortal", "element", "thief", "obsession", "fashionable", "alignment", "believable", "fake",
				"accomplice", "accidental", "convertible", "atrocity", "guide", "hangover", "became" };

		final String[] WORD_LIST_MID_FREQUENT = { "round", "fetish", "historic", "nice", "think", "astounding",
				"groundwave", "position", "approximation", "infinite", "desolate", "bludgeon", "hunter", "hundred",
				"pink", "annoying", "prophetic", "entity", "pastoral", "degrading" };

		final String[] WORD_LIST_FREQUENT = { "round", "fetish", "historic", "nice", "think" };

		final int nFiles = Commons.NUMBER_OF_VMS;
		final int nLinesPerFile = 1000;
		final int nWordsPerLine = 3;

		nInFreqCount = nMidFreqCount = nFreqCount = 0;

		Random rand = new Random();
		for (int file = 0; file < nFiles; ++file) {
			PrintWriter writer;
			try {
				writer = new PrintWriter(String.valueOf(file) + ".txt", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			for (int line = 0; line < nLinesPerFile; ++line) {
				int infreq = rand.nextInt(WORD_LIST_INFREQUENT.length);
				if (infreq == SEARCH_STRNG_INDEX)
					nInFreqCount++; // count number of time index 0 string is
									// generated
				int midFreq = rand.nextInt(WORD_LIST_MID_FREQUENT.length);
				if (midFreq == SEARCH_STRNG_INDEX)
					nMidFreqCount++; // count number of time index 0 string is
										// generated
				int freq = rand.nextInt(WORD_LIST_FREQUENT.length);
				if (freq == SEARCH_STRNG_INDEX)
					nFreqCount++; // count number of time index 0 string is
									// generated
				String sLine = WORD_LIST_INFREQUENT[infreq] + " " + WORD_LIST_MID_FREQUENT[midFreq] + " "
						+ WORD_LIST_FREQUENT[freq];
				writer.println(sLine);
			}
			writer.close();
		}

		// send files to remote nodes
		for (int i = 0; i < Commons.NUMBER_OF_VMS; i++) {
			Commons.SystemCommand(
					new String[] { "scp", String.valueOf(i) + ".txt", Commons.VM_NAMES[i] + ":$HOME/test/" });
		}
		
		return true;
	}

	@BeforeClass
	public static void SetupTest() {
		if (!SetupLogs()) {
			System.out.println(
					"Setting up synthetic logs failed. Tests will exit. Restart after " + "resolving the problem.");
			System.exit(1);
		}

		if (!SetupServices()) {
			System.out.println("Setting up the services and connections failed. Tests will exit. Restart after "
					+ "resolving the problem.");
			System.exit(1);
		}

	}

	String message = "Hello World";

	@Test
	public void testPrintMessage() {
		assertEquals(message, messageUtil.printMessage());
	}
}