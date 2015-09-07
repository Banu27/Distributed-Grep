package edu.uiuc.cs425;

import org.apache.thrift.TException;

/**
 * Hello world!
 *
 */
public class App 
{
	private static CommClient [] m_oClients;
	private static CommClient m_oMasterProxy;
	private static int m_nNodeID;
	public static CommServer m_oServer;
	
	public static void RunExecutables() {
		
		String sshCommand = "ssh";
		
		//command is the executable run with &
		String command = "java -cp $HOME/distGrepFinal.jar edu.uiuc.cs425.App";
		
		//Start all VMS
		for (int i=1; i<Commons.NUMBER_OF_VMS; i++) {
			Commons.SystemCommand(new String[] { sshCommand , Commons.VM_NAMES[i] , command + " " +  String.valueOf(i) + " > $HOME/grep.out 2>&1 & " }); 
		}				
		
	}
	
	public static void StartServer() {
	
		 m_oServer = new CommServer(Commons.SERVICE_PORT, m_nNodeID); 
	     m_oServer.Initialize();
	     m_oServer.StartService(); //New thread for master
	        
	}
	
	public static void CreateClients() {
		
		m_oClients = new CommClient[Commons.NUMBER_OF_VMS];
		for(int i=0; i<Commons.NUMBER_OF_VMS; i++) {
			m_oClients[i] = new CommClient();
			m_oClients[i].Initialize(Commons.VM_NAMES[i], Commons.SERVICE_PORT); 
		}
		
	}
	
	public static void CallStartProcessing() {
		
		for( int i=0; i<Commons.NUMBER_OF_VMS; i++) {
			try
			{
				m_oClients[i].startProcessing("some_pattern");
			} catch (TException e)
			{
				e.printStackTrace();
			}
		}
	}
	
    public static void main( String[] args ) throws InterruptedException
    {
        m_nNodeID = Integer.parseInt(args[0]);
        
        System.out.println( "Node ID: " + String.valueOf(m_nNodeID));
        //Starting the servers
        StartServer();
        
        if(m_nNodeID == Commons.MASTER) {
        	
        	RunExecutables();
        	Thread.sleep(4000);
        	CreateClients();
        	m_oMasterProxy = m_oClients[0];
        	
        }
        else {
        	
        	m_oMasterProxy = new CommClient();
        	m_oMasterProxy.Initialize(Commons.VM_NAMES[Commons.MASTER],Commons.SERVICE_PORT);
        
        } 
     
        m_oServer.setMasterProxy(m_oMasterProxy);
        m_oServer
        
        if(m_nNodeID == Commons.MASTER)
		CallStartProcessing();
        
        // waits for the thrift service to stop
        m_oServer.WaitForServiceToStop();
        
        
        
    }
}
