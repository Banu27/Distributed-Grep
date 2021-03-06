package edu.uiuc.cs425;

import java.io.Console;
import java.util.Scanner;
import java.util.Vector;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

public class Controller implements Runnable {
	private CommClient [] 			m_oClients;
	private  CommClient 			m_oMasterProxy;
	private  CommServer 			m_oServer;
	private int 					m_nNodeID;
	private String [] 				m_sGrepOutputData;
	private final Object 			m_oLock;
	private int 					m_nDoneProcessingNumber;	
	private Scanner 				m_oUser_input; 
	private boolean					m_bIsRunning;
	private int []					m_nPatternMatchCount;
	private String					m_sSearchDir;
	private long					m_lStartTime;
	private	boolean					m_bAskOnce;
	private Thread 					m_oHealthMonitorThread;
	Vector<Integer> 				m_oFailedNodes;
	
	Controller(int nodeID, String searchDir)	{
		m_nNodeID = nodeID;
		m_sGrepOutputData = new String[Commons.NUMBER_OF_VMS];
		m_nDoneProcessingNumber = 0;
		m_oLock = new Object();
		m_oUser_input = new Scanner(System.in);		
		m_bIsRunning = false;
		m_nPatternMatchCount = new int[Commons.NUMBER_OF_VMS];
		m_sSearchDir = searchDir;
		m_lStartTime = 0;
		m_bAskOnce = false;
		m_oFailedNodes = new Vector<Integer>();
	}
	
	public void setAskOnce()
	{
		m_bAskOnce = true;
	}
	
	public int GetMatchCount()
	{
		return 0;//m_nPatternMatchCount;
	}
	
	
	public void pingMachines() {
		Thread ping = new Thread();
		ping.start();
	}
	
	public void RunExecutables() {
		
		String sshCommand = "ssh";
		
		//command is the executable run with &
		String command = "java -cp $HOME/distGrepFinal.jar edu.uiuc.cs425.App";
		String killCommand = "pkill java";
		
		for (int i=1; i<Commons.NUMBER_OF_VMS; i++) 
			Commons.SystemCommand(new String[] { sshCommand , Commons.VM_NAMES[i] , killCommand }); 
			
		//Start all VMS
		for (int i=1; i<Commons.NUMBER_OF_VMS; i++) 
			Commons.SystemCommand(new String[] { sshCommand , Commons.VM_NAMES[i] , command + " " +  String.valueOf(i) + " " + m_sSearchDir  + " > $HOME/grep.out 2>&1 & " }); 
				
		
	}
	
	public int StartServer() {
	
		 m_oServer = new CommServer(Commons.SERVICE_PORT, m_nNodeID); 
	    
		 if( m_oServer.Initialize() == Commons.FAILURE)
		 {
			 System.out.println("Initalization of the Thrift service failed");
			 return Commons.FAILURE;
		 }
	     
		 m_oServer.StartService(); //New thread for master
	     return Commons.SUCCESS;
	}
	
	public int CreateClients() {
		
		m_oClients = new CommClient[Commons.NUMBER_OF_VMS];
		for(int i=0; i<Commons.NUMBER_OF_VMS; i++) {
			m_oClients[i] = new CommClient();
			if( Commons.FAILURE == m_oClients[i].Initialize(Commons.VM_NAMES[i], Commons.SERVICE_PORT))
			{
				System.out.println("Failed to initialize proxy for " + Commons.VM_NAMES[i]);
				return Commons.FAILURE;
			}
		}
		return Commons.SUCCESS;
		
	}
	
	public int CallStartProcessing(String pattern) {
		
		m_nDoneProcessingNumber = 0;
		m_bIsRunning = true;
		for(int i=0; i<Commons.NUMBER_OF_VMS; i ++)
		{
			m_nPatternMatchCount[i] = 0;
		}
		m_lStartTime = System.currentTimeMillis();
		for( int i=0; i<Commons.NUMBER_OF_VMS; i++) {
			if(m_oFailedNodes.contains(i)) continue;
			try
			{
				m_oClients[i].startProcessing(pattern);
			} catch (TException e)
			{
				e.printStackTrace();
				m_bIsRunning = false;
				return Commons.FAILURE;
			}
		}
		return Commons.SUCCESS;
	}
	
	public void WaitForServiceToStop() {
	
		m_oServer.WaitForServiceToStop();
	}
	
	
	public void ExitApp()
	{
		String sshCommand = "ssh";
		String killCommand = "pkill java";
		for (int i=1; i<Commons.NUMBER_OF_VMS; i++) 
			Commons.SystemCommand(new String[] { sshCommand , Commons.VM_NAMES[i] , killCommand }); 
		System.exit(Commons.FAILURE);
		return;
	}
	
