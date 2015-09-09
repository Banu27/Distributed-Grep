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
	
	private CommClient m_oMasterProxy;
	private int 	   m_nNodeIndex;
	private FileProcessing m_oFileProcessing;
	
	public DistGrepServiceImpl() {
		m_oMasterProxy = null;
		m_oFileProcessing = new FileProcessing();
		m_oFileProcessing.Initialize(Commons.VM_NAMES[m_nNodeIndex]);
	}

	public void setMasterProxy(CommClient masterProxy) {
		m_oMasterProxy = masterProxy;
	}
	
	public void setNodeID(int nodeID)
	{
		m_nNodeIndex = nodeID;
	}
	
	public void startProcessing(String pattern) throws TException {
		
		System.out.println("Received startProccessing request");
		//Call the file search
		m_oFileProcessing.StartSearching();
		doneProcessing();
		sendOutput();
		
	}
	
	public void doneProcessing() {
		try {
			//WHAT IS THIS? WHERE IS THIS BEING CALLED?
			m_oMasterProxy.doneProcessing(m_nNodeIndex);
			
		} catch (TException e) {
			e.printStackTrace();
		}		
	}

	public void sendOutput() {
		try {
			//System.out.println("Receiving data from node " + String.valueOf(nodeIndex));
			try {

				byte[] encoded = Files.readAllBytes(Paths.get("./logs/log1.txt"));
				String data = new String(encoded, Charset.defaultCharset());
				m_oMasterProxy.sendOutput(m_nNodeIndex, data );
				
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
	
	public void sendOutput(int nodeIndex, String data) throws TException {
		
		System.out.println("Receiving data from node " + String.valueOf(nodeIndex));		
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
		return;
	}

}
