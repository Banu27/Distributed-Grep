package edu.uiuc.cs425;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Queue;

public class FileProcessing {
	
	Queue m_oLogFiles;
    

	int GetProgress() {
		
		System.out.println("Returning progress");
		return 0;
	}
	
	AbstractMap.SimpleEntry<Integer, Integer> BlocksLeft() {
	
		System.out.println("Returning start and end");
		return new AbstractMap.SimpleEntry<Integer, Integer>(0,0);		
		
	}
	
	void Initialize() {
		
		m_oLogFiles = new LinkedList(); //Constructor needed??
		
		System.out.println("Initializing");	
		//Create queue of files 
		
	}
	
	void StartSearching() {
		
		System.out.println("Start searching file/files");
		
		//Dequeue files and search
		//Call to done processing function if queue is empty
	}
	 
	
}
