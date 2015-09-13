package edu.uiuc.cs425;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AppTest {

	static int nInFreqCount;
	static int nMidFreqCount;
	static int nFreqCount;
	static final String sDirPath = "$HOME/test";
	static final int SEARCH_STRNG_INDEX = 0;

	static Controller m_oController = null;
	
	static final String[] WORD_LIST_INFREQUENT = { "fashionable", "possession", "shiners", "trample", "toolshed", "emotionless",
			"gum", "educate", "bewildered", "obese", "fancy", "shiners", "languid", "minister", "seashore",
			"disagree", "bells", "drink", "upbeat", "nasty", "consist", "atrocity", "eastern", "dent", "dreamless",
			"mortal", "element", "thief", "obsession", "borrow", "alignment", "believable", "fake",
			"accomplice", "accidental", "convertible", "atrocity", "guide", "hangover", "became" };

	static final String[] WORD_LIST_MID_FREQUENT = { "groundwave", "fetish", "historic", "nice", "think", "astounding",
			"round", "position", "approximation", "infinite", "desolate", "bludgeon", "hunter", "hundred",
			"pink", "annoying", "prophetic", "entity", "pastoral", "degrading" };

	static final String[] WORD_LIST_FREQUENT = { "arithmetic", "disgusting", "imported", "wrench", "invention" };
	
	public static boolean SetupServices() {
		m_oController = new Controller(Commons.MASTER, sDirPath);
		if(m_oController.StartServer() == Commons.FAILURE)
        {
        	System.out.println("Failed to bring the distributed grep service up. Shutting down ...");
        	System.exit(Commons.FAILURE);
        }
        
        if(Commons.FAILURE == m_oController.SetupConnections())
        {
        	System.out.println("Connection amoung VMs failed, Shutting down ...");
        	System.exit(Commons.FAILURE);
        }
		return true;
	}

	public static boolean SetupLogs() {
		

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


	@Test
	public void testFreq() throws InterruptedException {
		m_oController.CallStartProcessing(WORD_LIST_FREQUENT[SEARCH_STRNG_INDEX]);
		while(m_oController.isRunning())
		{
			Thread.sleep(3000);
		}
		
		assertEquals(nFreqCount,m_oController.GetMatchCount());
	}
	
	@Test
	public void testMidFreq() throws InterruptedException {
		m_oController.CallStartProcessing(WORD_LIST_MID_FREQUENT[SEARCH_STRNG_INDEX]);
		while(m_oController.isRunning())
		{
			Thread.sleep(3000);
		}
		
		assertEquals(nMidFreqCount,m_oController.GetMatchCount());
	}
	
	@Test
	public void testInFreq() throws InterruptedException {
		m_oController.CallStartProcessing(WORD_LIST_INFREQUENT[SEARCH_STRNG_INDEX]);
		while(m_oController.isRunning())
		{
			Thread.sleep(3000);
		}
		
		assertEquals(nInFreqCount,m_oController.GetMatchCount());
	}
	
	@Test
	public void testRegEx1() throws InterruptedException {
		m_oController.CallStartProcessing("*ithme*");
		while(m_oController.isRunning())
		{
			Thread.sleep(3000);
		}
		
		assertEquals(nFreqCount,m_oController.GetMatchCount());
	}
	
	@Test
	public void testRegEx2() throws InterruptedException {
		m_oController.CallStartProcessing("groun.wave");
		while(m_oController.isRunning())
		{
			Thread.sleep(3000);
		}
		
		assertEquals(nMidFreqCount,m_oController.GetMatchCount());
	}
	
	@Test
	public void testRegEx3() throws InterruptedException {
		m_oController.CallStartProcessing("^fas");
		while(m_oController.isRunning())
		{
			Thread.sleep(3000);
		}
		
		assertEquals(nInFreqCount,m_oController.GetMatchCount());
	}
	
	@AfterClass
	public void CleanUp()
	{
		for (int file = 0; file < Commons.NUMBER_OF_VMS; ++file) {
			boolean success = (new File(String.valueOf(file) + ".txt")).delete();
	}
	
}