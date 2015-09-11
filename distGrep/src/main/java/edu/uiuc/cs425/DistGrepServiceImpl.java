package edu.uiuc.cs425;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.thrift.TException;

import edu.uiuc.cs425.DistributedGrep.Iface;

public class DistGrepServiceImpl implements DistributedGrep.Iface {
	
	private CommClient 				m_oMasterProxy;
	private int 	   				m_nNodeIndex;
	private FileProcessing 			m_oFileProcessing;
	private int 					m_nDoneProcessingNumber = 0;
	private int						m_nReceivedDataNumber = 0;
	private final Object 			m_oLock;
	private String [] 				m_sGrepOutputData;
	
	public DistGrepServiceImpl() {
		m_oMasterProxy = null;
		m_oFileProcessing = new FileProcessing();
		m_oFileProcessing.Initialize(Commons.VM_NAMES[m_nNodeIndex]);
		m_oLock = new Object();
		m_sGrepOutputData = new String[7];
	}

	public void setMasterProxy(CommClient masterProxy) {
		m_oMasterProxy = masterProxy;
	}
	
	public void setNodeID(int nodeID) {
		m_nNodeIndex = nodeID;
	}
	
	public void startProcessing(String pattern) throws TException {
		
		System.out.println("Received startProccessing request");
		//Call the file search
		m_oFileProcessing.StartSearching(pattern);
		m_oMasterProxy.doneProcessing(m_nNodeIndex);
		sendOutputHelper(); //Calls Send Output		
	}
	
	public void sendOutputHelper() {
		try {
			//System.out.println("Receiving data from node " + String.valueOf(nodeIndex));
			try {

				byte[] encoded = Files.readAllBytes(Paths.get("./logs/grepResult.out"));
				String data = new String(encoded, Charset.defaultCharset());
				m_oMasterProxy.transferOutput(m_nNodeIndex, data );
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			}			
			//return data;
		} catch (TException e) {
			e.printStackTrace();
		}		
	}
	
	public void transferOutput(int nodeIndex, String data) throws TException {
		
		System.out.println("Receiving data from node " + String.valueOf(nodeIndex));
		m_sGrepOutputData[nodeIndex] = data;
		synchronized (m_oLock) {
			m_nReceivedDataNumber = m_nReceivedDataNumber + 1;
		}
		System.out.println("Received data from : "+String.valueOf(m_nReceivedDataNumber));		
		if(m_nReceivedDataNumber == 7) {
			
			System.out.println("Printing data : ");
			for(int i=0; i<7; i++)
			{
				System.out.println("Node number : "+String.valueOf(i));
				System.out.println(m_sGrepOutputData[i]);
			}
			
		}
		//System.out.println(data);
		//Commons.SystemCommand(new String[] {  "echo ", myFile, " > $HOME/log"+nodeIndex+".txt &" }); 
	}
	
	public boolean isAlive() throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getProgress() throws TException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void doneProcessing(int nodeIndex) throws TException {
		
		System.out.println("Received doneProccessing message from node " + String.valueOf(nodeIndex));
		//ADD STUFF TO CALL FOR DATA TRANSFER
		
		return;
	}

}
