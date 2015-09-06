package edu.uiuc.cs425;

import org.apache.thrift.TException;

import edu.uiuc.cs425.DistributedGrep.Iface;

public class DistGrepServiceImpl implements Iface {
	
	private CommClient m_oMasterProxy;
	private int 	   m_nNodeIndex;
	
	public DistGrepServiceImpl() {
		// TODO Auto-generated constructor stub
		m_oMasterProxy = null;
	}

	public void setMasterProxy(CommClient masterProxy) {
		m_oMasterProxy = masterProxy;
	}
	
	public void setNodeID(int nodeID)
	{
		m_nNodeIndex = nodeID;
	}
	
	public void startProcessing(String pattern) throws TException {
		// TODO Auto-generated method stub

		System.out.println("Received startProccessing request");
		m_oMasterProxy.doneProcessing(m_nNodeIndex);
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
		// TODO Auto-generated method stub
		System.out.println("Received doneProccessing message from node " + String.valueOf(nodeIndex));
		return;
	}

}
