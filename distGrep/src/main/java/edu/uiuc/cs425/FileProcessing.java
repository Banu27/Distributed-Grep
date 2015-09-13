package edu.uiuc.cs425;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileProcessing {
	
    private String 			m_sVMName;
    final static Charset 	m_oEncoding= StandardCharsets.UTF_8;
	private String 			m_sSearchResult;
	private int				m_nCount;
	
	public FileProcessing()
	{
		m_nCount = 0;
	}
    
	public void Reset()
	{
		m_nCount = 0;
		m_sSearchResult = "";
	}
	
	int GetProgress() {
		System.out.println("Returning progress");
		return 0;
	}
	
	AbstractMap.SimpleEntry<Integer, Integer> BlocksLeft() {
	
		System.out.println("Returning start and end");
		return new AbstractMap.SimpleEntry<Integer, Integer>(0,0);		
		
	}
	
	void Initialize(String VMName) {
		
		m_sVMName = VMName;
	}
	
	int StartSearching(String pattern, String dirPath) {
		File directory = new File(dirPath);
	    System.out.println("Dir path: " + dirPath);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
				System.out.println("Start searching " + file.getAbsolutePath());
				m_sSearchResult += findMatches(file.getAbsolutePath(),pattern);
	        }
	    }
        return Commons.SUCCESS;
	}
	
	String GetMatchedLines()
	{
		return m_sSearchResult;
	}
	
	int GetLineCount()
	{
		return m_nCount;
	}
	
	
	String findMatches(String filePath, String pattern) {
	    //Pattern and Matcher are used here, not String.matches(regexp),
	    //since String.matches(regexp) would repeatedly compile the same
	    //regular expression
	    Pattern regexp = Pattern.compile(pattern);
	    Matcher m = null;
	    String output = "";
	    Path path = Paths.get(filePath);
	    try {
	    		BufferedReader reader = Files.newBufferedReader(path, m_oEncoding);
	    		LineNumberReader lineReader = new LineNumberReader(reader);
	    		String line = null;
	    		while ((line = lineReader.readLine()) != null) {
	    			m = regexp.matcher(line); //reset the input
	    			if (m.find()) {
	    				m_nCount++;
	    				if(m_nCount%100 == 0)
					{
						System.out.println("Progress here : " + String.valueOf(m_nCount)); 
					}
					output = output + String.valueOf(lineReader.getLineNumber()) +": "+ line + "\n";
	    			}
	    		}
	    } catch (IOException ex){
	      ex.printStackTrace();
	    }
	    return output;
	  }
}
