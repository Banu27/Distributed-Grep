package edu.uiuc.cs425;
import java.io.BufferedReader;
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
	
	Queue<String> 			m_oLogFiles;
    private String 			m_sVMName;
    final static Charset 	m_oEncoding= StandardCharsets.UTF_8;
	
    
	int GetProgress() {
		System.out.println("Returning progress");
		return 0;
	}
	
	AbstractMap.SimpleEntry<Integer, Integer> BlocksLeft() {
	
		System.out.println("Returning start and end");
		return new AbstractMap.SimpleEntry<Integer, Integer>(0,0);		
		
	}
	
	void Initialize(String VMName) {
		
		m_oLogFiles = new LinkedList<String>(); //Constructor needed??
		m_oLogFiles.add("log1.txt"); //Add multiple files		
		m_sVMName = VMName;
	}
	
	String StartSearching(String pattern) {
		
		System.out.println("Start searching file/files");
		String searchResult = new String();
		while(!m_oLogFiles.isEmpty()) {
			String fileName = m_oLogFiles.remove();
			String username = "/home/muthkmr2";
			searchResult = findMatches(fileName,pattern);			
		}
		return searchResult;
		
	}
	
	String findMatches(String fileName, String pattern) {
	    //Pattern and Matcher are used here, not String.matches(regexp),
	    //since String.matches(regexp) would repeatedly compile the same
	    //regular expression
	    Pattern regexp = Pattern.compile(pattern);
	    Matcher matcher = regexp.matcher("");
	    String output = "";
	    Path path = Paths.get("/home/muthkmr2/logs",fileName);
	    try {
	    		BufferedReader reader = Files.newBufferedReader(path, m_oEncoding);
	    		LineNumberReader lineReader = new LineNumberReader(reader);
	    		String line = null;
	    		while ((line = lineReader.readLine()) != null) {
	    			matcher.reset(line); //reset the input
	    			if (matcher.find()) {
	    				output = output + String.valueOf(lineReader.getLineNumber()) +" "+ line + "\n";
	    			}
	    		}
	    } catch (IOException ex){
	      ex.printStackTrace();
	    }
	    return output;
	  }

	 
	
}
