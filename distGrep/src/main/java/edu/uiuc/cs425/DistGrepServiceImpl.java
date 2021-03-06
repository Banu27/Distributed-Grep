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
	private Controller 				m_oControllerProxy;
	private String 					m_sDirPath;
	
	public DistGrepServiceImpl() {
		m_oMasterProxy = null;
		m_oFileProcessing = new FileProcessing();
		m_oFileProcessing.Initialize(Commons.VM_NAMES[m_nNodeIndex]);
	}

	public void setMasterProxy(CommClient masterProxy) {
		m_oMasterProxy = masterProxy;
	}
	
	public void setControllerProxy(Controller controllerProxy) {
		m_oControllerProxy = controllerProxy;
	}
	
	public void setSearchDir(String path)
	{
		m_sDirPath = path;
	}
	
	
	public void setNodeID(int nodeID) {
		m_nNodeIndex = nodeID;
	}
	
	public void startProcessing(String pattern) throws TException {
		
		System.out.println("Received startProccessing request");
		//Call the file search
		m_oFileProcessing.Reset();
		m_oFileProcessing.StartSearching(pattern,m_sDirPath);
		String data = m_oFileProcessing.GetMatchedLines();
		int count = m_oFileProcessing.GetLineCount();
		//System.out.println("Data : " + data);
		
		if(m_nNodeIndex == Commons.MASTER)
		{
			doneProcessing(m_nNodeIndex, data, count);
		}	
		else
			m_oMasterProxy.doneProcessing(m_nNodeIndex, data, count);
	}
	
	public boolean isAlive() throws TException {
		// TODO Auto-generated method stub
		return true;
	}

	public int getProgress() throws TException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void doneProcessing(int nodeID, String data, int count) throws TException {
		
		System.out.println("Received doneProccessing message from node " + String.valueOf(nodeID));
		//System.out.println("Data : " + data);
		m_oControllerProxy.setGrepOutputData(nodeID, data, count);
		return;
	}

}
