package edu.uiuc.cs425;

import java.io.Console;
import java.util.Scanner;

import org.apache.thrift.TException;

public class Controller {
	private CommClient [] 			m_oClients;
	private  CommClient 			m_oMasterProxy;
	private  CommServer 			m_oServer;
	private int 					m_nNodeID;
	private String [] 				m_sGrepOutputData;
	private final Object 			m_oLock;
	private int 					m_nDoneProcessingNumber;	
	
	Controller(int nodeID)	{
		m_nNodeID = nodeID;
		m_sGrepOutputData = new String[Commons.NUMBER_OF_VMS];
		m_nDoneProcessingNumber = 0;
		m_oLock = new Object();
		
	}
	
	public void RunExecutables() {
		
		String sshCommand = "ssh";
		
		//command is the executable run with &
		String command = "java -cp $HOME/distGrepFinal.jar edu.uiuc.cs425.App";
		String killCommand = "pkill java";
		
		//Start all VMS
		for (int i=1; i<Commons.NUMBER_OF_VMS; i++) {
			//Commons.SystemCommand(new String [] { sshCommand, Commons.VM_NAMES[i], killCommand} );
			Commons.SystemCommand(new String[] { sshCommand , Commons.VM_NAMES[i] , command + " " +  String.valueOf(i) + " > $HOME/grep.out 2>&1 & " }); 
		}				
		
	}
	
	public void StartServer() {
	
		 m_oServer = new CommServer(Commons.SERVICE_PORT, m_nNodeID); 
	     m_oServer.Initialize();
	     m_oServer.StartService(); //New thread for master
	        
	}
	
	public void CreateClients() {
		
		m_oClients = new CommClient[Commons.NUMBER_OF_VMS];
		for(int i=0; i<Commons.NUMBER_OF_VMS; i++) {
			m_oClients[i] = new CommClient();
			m_oClients[i].Initialize(Commons.VM_NAMES[i], Commons.SERVICE_PORT); 
		}
		
	}
	
	public void CallStartProcessing(String pattern) {
		
		for( int i=0; i<Commons.NUMBER_OF_VMS; i++) {
			try
			{
				m_oClients[i].startProcessing(pattern);
			} catch (TException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void WaitForServiceToStop() {
	
		m_oServer.WaitForServiceToStop();
	}
	
	public void userOption() {		

		Scanner user_input = new Scanner(System.in);
		String input;
		System.out.println("Enter 's' for new search or 'q' for quitting");
		input = user_input.nextLine(); 

		if(input.equals("s"))
		{
			System.out.println("Enter the pattern/string to search for : ");
			String pattern = user_input.nextLine();
			user_input.close();
			CallStartProcessing(pattern);
		}	
		return;
	}
	
	public void setGrepOutputData (int nodeID, String data ) {
		m_sGrepOutputData[nodeID] = data;
		synchronized (m_oLock) {
			m_nDoneProcessingNumber = m_nDoneProcessingNumber + 1;
		}
		if(m_nDoneProcessingNumber == Commons.NUMBER_OF_VMS) {
			printGrepOutput();
		}
	}
	
	public void printGrepOutput() {
		
		System.out.println("Printing data : ");
		for(int i=0; i<Commons.NUMBER_OF_VMS; i++) {
			System.out.println("Node number : "+String.valueOf(i));
			System.out.println(m_sGrepOutputData[i]);
		}
		userOption();
	}	
	
	//Closes client transport
	public void CloseClients() {
		for(int i=0; i<Commons.NUMBER_OF_VMS; i++) 
			m_oClients[i].Close(); 
		}
	
	//Decides which functions to call on appropriate node
	public void startGrep() {
		
		//Call startServer on all the VMS.
		StartServer();
        
		
        if(m_nNodeID == Commons.MASTER) {
        	
        	//Run the executable on all the VMS 
        	RunExecutables();
        	
        	try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        	//Create all VM clients on the master
        	CreateClients();
        	
        	//Master proxy for Master
        	m_oMasterProxy = m_oClients[0];
        	
        }
        else {
        	
        	//Create Master proxy on all VMs
        	m_oMasterProxy = new CommClient();
        	m_oMasterProxy.Initialize(Commons.VM_NAMES[Commons.MASTER],Commons.SERVICE_PORT);
        
        } 
     
        m_oServer.setMasterProxy(m_oMasterProxy);
        m_oServer.setControllerProxy(this);
        
        if(m_nNodeID == Commons.MASTER)
        {	
        	userOption();	
        }
        
        //Exit gracefully
	}
}
