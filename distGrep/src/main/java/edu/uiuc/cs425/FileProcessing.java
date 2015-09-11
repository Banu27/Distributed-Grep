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
		m_oLogFiles.add("./logs/log1.txt"); //Add multiple files		
		m_sVMName = VMName;
	}
	
	void StartSearching(String pattern) {
		
		System.out.println("Start searching file/files");
		while(!m_oLogFiles.isEmpty()) {
			String fileName = m_oLogFiles.remove();
			String username = "/home/muthkmr2";
			/*String sshCommand = "ssh";
			String command = "grep";
			//sshCommand , m_sVMName , 
			//Commons.SystemCommand(new String [] { "ls"," > newfile.txt"});
			Commons.SystemCommand(new String[] { "bash","-c",command +" as $HOME/logs/log1.txt > $HOME/logs/grepResult.out"});
			//> "+username+"/logs/grepResult.out & " });			
			System.out.println("grep call over"); //Why is this showing up on my command line??
*/		
			String searchResult = findMatches(fileName,pattern);
		}
		
	}
	
	public String findMatches(String fileName, String pattern) {
	    //Pattern and Matcher are used here, not String.matches(regexp),
	    //since String.matches(regexp) would repeatedly compile the same
	    //regular expression
	    Pattern regexp = Pattern.compile(pattern);
	    Matcher matcher = regexp.matcher("");
	    String output = "";
	    Path path = Paths.get(fileName);
	    try {
	    		BufferedReader reader = Files.newBufferedReader(path, m_oEncoding);
	    		LineNumberReader lineReader = new LineNumberReader(reader);
	    		String line = null;
	    		while ((line = lineReader.readLine()) != null) {
	    			matcher.reset(line); //reset the input
	    			if (matcher.find()) {
	    				output = output + line;
	    			}
	    		}
	    } catch (IOException ex){
	      ex.printStackTrace();
	    }
	    return output;
	  }

	 
	
}