	public String userOption() {		

		String input = "";
		System.out.println("Enter 's' for new search or 'q' for quitting");
		input = m_oUser_input.nextLine(); 
		String pattern = null;
		if(input.equals("s"))
		{
			System.out.println("Enter the pattern/string to search for : ");
			pattern = m_oUser_input.nextLine();
		} else if (input.equals("q"))
		{
			ExitApp();
		}
		return pattern;
	}
	
	public void setGrepOutputData (int nodeID, String data, int count ) {
		m_sGrepOutputData[nodeID] = data;
		m_nPatternMatchCount[nodeID] = count;
		synchronized (m_oLock) {
			m_nDoneProcessingNumber = m_nDoneProcessingNumber + 1;
			//m_nPatternMatchCount+=count;
		}
		/*if(m_nDoneProcessingNumber == Commons.aliveNumber) {
			m_bIsRunning = false;
			printGrepOutput();
			System.out.println("Query time: " + String.valueOf(System.currentTimeMillis() - m_lStartTime) + "ms");
			if( !m_bAskOnce && Commons.FAILURE == startGrep())
			{
				System.out.println("Failed to successfully start Grepping. Shutting down ...");
				ExitApp();
			}
		}*/
		
	}
	
	public void printGrepOutput() {
		
		System.out.println("Printing data : ");
		for(int i=0; i<Commons.NUMBER_OF_VMS; i++) {
			if(m_oFailedNodes.contains(i)) continue;
			System.out.println("Node number : "+String.valueOf(i));
			System.out.println(m_sGrepOutputData[i]);
		}
		int finalLineCount = 0;
		for(int i=0; i< Commons.NUMBER_OF_VMS; i++)
		{
			finalLineCount = finalLineCount + m_nPatternMatchCount[i];
		}
		System.out.println("Total Lines: "+ String.valueOf(finalLineCount));
		
	}	
	
	//Closes client transport
	public void CloseClients() {
		for(int i=0; i<Commons.NUMBER_OF_VMS; i++) 
			m_oClients[i].Close(); 
	}

	
	//Decides which functions to call on appropriate node
	public int SetupConnections() {
		
        if(m_nNodeID == Commons.MASTER) {
        	
        	//Run the executable on all the VMS 
        	RunExecutables();
        	
        	try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        	//Create all VM clients on the master
        	if(Commons.FAILURE == CreateClients())
        	{
        		System.out.println("Failed to connect to other VMs");
        		return Commons.FAILURE;
        	}
        	
        	//Master proxy for Master
        	m_oMasterProxy = m_oClients[0];
        	
        }
        else {
        	
        	//Create Master proxy on all VMs
        	m_oMasterProxy = new CommClient();
        	if(Commons.FAILURE ==  m_oMasterProxy.Initialize(Commons.VM_NAMES[Commons.MASTER],Commons.SERVICE_PORT))
        	{
        		System.out.println("Failed to connect to Master");
        		return Commons.FAILURE;
        	}
        
        } 
     
        m_oServer.setMasterProxy(m_oMasterProxy);
        m_oServer.setControllerProxy(this);
        m_oServer.setSearchDir(m_sSearchDir);
        if(m_nNodeID == Commons.MASTER)
        {	
        	m_oHealthMonitorThread = new Thread(this);
        	m_oHealthMonitorThread.start();
        }
        
        return Commons.SUCCESS;
	}

	
	public boolean isRunning()
	{
		return m_bIsRunning;
	}
	
	
	public int startGrep()
	{
		if(m_nNodeID == Commons.MASTER)
	    {	
	    	String pattern = userOption();
	    	if( Commons.FAILURE == CallStartProcessing(pattern))
	    	{
	    		return Commons.FAILURE;
	    	}
	    }
		return Commons.SUCCESS;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		//Diamonds are allowed in 7+
		while(true) {
			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(m_nDoneProcessingNumber == Commons.aliveNumber) {
				m_bIsRunning = false;
				printGrepOutput();
				System.out.println("Query time: " + String.valueOf(System.currentTimeMillis() - m_lStartTime) + "ms");
				if( !m_bAskOnce && Commons.FAILURE == startGrep())
				{
					System.out.println("Failed to successfully start Grepping. Shutting down ...");
					ExitApp();
				}
			}
			for(int i=1; i< Commons.NUMBER_OF_VMS; i++) {
				if(!m_oFailedNodes.contains(i))
				{
					try {
						if(!m_oClients[i].isAlive())
						{
							System.err.println("Failed to receive heartbeat message from " + Commons.VM_NAMES[i]
									+ " .Considered dead.");
							m_oFailedNodes.add(i);
							Commons.aliveNumber--;
						}
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
						
					
				}
				
			}			
		}
	}
}
