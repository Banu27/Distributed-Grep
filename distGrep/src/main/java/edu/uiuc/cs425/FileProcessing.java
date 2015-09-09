package edu.uiuc.cs425;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Queue;

public class FileProcessing {
	
	Queue<String> m_oLogFiles;
    private String m_sVMName;
	
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
	
	void StartSearching() {
		
		System.out.println("Start searching file/files");
		while(!m_oLogFiles.isEmpty()) {
			String fileName = m_oLogFiles.remove();
			String sshCommand = "ssh";
			String command = "grep Here ./logs/" + fileName;
			Commons.SystemCommand(new String[] { sshCommand , m_sVMName , command , " > $HOME/logs/grepResult.out & " });			
			System.out.println("grep call over"); //Why is this showing up on my command line??
		}
		
	}
	 
	
}
